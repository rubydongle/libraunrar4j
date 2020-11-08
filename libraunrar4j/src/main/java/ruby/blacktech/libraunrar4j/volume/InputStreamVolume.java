package ruby.blacktech.libraunrar4j.volume;

import java.io.InputStream;

import ruby.blacktech.libraunrar4j.Archive;

public class InputStreamVolume implements Volume {
    private final Archive archive;
    private final InputStream inputStream;
    private final int position;

    public InputStreamVolume(final Archive archive, final InputStream inputStream, final int position) {
        this.archive = archive;
        this.inputStream = inputStream;
        this.position = position;
    }

//    @Override
//    public SeekableReadOnlyByteChannel getChannel() {
//        return new SeekableReadOnlyInputStream(this.inputStream);
//    }

    @Override
    public long getLength() {
        return Long.MAX_VALUE;
    }

    @Override
    public Archive getArchive() {
        return this.archive;
    }

    public int getPosition() {
        return position;
    }
}
