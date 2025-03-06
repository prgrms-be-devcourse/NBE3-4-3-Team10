package com.ll.TeamProject.global.rsData;

import net.minidev.json.annotate.JsonIgnore;

public class RsData<T> {
    private final String resultCode;
    private final String msg;
    private final T data;

    public RsData(String resultCode, String msg) {
        this.resultCode = resultCode;
        this.msg = msg;
        this.data = null;
    }

    public RsData(String resultCode, String msg, T data) {
        this.resultCode = resultCode;
        this.msg = msg;
        this.data = data;
    }

    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        return new RsData<>(resultCode, msg, data);
    }

    public static RsData<Empty> okWithoutData(String msg) {
        return new RsData<>("S-1", msg, new Empty());
    }

    public static RsData<Empty> failWithoutData(String msg) {
        return new RsData<>("F-1", msg, new Empty());
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public static class Empty {}

    @JsonIgnore
    public int getStatusCode() {
        return Integer.parseInt(resultCode.split("-")[0]);
    }
}
