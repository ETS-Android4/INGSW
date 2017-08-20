package gci16.gci16mobile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ReadingsController extends AppCompatActivity{

    private final Collection<Reading> readingsDone = new HashSet<>();
    private final Collection<Assignment> assignmentsLeft = new HashSet<>();
    private Button updateButton;
    private Button saveButton;
    private Button sendButton;

    @Override
    public void onBackPressed(){
        //nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readings_controller);

        updateButton = (Button) findViewById(R.id.update_button);
        getSupportActionBar().setTitle("GCI '16");
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO check wether connection is available
                updateAssignments();
            }
        });

        saveButton = (Button) findViewById(R.id.save_reading_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Assignment r = new Assignment(12, 3392, "Via cazzo", "Gino");
                //retrieve reading

                readConsumption(r);
            }
        });

        sendButton = (Button) findViewById(R.id.send_readings_button);
        //JAVA8 btn.setOnClickListener( view -> {sendReadings();});
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendReadings();
            }
        });

        loadData();

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        if(pref.getString("readingsDone", null)==null)
            sendButton.setEnabled(false);

    }


    //TODO
    private void loadData(){

    }

    //TODO
    private void saveReading(Assignment a, float consumption) {
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
                        //TODO salva
                        sendButton.setEnabled(true);
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //TODO
    private void readConsumption(final Assignment a){
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
                    saveReading(a, consumption);
                }
            }
        });
    }

    //TODO
    private void sendReadings() {
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);

        //check internet connection
        //add cookie
        //urlconnection
        //wait for response

        sendButton.setEnabled(false);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ReadingsController.this, R.style.Theme_Design);
                builder.setMessage("Do you want to logout?" +
                        "Unsent will be stored in the phone untill you send them")
                        //JAVA8 .setPositiveButton("YES", (dialog, i) -> {finish();})
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
    private void updateAssignments(){

    }
}
