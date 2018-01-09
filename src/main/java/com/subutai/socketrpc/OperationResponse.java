package com.subutai.socketrpc;

import java.io.Serializable;
import java.util.UUID;

public class OperationResponse implements Serializable{

    private int operationId;
    private UUID id;
    private Object result;

    private OperationResponse(){}

    public static OperationResponse respond(Operation operation, Object result) {
        OperationResponse response = new OperationResponse();
        response.operationId = operation.getOperationId();
        response.id = operation.getId();
        response.result = result;
        return response;
    }

    public static OperationResponse error(UUID id){
        OperationResponse response = new OperationResponse();
        response.operationId = -1;
        response.id = id;
        response.result = new Object[0];
        return response;
    }
    public int getOperationId() {
        return this.operationId;
    }

    public Object get() {
        return this.result;
    }

    public Integer getInteger() {
        return (Integer) this.result;
    }

    public Boolean getBoolean() {
        return (Boolean) this.result;
    }

    public Float getFloat() {
        return (Float) this.result;
    }

    public UUID getUUID(){
        return this.id;
    }
}
