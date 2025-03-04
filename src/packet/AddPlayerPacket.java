package packet;

import java.io.Serializable;

public class AddPlayerPacket implements Serializable {

    private static final long serializableVersionUID = 1L;

    public int id;
    public String name;

    public AddPlayerPacket(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
