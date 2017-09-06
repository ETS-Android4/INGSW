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

        //TODO listener e proprietà degli edittext
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

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

                //TODO login
                if(login(operatorId, password)){
                    Intent intent = new Intent(LoginController.this, ReadingsController.class);
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

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        if(pref.getString("session", null)!=null){ //sessione salvata
            Intent intent = new Intent(LoginController.this, ReadingsController.class);
            startActivity(intent);
        }
    }



    //TODO
    //tenta il login, se questo ha successo salva il session id
    private boolean login(int operatorId, String password){
        //prova login
        String ip = getResources().getString(R.string.server_address);
        int port = getResources().getInteger(R.integer.server_port);
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            URL url = new URL("http", String.format("%s:%d/login?operatorId=%d&password=%s", ip, port, operatorId, password), null);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode==500){
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
                editor.putString(cookieName, cookieValue);
            }
            else{
                //bad credentials
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Invalid id or password")
                        .setPositiveButton("OK", null)
                        .setMessage("Invalid id or password");
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        } catch (MalformedURLException e) {
            Log.e("Malformed URL", String.format("%s/%d", R.string.server_address, R.integer.server_port));
        } catch (IOException e) {
            e.printStackTrace();
        }


        //salva token e ultime credenziali
        editor.putInt("operatorId", operatorId);
        editor.putString("password", password);

        return true;
    }
}
