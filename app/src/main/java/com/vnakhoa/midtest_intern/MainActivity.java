package com.vnakhoa.midtest_intern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.vnakhoa.midtest_intern.activity.LoginActivity;
import com.vnakhoa.midtest_intern.adapter.AdapterMagic;
import com.vnakhoa.midtest_intern.model.Login;
import com.vnakhoa.midtest_intern.model.Magic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView listMagic;
    private Button btnRandom,btnBack;
    ArrayList<Magic> magicArrayList;
    AdapterMagic adapterMagic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControls();
        addEvents();
        getMagic();
    }

    private void addEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().commit();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterMagic.randomMagic();
            }
        });
    }

    private void addControls() {
        btnBack = findViewById(R.id.btnBack);
        btnRandom = findViewById(R.id.btnRandom);
        listMagic = findViewById(R.id.listMagic);

        magicArrayList = new ArrayList<>();


    }

    private void getMagic() {
        ProgressDialog dialog =new ProgressDialog(MainActivity.this);
        dialog.setMessage("Processing. Please wait...");
        dialog.show();
        Service service = Server.getInstance().create(Service.class);
        service.getMagic().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    String messages = object.getString("messages");
                    boolean success = object.getBoolean("successful");
                    if (success) {
                        JSONArray jsonArray = object.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String url = Server.URL+"assets/magic/"+jsonArray.get(i).toString();
                            Magic magic = new Magic(url,false);
                            magicArrayList.add(magic);
                        }
                        adapterMagic = new AdapterMagic(MainActivity.this,magicArrayList);
                        listMagic.setAdapter(adapterMagic);
                        listMagic.setLayoutManager(new GridLayoutManager(MainActivity.this,3));

                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                        itemTouchHelper.attachToRecyclerView(listMagic);
                    } else {
                        Toast.makeText(MainActivity.this,messages,Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            adapterMagic.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

    };
}