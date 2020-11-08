package ruby.blacktech.libraunrar4j.volume;

import java.io.IOException;

import ruby.blacktech.libraunrar4j.Archive;

public interface Volume {
    /**
     * @return SeekableReadOnlyByteChannel the channel
     * @throws IOException .
     */
//    SeekableReadOnlyByteChannel getChannel() throws IOException;

    /**
     * @return the data length
     */
    long getLength();

    /**
     * @return the archive this volume belongs to
     */
    Archive getArchive();
}
