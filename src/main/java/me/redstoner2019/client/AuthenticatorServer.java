package me.redstoner2019.client;

import me.redstoner2019.events.PacketListener;
import me.redstoner2019.odclient.ODClient;
import me.redstoner2019.packets.JSONPacket;
import me.redstoner2019.util.ConnectionProtocol;
import me.redstoner2019.util.Util;
import org.json.JSONObject;

public class AuthenticatorServer extends ODClient {

    public static String authenticationServerIp = "localhost";
    public static int authenticationServerPort = 8009;
    private static JSONObject result;
    private static final Object REF = new Object();

    public static void main(String[] args) {
        setup();
    }

    public static void setup(){
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object o) {
                if(o instanceof JSONPacket p){
                    result = new JSONObject(p.getJson());
                    synchronized (REF){
                        REF.notify();
                    }
                }
            }
        });
        connect(authenticationServerIp, authenticationServerPort, ConnectionProtocol.UDP);
        startSender();
    }

    public static JSONObject getTokenInfo(String token){
        JSONObject object = new JSONObject();
        object.put("header","server");
        object.put("request","token-info");
        object.put("token",token);
        sendObject(new JSONPacket(object.toString()));
        try {
            synchronized (REF){
                REF.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static JSONObject getAccountInfo(String username){
        JSONObject object = new JSONObject();
        object.put("header","server");
        object.put("request","token-info");
        object.put("username",username);
        sendObject(new JSONPacket(object.toString()));
        try {
            synchronized (REF){
                REF.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
