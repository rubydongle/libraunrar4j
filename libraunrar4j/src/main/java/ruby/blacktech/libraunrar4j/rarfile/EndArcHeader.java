package ruby.blacktech.libraunrar4j.rarfile;

/**
 * End of archive header
 * End of archive marker. RAR does not read anything after this header letting to use third party tools to add extra information such as a digital signature to archive.
 *
 * Header CRC32	        |uint32 |
 * Header size	        |vint   |
 * Header type	        |vint	|5
 * Header flags	        |vint	|Flags common for all headers
 * End of archive flags	|vint	|0x0001   Archive is volume and it is not last volume in the set
 *
 *
 *
 * RAR 5.0 : HEAD_ENDARC((byte)0x05)
 * RAR 1.5 - 4.x : HEAD3_ENDARC((byte)0x7b)
 */
public class EndArcHeader extends BaseBlock {

    public static final short endArcArchiveDataCrcSize = 4;
    public static final short endArcVolumeNumberSize = 2;

    private int archiveDataCRC;
    private short volumeNumber;


    public EndArcHeader(BaseBlock bb, byte[] endArcHeader) {
        super(bb);

        int pos = 0;
//        if (hasArchiveDataCRC()) {
//            archiveDataCRC = Raw.readIntLittleEndian(endArcHeader, pos);
//            pos += 4;
//        }
//        if (hasVolumeNumber()) {
//            volumeNumber = Raw.readShortLittleEndian(endArcHeader, pos);
//        }
    }

    public int getArchiveDataCRC() {
        return archiveDataCRC;
    }

    public short getVolumeNumber() {
        return volumeNumber;
    }
}
