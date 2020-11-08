package ruby.blacktech.libraunrar4j;

import android.text.TextUtils;
import android.util.Log;

public class SuperUnrar {

    public static final int UNRAR_SUCCESS = 0;
    public static final int UNRAR_ERROR_ALREADY_IN_PROGRESS = 1;
    public static final int UNRAR_ERROR_UNRAR_INNER_ERROR = 2;
    public static final int UNRAR_ERROR_UNKNOWN = 2;


    String TAG = "superunrar";
    static {
        System.loadLibrary("superunrar-lib");
    }


    private long currentUncompressedSize = 0;
    UnrarTask mUnrarTask;

    public SuperUnrar(UnrarTask task) {
        mUnrarTask = task;
    }

    public int startExtract() {
        int result = UNRAR_ERROR_UNKNOWN;
        currentUncompressedSize = 0;
        if (extract(mUnrarTask.getArchive(), mUnrarTask.getToPath())) {
            result = UNRAR_SUCCESS;
        } else {
            result = UNRAR_ERROR_UNRAR_INNER_ERROR;
        }
        mUnrarTask.onUnRarFinished(result);
        return result;
    }

    public int startExtract(String archive, String toPath) {
        int result = UNRAR_ERROR_UNKNOWN;
        currentUncompressedSize = 0;
        if (extract(archive, toPath)) {
            result = UNRAR_SUCCESS;
        } else {
            result = UNRAR_ERROR_UNRAR_INNER_ERROR;
        }
        mUnrarTask.onUnRarFinished(result);
        return result;
    }

    public native boolean extract(String archive, String toPath);

    public native long computeArchiveSize(String archive);

    public void incrementCurrentUncompressedSize(long i) {
        long mTotalArchiveSize = computeArchiveSize(mUnrarTask.getArchive());
        currentUncompressedSize += i;
        mUnrarTask.onCurrentUncompressedSizeChanged(currentUncompressedSize, mTotalArchiveSize);
    }

    public void volumeChange(String nextVolume) {
        mUnrarTask.onVolumeChanged(nextVolume);
    }

    public void fileChange(String nextFileString) {
        mUnrarTask.onFileChanged(nextFileString);
    }

    private boolean mPasswordRequested;
    private String mPassword;
    private Object mObject = new Object();

    public void setPasswordForPasswordRequest(String password) {
        if (mPasswordRequested) {
            Log.d(TAG, "set Password For Password Request password:" + password);
            mPasswordRequested = false;
            mPassword = password;
            synchronized (mUnrarTask) {
                mUnrarTask.notifyAll();
            }
        }
        Log.d(TAG, "set password for password request password done");
    }

    public String requestPassword() {
        mPassword = null;
        while (TextUtils.isEmpty(mPassword)) {
            mPasswordRequested = true;
            mUnrarTask.onPassWordRequired(mUnrarTask);
            synchronized (mUnrarTask) {
                try {
                    Log.d(TAG, "requestPassword wait");
                    mUnrarTask.wait();
                } catch(InterruptedException e) {
                }
            }
        }
        return mPassword;
    }

    public synchronized String volumeNotFound(String volume) {
        Log.d(TAG, "volumeNotFound" + volume);
    //    volumeToUseInstead = null;
    //    volumeNotFound = volume;
    //    while (volumeToUseInstead == null) {
    //        try {
    //            wait();
    //        } catch(InterruptedException e) {
    //        }
    //    }
    //    return volumeToUseInstead;
        return "";
    }

    public synchronized void announceExpansionError() {
    //    expansionError = true;
    }

    public synchronized void announceExpansionSuccess() {
    //    expansionSuccess = true;
    }
}
