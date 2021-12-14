package com.vnakhoa.midtest_intern.activity;

import static com.vnakhoa.midtest_intern.activity.LoginActivity.validate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.vnakhoa.midtest_intern.MainActivity;
import com.vnakhoa.midtest_intern.R;
import com.vnakhoa.midtest_intern.Server;
import com.vnakhoa.midtest_intern.Service;
import com.vnakhoa.midtest_intern.model.Login;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {
    Button btnBack,btnJoin,btnCheckID;
    TextInputLayout txtID,txtPassword,txtPasswordConfirm,txtEmail;
    TextView txtBirth;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        addControls();
        addEvents();
    }

    private void addEvents() {
        txtBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectBirth();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleJoin();
            }
        });
        btnCheckID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkID();
            }
        });
    }

    private void checkID() {
        if (!validate(txtID)) {
            return;
        }
        String id = txtID.getEditText().getText().toString().trim();
        Service service = Server.getInstance().create(Service.class);
        service.checkID(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    String messages = object.getString("messages");
                    boolean success = object.getBoolean("successful");
                    if (success) {
                        Toast.makeText(JoinActivity.this,messages,Toast.LENGTH_LONG).show();
                    } else {
                       txtID.setError(messages);
                    }
                } catch (Exception e){
                    Toast.makeText(JoinActivity.this,"Failed",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(JoinActivity.this,"Failed",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selectBirth() {
        try {
            calendar.setTime(spf.parse(txtBirth.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                txtBirth.setText(spf.format(calendar.getTime()));
            }
        };
        DatePickerDialog date = new DatePickerDialog(
                JoinActivity.this,
                callback,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        date.show();
    }

    private void handleJoin() {
        if (!validate(txtID) | !validate(txtPassword )| !validate(txtPasswordConfirm)| !validate(txtEmail)) {
            return;
        }
        String id = txtID.getEditText().getText().toString().trim();
        String password = txtPassword.getEditText().getText().toString().trim();
        String passComfirm = txtPasswordConfirm.getEditText().getText().toString().trim();
        String email = txtEmail.getEditText().getText().toString().trim();
        String birth = txtBirth.getText().toString().trim();

        if (birth == null) {
            Toast.makeText(JoinActivity.this, "Birth cann't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passComfirm)) {
            Toast.makeText(JoinActivity.this, "Please confirm password again", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog dialog =new ProgressDialog(JoinActivity.this);
        dialog.setMessage("Processing. Please wait...");
        dialog.show();
        Service service = Server.getInstance().create(Service.class);
        service.join(id,password,email,convertDate(birth)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    String messages = object.getString("messages");
                    boolean success = object.getBoolean("successful");
                    if (success) {
                        Toast.makeText(JoinActivity.this,messages,Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(JoinActivity.this,messages,Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    Toast.makeText(JoinActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(JoinActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    public String convertDate(String birth) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("yyyy-dd-MM'T'HH:mm:ss.SSS'Z'");
            Date date = input.parse(birth);
            birth = output.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return birth;
    }

    private void addControls() {
        btnJoin = findViewById(R.id.btnJoin);
        txtID = findViewById(R.id.txtID);
        txtPassword = findViewById(R.id.txtPassword);
        txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);
        txtEmail = findViewById(R.id.txtEmail);
        btnBack = findViewById(R.id.btnBack);
        txtBirth = findViewById(R.id.txtBirth);
        btnCheckID = findViewById(R.id.btnCheckID);
    }
}