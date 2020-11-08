package ruby.blacktech.libraunrar4j.io;

import java.io.IOException;

public interface SeekableReadOnlyByteChannel {
    /**
     * @return the current position in the channel
     * @throws IOException .
     */
    long getPosition() throws IOException;

    /**
     * @param pos the position in the channel
     * @throws IOException .
     */
    void setPosition(long pos) throws IOException;

    /**
     * Read a single byte of data.
     *
     * @return read read
     *
     * @throws IOException .
     */
    int read() throws IOException;

    /**
     * Read up to <tt>count</tt> bytes to the specified buffer.
     *
     * @param buffer .
     * @param off .
     * @param count .
     *
     * @return read read
     *
     * @throws IOException .
     *
     */
    int read(byte[] buffer, int off, int count) throws IOException;

    /**
     * Read exactly <tt>count</tt> bytes to the specified buffer.
     *
     * @param buffer where to store the read data
     * @param count how many bytes to read
     * @return bytes read || -1 if  IO problem
     *
     * @throws IOException .
     */
    int readFully(byte[] buffer, int count) throws IOException;

    /**
     * Close this channel.
     * @throws IOException .
     */
    void close() throws IOException;
}
