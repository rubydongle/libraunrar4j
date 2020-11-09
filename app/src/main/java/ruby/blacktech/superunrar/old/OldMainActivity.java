package ruby.blacktech.superunrar.old;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ruby.blacktech.libraunrar4j.Archive;
import ruby.blacktech.libraunrar4j.core.SuperUnrar;
import ruby.blacktech.libraunrar4j.core.SuperUnrarContext;
import ruby.blacktech.libraunrar4j.core.UnRarEventListener;
import ruby.blacktech.libraunrar4j.core.UnrarTask;
import ruby.blacktech.libraunrar4j.exception.RarException;
import ruby.blacktech.libraunrar4j.rarfile.FileHeader;
import ruby.blacktech.superunrar.R;

public class OldMainActivity extends AppCompatActivity {

    String TAG = "superunrar";
    EditText mArchive;
    EditText mDestination;
    Button mStartUnrar;
    Button mGetUnrarInfo;
    TextView mUnrarPercent;

    TextView mCurrentVolume;
    TextView mCurrentFile;
    TextView mUnrarDetails;

    Button mArchiveSelect;
    Button mDestinationSelect;

    SuperUnrarContext mUnrarContext;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    UnRarEventListener mUnRarEventListener = new UnRarEventListener() {
        @Override
        public void onVolumeChanged(final String nextVolume) {
            mCurrentVolume.setText("Extracting Volume:" + nextVolume);
        }

        @Override
        public void onFileChanged(final String nextFileString) {
            mCurrentFile.setText("Extracting File: " + nextFileString);
        }

        @Override
        public void onCurrentUncompressedSizeChanged(long size, final long totalSize) {
            Log.d(TAG, "onCurrentUncompressedSizeChanged size " + size + " totalSize:"+ totalSize);
            if (size != 0) {
                String percentString = (int)((size*100)/totalSize) + "%";
                Log.d(TAG, "percent:" + percentString);
                mUnrarPercent.setText(percentString);
            }
        }

        private void showPasswordInputDialog() {
            AlertDialog.Builder customizeDialog =
                    new AlertDialog.Builder(OldMainActivity.this);
            final View dialogView = LayoutInflater.from(OldMainActivity.this)
                    .inflate(R.layout.passwordinputdialog,null);
            customizeDialog.setTitle("请输入 " + mUnrarTask.getArchive() + " 密码");
            customizeDialog.setView(dialogView);
            customizeDialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 获取EditView中的输入内容
                            //unrarTask.
                            EditText edit_text =
                                    (EditText) dialogView.findViewById(R.id.password_input_text);
                            Toast.makeText(OldMainActivity.this,
                                    edit_text.getText().toString(),
                                    Toast.LENGTH_SHORT).show();
                            mUnrarTask.setPassword(edit_text.getText().toString());
                        }
                    });
            customizeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    mUnrarTask.setPassword("xxxxxxxxxx");
                }
            });
            customizeDialog.show();
        }

        @Override
        public void onPassWordRequired(UnrarTask task) {
            //mPasswordFrameLayout.setVisibility(View.VISIBLE);
            //show password input dialog here
            showPasswordInputDialog();
        }

        @Override
        public void onUnRarFinished(int result) {
            switch (result) {
                case SuperUnrar.UNRAR_SUCCESS:
                    mCurrentVolume.setText("");
                    mCurrentFile.setText("");
                    mUnrarDetails.setText("unRar Successs");
                    mUnrarTask = null;
                    break;
                case SuperUnrar.UNRAR_ERROR_ALREADY_IN_PROGRESS:
                    mCurrentVolume.setText("");
                    mCurrentFile.setText("");
                    mUnrarDetails.setText("unRar already in progress");
                    break;
                case SuperUnrar.UNRAR_ERROR_UNRAR_INNER_ERROR:
                    mCurrentVolume.setText("");
                    mCurrentFile.setText("");
                    mUnrarDetails.setText("unRar Error Occur");
                    mUnrarTask = null;
                    break;
                default:
                    break;

            }
        }
    };

    private UnrarTask mUnrarTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate In");
        Toast.makeText(getApplicationContext(), "onCreate In", Toast.LENGTH_SHORT).show();
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_main);

        mCurrentFile = findViewById(R.id.currentfile);
        mCurrentVolume = findViewById(R.id.currentvolume);
        mUnrarDetails = findViewById(R.id.sample_text);
        mArchive = findViewById(R.id.archive);
        mDestination = findViewById(R.id.destination);
        mStartUnrar = findViewById(R.id.start_unrar);
        mGetUnrarInfo = findViewById(R.id.get_unrar_info);
        mUnrarPercent = findViewById(R.id.unrar_percent);
        mArchiveSelect = findViewById(R.id.archive_select);
        mDestinationSelect = findViewById(R.id.destination_select);
        mUnrarContext = new SuperUnrarContext();


        mStartUnrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String archivePath = mArchive.getText().toString();
                final String destinationPath = mDestination.getText().toString();
                if (TextUtils.isEmpty(archivePath)) {
                    Log.e(TAG, "should input a archive to unrar");
                    return;
                }
                if (mUnrarTask == null) {
                    UnrarTask.Builder builder = new UnrarTask.Builder(archivePath);
                    builder.setToPath(destinationPath);
                    builder.setUnRarEventListener(mUnRarEventListener);
                    mUnrarTask = builder.build();
                    Log.d(TAG, "start unrar " + archivePath + " to " + destinationPath);
                    mUnrarDetails.setText("");
                    mUnrarContext.startExtract(mUnrarTask);
                    //mUnrarTask.startExtract();
                } else {
                    Log.e(TAG, "one unrar at a time");
                }
            }
        });

        mGetUnrarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String archivePath = mArchive.getText().toString();
                if (TextUtils.isEmpty(archivePath)) {
                    mUnrarDetails.setText("Should Input a Archive to get Info");
                    Log.e(TAG, "should input a archive to unrar");
                    return;
                }

                try {
                    Archive archive = new Archive(archivePath);
                    List<FileHeader> files = archive.testGetInfo();
                    if (files != null) {
                        String filesText = new String();
                        for (FileHeader file : files) {
                            filesText += file.getFileName() + "\n";
                        }
                        mUnrarDetails.setText(filesText);
                    }

                } catch (RarException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        mArchiveSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,REQUEST_CODE_FILE);
            }
        });

        mDestinationSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OldMainActivity.this, DirectoryBrowser.class);
                startActivityForResult(intent,REQUEST_CODE_DIR);
            }
        });
        Toast.makeText(getApplicationContext(), "onCreate Done", Toast.LENGTH_SHORT).show();
    }

    private static final int REQUEST_CODE_FILE = 1;
    private static final int REQUEST_CODE_DIR = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri archiveUri = data.getData();
                    String archivePath = FileUtils.getPath(this, archiveUri);
                    mArchive.setText(archivePath);
                    Log.d(TAG, "select archive " + archivePath);
                }
                break;
            case REQUEST_CODE_DIR:
                String destinationPath = "";
                Bundle bundle = null;
                if (data != null) {
                    bundle = data.getExtras();
                    if (bundle != null) {
                        if (resultCode == DirectoryBrowser.RESULT_CHOOSE_DIR_OK) {
                            destinationPath = bundle.getString("selFolderPath");
                        } else if (resultCode == DirectoryBrowser.RESULT_CHOOSE_DIR_CANCEL) {
                        }
                    }
                }
                mDestination.setText(destinationPath);
                Log.d(TAG, "select destination " + destinationPath);
                break;
            default:
                break;
        }

    }
}
