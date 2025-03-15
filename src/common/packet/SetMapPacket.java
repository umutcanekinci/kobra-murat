package common.packet;

public class SetMapPacket extends Packet {

    public SetMapPacket(int id) {
        super(id);
    }

    @Override
    public String toString() {
        return "SetMapPacket{" + "id=" + id + '}';
    }
    
}
