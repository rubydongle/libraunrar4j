package ruby.blacktech.libraunrar4j.volume;

import java.io.IOException;

import ruby.blacktech.libraunrar4j.Archive;

public interface VolumeManager {

    /**
     * Returns either the first volume or the next volume.
     *
     * @param archive    the archive the volumes are part of
     * @param lastVolume the last volume before the one to return
     * @return the first volume if lastVolume is null, else the next volume after lastVolume
     * @throws IOException if the volume cannot be read
     */
    Volume nextVolume(Archive archive, Volume lastVolume) throws IOException;
}
