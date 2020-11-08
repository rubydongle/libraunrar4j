package ruby.blacktech.libraunrar4j.listener;

import ruby.blacktech.libraunrar4j.Archive;

public interface UnRarFinishListener {
    void onUnRarFinished(Archive archive, int result);
}
