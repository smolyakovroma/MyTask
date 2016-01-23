package smolyakov.roman.ru.mytask.activities;


import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import smolyakov.roman.ru.mytask.R;
import smolyakov.roman.ru.mytask.activities.utils.ImageUtils;
import smolyakov.roman.ru.mytask.objects.AppContext;

public class FullImage extends Activity {

    private ImageView imgFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO уберем заголовок у приложения
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_full_image);

        imgFull = (ImageView) findViewById(R.id.image_full);

        String imagePath = getIntent().getStringExtra(TodoDetails.IMAGE_PATH);

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        imgFull.setImageBitmap(ImageUtils.getSizedBitmap(imagePath, displaymetrics.widthPixels, displaymetrics.heightPixels));
//        imgFull.setImageBitmap(ImageUtils.getSizedBitmap(imagePath, 500, 500));
        imgFull.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.full_image, menu);
        return true;
    }


}
