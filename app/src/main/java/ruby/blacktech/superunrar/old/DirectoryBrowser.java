package ruby.blacktech.superunrar.old;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ruby.blacktech.superunrar.MainActivity;
import ruby.blacktech.superunrar.R;

public class DirectoryBrowser extends ListActivity {
    private String rootPath = null;
    private String curAbsPath = null;
    List<String> folderNameList = null;
    private TextView txvCurPath;
    public static final int  RESULT_CHOOSE_DIR_OK       = 0;
    public static final int RESULT_CHOOSE_DIR_CANCEL = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_directory_browser);

        // Show the Up button in the action bar.
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        txvCurPath = (TextView) findViewById(R.id.txvCurPath);

        File extStorDir = Environment.getExternalStorageDirectory();
        String extStroAbsPath = extStorDir.getAbsolutePath();

        rootPath = extStroAbsPath;

        updateCurFolder(extStroAbsPath);
    }

    private void updateCurFolder(String newAbsPath)
    {
        curAbsPath = newAbsPath;
        txvCurPath.setText(curAbsPath);

        folderNameList = getFolerStrList(new File(curAbsPath));
        ArrayAdapter<String> folderItemList = new ArrayAdapter<String>(this, R.layout.file_list_row, folderNameList);
        setListAdapter(folderItemList);
    }

    private List<String> getFolerStrList(File curPath){
        List<String> folerNameList = new ArrayList<String>();
        File[] files = curPath.listFiles();
        for(File eachFile : files){
            if(eachFile.isDirectory())
            {
                folerNameList.add(eachFile.getName());
            }
        }
        return folerNameList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        updateCurFolder(curAbsPath + "/" + folderNameList.get(position));
    }

    /** return prev UI */
    public void ChooseFolerOk(View v) {
        Intent intent = new Intent(DirectoryBrowser.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("selFolderPath", curAbsPath);
        intent.putExtras(bundle);
        setResult(RESULT_CHOOSE_DIR_OK, intent);
        finish();
    }

    public void ChooseFolerCancel(View v) {
        Intent intent = new Intent(DirectoryBrowser.this, MainActivity.class);
        setResult(RESULT_CHOOSE_DIR_CANCEL, intent);
        finish();
    }

    public void browseRoot(View v) {
        if(curAbsPath != rootPath)
        {
            updateCurFolder(rootPath);
        }
    }

    public void browseUp(View v) {
        if(curAbsPath == rootPath)
        {
            //do nothing when is root
        }
        else
        {
            String parentFoler = new File(curAbsPath).getParent().toString();
            updateCurFolder(parentFoler);
        }
    }
}
