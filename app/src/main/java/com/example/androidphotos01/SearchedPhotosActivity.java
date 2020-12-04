package com.example.androidphotos01;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.androidphotos01.adapters.PhotoAdapter;
import com.example.androidphotos01.dialogs.ErrorDialogFragment;
import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.model.Photo;
import com.example.androidphotos01.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class SearchedPhotosActivity extends AppCompatActivity {

    private GridView photoGridView;
    private User user = LoadSaveController.user();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searched_photos_screen);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent other = this.getIntent();
        Bundle extras = other.getExtras();

        this.photoGridView = (GridView) findViewById(R.id.photo_grid_view);

        photoGridView.setAdapter(new PhotoAdapter(this, R.layout.view_photo_list, user.searchedPhotos(), null));
        photoGridView.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {

                    //Navigate to slide show screen
                    Bundle bundle = new Bundle();

                    bundle.putInt("albumIndex", -1);
                    bundle.putInt("photoIndex", position);

                    Intent intent = new Intent(this, PhotoSlideshowActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
        );

    }

    //Back button to album list screen
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
        return true;
    }

    public void onDestroy(){
        super.onDestroy();
        LoadSaveController.saveUser();
        System.out.println("SP Destroy");
    }

    public void onStop(){
        super.onStop();
        LoadSaveController.saveUser();
        System.out.println("SP Stop");
    }
}

