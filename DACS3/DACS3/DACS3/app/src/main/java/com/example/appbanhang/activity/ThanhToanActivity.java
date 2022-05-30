package com.example.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import com.example.appbanhang.R;
import com.example.appbanhang.model.NotiSendData;
import com.example.appbanhang.retrofit.ApiBanHang;
import com.example.appbanhang.retrofit.ApiPushNofication;
import com.example.appbanhang.retrofit.RetrofitClient;
import com.example.appbanhang.retrofit.RetrofitClientNoti;
import com.example.appbanhang.utils.Utils;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txttongtien, txtsodt, txtemail;
    EditText edtdiachi;
    AppCompatButton btndathang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    long tongtien;
    int totalItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        initView();
        countItem();
        initControl();
    }

    private void countItem() {
        totalItem = 0;
        for (int i=0; i<Utils.mangmuahang.size(); i++){
            totalItem = totalItem + Utils.mangmuahang.get(i).getSoluong();
        }
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien = getIntent().getLongExtra("tongtien", 0);
        txttongtien.setText( decimalFormat.format(tongtien));
        txtemail.setText(Utils.user_current.getEmail());
        txtsodt.setText(Utils.user_current.getMobile());

        btndathang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_diachi = edtdiachi.getText().toString().trim();
                if (TextUtils.isEmpty(str_diachi)){
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập địa chỉ", Toast.LENGTH_SHORT).show();
                }else {
                    //postdata
                    String str_email = Utils.user_current.getEmail();
                    String str_sdt = Utils.user_current.getMobile();
                    int id= Utils.user_current.getId();
                    Log.d("test", new Gson().toJson(Utils.mangmuahang)); //kiemtra
                    compositeDisposable.add(apiBanHang.createOder(str_email,str_sdt,String.valueOf(tongtien),id,str_diachi,totalItem, new Gson().toJson(Utils.mangmuahang))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            userModel -> {
                                pushNotiToUser();
                                Toast.makeText(getApplicationContext(),"Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                Utils.mangmuahang.clear();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
                }
            }

            private void pushNotiToUser() {
                //gettoken
                compositeDisposable.add(apiBanHang.gettoken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()){
                               for (int i=0; i<userModel.getResult().size(); i++){
                                   Map<String, String> data = new HashMap<>();
                                   data.put("title", "thong bao");
                                   data.put("body", "Ban co don hang moi");
                                   NotiSendData notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(), data);
                                   ApiPushNofication apiPushNofication = RetrofitClientNoti.getInstance().create(ApiPushNofication.class);
                                   compositeDisposable.add(apiPushNofication.sendNofication(notiSendData)
                                           .subscribeOn(Schedulers.io())
                                           .observeOn(AndroidSchedulers.mainThread())
                                           .subscribe(
                                                   notiRespone -> {

                                                   },
                                                   throwable -> {
                                                       Log.d("logg", throwable.getMessage());
                                                   }
                                           ));
                               }
                            }
                        },
                        throwable -> {
                            Log.d("logg", throwable.getMessage());
                        }
                ));


            }
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        toolbar = findViewById(R.id.toobar);
        txttongtien = findViewById(R.id.txttongtien);
        txtsodt = findViewById(R.id.txtsodienthoai);
        txtemail = findViewById(R.id.txtemail);
        edtdiachi = findViewById(R.id.edtdiachi);
        btndathang = findViewById(R.id.btndathang);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}