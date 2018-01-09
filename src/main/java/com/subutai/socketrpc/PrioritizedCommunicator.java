package com.subutai.socketrpc;

import com.subutai.socketrpc.core.InstructionSocket;
import com.subutai.socketrpc.core.OperationListener;
import com.subutai.socketrpc.core.OperationResponseListener;
import com.subutai.socketrpc.core.ResponseListener;

import java.util.*;

public class PrioritizedCommunicator implements Runnable, ResponseListener, OperationListener {

    private static final long TIMEOUT = 100;
    private boolean run;
    private Map<Integer, OperationResponseListener> callbacks;
    private Map<Integer, OperationListener> operationListeners;
    private PriorityQueue<Operation> operationPriorityQueue;
    private Map<UUID, Long> registrationTime;
    private InstructionSocket instructionSocket;

    public PrioritizedCommunicator(InstructionSocket socket){
        this.run = true;
        callbacks = new HashMap<>();
        operationPriorityQueue = new PriorityQueue<>();
        registrationTime = new HashMap<>();
        operationListeners = new HashMap<>();
        this.instructionSocket = socket;
        socket.setOperationListener(this);
        socket.setResponseListener(this);
    }

    @Override
    public void run() {
        while(run) {
            send();
            checkTimeouts();
        }
    }

    private synchronized void send(){
        Operation nextOperation = this.operationPriorityQueue.poll();
        if (nextOperation != null) {
            this.instructionSocket.sendOperation(nextOperation);
            if( nextOperation.requireResponse()) {
                this.registrationTime.put(nextOperation.getId(), System.currentTimeMillis());
            }
        }
    }

    private synchronized void checkTimeouts(){
        Set<UUID> uuids = this.registrationTime.keySet();
        for (UUID id : uuids) {
            Long time = this.registrationTime.get(id);
            if (System.currentTimeMillis() - time > TIMEOUT) {
                this.registrationTime.remove(id);
                this.callbacks.get(id).onError(OperationResponse.error(id));
            }
        }
    }

    public synchronized void sendOperation(Operation operation){
        this.operationPriorityQueue.add(operation);
        if (operation.requireResponse()) {
            this.registrationTime.put(operation.getId(), System.currentTimeMillis());
        }
    }

    public void registerOperationListener(int operationId, OperationListener operationListener) {
        this.operationListeners.put(operationId, operationListener);
    }

    public void registerResponseListener(int operationId, OperationResponseListener responseListener) {
        this.callbacks.put(operationId, responseListener);
    }

    public void stop(){
        this.run = false;
    }

    @Override
    public void onOperationReceived(Operation operation) {
        this.operationListeners.get(operation.getOperationId()).onOperationReceived(operation);
    }

    @Override
    public void onResponse(OperationResponse response) {
        this.callbacks.get(response.getOperationId()).onResponse(response);
        if (this.registrationTime.containsKey(response.getUUID())) {
            this.registrationTime.remove(response.getUUID());
        }
    }
}
