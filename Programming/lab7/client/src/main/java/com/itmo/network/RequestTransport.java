package com.itmo.network;

import com.itmo.util.request.Request;
import com.itmo.util.response.Response;

public interface RequestTransport {
    Response<?> send(Request request);
}