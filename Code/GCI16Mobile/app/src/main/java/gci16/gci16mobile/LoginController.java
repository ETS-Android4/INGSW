package gci16.gci16mobile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

public class LoginController extends AppCompatActivity {
    private Button loginButton;
    private int operatorId;
    private String password;
    private String session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText operatorIdEditText = (EditText) findViewById(R.id.operator_id_edit_text);
        final EditText passwordEditText = (EditText) findViewById(R.id.operator_password_edit_text);
        loginButton = (Button) findViewById(R.id.login_button);

        final SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit(); //TODO elimina
        editor.clear().apply();
        operatorId = pref.getInt("operatorId", -1);
        if(operatorId!=-1) operatorIdEditText.setText(Integer.toString(operatorId));
        password = pref.getString("password", null);
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
                String operatorId = operatorIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(password!=null && !password.equals("") && operatorId!=null && !operatorId.equals(""))
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
                if(operatorIdText==null || operatorIdText.equals("") || password==null || password.equals(""))
                    return;

                int operatorId = Integer.valueOf(operatorIdText);
                LoginController.this.operatorId = operatorId;
                LoginController.this.password = password;
                Boolean logged = login(operatorId, password);
                if(logged==null){
                    Log.d("DEBUG", "Impossibile raggiungere il server");
                }
                else if(logged){
                    Intent intent = new Intent(LoginController.this, ReadingsController.class);
                    intent.putExtra("operatorId", operatorId);
                    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                    String session = preferences.getString("session", null);
                    if(session==null) Log.d("DEBUG", "session is null nel blocco che chiama l'intent");
                    intent.putExtra("session", session);
                    startActivity(intent);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginController.this, R.style.Theme_Design);
                    builder.setMessage("Wrong id or password").setTitle("Error")
                           .setCancelable(false)
                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        String session = pref.getString("session", null);
        if(session!=null){ //sessione salvata
            Intent intent = new Intent(LoginController.this, ReadingsController.class);
            intent.putExtra("operatorId", operatorId);
            intent.putExtra("session", session);
            Log.d("DEBUG", "Operator: "+intent.getIntExtra("operatorId", -1));
            Log.d("DEBUG", "Cookie: "+intent.getStringExtra("session"));

            startActivity(intent);
        }

    }



    //TODO
    //tenta il login, se questo ha successo salva il session id
    private Boolean login(final int operatorId,final String password) {
        loginButton.setEnabled(false);
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... strings) {

                SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                String ip = getResources().getString(R.string.server_address);
                int port = getResources().getInteger(R.integer.server_port);

                //salva token e ultime credenziali
                editor.putInt("operatorId", operatorId)
                      .putString("password", password)
                      .apply();
                try {
                    String urlString = String.format("http://%s:%d/GCI16/ReadingsOperatorLogin?operatorId=%d&password=%s", ip, port, operatorId, password);
                    Log.d("DEBUG", "url = " + urlString);
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(getResources().getInteger(R.integer.connection_timeout));
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    Log.d("DEBUG", "response code : "+responseCode);
                    if (responseCode == getResources().getInteger(R.integer.server_ok)) {
                        String cookieName = getResources().getString(R.string.session_cookie_name);
                        String cookieNameMatch = cookieName+".*";
                        String cookie = null;
                        String cookieString = connection.getHeaderField("Set-Cookie").replaceAll("\\s", "");
                        for (String s : cookieString.split(";")) {
                            if (s.matches(cookieNameMatch)) {
                                cookie = s;
                                Log.d("Debug", cookie);
                                break;
                            }
                        }
                        if (cookie == null) {
                            Log.d("DEBUG", "No cookie, cookiestring = "+cookieString);
                            return false;
                        } else {
                            Log.d("DEBUG", "Cookie obtained: "+cookie);
                        }
                        editor.putString("session", cookie).apply();
                        return true;
                    }
                } catch (MalformedURLException e) {
                    Log.e("Malformed URL", String.format("%s/%d", R.string.server_address, R.integer.server_port));
                    return null;
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                    return null;
                }
                return false;
            }
        };
        asyncTask.execute();
        Boolean result = null;
        try {
            result = asyncTask.get();
        } catch (InterruptedException e) {
            Log.d("Interrupted", e.getMessage());
        } catch (ExecutionException e) {
            Log.d("Execution", e.getMessage());
        }
        loginButton.setEnabled(true);
        return result;
    }
}
