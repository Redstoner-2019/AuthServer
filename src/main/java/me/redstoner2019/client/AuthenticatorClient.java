package me.redstoner2019.client;

import me.redstoner2019.data.Token;
import me.redstoner2019.events.PacketListener;
import me.redstoner2019.odclient.ODClient;
import me.redstoner2019.packets.JSONPacket;
import me.redstoner2019.util.ConnectionProtocol;
import me.redstoner2019.util.Util;
import org.json.JSONObject;

import java.util.Scanner;

public class AuthenticatorClient extends ODClient {
    public static String authenticationServerIp = "localhost";
    public static int authenticationServerPort = 8009;
    private static JSONObject result;
    private static final Object REF = new Object();
    public static void main(String[] args) {
        setup();
        Scanner scanner = new Scanner(System.in);
        while (true){
            String arg = scanner.nextLine();
            switch (arg){
                case "login":{
                    System.out.println("username?");
                    String username = scanner.nextLine();
                    System.out.println("password?");
                    String password = scanner.nextLine();
                    login(username,password);
                    break;
                }
                case "info":{
                    System.out.println("username?");
                    String username = scanner.nextLine();
                    System.out.println(getUserInfo(username));
                    break;
                }
                case "create":{
                    System.out.println("username?");
                    String username = scanner.nextLine();
                    System.out.println("displayname?");
                    String displayname = scanner.nextLine();
                    System.out.println("password?");
                    String password = scanner.nextLine();
                    createAccount(username,displayname,password);
                    break;
                }
            }
        }
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
        object.put("header","client");
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
        object.put("header","client");
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

    public static JSONObject getUserInfo(String username){
        JSONObject object = new JSONObject();
        object.put("header","client");
        object.put("request","account-info");
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

    public static void deleteAccount(String username){
        JSONObject object = new JSONObject();
        object.put("header","client");
        object.put("request","delete-account");
        object.put("username",username);
        sendObject(new JSONPacket(object.toString()));
    }

    public static void changePassword(String username, String password){
        JSONObject object = new JSONObject();
        object.put("header","client");
        object.put("request","delete-account");
        object.put("username",username);
        object.put("password",password);
        sendObject(new JSONPacket(object.toString()));
    }
    public static void changeDisplayname(String username, String displayname){
        JSONObject object = new JSONObject();
        object.put("header","client");
        object.put("request","delete-account");
        object.put("username",username);
        object.put("displayname",displayname);
        sendObject(new JSONPacket(object.toString()));
    }

    /*public static void request(){
        JSONObject object = new JSONObject();
    }*/
}
