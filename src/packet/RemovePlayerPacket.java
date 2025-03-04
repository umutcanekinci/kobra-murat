package packet;

import java.io.Serializable;

public class RemovePlayerPacket implements Serializable {

    private static final long serializableVersionUID = 1L;

    public int id;
    public String name;
}
