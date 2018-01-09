package com.subutai.socketrpc.core;

import com.subutai.socketrpc.Operation;

public interface OperationListener {

    void onOperationReceived(Operation operation);
}
