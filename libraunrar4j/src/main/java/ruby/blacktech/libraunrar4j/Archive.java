package ruby.blacktech.libraunrar4j;

import androidx.annotation.NonNull;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ruby.blacktech.libraunrar4j.exception.RarException;
import ruby.blacktech.libraunrar4j.io.SeekableReadOnlyByteChannel;
import ruby.blacktech.libraunrar4j.rarfile.BaseBlock;
import ruby.blacktech.libraunrar4j.rarfile.FileHeader;
import ruby.blacktech.libraunrar4j.rarfile.MainHeader;
import ruby.blacktech.libraunrar4j.rarfile.UnrarHeadertype;
import ruby.blacktech.libraunrar4j.volume.FileVolumeManager;
import ruby.blacktech.libraunrar4j.volume.InputStreamVolumeManager;
import ruby.blacktech.libraunrar4j.volume.Volume;
import ruby.blacktech.libraunrar4j.volume.VolumeManager;

public class Archive implements Closeable, Iterable<Object> {

    /**
     * Size of packed data in current file.
     */
    private long totalPackedSize = 0L;

    /**
     * Number of bytes of compressed data read from current file.
     */
    private long totalPackedRead = 0L;
    private SeekableReadOnlyByteChannel channel;
    private final UnrarCallback mUnrarCallback;
    private final List<BaseBlock> mHeaders = new ArrayList<>();

    private VolumeManager mVolumeManager;
    private Volume mVolume;
//    private FileHeader nextFileHeader;
    private String mPassword;
    private int currentHeaderIndex;

    private MainHeader newMhd = null;

    public Archive(
            final VolumeManager volumeManager,
            final UnrarCallback unrarCallback,
            final String password
    ) throws RarException, IOException {
        mUnrarCallback = unrarCallback;

    }

    public Archive(final File firstVolume) throws RarException, IOException {
        this(new FileVolumeManager(firstVolume), null, null);
    }

    public Archive(final File firstVolume, final UnrarCallback unrarCallback) throws RarException, IOException {
        this(new FileVolumeManager(firstVolume), unrarCallback, null);
    }

    public Archive(final File firstVolume, final String password) throws RarException, IOException {
        this(new FileVolumeManager(firstVolume), null, password);
    }

    public Archive(final File firstVolume, final UnrarCallback unrarCallback, final String password) throws RarException, IOException {
        this(new FileVolumeManager(firstVolume), unrarCallback, password);
    }

    public Archive(final InputStream rarAsStream) throws RarException, IOException {
        this(new InputStreamVolumeManager(rarAsStream), null, null);
    }

    public Archive(final InputStream rarAsStream, final UnrarCallback unrarCallback) throws RarException, IOException {
        this(new InputStreamVolumeManager(rarAsStream), unrarCallback, null);
    }

    public Archive(final InputStream rarAsStream, final String password) throws IOException, RarException {
        this(new InputStreamVolumeManager(rarAsStream), null, password);
    }

    public Archive(final InputStream rarAsStream, final UnrarCallback unrarCallback, final String password) throws IOException, RarException {
        this(new InputStreamVolumeManager(rarAsStream), unrarCallback, password);
    }

    private void setChannel(final SeekableReadOnlyByteChannel channel, final long length) throws IOException, RarException {
        this.totalPackedSize = 0L;
        this.totalPackedRead = 0L;
        close();
        this.channel = channel;
//        try {
//            readHeaders(length);
//        } catch (UnsupportedRarEncryptedException | UnsupportedRarV5Exception | CorruptHeaderException | BadRarArchiveException e) {
//            LogUtil.warn("exception in archive constructor maybe file is encrypted, corrupt or support not yet implemented", e);
//            throw e;
//        } catch (final Exception e) {
//            LogUtil.warn("exception in archive constructor maybe file is encrypted, corrupt or support not yet implemented", e);
//        }
//        // Calculate size of packed data
//        for (final BaseBlock block : this.headers) {
//            if (block.getHeaderType() == UnrarHeadertype.FileHeader) {
//                this.totalPackedSize += ((FileHeader) block).getFullPackSize();
//            }
//        }
        if (mUnrarCallback != null) {
            mUnrarCallback.volumeProgressChanged(this.totalPackedRead,
                    this.totalPackedSize);
        }
    }

    public void bytesReadRead(final int count) {
        if (count > 0) {
            this.totalPackedRead += count;
            if (mUnrarCallback != null) {
                mUnrarCallback.volumeProgressChanged(this.totalPackedRead,
                        this.totalPackedSize);
            }
        }
    }

    public SeekableReadOnlyByteChannel getChannel() {
        return this.channel;
    }

    /**
     * Gets all of the headers in the archive.
     *
     * @return returns the headers.
     */
    public List<BaseBlock> getmHeaders() {
        return new ArrayList<>(this.mHeaders);
    }

    /**
     * @return returns all file headers of the archive
     */
    public List<FileHeader> getFileHeaders() {
        final List<FileHeader> list = new ArrayList<>();
        for (final BaseBlock block : this.mHeaders) {
            if (block.getHeaderType().equals(UnrarHeadertype.FileHeader)) {
                list.add((FileHeader) block);
            }
        }
        return list;
    }

    public FileHeader nextFileHeader() {
        final int n = this.mHeaders.size();
        while (this.currentHeaderIndex < n) {
            final BaseBlock block = this.mHeaders.get(this.currentHeaderIndex++);
            if (block.getHeaderType() == UnrarHeadertype.FileHeader) {
                return (FileHeader) block;
            }
        }
        return null;
    }

    public UnrarCallback getUnrarCallback() {
        return mUnrarCallback;
    }

    /**
     * @return whether the archive is encrypted
     * @throws RarException when the main header is not present
     */
    public boolean isEncrypted() throws RarException {
        return false;
//        if (this.newMhd != null) {
//            return this.newMhd.isEncrypted();
//        } else {
//            throw new MainHeaderNullException();
//        }
    }

    /**
     * @return whether the archive content is password protected
     * @throws RarException when the main header is not present
     */
    public boolean isPasswordProtected() throws RarException {
        return true;
//        if (isEncrypted()) return true;
//        return getFileHeaders().stream().anyMatch(FileHeader::isEncrypted);
    }

    /**
     * Extract the file specified by the given header and write it to the
     * supplied output stream
     *
     * @param hd the header to be extracted
     * @param os the outputstream
     * @throws RarException .
     */
    public void extractFile(final FileHeader hd, final OutputStream os) throws RarException {
//        if (!this.headers.contains(hd)) {
//            throw new HeaderNotInArchiveException();
//        }
//        try {
//            doExtractFile(hd, os);
//        } catch (final Exception e) {
//            if (e instanceof RarException) {
//                throw (RarException) e;
//            } else {
//                throw new RarException(e);
//            }
//        }
    }

    /**
     * Returns an {@link InputStream} that will allow to read the file and
     * stream it. Please note that this method will create a new Thread and an a
     * pair of Pipe streams.
     *
     * @param hd the header to be extracted
     * @return inputstream
     * @throws IOException if any IO error occur
     */
    public InputStream getInputStream(final FileHeader hd) throws IOException {
        final PipedInputStream in = new PipedInputStream(32 * 1024);
        final PipedOutputStream out = new PipedOutputStream(in);

        // creates a new thread that will write data to the pipe. Data will be
        // available in another InputStream, connected to the OutputStream.
        new Thread(() -> {
            try {
                extractFile(hd, out);
            } catch (final RarException e) {
            } finally {
                try {
                    out.close();
                } catch (final IOException e) {
                }
            }
        }).start();

        return in;
    }


    /**
     * @return returns the main header of this archive
     */
    public MainHeader getMainHeader() {
        return this.newMhd;
    }

    /**
     * @return whether the archive is old format
     */
    public boolean isOldFormat() {
        return false;//this.markHead.isOldFormat();
    }


    @Override
    public void close() throws IOException {

    }

    @NonNull
    @Override
    public Iterator<Object> iterator() {
        return null;
    }
}
