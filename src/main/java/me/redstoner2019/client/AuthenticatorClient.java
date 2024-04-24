package me.redstoner2019.client;

import me.redstoner2019.data.Token;
import me.redstoner2019.events.PacketListener;
import me.redstoner2019.odclient.ODClient;
import me.redstoner2019.packets.JSONPacket;
import me.redstoner2019.util.ConnectionProtocol;
import me.redstoner2019.util.Util;
import org.json.JSONObject;

public class AuthenticatorClient extends ODClient {
    public static String authenticationServerIp = "localhost";
    public static int authenticationServerPort = 8009;
    public static void main(String[] args) throws InterruptedException {
        setup();
        createAccount("Lukas","Lukas","test");
        Thread.sleep(1000);
        login("Lukas","test");
    }
    public static void setup(){
        setPacketListener(new PacketListener() {
            @Override
            public void packetRecievedEvent(Object o) {
                if(o instanceof JSONPacket p){
                    System.out.println(Util.prettyJSON(p.getJson()));
                }
            }
        });
        connect(authenticationServerIp, authenticationServerPort, ConnectionProtocol.UDP);
        startSender();
    }
    public static void createAccount(String username,String displayname, String password){
        JSONObject object = new JSONObject();
        object.put("header","client");
        object.put("request","create-account");
        object.put("username",username);
        object.put("displayname",displayname);
        try {
            object.put("password", Token.hashPassword(password));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendObject(new JSONPacket(object.toString()));
    }

    public static void login(String username, String password){
        JSONObject object = new JSONObject();
        object.put("header","client");
        object.put("request","login");
        object.put("username",username);
        try {
            object.put("password",Token.hashPassword(password));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sendObject(new JSONPacket(object.toString()));
    }
    public static void request(){
        JSONObject object = new JSONObject();
    }
}
