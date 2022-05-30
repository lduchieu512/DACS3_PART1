package com.example.appbanhang.model;

import com.example.appbanhang.model.Item;

import java.util.List;

public class DonHang {
    int id;
    int iduserr;
    String diachi;
    String sodienthoai;
    String tongtien;
    List<Item> item;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIduserr() {
        return iduserr;
    }

    public void setIduserr(int iduserr) {
        this.iduserr = iduserr;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getTongtien() {
        return tongtien;
    }

    public void setTongtien(String tongtien) {
        this.tongtien = tongtien;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }
}
