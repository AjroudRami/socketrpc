package com.subutai.socketrpc.core;

import com.subutai.socketrpc.OperationResponse;

public interface OperationResponseListener extends ResponseListener {
    void onError(OperationResponse response);
}
