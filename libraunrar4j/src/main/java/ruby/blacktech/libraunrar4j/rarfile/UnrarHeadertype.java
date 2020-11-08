package ruby.blacktech.libraunrar4j.rarfile;

// from lib unrar headers.hpp HEADER_TYPE
public enum UnrarHeadertype {

    // RAR 5.0 header types.
    HEAD_MARK((byte)0x00),
    HEAD_MAIN((byte)0x01),
    HEAD_FILE((byte)0x02),
    HEAD_SERVICE((byte)0x03),
    HEAD_CRYPT((byte)0x04),
    HEAD_ENDARC((byte)0x05),
    HEAD_UNKNOWN((byte)0xff),

    // RAR 1.5 - 4.x header types.
    HEAD3_MARK((byte)0x72),
    HEAD3_MAIN((byte)0x73),
    HEAD3_FILE((byte)0x74),
    HEAD3_CMT((byte)0x75),
    HEAD3_AV((byte)0x76),
    HEAD3_OLDSERVICE((byte)0x77),
    HEAD3_PROTECT((byte)0x78),
    HEAD3_SIGN((byte)0x79),
    HEAD3_SERVICE((byte)0x7a),
    HEAD3_ENDARC((byte)0x7b),

    //===================
    // from junrar
    MainHeader((byte) 0x73),
    MarkHeader((byte) 0x72),
    FileHeader((byte) 0x74),
    CommHeader((byte) 0x75),
    AvHeader((byte) 0x76),
    SubHeader((byte) 0x77),
    ProtectHeader((byte) 0x78),
    SignHeader((byte) 0x79),
    NewSubHeader((byte) 0x7a),
    EndArcHeader((byte) 0x7b);

    /**
     * Returns the enum according to the given byte or null
     * @param headerType the headerbyte
     * @return the enum or null
     */
    public static UnrarHeadertype findType(byte headerType) {
        if (UnrarHeadertype.MarkHeader.equals(headerType)) {
            return UnrarHeadertype.MarkHeader;
        }
        if (UnrarHeadertype.MainHeader.equals(headerType)) {
            return UnrarHeadertype.MainHeader;
        }
        if (UnrarHeadertype.FileHeader.equals(headerType)) {
            return UnrarHeadertype.FileHeader;
        }
        if (UnrarHeadertype.EndArcHeader.equals(headerType)) {
            return UnrarHeadertype.EndArcHeader;
        }
        if (UnrarHeadertype.NewSubHeader.equals(headerType)) {
            return UnrarHeadertype.NewSubHeader;
        }
        if (UnrarHeadertype.SubHeader.equals(headerType)) {
            return UnrarHeadertype.SubHeader;
        }
        if (UnrarHeadertype.SignHeader.equals(headerType)) {
            return UnrarHeadertype.SignHeader;
        }
        if (UnrarHeadertype.ProtectHeader.equals(headerType)) {
            return UnrarHeadertype.ProtectHeader;
        }
        if (UnrarHeadertype.MarkHeader.equals(headerType)) {
            return UnrarHeadertype.MarkHeader;
        }
        if (UnrarHeadertype.MainHeader.equals(headerType)) {
            return UnrarHeadertype.MainHeader;
        }
        if (UnrarHeadertype.FileHeader.equals(headerType)) {
            return UnrarHeadertype.FileHeader;
        }
        if (UnrarHeadertype.EndArcHeader.equals(headerType)) {
            return UnrarHeadertype.EndArcHeader;
        }
        if (UnrarHeadertype.CommHeader.equals(headerType)) {
            return UnrarHeadertype.CommHeader;
        }
        if (UnrarHeadertype.AvHeader.equals(headerType)) {
            return UnrarHeadertype.AvHeader;
        }
        return null;
    }

    private byte headerByte;

    UnrarHeadertype(byte headerByte) {
        this.headerByte = headerByte;
    }

    /**
     * Return true if the given byte is equal to the enum's byte
     * @param header header
     * @return true if the given byte is equal to the enum's byte
     */
    public boolean equals(byte header) {
        return headerByte == header;
    }

    /**
     * the header byte of this enum
     * @return the header byte of this enum
     */
    public byte getHeaderByte() {
        return headerByte;
    }

}
