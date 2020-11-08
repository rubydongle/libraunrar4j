package ruby.blacktech.libraunrar4j.volume;

import java.io.File;

import ruby.blacktech.libraunrar4j.Archive;

public class FileVolumeManager implements VolumeManager {

    private final File firstVolume;

    public FileVolumeManager(final File firstVolume) {
        this.firstVolume = firstVolume;
    }

    @Override
    public Volume nextVolume(final Archive archive, final Volume last) {
        return null;
//        if (last == null) return new FileVolume(archive, this.firstVolume);
//
//        final FileVolume lastFileVolume = (FileVolume) last;
//        final boolean oldNumbering = !archive.getMainHeader().isNewNumbering()
//                || archive.isOldFormat();
//        final String nextName = VolumeHelper.nextVolumeName(lastFileVolume.getFile().getAbsolutePath(), oldNumbering);
//        final File nextVolume = new File(nextName);

//        return new FileVolume(archive, nextVolume);
    }
}
