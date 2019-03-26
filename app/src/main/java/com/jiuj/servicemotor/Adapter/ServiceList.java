package com.jiuj.servicemotor.Adapter;

public class ServiceList {
    String _title;
    String _sub;
    String _detail;
    String _image;
    String _noref;

    public ServiceList(String noref, String title, String sub, String detail, String image) {
        this._noref = noref;
        this._title = title;
        this._sub = sub;
        this._detail = detail;
        this._image = image;
    }
}
