package me.redstoner2019.server;

import me.redstoner2019.data.Token;
import me.redstoner2019.events.ClientConnectEvent;
import me.redstoner2019.events.PacketListener;
import me.redstoner2019.odserver.ClientHandler;
import me.redstoner2019.odserver.ODServer;
import me.redstoner2019.packets.JSONPacket;
import me.redstoner2019.util.ConnectionProtocol;
import me.redstoner2019.util.Util;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class AuthServer extends ODServer {
    public static HashMap<String, Token> tokens = new HashMap<>();
    public static HashMap<String, Token> usernameTokens = new HashMap<>();
    public static JSONObject userdata = new JSONObject();
    public static File userdataFile = new File("userdata.json");

    public static void saveData(){
        try {
            Util.writeStringToFile(Util.prettyJSON(userdata.toString()),userdataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        if(!userdataFile.exists()){
            userdataFile.createNewFile();
            Util.writeStringToFile("{ }",userdataFile);
        }
        userdata = new JSONObject(Util.readFile(userdataFile));

        Token token = Token.createToken("Lukas");
        tokens.put(token.getToken(), token);
        usernameTokens.put(token.getUsername(),token);

        System.out.println(token.getToken());
        setup(8009, ConnectionProtocol.UDP);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    usernameTokens.keySet().removeIf(username -> !usernameTokens.get(username).isValid());
                    tokens.keySet().removeIf(username -> !tokens.get(username).isValid());
                }
            }
        });
        t.start();

        setClientConnectEvent(new ClientConnectEvent() {
            @Override
            public void connectEvent(ClientHandler clientHandler) throws Exception {
                clientHandler.startPacketSender();
                clientHandler.startPacketListener(new PacketListener() {
                    @Override
                    public void packetRecievedEvent(Object o) {
                        if(!(o instanceof JSONPacket)) return;
                        JSONObject data = new JSONObject(((JSONPacket) o).getJson());
                        if(data.has("header")){
                            switch (data.getString("header")){
                                case "client":{
                                    switch (data.getString("request")){
                                        case "token-info":{
                                            break;
                                        }
                                        case "account-info":{
                                            break;
                                        }
                                        case "login": {
                                            Util.log("login");
                                            String username = data.getString("username");
                                            String password = data.getString("password");

                                            JSONObject response = new JSONObject();
                                            if(userdata.has(username)){
                                                JSONObject user = userdata.getJSONObject(username);
                                                if(user.getString("password").equals(password)){
                                                    response.put("header","login-complete");
                                                    if(!usernameTokens.containsKey(username)){
                                                        Token token = Token.createToken(username);
                                                        tokens.put(token.getToken(), token);
                                                        usernameTokens.put(token.getUsername(),token);
                                                    }
                                                    response.put("token",usernameTokens.get(username).getToken());
                                                } else {
                                                    response.put("header","invalid-password");
                                                }
                                            } else {
                                                response.put("header","invalid-username");
                                            }
                                            clientHandler.sendObject(new JSONPacket(response.toString()));
                                            break;
                                        }
                                        case "create-account": {
                                            try{
                                                Util.log("create accpimt");
                                                String username = data.getString("username");
                                                String displayname = data.getString("displayname");
                                                String password = data.getString("password");
                                                JSONObject response = new JSONObject();
                                                if(userdata.has(username)) {
                                                    response.put("header","account-already-exists");
                                                    clientHandler.sendObject(new JSONPacket(response.toString()));
                                                    return;
                                                }
                                                JSONObject user = new JSONObject();
                                                user.put("displayname",displayname);
                                                user.put("password",password);
                                                userdata.put(username,user);
                                                saveData();
                                                response.put("header","created-account");
                                                clientHandler.sendObject(new JSONPacket(response.toString()));
                                            }catch (Exception e){
                                                Util.log(e.getLocalizedMessage());
                                            }
                                            break;
                                        }
                                        case "delete-account":{
                                            break;
                                        }
                                        case "change-password": {
                                            break;
                                        }
                                        case "change-displayname": {
                                            break;
                                        }
                                    }
                                    break;
                                }
                                case "server":{
                                    switch (data.getString("request")){
                                        case "token-info":{

                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });
        start();
    }
}
