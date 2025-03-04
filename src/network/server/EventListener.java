package network.server;

import packet.AddPlayerPacket;
import packet.RemovePlayerPacket;

public class EventListener {
    public void received(Object p) {
        if(p instanceof AddPlayerPacket) {
            AddPlayerPacket packet = (AddPlayerPacket) p;
            ClientHandler.players.put(packet.id, new NetPlayer(packet.id, packet.name));
            System.out.println(packet.name + " has joined to the game.");
        } else if(p instanceof RemovePlayerPacket) {
            RemovePlayerPacket packet = (RemovePlayerPacket) p;
            ClientHandler.players.remove(packet.id);
            System.out.println(ClientHandler.players.get(packet.id) + " has left the game.");
        }

    }
}
