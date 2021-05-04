package triathlon.network.utils;

import triathlon.network.protobuffprotocol.ProtoWorker;
import triathlon.services.IServices;

import java.net.Socket;


public class ProtobuffConcurrentServer extends AbsConcurrentServer {
    private IServices chatServer;
    public ProtobuffConcurrentServer(int port, IServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- ChatProtobuffConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ProtoWorker worker = new ProtoWorker(chatServer,client);
        Thread tw = new Thread(worker);
        return tw;
    }
}
