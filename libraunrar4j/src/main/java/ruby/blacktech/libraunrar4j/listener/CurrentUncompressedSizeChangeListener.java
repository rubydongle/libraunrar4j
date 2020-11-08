package ruby.blacktech.libraunrar4j.listener;

import ruby.blacktech.libraunrar4j.Archive;

public interface CurrentUncompressedSizeChangeListener {
    void onCurrentUncompressedSizeChanged(Archive archive, long uncompressedSize, long totalSize);
}
