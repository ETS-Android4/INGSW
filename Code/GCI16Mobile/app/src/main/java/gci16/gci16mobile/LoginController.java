package gci16.gci16mobile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginController extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText operatorIdEditText = (EditText) findViewById(R.id.operator_id_edit_text);
        final EditText passwordEditText = (EditText) findViewById(R.id.operator_password_edit_text);
        final Button loginButton = (Button) findViewById(R.id.login_button);

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        int op = pref.getInt("operatorId", -1);
        if(op!=-1) operatorIdEditText.setText(Integer.toString(op));
        String pass = pref.getString("password", null);
        if(pass!=null){
            passwordEditText.setText(pass);
            loginButton.setEnabled(true);
        }

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
                boolean logged = false;
                try {
                    logged = login(operatorId, password);
                }catch(IOException ex){
                    Log.d("DEBUG", "Impossibile raggiungere il server");
                    Log.d("DEBUG", ex.getMessage());
                    return;
                }
                if(logged){
                    Intent intent = new Intent(LoginController.this, ReadingsController.class);
                    intent.putExtra("operatorId", operatorId);
                    startActivity(intent);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginController.this, R.style.Theme_Design);
                    builder.setMessage("Wrong id or password").setTitle("Error");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

        if(pref.getString("session", null)!=null){ //sessione salvata
            Intent intent = new Intent(LoginController.this, ReadingsController.class);
            intent.putExtra("operatorId", pref.getInt("operatorId", -1));
            startActivity(intent);
        }

    }



    //TODO
    //tenta il login, se questo ha successo salva il session id
    private boolean login(int operatorId, String password) throws IOException{
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String ip = getResources().getString(R.string.server_address);
        int port = getResources().getInteger(R.integer.server_port);

        //salva token e ultime credenziali
        editor.putInt("operatorId", operatorId);
        editor.putString("password", password);
        editor.apply();

        try {
            String urlString = String.format("http://%s:%d/GCI16/ReadingsOperatorLogin?operatorId=%d&password=%s", ip, port, operatorId, password);
            Log.d("DEBUG", "url = " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(getResources().getInteger(R.integer.connection_timeout));
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode==getResources().getInteger(R.integer.server_ok)){
                String cookieName = getResources().getString(R.string.session_cookie_name);
                String cookieValue = null;
                String cookieString = connection.getHeaderField("Set-Cookie");
                for(String s : cookieString.split(";")) {
                    if (s.matches(cookieName)) {
                        cookieValue = s.substring(s.indexOf('='));
                        Log.d("Debug", cookieName+":"+cookieValue);
                        break;
                    }
                }
                if(cookieValue==null){
                    Log.d("DEBUG", "No cookie");
                    return false;
                }
                editor.putString(cookieName, cookieValue);
                editor.apply();
            }
            else{
                return false;
            }

        } catch (MalformedURLException e) {
            Log.e("Malformed URL", String.format("%s/%d", R.string.server_address, R.integer.server_port));
        }
        return false;
    }
}
