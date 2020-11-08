package ruby.blacktech.libraunrar4j.listener;

import ruby.blacktech.libraunrar4j.Archive;

public interface FileChangeListener {
    void onFileChanged(Archive archive, String nextFileString);
}
