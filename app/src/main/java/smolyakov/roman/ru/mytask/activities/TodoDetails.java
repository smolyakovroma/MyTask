package smolyakov.roman.ru.mytask.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import smolyakov.roman.ru.mytask.R;
import smolyakov.roman.ru.mytask.activities.utils.ImageUtils;
import smolyakov.roman.ru.mytask.objects.AppContext;
import smolyakov.roman.ru.mytask.objects.PriorityType;
import smolyakov.roman.ru.mytask.objects.TodoDocument;

public class TodoDetails extends AppCompatActivity {

    public static final int RESULT_SAVE = 100;
    public static final int RESULT_DELETE = 101;
    public static final int NAME_LENGTH = 20;

    private EditText txtTodoDetails;
    private TodoDocument todoDocument;
    private ImageView imgTodo;
    private FrameLayout frameImage;


    public static final String LOG_TAG = "myTaskError";

    private ArrayList<TodoDocument> listDocuments;

    private int actionType;
    private int docIndex;
    private String imagePath;

    private PriorityType currentPriorityType;

    public static final String IMAGE_PATH = "smolyakov.roman.ru.mytask.activities.TodoDetails.ImagePath";

    private static final int CAPTURE_IMAGE_REQUEST = 100;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);
        txtTodoDetails = (EditText) findViewById(R.id.txtTodoDetails);

        listDocuments = ((AppContext) getApplicationContext()).getListDocuments();

//        getActionBar().setDisplayHomeAsUpEnabled(true);

        actionType = getIntent().getExtras().getInt(AppContext.ACTION_TYPE);
        frameImage = (FrameLayout) findViewById(R.id.frameImage);

        prepareDocument(actionType);

        imgTodo = (ImageView) findViewById(R.id.imgTodo);
        imgTodo.setOnClickListener(new ImageClickListener());

        if (todoDocument.getImagePath() != null) {
            frameImage.setVisibility(View.VISIBLE);
            imagePath = todoDocument.getImagePath();
            attachPhoto(imagePath);
        } else {
            frameImage.setVisibility(View.GONE);
        }

    }

    private void prepareDocument(int actionType) {
        switch (actionType) {
            case AppContext.ACTION_NEW_TASK:
                todoDocument = new TodoDocument();
                todoDocument.setCreateDate(new Date());
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

    public void deleteImage(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_image);

        builder.setPositiveButton(R.string.delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (notEmpty(imagePath)){
                            deleteImageFile(imagePath);
                        }
                        frameImage.setVisibility(View.GONE);
                        imagePath = null;
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

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

            case R.id.menu_take_photo: {

                Intent intentAttachPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                Uri uri = Uri.fromFile(getImagePath());

                imagePath = uri.getPath();

                intentAttachPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intentAttachPhoto, CAPTURE_IMAGE_REQUEST);

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
            if (!txtTodoDetails.getText().toString().trim()
                    .equals(todoDocument.getContent())) {

                todoDocument.setName(getDocumentName());
                todoDocument.setContent(txtTodoDetails.getText().toString()
                        .trim());
                editor.putString(AppContext.FIELD_CONTENT,
                        todoDocument.getContent());
                edited = true;
            }

            // если приоритет изменился
            if (currentPriorityType != todoDocument.getPriorityType()) {
                todoDocument.setPriorityType(currentPriorityType);
                editor.putInt(AppContext.FIELD_PRIORITY_TYPE, todoDocument
                        .getPriorityType().getIndex());
                edited = true;
            }

            // если изменили/добавили/удалили изображение
            if ((notEmpty(imagePath) && !imagePath.equals(todoDocument.getImagePath())) ||
                    (notEmpty(todoDocument.getImagePath()) && !todoDocument.getImagePath().equals(imagePath))

                    ) {

                if (notEmpty(todoDocument.getImagePath())) {// удалить старое изображение
                    deleteImageFile(todoDocument.getImagePath());
                }

                todoDocument.setImagePath(imagePath);
                edited = true;
            }

            if (edited) {
                String path = ((AppContext) getApplicationContext())
                        .getPrefsDirPath();
                File file = new File(path, todoDocument.getCreateDate()
                        .getTime() + ".xml");

                todoDocument.setCreateDate(new Date());
                editor.putString(AppContext.FIELD_NAME, todoDocument.getName());
                editor.putLong(AppContext.FIELD_CREATE_DATE, todoDocument
                        .getCreateDate().getTime());
                editor.putString(AppContext.FIELD_IMAGE_PATH,
                        todoDocument.getImagePath());
                editor.commit();

                file.renameTo(new File(path, todoDocument.getCreateDate()
                        .getTime() + ".xml"));

            }

        } else if (actionType == AppContext.ACTION_NEW_TASK) {
            todoDocument.setName(getDocumentName());
            todoDocument.setCreateDate(new Date());
            todoDocument.setContent(txtTodoDetails.getText().toString().trim());
            todoDocument.setPriorityType(currentPriorityType);

            if (imagePath != null)
                todoDocument.setImagePath(imagePath);

            SharedPreferences sharedPref = getSharedPreferences(
                    String.valueOf(todoDocument.getCreateDate().getTime()),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(AppContext.FIELD_CONTENT,
                    todoDocument.getContent());
            editor.putString(AppContext.FIELD_NAME, todoDocument.getName());
            editor.putLong(AppContext.FIELD_CREATE_DATE, todoDocument
                    .getCreateDate().getTime());
            editor.putInt(AppContext.FIELD_PRIORITY_TYPE, todoDocument
                    .getPriorityType().getIndex());
            editor.putString(AppContext.FIELD_IMAGE_PATH,
                    todoDocument.getImagePath());

            editor.commit();

            listDocuments.add(todoDocument);

        }

        finish();


    }

    @SuppressLint("NewApi")
    private void deleteDocument(TodoDocument todoDocument) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete_image);

        builder.setPositiveButton(R.string.delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (notEmpty(imagePath)){
                            deleteImageFile(imagePath);
                        }
                        frameImage.setVisibility(View.GONE);
                        imagePath = null;
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean notEmpty(String val) {
        return val != null && !val.equals("");
    }

    private boolean deleteImageFile(String path) {
        File f = new File(path);
        if (f.exists()) {
            return f.delete();
        }
        return false;
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
    private File getImagePath() {
        File directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                getPackageName());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return new File(directory.getPath() + File.separator
                + UUID.randomUUID() + ".jpg");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(IMAGE_PATH, imagePath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imagePath = savedInstanceState.getString(IMAGE_PATH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK && notEmpty(imagePath) && new File(imagePath).exists()) {
                // imagePath = data.getData().getPath();// не работает, если
                // указывать EXTRA_OUTPUT
                attachPhoto(imagePath);
            } else {// если отменили фотографирование
                if (todoDocument.getImagePath()!=null){
                    imagePath = todoDocument.getImagePath();
                }
            }
        }
    }

    private void attachPhoto(String imagePath) {
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {

            frameImage.setVisibility(View.VISIBLE);
            imgTodo.setImageBitmap(ImageUtils.getSizedBitmap(imagePath,
                    AppContext.IMAGE_WIDTH_THMB, AppContext.IMAGE_HEIGHT_THMB));
        }
    }
    public void openImage(View view) {
        Intent intentFullImage = new Intent(TodoDetails.this, FullImage.class);
        if (notEmpty(imagePath)) {// если открываем только что сфотанное изображение
            intentFullImage.putExtra(IMAGE_PATH, imagePath);
        }else{
            intentFullImage.putExtra(IMAGE_PATH, todoDocument.getImagePath());
        }
        startActivity(intentFullImage);
    }

    private class ImageClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            openImage(v);
        }

    }

}













































