package com.subutai.socketrpc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

public class Operation implements Comparable<Operation>, Serializable{

    public final static int PRIORITY_LOW = 0;
    public final static int PRIORITY_MEDIUM = 1;
    public final static int PRIORITY_HIGH = 2;

    private final UUID id = UUID.randomUUID();
    private int priority;
    private int operationId;
    private boolean requireResponse;
    private Object[] arguments;

    public Operation(int operationId, Object[] arguments){
        this(operationId, false, arguments, PRIORITY_LOW);
    }

    public Operation(int operationId, boolean requireResponse, Object[] arguments) {
        this(operationId, requireResponse, arguments, PRIORITY_LOW);
    }

    public Operation(int operationId, boolean requireResponse, Object[] arguments, int priority) {
        this.operationId = operationId;
        this.requireResponse = requireResponse;
        this.arguments = arguments;
        this.priority = priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Integer getInteger(int index) {
        return (Integer) arguments[index];
    }

    public Boolean getBoolean(int index) {
        return (Boolean) arguments[index];
    }

    public Float getFloat(int index) {
        return (Float) arguments[index];
    }

    public UUID getId() {
        return id;
    }

    public Short getShort(int index) {
        return (Short) arguments[index];
    }

    public Object get(int index) {
        return arguments[index];
    }

    public Byte getByte(int index) {
        return (Byte) arguments[index];
    }

    public int getOperationId(){
        return this.operationId;
    }

    @Override
    public int compareTo(Operation operation) {
       return Integer.compare(this.priority, operation.priority);
    }

    public boolean requireResponse(){
        return this.requireResponse;
    }

    @Override
    public String toString(){
        String res = "UUID : " + id.toString() +
                " priotity : " + priority +
                " operationId : " + operationId +
                " requireResponse : " + requireResponse +
                " arguments : " + Arrays.toString(arguments);
        return res;
    }
}
