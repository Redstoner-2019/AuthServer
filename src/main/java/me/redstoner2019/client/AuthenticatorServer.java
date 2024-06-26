package me.redstoner2019.client;

import me.redstoner2019.authserver.packets.JSONPacket;
import me.redstoner2019.server.events.PacketListener;
import me.redstoner2019.server.odclient.ODClient;
import me.redstoner2019.server.util.ConnectionProtocol;
import org.json.JSONObject;

public class AuthenticatorServer extends ODClient {

    public static String authenticationServerIp = "localhost";
    public static int authenticationServerPort = 8009;
    private static JSONObject result;
    private static final Object REF = new Object();

    public static void main(String[] args) {
        new AuthenticatorServer().setup();
    }

    public void setup(){
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

    public JSONObject getTokenInfo(String token){
        JSONObject object = new JSONObject();
        object.put("header","server");
        object.put("request","token-info");
        object.put("token",token);
        sendObject(new JSONPacket(object.toString()));
        try {
            synchronized (REF){
                REF.wait(5000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public JSONObject getAccountInfo(String username){
        JSONObject object = new JSONObject();
        object.put("header","server");
        object.put("request","token-info");
        object.put("username",username);
        sendObject(new JSONPacket(object.toString()));
        try {
            synchronized (REF){
                REF.wait(5000);

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
