package com.vnakhoa.midtest_intern.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.vnakhoa.midtest_intern.MainActivity;
import com.vnakhoa.midtest_intern.R;
import com.vnakhoa.midtest_intern.Server;
import com.vnakhoa.midtest_intern.Service;
import com.vnakhoa.midtest_intern.model.Login;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin,btnJoin;
    TextInputLayout txtID,txtPassword;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        if (preferences != null && preferences.getString("Login","").length() > 1) {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,JoinActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        if (!validate(txtID) | !validate(txtPassword)){
            return;
        }
        ProgressDialog dialog =new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Processing. Please wait...");
        dialog.show();
        String id = txtID.getEditText().getText().toString().trim();
        String password = txtPassword.getEditText().getText().toString().trim();
        Service service = Server.getInstance().create(Service.class);
        service.login(id,password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    String messages = object.getString("messages");
                    boolean success = object.getBoolean("successful");
                    if (success) {
                        Login login = Login.convectJson(object.getJSONObject("data").toString());
                        SharedPreferences preferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("Login",object.getJSONObject("data").toString());
                        editor.commit();
                        messages = messages + ". Wellcome " + login.getEmail();
                        Toast.makeText(LoginActivity.this,messages,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this,messages,Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    static boolean validate(TextInputLayout textInputLayout) {
        boolean check = true ;
        String text = textInputLayout.getEditText().getText().toString();
        if (text.isEmpty()) {
            textInputLayout.setError(textInputLayout.getHint()+" can not be empty");
            return false;
        }
        textInputLayout.setError("");
        return check;
    }

    private void addControls() {
        btnJoin = findViewById(R.id.btnJoin);
        btnLogin = findViewById(R.id.btnLogin);
        txtID = findViewById(R.id.txtID);
        txtPassword = findViewById(R.id.txtPassword);
    }
}