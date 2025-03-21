package common.packet;

import java.io.Serializable;

public class Packet implements Serializable {
    private int id;
    
    public Packet() {}
    
    protected Packet(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + id + ']'; // https://stackoverflow.com/questions/5176294/get-child-class-name-from-parent
    }
}
