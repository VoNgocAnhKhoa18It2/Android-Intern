package com.vnakhoa.midtest_intern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vnakhoa.midtest_intern.activity.LoginActivity;
import com.vnakhoa.midtest_intern.adapter.AdapterMagic;
import com.vnakhoa.midtest_intern.model.Login;
import com.vnakhoa.midtest_intern.model.Magic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView listMagic;
    private Button btnRandom,btnBack;
    ArrayList<Magic> magicArrayList;
    AdapterMagic adapterMagic;
    RelativeLayout list_item;
    private int xDelta;
    private int yDelta;
    
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

    @SuppressLint("ClickableViewAccessibility")
    private void addControls() {
        btnBack = findViewById(R.id.btnBack);
        btnRandom = findViewById(R.id.btnRandom);
        listMagic = findViewById(R.id.listMagic);
        list_item = findViewById(R.id.list_item);

        magicArrayList = new ArrayList<>();

    }

    private void getMagic() {
        ProgressDialog dialog =new ProgressDialog(MainActivity.this);
        dialog.setMessage("Processing. Please wait...");
        dialog.show();
        Service service = Server.getInstance().create(Service.class);
        service.getMagic().enqueue(new Callback<ResponseBody>() {
            @SuppressLint("ClickableViewAccessibility")
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
                            ImageView img = new ImageView(MainActivity.this);
                            img.setImageResource(R.drawable.bb);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(dpToPx(70,MainActivity.this),dpToPx(140,MainActivity.this));
                            img.setLayoutParams(layoutParams);
                            img.setOnTouchListener(new View.OnTouchListener() {
                                private int CLICK_ACTION_THRESHOLD = 1;
                                private boolean checkMove = true;
                                @Override
                                public boolean onTouch(View view, MotionEvent motionEvent) {
                                    final int x = (int) motionEvent.getRawX();
                                    final int y = (int) motionEvent.getRawY();

                                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                                        case MotionEvent.ACTION_DOWN:
                                            checkMove = false;
                                            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                                    view.getLayoutParams();

                                            xDelta = x - lParams.leftMargin;
                                            yDelta = y - lParams.topMargin;
                                            break;

                                        case MotionEvent.ACTION_UP:

                                            if (!checkMove) {
                                                Picasso.get().load(url).into(img);
                                                checkMove = false;
                                            }

                                            break;

                                        case MotionEvent.ACTION_MOVE:
                                            checkMove = true;
                                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                                    .getLayoutParams();
                                            layoutParams.leftMargin = x - xDelta;
                                            layoutParams.topMargin = y - yDelta;
                                            layoutParams.rightMargin = 0;
                                            layoutParams.bottomMargin = 0;
                                            view.setLayoutParams(layoutParams);
                                            break;
                                    }

                                    list_item.invalidate();
                                    return true;
                                }
                            });

                            img.setX(0);
                            img.setY(0);
                            img.setZ(i);
                            list_item.addView(img);

                        }

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

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

}