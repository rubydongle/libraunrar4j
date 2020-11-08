package ruby.blacktech.libraunrar4j.rarfile;

import ruby.blacktech.libraunrar4j.utils.LogUtil;

/**
 * The main header of an rar archive. holds information concerning the whole archive (solid, encrypted etc).
 */

/**
 * Main archive header
 *
 * Header CRC32	    |uint32 |
 * Header size	    |vint   |
 * Header type	    |vint	|1
 * Header flags	    |vint	|Flags common for all headers
 * Extra area size	|vint	|Size of extra area. Optional field, present only if 0x0001 header flag is set.
 *
 * Archive flags	|vint	|0x0001   Volume. Archive is a part of multivolume set.
 *                          |0x0002   Volume number field is present. This flag is present in all volumes except first.
 *                          |0x0004   Solid archive.
 *                          |0x0008   Recovery record is present.
 *                          |0x0010   Locked archive.
 * Volume number	|vint	|Optional field, present only if 0x0002 archive flag is set. Not present for first volume, 1 for second volume, 2 for third and so on.
 * Extra area	    |...	|Optional area containing additional header fields, present only if 0x0001 header flag is set.
 */
// 参考https://www.rarlab.com/technote.htm#rarsign
public class MainHeader extends BaseBlock {
    public static final short mainHeaderSizeWithEnc = 7;
    public static final short mainHeaderSize = 6;
    private final short highPosAv = 0;
    private final int posAv = 0;
    private byte encryptVersion;

    public MainHeader(BaseBlock bb, byte[] mainHeader) {
        super(bb);
//        int pos = 0;
//        highPosAv = Raw.readShortLittleEndian(mainHeader, pos);
//        pos += 2;
//        posAv = Raw.readIntLittleEndian(mainHeader, pos);
//        pos += 4;
//
//        if (hasEncryptVersion()) {
//            encryptVersion |= mainHeader[pos] & 0xff;
//        }
    }

    /**
     * old cmt block is present
     * @return true if has cmt block
     */
    public boolean hasArchCmt() {
        return (this.flags & BaseBlock.MHD_COMMENT) != 0;
    }
    /**
     * the version the the encryption
     * @return .
     */
    public byte getEncryptVersion() {
        return encryptVersion;
    }

    public short getHighPosAv() {
        return highPosAv;
    }

    public int getPosAv() {
        return posAv;
    }

    /**
     * returns whether the archive is encrypted
     * @return .
     */
    public boolean isEncrypted() {
        return (this.flags & BaseBlock.MHD_PASSWORD) != 0;
    }

    /**
     *
     * @return whether the archive is a multivolume archive
     */
    public boolean isMultiVolume() {
        return (this.flags & BaseBlock.MHD_VOLUME) != 0;
    }

    /**
     *
     * @return if the archive is a multivolume archive this method returns whether this instance is the first part of the multivolume archive
     */
    public boolean isFirstVolume() {
        return (this.flags & BaseBlock.MHD_FIRSTVOLUME) != 0;
    }

    public void print() {
        super.print();
        StringBuilder str = new StringBuilder();
        str.append("posav: " + getPosAv());
        str.append("\nhighposav: " + getHighPosAv());
        str.append("\nhasencversion: " + hasEncryptVersion() + (hasEncryptVersion() ? getEncryptVersion() : ""));
        str.append("\nhasarchcmt: " + hasArchCmt());
        str.append("\nisEncrypted: " + isEncrypted());
        str.append("\nisMultivolume: " + isMultiVolume());
        str.append("\nisFirstvolume: " + isFirstVolume());
        str.append("\nisSolid: " + isSolid());
        str.append("\nisLocked: " + isLocked());
        str.append("\nisProtected: " + isProtected());
        str.append("\nisAV: " + isAV());
        LogUtil.info(str.toString());
    }

    /**
     * @return whether this archive is solid. in this case you can only extract all file at once
     */
    public boolean isSolid() {
        return (this.flags & MHD_SOLID) != 0;
    }

    public boolean isLocked() {
        return (this.flags & MHD_LOCK) != 0;
    }

    public boolean isProtected() {
        return (this.flags & MHD_PROTECT) != 0;
    }

    public boolean isAV() {
        return (this.flags & MHD_AV) != 0;
    }

    /**
     *
     * @return the numbering format a multivolume archive
     */
    public boolean isNewNumbering() {
        return (this.flags & MHD_NEWNUMBERING) != 0;
    }
}
