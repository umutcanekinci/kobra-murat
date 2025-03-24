package common.packet;

public class AddPacket extends Packet {

    private int length;

    public AddPacket(int id, int length) {
        super(id);
        this.length = length;
        System.out.println("AddPacket created: " + this);
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return super.toString() + "{length=" + length + "}";
    }
}
