package gci16.mobile.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import gci16.mobile.R;
import gci16.mobile.entities.Assignment;
import gci16.mobile.entities.Reading;

/**
 * Handles user's interaction with the application.
 * It lets the user refresh the list of his assignment,
 * save a reading and send all the readings done.
 *
 * @author Riccardo
 */
public class ReadingsController extends AppCompatActivity{
    private int operatorId;
    private int selectedItem = -1;
    private String session;
    private List<Reading> readingsDone;
    private Set<Assignment> assignmentsCompleted;
    private List<Assignment> assignmentsLeft;
    private ArrayAdapter<Assignment> assignmentTableAdapter;

    @Override
    public void onBackPressed(){
        Intent mainActivity = new Intent(Intent.ACTION_MAIN);
        mainActivity.addCategory(Intent.CATEGORY_HOME);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readings_controller);
        getSupportActionBar().setTitle("GCI '16");

        final Button updateButton = (Button) findViewById(R.id.update_button);
        Button saveButton = (Button) findViewById(R.id.save_reading_button);
        Button sendButton = (Button) findViewById(R.id.send_readings_button);
        ListView assignmentTable = (ListView) findViewById(R.id.assignment_table);

        session = this.getIntent().getStringExtra("session");
        operatorId = this.getIntent().getIntExtra("operatorId", -1);
        loadData(); //loads the attributes from the storage

        // event of "update" button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAssignments();
            }
        });

        // event of "save" button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				if(selectedItem<0 || selectedItem>assignmentsLeft.size()) return;
				AlertDialog.Builder builder = new AlertDialog.Builder(ReadingsController.this);

				// input for float values
				final EditText input = new EditText(builder.getContext());
				input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View view, boolean hasFocus) {
						if (!hasFocus) {
							InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
							inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
						}
					}
				});

				// shows consumption input popup
				builder.setMessage("Insert water consumption\n(cubic meters)")
						.setView(input)
						.setNegativeButton("Cancel", null)
						.setPositiveButton("Save", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int i) {
								String text = input.getText().toString();
								if (text.length()>0){
									dialog.cancel();
									try {
										saveReading(Float.valueOf(text), assignmentsLeft.get(selectedItem));
									}catch(NumberFormatException e){}
								}
							}
						});
				builder.create().show();
            }
        });

        // event of "send" button
        sendButton.setEnabled(!readingsDone.isEmpty());
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReadings();
            }
        });

        // populates assignment's table
        assignmentTableAdapter = new ArrayAdapter<Assignment>(this,R.layout.list_row_item, assignmentsLeft){
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent){
                Context context = getContext();
                View row = LayoutInflater.from(context).inflate(R.layout.assignment_table_row, parent, false);
                Assignment a = getItem(position);
                ((TextView) row.findViewById(R.id.meterID)).setText(String.valueOf(a.getMeterId()));
                ((TextView) row.findViewById(R.id.address)).setText(a.getAddress());
                ((TextView) row.findViewById(R.id.customer)).setText(a.getCustomer());
                return row;
            }
        };
        assignmentTable.setAdapter(assignmentTableAdapter);
        assignmentTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = i;
            }
        });

    }

    /**
     * Loads activity's attributes from storage.
     */
    private void loadData(){
        Type type; // type of the collection
        String json; // json string representing the collection
        Gson gson = new Gson(); // json parser
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);

        type = new TypeToken<HashSet<Assignment>>(){}.getType();
        json = pref.getString("assignmentsCompleted"+operatorId, null);
        if(json==null) assignmentsCompleted = new HashSet<>();
        else assignmentsCompleted = gson.fromJson(json, type);
        type = new TypeToken<ArrayList<Assignment>>(){}.getType();
        json = pref.getString("assignmentsLeft"+operatorId, null);
        if(json==null) assignmentsLeft = new ArrayList<>();
        else assignmentsLeft = gson.fromJson(json, type);
        type = new TypeToken<LinkedList<Reading>>(){}.getType();
        json = pref.getString("readingsDone"+operatorId, null);
        if(json==null) readingsDone= new LinkedList<>();
        else readingsDone = gson.fromJson(json, type);
    }

    /**
     * Saves the reading in phone's storage.
     * The method uses the assignment selected by the user
     * to create the reading.
     * The assignment is also removed from the list of the ones left
     * and put in the ones completed.
     *
     * @param consumption the amount of water consumed
     */
    private void saveReading(final float consumption, final Assignment a){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View recap  = getLayoutInflater().inflate(R.layout.reading_recap_layout, null);
        ((TextView)recap.findViewById(R.id.meterid_value)).setText(String.valueOf(a.getMeterId()));
        ((TextView)recap.findViewById(R.id.address_value)).setText(a.getAddress());
        ((TextView)recap.findViewById(R.id.customer_value)).setText(a.getCustomer());
        ((TextView)recap.findViewById(R.id.consumption_value)).setText(String.valueOf(consumption));
        builder.setView(recap).setTitle("Confirm Reading")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Reading r = new Reading(operatorId, a.getMeterId(),new Date(), consumption);
                        Gson gson = new Gson();

                        // modifies the collections
                        assignmentsCompleted.add(a);
                        readingsDone.add(r);
                        assignmentTableAdapter.remove(a);

                        // saves collections' states in the storage
                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putString("readingsDone"+operatorId, gson.toJson(readingsDone))
                        .putString("assignmentsCompleted"+operatorId, gson.toJson(assignmentsCompleted))
                        .putString("assignmentsLeft"+operatorId, gson.toJson(assignmentsLeft))
                        .apply();

                        // updates the UI and reset the selectedItem
                        ((ListView) findViewById(R.id.assignment_table)).performItemClick(null, -1, 0);
                        findViewById(R.id.send_readings_button).setEnabled(true);
                        selectedItem = -1;
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    /**
     * Sends all the readings saved to the server.
     * Shows a popup message which shows the result of the action.
     */
    private void sendReadings(){
        if(readingsDone.isEmpty()) return;
        // this task sends the request to the server
        // it returns null on connection error, the response code otherwise
        AsyncTask<Void, Void, Integer> asyncTask = new AsyncTask<Void, Void, Integer>(){
            @Override
            protected Integer doInBackground(Void... voids) {
                Gson gson = new Gson();
                String json = gson.toJson(readingsDone);
                Integer responseCode = null;
                try {
                    String ip = getResources().getString(R.string.server_address);
                    int port = getResources().getInteger(R.integer.server_port);
                    String formatString = "http://%s:%d/GCI16/Readings";
                    URL url = new URL(String.format(Locale.getDefault(),formatString, ip, port));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(getResources().getInteger(R.integer.connection_timeout));
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Cookie", session);
                    // scrive nel messaggio le letture effettuate in formato json
                    connection.setDoOutput(true);
                    DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                    writer.write(json.getBytes());
                    writer.close();

                    //invia
                    connection.connect();
                    responseCode = connection.getResponseCode();
                } catch (MalformedURLException e) {
                    Log.e("Bad URL", e.getMessage());
                } catch (SocketTimeoutException e) {
                    Log.e("SocketTimeout", e.getMessage());
                } catch (IOException e) {
                    Log.e("Connection error", e.getMessage());
                }
                return responseCode;
            }
        };

        // executes the task
        asyncTask.execute();
        Integer responseCode = null;
        try {
            responseCode = asyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("TaskError", e.getMessage()); return;
        }

        if(responseCode==null || responseCode!=200){ // shows error messages
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(false);
            if(responseCode==null)
                builder.setView(R.layout.error_no_connection_layout);
            else if(responseCode==462)
                builder.setView(R.layout.error_session_expired_layout);
            else
                builder.setView(R.layout.error_unknown_layout);
            if(responseCode!=null && responseCode==462)
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        disconnect();
                        dialog.cancel();
                    }
                });
            else
                builder.setPositiveButton("OK", null);
            builder.create().show();
            return;
        }

        // resets the collections and erases them from preferences
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        readingsDone.clear();
        assignmentsCompleted.clear();
        editor.remove("readingsDone"+operatorId)
                .remove("assignmentsCompleted"+operatorId)
                .apply();

        // manages changes on UI
        findViewById(R.id.send_readings_button).setEnabled(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReadingsController.this);
        builder.setMessage("Readings successfully sent!")
                .setPositiveButton("OK", null);
        builder.create().show();
    }

    /**
     * Creates the options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.readings_menu, menu);
        return true;
    }

    /**
     * Manages the selection of menu item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout_item:  // Presses logout
                AlertDialog.Builder builder = new AlertDialog.Builder(ReadingsController.this);
                builder.setView(R.layout.logout_message_layout)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                disconnect();
                            }
                        })
                        .setNegativeButton("NO", null);
                builder.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Downloads from the server the assignments of the operator.
     * Shows a message whenever an error occurs and disconnects if
     * the session has expired.
     */
    private void updateAssignments() {
        // this buffer contains the json string returned by the server
        // the StringBuffer has been used because it is thread-safe and
        // can contain a string which can be edited without the buffer
        // reference being modified
        final StringBuffer buffer = new StringBuffer();
        // this task sends the request to the server and returns
        // it returns null on connection error, the response code otherwise
        AsyncTask<Void, Void, Integer> asyncTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                Integer responseCode = null;
                try {
                    String ip = getResources().getString(R.string.server_address);
                    int port = getResources().getInteger(R.integer.server_port);
                    String formatString = "http://%s:%d/GCI16/Assignments";
                    URL url = new URL(String.format(Locale.getDefault(),formatString,ip, port));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(getResources().getInteger(R.integer.connection_timeout));
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", session);
                    connection.setDoOutput(false);
                    connection.connect();
                    responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        // reads json string and puts it in the buffer
                        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        for (String s = reader.readLine(); s != null; s = reader.readLine())
                            buffer.append(s);
                        reader.close();
                    }
                } catch (SocketTimeoutException e) {
                    Log.e("Timeout", e.getMessage());
                } catch (MalformedURLException e) {
                    Log.e("URL", e.getMessage());
                } catch (IOException e) {
                    Log.e("Connection", e.getMessage());
                }
                return responseCode;
            }
        };

        //gets the response from server
        asyncTask.execute();
        Integer responseCode = null;
        try {
            responseCode = asyncTask.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("TaskError", e.getMessage()); return;
        }
        //shows error messages
        if(responseCode == null || responseCode!=200){
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(false);
            if(responseCode==null)
                builder.setView(R.layout.error_no_connection_layout);
            else if(responseCode==462)
                builder.setView(R.layout.error_session_expired_layout);
            else {
                builder.setView(R.layout.error_unknown_layout);
                Log.e("ServerResponse", "Response code : " + responseCode);
            }

            if(responseCode!=null && responseCode==462)
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        disconnect();
                        dialog.cancel();
                    }
                });
            else
                builder.setPositiveButton("OK", null);
            builder.create().show();
            return;
        }

        // gets the assignments from the json string
        String json = buffer.toString();
        Gson gson = new Gson();
        Type type = new TypeToken<HashSet<Assignment>>(){}.getType();
        Set<Assignment> downloadedAssignments = gson.fromJson(json, type);

        // filters only the assingments which have not been completed
        downloadedAssignments.removeAll(assignmentsCompleted);
        downloadedAssignments.removeAll(assignmentsLeft);

        // adds to the ones left
        if (!downloadedAssignments.isEmpty()) {
            assignmentTableAdapter.addAll(downloadedAssignments);
            SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
            editor.putString("assignmentsLeft"+operatorId, gson.toJson(assignmentsLeft)).apply();
        }
    }

    /**
     * Deletes the session cookie and finishes the activity
     */
    private void disconnect(){
        SharedPreferences sharedPreferences = getSharedPreferences("session_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.remove("session").apply();
        finish();
    }
}