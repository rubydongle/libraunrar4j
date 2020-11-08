package ruby.blacktech.libraunrar4j.rarfile;

import ruby.blacktech.libraunrar4j.utils.LogUtil;

/**
 * the header to recognize a file to be a rar archive
 *
 *
 * RAR 5.0 : HEAD_MARK((byte)0x00),
 * RAR 1.5 - 4.x : HEAD3_MARK((byte)0x72),
 */
public class MarkHeader extends BaseBlock {
//    private RARVersion version;

    public MarkHeader(BaseBlock bb) {
        super(bb);
    }
    public boolean isValid() {
        if (!(getHeadCRC() == 0x6152)) {
            return false;
        }
        if (!(getHeaderType() == UnrarHeadertype.MarkHeader)) {
            return false;
        }
        if (!(getFlags() == 0x1a21)) {
            return false;
        }
        return getHeaderSize(false) == BaseBlockSize;
    }

//    public boolean isSignature() {
//        byte[] d = new byte[BaseBlock.BaseBlockSize];
//        Raw.writeShortLittleEndian(d, 0, headCRC);
//        d[2] = headerType;
//        Raw.writeShortLittleEndian(d, 3, flags);
//        Raw.writeShortLittleEndian(d, 5, headerSize);
//
//        if (d[0] == 0x52) {
//            if (d[1] == 0x45 && d[2] == 0x7e && d[3] == 0x5e) {
//                version = RARVersion.OLD;
//            } else if (d[1] == 0x61 && d[2] == 0x72 && d[3] == 0x21 && d[4] == 0x1a && d[5] == 0x07) {
//                if (d[6] == 0x00) {
//                    version = RARVersion.V4;
//                } else if (d[6] == 0x01) {
//                    version = RARVersion.V5;
//                }
//            }
//        }
//        return version == RARVersion.OLD || version == RARVersion.V4;
//    }

//    public boolean isOldFormat() {
//        return RARVersion.isOldFormat(version);
//    }
//
//    public RARVersion getVersion() {
//        return version;
//    }

    public void print() {
        super.print();
        LogUtil.info("valid: " + isValid());
    }
}
