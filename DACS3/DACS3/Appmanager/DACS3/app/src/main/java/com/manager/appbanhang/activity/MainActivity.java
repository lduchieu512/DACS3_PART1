package com.manager.appbanhang.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.manager.appbanhang.R;
import com.manager.appbanhang.adapter.LoaiSpAdapter;
import com.manager.appbanhang.adapter.SanPhamMoiAdapter;
import com.manager.appbanhang.model.LoaiSp;
import com.manager.appbanhang.model.SanPhamMoi;
import com.manager.appbanhang.model.User;
import com.manager.appbanhang.retrofit.ApiBanHang;
import com.manager.appbanhang.retrofit.RetrofitClient;
import com.manager.appbanhang.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerlayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if (Paper.book().read("user") != null){
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }

        getToken();

        Anhxa();
        ActionBar();

        if(isConnected(this)){

            ActionViewFlipper();
            getLoaiSanPham();
            getSpmoi();
            getEventClick();
        }else {
            Toast.makeText(getApplicationContext(), "Không có internet, vui lòng kết nối", Toast.LENGTH_LONG).show();
        }
    }


    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(),s)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {

                                    },
                                    throwable -> {
                                        Log.d("log",throwable.getMessage());
                                    }
                            ));
                        }
                    }
                });
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        dienthoai.putExtra("loai",1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop = new Intent(getApplicationContext(), DienThoaiActivity.class);
                        laptop.putExtra("loai", 2);
                        startActivity(laptop);
                        break;
                    case 5:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 6:
                        Intent quanli = new Intent(getApplicationContext(), QuanLiActivity.class);
                        startActivity(quanli);
                        break;
                    case 7:
                        // xoa key user
                        Paper.book().delete("user");
                        FirebaseAuth.getInstance().signOut();
                        Intent dangnhap = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        finish();
                        break;
                }
            }
        });
    }

    private void getSpmoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                sanPhamMoiModel -> {
                    if (sanPhamMoiModel.isSuccess()){
                        mangSpMoi = sanPhamMoiModel.getResult();
                        spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                        recyclerViewManHinhChinh.setAdapter(spAdapter);
                    }

                },
                throwable -> {
                    Toast.makeText(getApplicationContext(), "Không kết nối được với sever"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
        ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaisp()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                loaiSpModel -> {
                    if (loaiSpModel.isSuccess()){
                        mangloaisp = loaiSpModel.getResult();
                        mangloaisp.add(new LoaiSp("Quản lý", ""));
                        mangloaisp.add(new LoaiSp("Đăng xuất", ""));
                        loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(),mangloaisp);
                        listViewManHinhChinh.setAdapter(loaiSpAdapter);
                    }

                }
        ));
    }

    private void ActionViewFlipper(){
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("http://file.hstatic.net/1000282067/file/banner_1900x870_da830551_3590986f007d47e6bcdf3c48ea6347fa_2048x2048.jpg");
        mangquangcao.add("data:image/jpeg;base64,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");
        mangquangcao.add("data:image/jpeg;base64,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");
        for (int i = 0; i<mangquangcao.size(); i++){
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerlayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Anhxa() {
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toobarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerlayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);
        //khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if (Utils.manggiohang == null){
            Utils.manggiohang = new ArrayList<>();
        }else{
            int totalItem = 0;
            for (int i=0; i<Utils.manggiohang.size(); i++){
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem  ));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i=0; i<Utils.manggiohang.size(); i++){
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem  ));
    }

    private boolean isConnected (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //them quyen vao khong bi loi
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null && wifi.isConnected()) ||(mobile != null && mobile.isConnected()) ){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}