package me.redstoner2019.packets;

import me.redstoner2019.defaultpackets.Packet;

public class JSONPacket extends Packet {
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public JSONPacket(String json) {
        this.json = json;
    }
}
