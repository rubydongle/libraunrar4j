package ruby.blacktech.libraunrar4j.rarfile;

import ruby.blacktech.libraunrar4j.utils.LogUtil;

public class BlockHeader extends BaseBlock {
    public static final short blockHeaderSize = 4;

    private long dataSize;
    private long packSize;

    public BlockHeader() {

    }

    public BlockHeader(BlockHeader bh) {
        super(bh);
        this.packSize = bh.getDataSize();
        this.dataSize = packSize;
        this.positionInFile = bh.getPositionInFile();
    }

    public BlockHeader(BaseBlock bb, byte[] blockHeader) {
        super(bb);

//        this.packSize = Raw.readIntLittleEndianAsLong(blockHeader, 0);
        this.dataSize  = this.packSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public long getPackSize() {
        return packSize;
    }

    public void print() {
        super.print();
        String s = "DataSize: " + getDataSize() + " packSize: " + getPackSize();
        LogUtil.info(s);
    }
}
