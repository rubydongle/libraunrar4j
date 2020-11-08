package ruby.blacktech.libraunrar4j.listener;

import ruby.blacktech.libraunrar4j.Archive;

public interface VolumeChangeListener {
    void onVolumeChanged(Archive archive, String nextVolume);
}
