package ruby.blacktech.libraunrar4j.rarfile;

/**
 *
 * RAR 1.5 - 4.x : HEAD3_AV((byte)0x76)
 */
public class AVHeader extends BaseBlock {

    public static final int avHeaderSize = 7;

    private byte unpackVersion;
    private byte method;
    private byte avVersion;
//    private final int avInfoCRC;

    public AVHeader(BaseBlock bb, byte[] avHeader) {
        super(bb);

        int pos = 0;
        unpackVersion |= avHeader[pos] & 0xff;
        pos++;
        method |= avHeader[pos] & 0xff;
        pos++;
        avVersion |= avHeader[pos] & 0xff;
        pos++;
//        avInfoCRC = Raw.readIntLittleEndian(avHeader, pos);
    }

//    public int getAvInfoCRC() {
//        return avInfoCRC;
//    }

    public byte getAvVersion() {
        return avVersion;
    }

    public byte getMethod() {
        return method;
    }

    public byte getUnpackVersion() {
        return unpackVersion;
    }
}
