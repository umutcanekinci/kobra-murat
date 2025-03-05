package packet;

public class AddPlayerPacket extends Packet {

    public String name;

    public AddPlayerPacket(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
