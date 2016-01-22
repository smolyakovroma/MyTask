package smolyakov.roman.ru.mytask.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import smolyakov.roman.ru.mytask.R;
import smolyakov.roman.ru.mytask.objects.AppContext;
import smolyakov.roman.ru.mytask.objects.PriorityType;
import smolyakov.roman.ru.mytask.objects.TodoDocument;

public class TodoDetails extends AppCompatActivity {

    public static final int RESULT_SAVE = 100;
    public static final int RESULT_DELETE = 101;
    public static final int NAME_LENGTH = 20;

    private EditText txtTodoDetails;
    private TodoDocument todoDocument;

    public static final String LOG_TAG = "myTaskError";

    private ArrayList<TodoDocument> listDocuments;

    private int actionType;
    private int docIndex;

    private PriorityType currentPriorityType;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);
        txtTodoDetails = (EditText) findViewById(R.id.txtTodoDetails);

        listDocuments = ((AppContext) getApplicationContext()).getListDocuments();

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        actionType = getIntent().getExtras().getInt(AppContext.ACTION_TYPE);
        prepareDocument(actionType);

    }

    private void prepareDocument(int actionType) {
        switch (actionType) {
            case AppContext.ACTION_NEW_TASK:
                todoDocument = new TodoDocument();
                break;
            case AppContext.ACTION_UPDATE:
                docIndex = getIntent().getExtras().getInt(AppContext.DOC_INDEX);
                todoDocument = listDocuments.get(docIndex);
                txtTodoDetails.setText(todoDocument.getContent());
                break;
            default:
                break;
        }

        currentPriorityType = todoDocument.getPriorityType();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (txtTodoDetails.getText().toString().trim().length() == 0) {

                } else {
                    saveDocument();
                }

                return true;
            }
            case R.id.back: {
                if (txtTodoDetails.getText().toString().trim().length() == 0) {

                } else {
                    saveDocument();
                }

                return true;
            }
            case R.id.save: {
                saveDocument();
                return true;
            }
            case R.id.delete: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.confirm_delete);

                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDocument(todoDocument);

                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }

            case R.id.menu_priority_high:
            case R.id.menu_priority_low:
            case R.id.menu_priority_middle: {
                item.setChecked(true);
                currentPriorityType = PriorityType.values()[Integer.valueOf(item.getTitleCondensed().toString())];
                return true;
            }
            default:
                break;
        }
       return super.onOptionsItemSelected(item);
    }

    private void saveDocument() {



        if (actionType == AppContext.ACTION_UPDATE) {
            boolean edited = false;

            SharedPreferences sharedPref = getSharedPreferences(
                    String.valueOf(todoDocument.getCreateDate().getTime()),
                    Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPref.edit();

            // если документ старый и текст изменился
            if (!txtTodoDetails.getText().toString().trim().equals(todoDocument.getContent())) {

                todoDocument.setName(getDocumentName());
                todoDocument.setContent(txtTodoDetails.getText().toString().trim());
                editor.putString(AppContext.FIELD_CONTENT,todoDocument.getContent());
                edited = true;
            }

            // если приоритет изменился
            if (currentPriorityType != todoDocument.getPriorityType()) {
                todoDocument.setPriorityType(currentPriorityType);
                editor.putInt(AppContext.FIELD_PRIORITY_TYPE, todoDocument.getPriorityType().getIndex());
                edited = true;
            }

            if (edited) {
                String path = ((AppContext) getApplicationContext()).getPrefsDir();
                File file = new File(path, todoDocument.getCreateDate().getTime()+".xml");

                todoDocument.setCreateDate(new Date());
                editor.putString(AppContext.FIELD_NAME, todoDocument.getName());
                editor.putLong(AppContext.FIELD_CREATE_DATE, todoDocument.getCreateDate().getTime());
                editor.commit();

                file.renameTo(new File(path, todoDocument.getCreateDate().getTime() + ".xml"));

            }

        } else if (actionType == AppContext.ACTION_NEW_TASK) {
            todoDocument.setName(getDocumentName());
            todoDocument.setCreateDate(new Date());
            todoDocument.setContent(txtTodoDetails.getText().toString().trim());
            todoDocument.setPriorityType(currentPriorityType);

            try{
                SharedPreferences sharedPref = getSharedPreferences(String.valueOf(todoDocument.getCreateDate().getTime()), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(AppContext.FIELD_CONTENT, todoDocument.getContent());
                editor.putString(AppContext.FIELD_NAME, todoDocument.getName());
                editor.putLong(AppContext.FIELD_CREATE_DATE, todoDocument.getCreateDate().getTime());
                editor.putInt(AppContext.FIELD_PRIORITY_TYPE, todoDocument.getPriorityType().getIndex());
                editor.commit();

                listDocuments.add(todoDocument);
            } catch (Exception e){
                Log.e("error", e.getMessage());
            }
        }


        finish();
//        StringBuilder sb = new StringBuilder(txtTodoDetails.getText());
//
//
//        if (sb.length() > NAME_LENGTH) {
//            sb.delete(NAME_LENGTH, sb.length()).append("...");
//        }
//        String tmpName = sb.toString().trim().split("\n")[0];
//
//        String name = (tmpName.length() > 0) ? tmpName : todoDocument.getName();
//        todoDocument.setName(name);
//
//        if (todoDocument.getContent() != null && txtTodoDetails.getText().toString().equals(todoDocument.getContent())) {
//            setResult(RESULT_CANCELED, getIntent());
//        } else {
//            todoDocument.setContent(sb.toString());
//            setResult(RESULT_SAVE, getIntent());
//        }
    }

    @SuppressLint("NewApi")
    private void deleteDocument(TodoDocument todoDocument) {
        if (actionType == AppContext.ACTION_UPDATE) {
            Intent intent = new Intent(AppContext.RECEIVER_DELETE_DOCUMENT);
            intent.putExtra(AppContext.DOC_INDEX, todoDocument.getNumber());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        }
        finish();
    }

    private File getCurrentTodoFile() {
        String filePath = ((AppContext) getApplicationContext()).getPrefsDir()
                +"/"+todoDocument.getCreateDate().getTime()+".xml";
        return new File(filePath);
    }

    private void updateIndexes() {
        int i = 0;
        for (TodoDocument doc : listDocuments) {
            doc.setNumber(i++);
        }
    }

    private String getDocumentName() {
        StringBuilder sb = new StringBuilder(txtTodoDetails.getText());


        if (sb.length() > NAME_LENGTH) {
            sb.delete(NAME_LENGTH, sb.length()).append("...");
        }
        String tmpName = sb.toString().trim().split("\n")[0];

        String name = (tmpName.length() > 0) ? tmpName : getResources().getString(R.string.new_document);
        return name;
    }

}













































