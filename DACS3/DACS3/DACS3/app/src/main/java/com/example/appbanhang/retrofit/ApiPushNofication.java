package com.example.appbanhang.retrofit;

import com.example.appbanhang.model.NotiRespone;
import com.example.appbanhang.model.NotiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNofication {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key=AAAA6mK-eno:APA91bGkbLgaYqv_W3gAGTcZ90kqn4uYP-X9Bgtpq6bSV6I3BHgGas0xHWDeaX4WqE4Ye6eQZXazWoMbnrryGkKt-y8GjmVXaeWBLotTbkxrwHLmb9Doh3U0dTEItg_l6GlXX9e3cKPI"
                    //"Authorization: key=AAAA6mK-eno:APA91bGkbLgaYqv_W3gAGTcZ90kqn4uYP-X9Bgtpq6bSV6I3BHgGas0xHWDeaX4WqE4Ye6eQZXazWoMbnrryGkKt-y8GjmVXaeWBLotTbkxrwHLmb9Doh3U0dTEItg_l6GlXX9e3cKPI"

            }
    )
    @POST("fcm/send")
    Observable<NotiRespone> sendNofication(@Body NotiSendData data);
}
