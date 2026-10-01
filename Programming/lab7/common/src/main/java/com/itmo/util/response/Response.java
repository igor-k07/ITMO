package com.itmo.util.response;

import com.itmo.util.Status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Класс ответа сервера
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<T> body = new ArrayList<>();
    private Status status = Status.OK;

    public Response() {}

    public Response(List<T> body) {
        this.body = body;
    }

    public Response(Status status) {
        this.status = status;
    }

    public Response(List<T> body, Status status) {
        this.body = body;
        this.status = status;
    }

    public void setStatus(Status newStatus) {
        this.status = newStatus;
    }

    public List<T> getBody() {
        return body;
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean IsEmpty() {
        return this.body.size() > 0;
    }
    
    public void put(T element) {
        body.add(element);
    };

    @Override
    public String toString() {
        return String.format("{\n\tстатус: %s,\n\tтело: %s\n}", status, body);
    }

};

