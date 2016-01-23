package smolyakov.roman.ru.mytask.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import smolyakov.roman.ru.mytask.R;
import smolyakov.roman.ru.mytask.adapters.TodoAdapter;
import smolyakov.roman.ru.mytask.objects.AppContext;
import smolyakov.roman.ru.mytask.objects.PriorityType;
import smolyakov.roman.ru.mytask.objects.TodoDocument;
import smolyakov.roman.ru.mytask.objects.TodoListComparator;

@SuppressLint("NewApi")
public class TodoList extends AppCompatActivity {

    public static final String TODO_DOCUMENT = "ru.smolyakov.roma.mytask.TodoDocument";
    public static int TODO_DETAILS_REQUEST = 1;

    private ArrayList<TodoDocument> listDocuments;
    private ListView listviewTasks;
    private EditText txtSearch;
    private Button clearSearch;
    private RelativeLayout layoutListView;

    private Intent intent;
    private TodoAdapter todoAdapter;

    private BroadcastReceiver refreshListViewReceiver = new RefreshListViewReceiver();
    private CheckboxListener checkboxListener = new CheckboxListener();

    private static Comparator<TodoDocument> comparator = TodoListComparator.getDateComparator();
    private MenuItem menuDelete;
    private MenuItem menuSort;
    private MenuItem menuCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        listviewTasks = (ListView) findViewById(R.id.listTasks);
        listviewTasks.setOnItemClickListener(new ListViewClickListener());
        listviewTasks.setEmptyView(findViewById(R.id.emptyView));
        txtSearch = (EditText) findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextChangeListener());
        layoutListView = (RelativeLayout) findViewById(R.id.layoutListView);
        clearSearch = (Button) findViewById(R.id.clear_search);
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtSearch.isEnabled()) {
                    txtSearch.setText("");
                }
            }
        });
        listDocuments = ((AppContext) getApplicationContext()).getListDocuments();

        intent = new Intent(this, TodoDetails.class);

        LocalBroadcastManager.getInstance(this).registerReceiver(refreshListViewReceiver,
                new IntentFilter(AppContext.RECEIVER_REFRESH_LISTVIEW));


        if (((AppContext)getApplicationContext()).getListDocuments()==null) {// если в первый раз загружаем документы
            new LoadDocumentsTask().execute();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (listDocuments!=null) {
            sort();
        }

    }

    private void checkSearchActive() {
        if (listDocuments.isEmpty()) {
            txtSearch.setEnabled(false);
        } else {
            txtSearch.setEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_list, menu);
        menuSort = menu.findItem(R.id.menu_sort);
        menuDelete = menu.findItem(R.id.menu_delete_check);
        menuCreate = menu.findItem(R.id.add_task);

        checkControlsActive();

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.isChecked()) return true;


        switch (item.getItemId()) {
            case R.id.add_task: {
                Bundle bundle = new Bundle();
                bundle.putInt(AppContext.ACTION_TYPE, AppContext.ACTION_NEW_TASK);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
            case R.id.menu_sort_name: {
                item.setChecked(true);
                comparator = TodoListComparator.getNameComparator();
                sort();
                return true;
            }
            case R.id.menu_sort_date: {
                item.setChecked(true);
                comparator = TodoListComparator.getDateComparator();
                sort();
                return true;
            }
            case R.id.menu_sort_priority: {
                item.setChecked(true);
                comparator = TodoListComparator.getPriorityComparator();
                sort();
                return true;
            }
            case R.id.menu_delete_check: {

                if (!indexesForDelete.isEmpty()) {

                    Intent intent = new Intent(AppContext.RECEIVER_DELETE_DOCUMENT);

                    intent.putIntegerArrayListExtra(AppContext.DOC_INDEXES,
                            new ArrayList<Integer>(indexesForDelete));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                }


                return true;
            }

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sort() {
        indexesForDelete.clear();

        Collections.sort(listDocuments, comparator);
        updateIndexes();

        // возможны более оптимальные решения: наследование от BaseAdapter,
        // запуск в параллельном потоке
        todoAdapter = new TodoAdapter(this, listDocuments, checkboxListener);
        listviewTasks.setAdapter(todoAdapter);

        todoAdapter.getFilter().filter(txtSearch.getText());

        checkControlsActive();

        setTitle(getResources().getString(R.string.app_name)+" ("+listDocuments.size()+")");

    }

    private void showDocument(TodoDocument todoDocument) {
        Intent intent = new Intent(this, TodoDetails.class);
        intent.putExtra(TODO_DOCUMENT, todoDocument);
        startActivityForResult(intent, TODO_DETAILS_REQUEST);
    }


    private int docNumber;

    @SuppressLint("NewApi")
    private void addDocument(TodoDocument todoDocument) {
        todoDocument.setCreateDate(new Date());
        if (todoDocument.getNumber() == null) {
            docNumber += 1;
            todoDocument.setNumber(docNumber);
            listDocuments.add(todoDocument);
        } else {
            listDocuments.set(todoDocument.getNumber(), todoDocument);
        }
        Collections.sort(listDocuments);
        todoAdapter.notifyDataSetChanged();

    }


    private class ListViewClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            TodoDocument todoDocument = (TodoDocument) parent.getAdapter().getItem(position);
//            todoDocument.setNumber(position);
//            showDocument(todoDocument);

            Bundle bundle = new Bundle();
            bundle.putInt(AppContext.ACTION_TYPE, AppContext.ACTION_UPDATE);
            bundle.putInt(AppContext.DOC_INDEX, ((TodoDocument) parent.getAdapter().getItem(position)).getNumber());

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void updateIndexes() {
        int i = 0;
        for (TodoDocument doc : listDocuments) {
            doc.setNumber(i++);
        }
    }

    private class TextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            todoAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


    private class RefreshListViewReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sort();

        }

    }

    private Set<Integer> indexesForDelete = new TreeSet<Integer>();

    private class CheckboxListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox) v;
            TodoDocument todoDocument = (TodoDocument) checkBox.getTag();
            todoDocument.setChecked(checkBox.isChecked());

            RelativeLayout ve = (RelativeLayout)v.getParent();

            TextView txtTodoName = (TextView)ve.findViewById(R.id.todo_name);
            TextView txtTodoDate = (TextView)ve.findViewById(R.id.todo_date);

            if (checkBox.isChecked()) {
                indexesForDelete.add(todoDocument.getNumber());
                txtTodoName.setTextColor(Color.LTGRAY);
                txtTodoDate.setTextColor(Color.LTGRAY);
            } else {
                indexesForDelete.remove(todoDocument.getNumber());
                txtTodoName.setTextColor(Color.BLACK);
                txtTodoDate.setTextColor(Color.BLACK);
            }



            checkControlsActive();

        }

    }

    private void checkControlsActive() {
        if (menuSort == null || menuDelete == null)
            return;
        if (listDocuments.isEmpty()) {
            menuDelete.setEnabled(false);
            menuSort.setEnabled(false);
            menuCreate.setEnabled(true);
            txtSearch.setEnabled(false);
        } else {
            menuDelete.setEnabled(!indexesForDelete.isEmpty());
            menuSort.setEnabled(indexesForDelete.isEmpty());
            menuCreate.setEnabled(indexesForDelete.isEmpty());
            txtSearch.setEnabled(indexesForDelete.isEmpty());
        }
    }

    private class LoadDocumentsTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog dialog;

        private File prefsDir = ((AppContext) getApplicationContext())
                .getPrefsDir();

        @Override
        protected void onPreExecute() {

            this.dialog = new ProgressDialog(TodoList.this);
//			this.dialog.setTitle("");
            this.dialog.setMessage(getResources().getString(R.string.loading));

            if (!this.dialog.isShowing()) {
                this.dialog.show();
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            sort();
            layoutListView.setVisibility(View.VISIBLE);
            this.dialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {

            imitateLoaindg();

            AppContext appContext = ((AppContext) getApplicationContext());

            listDocuments = new ArrayList<TodoDocument>();

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
                    todoDocument
                            .setPriorityType(PriorityType.values()[sharedPref
                                    .getInt(AppContext.FIELD_PRIORITY_TYPE, 0)]);
                    todoDocument.setImagePath(sharedPref.getString(
                            AppContext.FIELD_IMAGE_PATH, null));
                    listDocuments.add(todoDocument);

                    imitateLoaindg();

                }

            }

            appContext.setListDocuments(listDocuments);

            return null;
        }


        private void imitateLoaindg() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

    }
}












































