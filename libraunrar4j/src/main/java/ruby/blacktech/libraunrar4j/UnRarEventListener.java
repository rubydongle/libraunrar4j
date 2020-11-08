package ruby.blacktech.libraunrar4j;

public interface UnRarEventListener {
    void onVolumeChanged(String nextVolume);
    void onFileChanged(String nextFileString);
    void onCurrentUncompressedSizeChanged(long uncompressedSize, long totalSize);
    void onPassWordRequired(UnrarTask task);
    void onUnRarFinished(int result);
}
