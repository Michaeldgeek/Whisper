package challenge.magnet.android.whisper.activities;

import android.support.v7.app.ActionBar;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import challenge.magnet.android.whisper.R;
import challenge.magnet.android.whisper.helpers.TouchImageView;

public class ImageViewActivity extends AppCompatActivity {

    public TouchImageView ivMessageImage;
    final String TAG = "ImageViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        String imageUrl = getIntent().getStringExtra("imageUrl");
        ivMessageImage = (TouchImageView) findViewById(R.id.ivMessageImage);
        boolean isInstagram = getIntent().getBooleanExtra("isInstagram", false);
        if (imageUrl != null) {
            if(isInstagram){
                Picasso.with(this).load(imageUrl).into(ivMessageImage);
            }
            else {
                Picasso.with(this).load(Uri.fromFile(new File(imageUrl))).into(ivMessageImage);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}