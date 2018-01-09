import com.subutai.socketrpc.Operation;
import com.subutai.socketrpc.PrioritizedCommunicator;
import com.subutai.socketrpc.core.InstructionSocketClient;
import com.subutai.socketrpc.core.InstructionSocketServer;
import java.io.IOException;
import java.net.InetAddress;

public class Main1 {

    public static boolean debug = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        InstructionSocketServer server = new InstructionSocketServer(50000);

        InstructionSocketClient client = new InstructionSocketClient(InetAddress.getLocalHost(), 50000);

        Thread serverThread = new Thread(server);
        Thread clientThread = new Thread(client);

        PrioritizedCommunicator serverCommunicator = new PrioritizedCommunicator(server);
        PrioritizedCommunicator clientCommunicator = new PrioritizedCommunicator(client);

        Thread clientCommunicatorThread = new Thread(clientCommunicator);
        Thread serverCommunicatorThread = new Thread(serverCommunicator);

        serverThread.start();
        clientThread.start();

        serverCommunicatorThread.start();
        clientCommunicatorThread.start();

        Thread.sleep(3000);


        serverCommunicator.registerOperationListener(11, e -> {System.out.println(e.getFloat(0));});
        clientCommunicator.sendOperation(new Operation(11, new Object[]{3.14f}));
        Thread.sleep(3000);

        serverCommunicator.stop();
        clientCommunicator.stop();

        server.stop();
        client.stop();
        /**if(debug) {
            clientThread.start();

            Thread.sleep(1000);


            client.send(new PingInst().send());

            client.send(new PingInst().send());

            Thread.sleep(3000);

            client.stop();
        }**/
    }
}
