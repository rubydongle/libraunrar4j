package ruby.blacktech.libraunrar4j.rarfile;


// from lib unrar headers.hpp
public enum SubBlockHeaderType {

// RAR 2.9 and earlier.
    //好像rar2.9之前版本才有SubBlock
//    EA_HEAD((byte)0x100),
//    UO_HEAD((byte)0x101),
//    MAC_HEAD((byte)0x102),
//    BEEA_HEAD((byte)0x103),
//    NTACL_HEAD((byte)0x104),
//    STREAM_HEAD((byte)0x105),

    // from junrar
    EA_HEAD((short) 0x100),
    UO_HEAD((short) 0x101),
    MAC_HEAD((short) 0x102),
    BEEA_HEAD((short) 0x103),
    NTACL_HEAD((short) 0x104),
    STREAM_HEAD((short) 0x105);

    private final short subblocktype;

    SubBlockHeaderType(short subblocktype) {
        this.subblocktype = subblocktype;
    }

    /**
     * Return true if the given value is equal to the enum's value
     * @param subblocktype .
     * @return true if the given value is equal to the enum's value
     */
    public boolean equals(short subblocktype) {
        return this.subblocktype == subblocktype;
    }

    /**
     * find the header type for the given short value
     * @param subType the short value
     * @return the corresponding enum or null
     */
    public static SubBlockHeaderType findSubblockHeaderType(short subType) {
        if (EA_HEAD.equals(subType)) {
            return EA_HEAD;
        } else if (UO_HEAD.equals(subType)) {
            return UO_HEAD;
        } else if (MAC_HEAD.equals(subType)) {
            return MAC_HEAD;
        } else if (BEEA_HEAD.equals(subType)) {
            return BEEA_HEAD;
        } else if (NTACL_HEAD.equals(subType)) {
            return NTACL_HEAD;
        } else if (STREAM_HEAD.equals(subType)) {
            return STREAM_HEAD;
        }
        return null;
    }

    /**
     * @return the short representation of this enum
     */
    public short getSubblocktype() {
        return subblocktype;
    }
}
