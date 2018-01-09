package com.subutai.socketrpc.core;

import com.subutai.socketrpc.OperationResponse;

public interface ResponseListener {
    void onResponse(OperationResponse response);
}
