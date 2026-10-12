package com.fmarket.resource;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientIpResolver {

    public String resolve(HttpServerRequest request) {
        SocketAddress address = request.remoteAddress();
        return address == null ? null : address.hostAddress();
    }
}
