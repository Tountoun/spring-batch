package com.gofar.springbatch.utils;

import org.springframework.http.HttpStatus;

public class Response {

    private Object data;
    private String message;
    private HttpStatus status;

    public Response() {
    }

    public Response(Object data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
