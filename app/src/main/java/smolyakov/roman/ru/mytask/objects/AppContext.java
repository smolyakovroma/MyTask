package smolyakov.roman.ru.mytask.objects;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import smolyakov.roman.ru.mytask.receivers.DeleteDocumentReceiver;


public class AppContext extends Application {

    public static final String ACTION_TYPE = "smolyakov.roman.ru.mytask.AppContext.ActionType";
    public static final String DOC_INDEX = "smolyakov.roman.ru.mytask.AppContext.ActionIndex";
    public static final String DOC_INDEXES = "smolyakov.roman.ru.mytask.AppContext.DocIndexes";

    public static final int ACTION_NEW_TASK = 0;
    public static final int ACTION_UPDATE = 1;

    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CREATE_DATE = "createDate";
    public static final String FIELD_PRIORITY_TYPE = "priorityType";

    public static final String RECEIVER_DELETE_DOCUMENT = "smolyakov.roman.ru.mytask.AppContext.DeleteDocument";
    public static final String RECEIVER_REFRESH_LISTVIEW = "smolyakov.roman.ru.mytask.AppContext.RefreshListView";
    public static final String FIELD_IMAGE_PATH = "imagePath";

    public static final int IMAGE_WIDTH_THMB = 300;
    public static final int IMAGE_HEIGHT_THMB = 300;

    private ArrayList<TodoDocument> listDocuments;


    private BroadcastReceiver deleteDocumentReceiver = new DeleteDocumentReceiver();

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteDocumentReceiver, new IntentFilter(RECEIVER_DELETE_DOCUMENT));
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deleteDocumentReceiver);
    }

    public ArrayList<TodoDocument> getListDocuments() {
        return listDocuments;
    }

    public void setListDocuments(ArrayList<TodoDocument> listDocuments) {
        this.listDocuments = listDocuments;
    }

    public File getPrefsDir() {
        return new File(getApplicationInfo().dataDir + "/" + "shared_prefs");
    }

    public String getPrefsDirPath() {
        return getApplicationInfo().dataDir + "/" + "shared_prefs";
    }

}




















