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

    private BroadcastReceiver deleteDocumentReceiver = new DeleteDocumentReceiver();

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteDocumentReceiver, new IntentFilter(RECEIVER_DELETE_DOCUMENT));
        populateList();
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deleteDocumentReceiver);
    }

    private ArrayList<TodoDocument> listDocuments = new ArrayList<TodoDocument>();

    public ArrayList<TodoDocument> getListDocuments() {
        return listDocuments;
    }

    public void setListDocuments(ArrayList<TodoDocument> listDocuments) {
        this.listDocuments = listDocuments;
    }

    public String getPrefsDir() {
        return getApplicationInfo().dataDir + "/" + "shared_prefs";
    }

    private void populateList() {
        File prefsDir = new File(
                ((AppContext) getApplicationContext()).getPrefsDir());

        if (prefsDir.exists() && prefsDir.isDirectory()) {
            String[] list = prefsDir.list();
            for (int i = 0; i < list.length; i++) {
                SharedPreferences sharedPref = getSharedPreferences(
                        list[i].replace(".xml", ""), Context.MODE_PRIVATE);
                TodoDocument todoDocument = new TodoDocument();
                todoDocument.setContent(sharedPref.getString(
                        AppContext.FIELD_CONTENT, null));
                todoDocument.setCreateDate(new Date(sharedPref.getLong(
                        AppContext.FIELD_CREATE_DATE, 0)));
                todoDocument.setName(sharedPref.getString(
                        AppContext.FIELD_NAME, null));
                todoDocument.setPriorityType(PriorityType.values()[sharedPref
                        .getInt(AppContext.FIELD_PRIORITY_TYPE, 0)]);
                listDocuments.add(todoDocument);
            }

        }

    }
}



















