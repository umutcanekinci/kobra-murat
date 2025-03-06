package packet;

import java.io.Serializable;

public class Packet implements Serializable {
    public int id;
    
    Packet() {}
    
    Packet(int id) {
        this.id = id;
    }

}
