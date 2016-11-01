package com.quanlt.vietcomic.api;


import com.google.gson.annotations.SerializedName;

public class ComicResponse<T> {
    private int error;
    private String description;
    @SerializedName("data")
    private T data;

    public void setError(int error) {
        this.error = error;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ComicResponse{" +
                "error=" + error +
                ", description='" + description + '\'' +
                ", data=" + data +
                '}';
    }
}
