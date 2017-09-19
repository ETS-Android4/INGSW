package gci16.gci16mobile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ReadingsController extends AppCompatActivity{
    private int operatorId;
    int selectedItem;
    private String session;
    private List<Reading> readingsDone;
    private Set<Assignment> assignmentsCompleted;
    private List<Assignment> assignmentsLeft;
    private ArrayAdapter<Assignment> assignmentTableAdapter;
    private ListView assignmentTable;
    private Button sendButton;
    private Button saveButton;

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
        saveButton = (Button) findViewById(R.id.save_reading_button);
        sendButton = (Button) findViewById(R.id.send_readings_button);
        assignmentTable = (ListView) findViewById(R.id.assignment_table);

        loadData();
        if(session==null) Log.d("DEBUG", "session is null!");
        else Log.d("DEBUG", session);
        if(operatorId==-1) Log.d("DEBUG", "operatorId missing!");
        else Log.d("DEBUG", "operatorId = "+operatorId);

        // evento del bottone update
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean update = updateAssignments();
                Log.d("DEBUG", "update = " + update);
            }
        });

        // evento del bottone save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedItem<0 || selectedItem>assignmentsLeft.size()) return;
                readConsumption();
                if(!readingsDone.isEmpty())
                    sendButton.setEnabled(true);
            }
        });

        // evento del bottone send
        sendButton.setEnabled(!readingsDone.isEmpty());
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Boolean sent = sendReadings();
                    if(sent!=null && sent){
                        sendButton.setEnabled(false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ReadingsController.this);
                        builder.setMessage("Readings successfully sent!")
                                .setPositiveButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
            }
        });

        // popolamento della tablle delle assegnazioni
        assignmentTableAdapter = new AssignmentListAdapter(getApplicationContext(), assignmentsLeft);
        assignmentTable.setAdapter(assignmentTableAdapter);
        assignmentTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = i;
                saveButton.setEnabled(true);
            }
        });

    }


    //TODO
    private void loadData(){
        Type type;
        String json;
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        pref.edit().clear().apply();//todo rimuovi
        session = this.getIntent().getStringExtra("session");
        if(session==null){
            Log.d("DEBUG", "session is null da loaddata intent");
            session = pref.getString("session", null);
            if(session==null){
                Log.d("DEBUG", "session is null da loaddata preferences");
                finish(); return;}
        }
        operatorId = this.getIntent().getIntExtra("operatorId", -1);
        if(operatorId==-1){ finish(); return;}
        Gson gson = new Gson();
        type = new TypeToken<HashSet<Assignment>>(){}.getType();
        json = pref.getString("assignmentsCompleted"+operatorId, null);
        if(json==null) assignmentsCompleted = new HashSet<>();
        else assignmentsCompleted = (Set<Assignment>) gson.fromJson(json, type);
        type = new TypeToken<ArrayList<Assignment>>(){}.getType();
        json = pref.getString("assignmentsLeft"+operatorId, null);
        if(json==null) assignmentsLeft = new ArrayList<>();
        else assignmentsLeft = (List<Assignment>) gson.fromJson(json, type);
        type = new TypeToken<LinkedList<Reading>>(){}.getType();
        json = pref.getString("readingsDone"+operatorId, null);
        if(json==null) readingsDone= new LinkedList<>();
        else readingsDone = gson.fromJson(json, type);
    }

    //TODO
    private void saveReading(final float consumption){
        final Assignment a = assignmentsLeft.get(selectedItem);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm reading")
                .setMessage(String.format(
                "Meter ID       %d\n" +
                "Address        %s\n" +
                "Customer       %s\n" +
                "Consumption    %.2f m^3",
                a.getMeterId(),
                a.getAddress(),
                a.getCustomer(),
                consumption))
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        Reading r = new Reading(operatorId, a.getMeterId(),new java.util.Date(), consumption);
                        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        Resources res = getResources();
                        Gson gson = new Gson();
                        String s; //nomi degli elementi salvati nelle SharedPreferences
                        assignmentsCompleted.add(a);
                        s = res.getString(R.string.assignments_completed_pref)+operatorId;
                        editor.putString(s, gson.toJson(assignmentsCompleted));
                        readingsDone.add(r);
                        s = res.getString(R.string.readings_done_pref)+operatorId;
                        editor.putString(s, gson.toJson(readingsDone));
                        if(assignmentsLeft.remove(a)) Log.d("DEBUG", "Eliminato");
                        else Log.d("DEBUG", "Non eliminato");
                        assignmentTableAdapter.notifyDataSetChanged();
                        assignmentTable.invalidateViews();
                        s = res.getString(R.string.assignments_left_pref)+operatorId;
                        editor.putString(s, gson.toJson(assignmentsLeft));
                        editor.apply();
                        selectedItem=-1;
                        assignmentTable.performItemClick(null, -1, 0);
                        sendButton.setEnabled(true);
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //TODO
    private void readConsumption(){
        //input numerico
        final EditText input = new EditText(this);
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

        //mostra popup di input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Insert water consumption\n" +
                "(cubic meters)")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = input.getText().toString();
                if (text != null && !text.equals("")) {
                    float consumption = Float.valueOf(text);
                    dialog.cancel();
                    saveReading(consumption);
                }
            }
        });
    }

    //TODO
    private Boolean sendReadings(){
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                String ip = getResources().getString(R.string.server_address);
                int port = getResources().getInteger(R.integer.server_port);
                Gson gson = new Gson();
                String json = gson.toJson(readingsDone);
                Log.d("DEBUG", "Json = "+json);
                int responseCode=-1;
                try {
                    URL url = new URL(String.format("http://%s:%d/GCI16/Readings", ip, port));
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
                } catch(MalformedURLException e){
                    Log.e("Bad URL", e.getMessage());
                }catch(IOException e) {
                    Log.e("Connection error", e.getMessage());
                }

                Log.d("DEBUG", "response code : " + responseCode);
                if(responseCode==403) disconnect();
                if (responseCode == 200) {
                    SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    readingsDone.clear();
                    String s = getResources().getString(R.string.readings_done_pref) + operatorId;
                    editor.remove(s);
                    assignmentsCompleted.clear();
                    s = getResources().getString(R.string.assignments_completed_pref) + operatorId;
                    editor.remove(s);
                    editor.apply();
                    return true;
                }
                return null;
            }
        };
        asyncTask.execute();
        Boolean result = null;
        try {
            result = asyncTask.get();
        } catch (InterruptedException e) {
            Log.e("Interrupted", e.getMessage());
        } catch (ExecutionException e) {
            Log.e("Execution", e.getMessage());
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.readings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout_item:
                AlertDialog.Builder builder = new AlertDialog.Builder(ReadingsController.this);
                builder.setMessage("Do you want to logout?" +
                        "Unsent will be stored in the phone until you send them")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("NO", null);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO metodo che aggiorna la lista degli assegnamenti
    private boolean updateAssignments() {
        AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Resources res = getResources();
                String ip = res.getString(R.string.server_address);
                int port = res.getInteger(R.integer.server_port);
                int responseCode = -1;
                String json = null;
                try {
                    URL url = new URL(String.format("http://%s:%d/GCI16/Assignments", ip, port));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(getResources().getInteger(R.integer.connection_timeout));
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", session);
                    connection.setDoOutput(false);
                    Log.d("DEBUG", url.toString());
                    connection.connect();
                    responseCode = connection.getResponseCode();
                    Log.d("DEBUG", "response code : " + responseCode);
                    if (responseCode == res.getInteger(R.integer.server_unauthorized))
                        return null;
                    //todo sposta fuori dal thread
                    if (responseCode == 200) {
                        // leggi stringa json
                        BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        json = "";
                        for (String s = reader.readLine(); s != null; s = reader.readLine())
                            json = json + s;
                        reader.close();
                    }
                }catch (SocketTimeoutException e){
                    Log.e("Timeout", e.getMessage());
                } catch (MalformedURLException e) {
                    Log.e("URL", e.getMessage());
                    return null;
                } catch (IOException e) {
                    Log.e("Connection", e.getMessage());
                    return null;
                }
                return json;
            }
        };
        asyncTask.execute();
        String json = null;
        try {
            json = asyncTask.get();
        } catch (InterruptedException e) {
            Log.e("Interrupted task", e.getMessage());
        } catch (ExecutionException e) {
            Log.e("Exception in task", e.getMessage());
        }
        if(json==null) return false;

        // ottieni collection richiesta
        Gson gson = new Gson();
        Type type = new TypeToken<HashSet<Assignment>>(){}.getType();
        Set<Assignment> downloadedAssignments = gson.fromJson(json, type);

        // distingui quelle già salvate ma non inviate
        downloadedAssignments.removeAll(assignmentsCompleted);
        downloadedAssignments.removeAll(assignmentsLeft);

        if (!downloadedAssignments.isEmpty()) {
            assignmentTableAdapter.addAll(downloadedAssignments);
            SharedPreferences pref = getPreferences(Context.MODE_APPEND);
            SharedPreferences.Editor editor = pref.edit();
            String prefname = getResources().getString(R.string.assignments_left_pref) + operatorId;
            editor.putString(prefname, gson.toJson(assignmentsLeft));
            editor.apply();
        }
        return true;
    }

    private void ShowConnectionError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReadingsController.this, R.style.Theme_Design);
    }

    private void disconnect(){
        //TODO mostra sessione finita!!
        finish();
    }

}