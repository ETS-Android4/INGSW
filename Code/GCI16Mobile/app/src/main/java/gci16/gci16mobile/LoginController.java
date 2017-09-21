package gci16.gci16mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class LoginController extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText operatorIdEditText = (EditText) findViewById(R.id.operator_id_edit_text);
        final EditText passwordEditText = (EditText) findViewById(R.id.operator_password_edit_text);
        final Button loginButton = (Button) findViewById(R.id.login_button);

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit(); //TODO elimina
        editor.clear().apply();
        final int operatorId = pref.getInt("operatorId", -1);
        if(operatorId!=-1) operatorIdEditText.setText(String.valueOf(operatorId));
        String password = pref.getString("password", null);
        if(password!=null) passwordEditText.setText(password);
        if(password!=null && operatorId!=-1) loginButton.setEnabled(true);

        // aggiunge listener che nasconde la tastiera quando il focus cambia
        View.OnFocusChangeListener keyboardCloser = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        };
        operatorIdEditText.setOnFocusChangeListener(keyboardCloser);
        passwordEditText.setOnFocusChangeListener(keyboardCloser);

        TextWatcher loginButtonActivator = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String operatorIdText = operatorIdEditText.getText().toString();
                String passwordText = passwordEditText.getText().toString();
                if(passwordText.length()>0 && operatorIdText.length()>0)
                    loginButton.setEnabled(true);
                else
                    loginButton.setEnabled(false);
            }
        };
        operatorIdEditText.addTextChangedListener(loginButtonActivator);
        passwordEditText.addTextChangedListener(loginButtonActivator);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String operatorIdText = operatorIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(password.length()<=0 || operatorIdText.length()<=0)
                    return;
                login(Integer.parseInt(operatorIdText), password);
            }
        });

        pref = getSharedPreferences("session_preference", Context.MODE_PRIVATE);
        String session = pref.getString("session", null);
        if(session!=null && operatorId>0) //sessione salvata
            startReadingController(operatorId, session);
    }


    //TODO
    //tenta il login, se questo ha successo salva il session id
    private void login(final int operatorId, final String password) {
        final StringBuffer buffer = new StringBuffer();
        AsyncTask<Void, Void, Integer> asyncTask = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... strings) {
                String ip = getResources().getString(R.string.server_address);
                int port = getResources().getInteger(R.integer.server_port);
                Integer responseCode = null;
                try {
                    String formatString = "http://%s:%d/GCI16/ReadingsOperatorLogin?operatorId=%d&password=%s";
                    String urlString = String.format(Locale.getDefault(), formatString, ip, port, operatorId, password);
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(getResources().getInteger(R.integer.connection_timeout));
                    connection.setRequestMethod("GET");
                    connection.connect();
                    responseCode = connection.getResponseCode();
                    if(responseCode==200) { // setta cookie
                        String cookieName = getResources().getString(R.string.session_cookie_name);
                        String cookieNameMatch = cookieName+".*";
                        String cookieString = connection.getHeaderField("Set-Cookie").replaceAll("\\s", "");
                        for (String s : cookieString.split(";")) {
                            if (s.matches(cookieNameMatch)) {
                                buffer.append(s);
                                break;
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    Log.e("Malformed URL", String.format("%s/%d", R.string.server_address, R.integer.server_port));
                } catch (SocketTimeoutException e) {
                    Log.e("SocketTimeout", e.getMessage());
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                }
                return responseCode;
            }
        };
        asyncTask.execute();
        Integer responseCode = null;
        try {
            responseCode = asyncTask.get();
        } catch (InterruptedException e) {
            Log.d("Interrupted", e.getMessage());
            return;
        } catch (ExecutionException e) {
            Log.d("Execution", e.getMessage());
            return;
        }
        //TODO
        //MESSAGGI DI ERRORE CON LAYOUT
        String session = buffer.toString();
        if (responseCode==null || responseCode!=200 || session.length()<=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(responseCode==null)
                builder.setView(R.layout.error_no_connection).setTitle("Connectivity Problems");
            else if (responseCode==402)
                builder.setView(R.layout.error_login);
            else
                builder.setView(R.layout.error_unknown);
            builder.setPositiveButton("OK", null);
            builder.create().show();
            return;
        }

        // salva ultime credenziali e token di accesso
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt("operatorId",operatorId)
                .putString("password", password)
                .apply();

        startReadingController(operatorId, password);
    }

    private void startReadingController(int operatorId, String session){
        SharedPreferences sharedPreferences = getSharedPreferences("session_preference", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putString("session", session).apply();

        Intent intent = new Intent(LoginController.this, ReadingsController.class);
        intent.putExtra("operatorId", operatorId);
        intent.putExtra("session", session);
        startActivity(intent);
    }
}
