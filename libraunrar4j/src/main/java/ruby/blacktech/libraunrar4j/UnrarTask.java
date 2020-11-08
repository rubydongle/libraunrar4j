package ruby.blacktech.libraunrar4j;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class UnrarTask implements UnRarEventListener{
    private String mArchive;
    private String mToPath;
    private UnRarEventListener mUnRarEventListener;

    private SuperUnrar mSuperUnrar;

    public void startExtract() {
        mSuperUnrar = new SuperUnrar(this);
        mSuperUnrar.startExtract();
    }

    public void setPassword(String password) {
        mSuperUnrar.setPasswordForPasswordRequest(password);
    }

    private static final int EVENT_VOLUME_CHANGED = 1;
    private static final int EVENT_FILE_CHANGED = 2;
    private static final int EVENT_CURRENT_UNCOMPRESSED_SIZE_CHANGED = 3;
    private static final int EVENT_PASSWORD_REQUIRED = 4;
    private static final int EVENT_UNRAR_FINISHED = 5;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mUnRarEventListener != null) {
                switch (msg.what) {
                    case EVENT_VOLUME_CHANGED:
                        mUnRarEventListener.onVolumeChanged((String)msg.obj);
                        break;
                    case EVENT_FILE_CHANGED:
                        mUnRarEventListener.onFileChanged((String)msg.obj);
                        break;
                    case EVENT_CURRENT_UNCOMPRESSED_SIZE_CHANGED:
                        long[] sizes = (long[])msg.obj;
                        mUnRarEventListener.onCurrentUncompressedSizeChanged(sizes[0], sizes[1]);
                        break;
                    case EVENT_PASSWORD_REQUIRED:
                        mUnRarEventListener.onPassWordRequired((UnrarTask)msg.obj);
                        break;
                    case EVENT_UNRAR_FINISHED:
                        mUnRarEventListener.onUnRarFinished((int)msg.obj);
                        break;
                    default:
                        break;
                }

            }
        }
    };

    private UnrarTask(String archive) {
        mArchive = archive;
    }

    private void setToPath(String toPath) {
        mToPath = toPath;
    }

    private void setUnRarEventListener(UnRarEventListener listener) {
        mUnRarEventListener = listener;
    }

    public String getArchive() {
        return mArchive;
    }

    public String getToPath() {
        return mToPath;
    }

    public UnRarEventListener getUnRarEventListener() {
        return mUnRarEventListener;
    }

    @Override
    public void onVolumeChanged(String nextVolume) {
        Message msg = mHandler.obtainMessage();
        msg.what = EVENT_VOLUME_CHANGED;
        msg.obj = nextVolume;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onFileChanged(String nextFileString) {
        Message msg = mHandler.obtainMessage();
        msg.what = EVENT_FILE_CHANGED;
        msg.obj = nextFileString;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onCurrentUncompressedSizeChanged(long uncompressedSize, long totalSize) {
        Message msg = mHandler.obtainMessage();
        msg.what = EVENT_CURRENT_UNCOMPRESSED_SIZE_CHANGED;
        long[] sizes = {uncompressedSize, totalSize};
        msg.obj = sizes;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onPassWordRequired(UnrarTask task) {
        Message msg = mHandler.obtainMessage();
        msg.what = EVENT_PASSWORD_REQUIRED;
        msg.obj = task;
        mHandler.sendMessage(msg);

    }


    @Override
    public void onUnRarFinished(int result) {
        Message msg = mHandler.obtainMessage();
        msg.what = EVENT_UNRAR_FINISHED;
        msg.obj = result;
        mHandler.sendMessage(msg);

    }

    public static class Builder {
        private String mArchive;
        private String mToPath;
        private UnRarEventListener mUnRarEventListener;

        public Builder(String archive) {
            mArchive =archive;
        }

        public void setArchive(String archive) {
            mArchive = archive;
        }

        public void setToPath(String toPath) {
            mToPath = toPath;
        }

        public void setUnRarEventListener(UnRarEventListener listener) {
            mUnRarEventListener = listener;
        }

        public UnrarTask build() {
            if (TextUtils.isEmpty(mArchive)) {
                return null;
            }
            UnrarTask task = new UnrarTask(mArchive);
            if (TextUtils.isEmpty(mToPath)) {
                task.setToPath("/sdcard/tmpunrar/");
            } else {
                task.setToPath(mToPath);
            }
            task.setUnRarEventListener(mUnRarEventListener);
            return task;
        }
    }
}
