package network.packet;

import java.io.Serializable;

public class Packet implements Serializable {
    public int id;
    
    public Packet() {}
    
    protected Packet(int id) {
        this.id = id;
    }

}
