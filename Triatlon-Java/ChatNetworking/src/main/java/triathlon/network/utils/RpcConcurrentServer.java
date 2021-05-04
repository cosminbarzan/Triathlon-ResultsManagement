package triathlon.network.utils;

import triathlon.network.rpcprotocol.ClientRpcReflectionWorker;
import triathlon.services.IServices;

import java.net.Socket;


public class RpcConcurrentServer extends AbsConcurrentServer {
    private IServices chatServer;
    public RpcConcurrentServer(int port, IServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        // ChatClientRpcWorker worker=new ChatClientRpcWorker(chatServer, client);
        ClientRpcReflectionWorker worker = new ClientRpcReflectionWorker(chatServer, client);

        Thread tw = new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
