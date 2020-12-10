package com.example.androidphotos01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.graphics.BitmapFactory;

import com.example.androidphotos01.adapters.PhotoAdapter;
import com.example.androidphotos01.adapters.RealAlbumAdapter;
import com.example.androidphotos01.adapters.TagAdapter;
import com.example.androidphotos01.dialogs.ErrorDialogFragment;
import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.model.Photo;
import com.example.androidphotos01.model.Tag;
import com.example.androidphotos01.model.User;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

//By Shreyas Heragu and Jonathan Wong

public class PhotoSlideshowActivity extends AppCompatActivity {

    private User user;
    private Album album;
    private ImageView photoDisplay;
    private ListView tagListView;
    private int selectedPhotoIndex;
    private int albumIndex;
    private TextView albumNameLabel;
    private ArrayList<Tag> tags;
    private ArrayList<Photo> foundPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_slideshow);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.photoDisplay = (ImageView) findViewById(R.id.photo_image_view);
        this.tagListView = (ListView) findViewById(R.id.tag_list_view);
        Bundle bundle = getIntent().getExtras();
        this.user = LoadSaveController.user();
        this.albumIndex = bundle.getInt("albumIndex");
        this.selectedPhotoIndex = bundle.getInt("photoIndex");
        this.albumNameLabel = (TextView) findViewById(R.id.slideshow_album_name);

        //Searched photos, no album
        if(albumIndex == -1){
            this.foundPhotos = user.searchedPhotos();
            this.album = null;
        }
        //Photos come from album
        else{
            this.album = user.getAlbum(albumIndex);
        }

        Photo currPhoto = null;
        if(this.album != null){
            //Set photo image
            currPhoto = album.photos().get(selectedPhotoIndex);
        }
        else{
            currPhoto = this.foundPhotos.get(selectedPhotoIndex);
        }


        setImage(currPhoto);
        /*this.tags = currPhoto.getTags();


        //set tag list view TODO
        this.tagListView.setAdapter(new TagAdapter(this,
                R.layout.view_album_list,
                tags,
                currPhoto));*/
    }
    //Sets photo and its tags
    private void setImage(Photo p){

        //Retrieve activity's constraint layout
        ConstraintLayout cL = (ConstraintLayout)findViewById(R.id.slideLayout);
        String name = p.fileName().substring(p.fileName().lastIndexOf(File.separator)+1);
        this.albumNameLabel.setText(name);

        String fileString = p.fileDir();
        Uri uri = Uri.parse(fileString);
        Bitmap bitmapImage = this.loadFromUri(uri);
        this.photoDisplay.setImageBitmap(bitmapImage);

        this.tags = p.getTags();
        //Set tag list view as well
        this.tagListView.setAdapter(new TagAdapter(this,
                R.layout.view_album_list,
                this.tags,
                p));
        ((BaseAdapter)tagListView.getAdapter()).registerDataSetObserver(new DataSetObserver(){
            @Override
            public void onChanged()
            {
                LoadSaveController.saveUser();
            }
        });
    }
    private Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), photoUri);
            image = ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    public void previousPhotoClicked(View view){
        System.out.println("go to prev photo");
        //No previous
        if(selectedPhotoIndex == 0){
            Bundle bundle = new Bundle();
            bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"No previous photo.");
            DialogFragment fragment = new ErrorDialogFragment();
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), "asdf");
            return;
        }
        this.selectedPhotoIndex--;
        Photo p = null;
        if(this.album != null){
            p = this.album.photos().get(selectedPhotoIndex);
        }
        else{
            p = this.foundPhotos.get(selectedPhotoIndex);
        }
        setImage(p);
    }
    public void nextPhotoClicked(View view){
        System.out.println("go to next photo");
        //No next photo
        if(album != null){
            if(selectedPhotoIndex == this.album.photos().size() - 1){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"No next photo.");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }
        }
        else{
            if(selectedPhotoIndex == this.foundPhotos.size() - 1){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"No next photo.");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }
        }

        this.selectedPhotoIndex++;
        Photo p = null;
        if(this.album != null){
            p = this.album.photos().get(selectedPhotoIndex);
        }
        else{
            p = this.foundPhotos.get(selectedPhotoIndex);
        }
        setImage(p);
    }
    public void addTagClicked(View view){
        System.out.println("add tag popup");
        Photo p = null;
        if(this.album != null){
            p = this.album.photos().get(selectedPhotoIndex);
        }
        else{
            p = this.foundPhotos.get(selectedPhotoIndex);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a tag");
        EditText input = new EditText(this);
        builder.setMessage("Choose a tag to be person/location and enter its value");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        Photo finalP = p;
        builder.setPositiveButton("Add Person Tag", (dialog, which) -> {
            String val = input.getText().toString().trim();
            //No empty tags allowed
            if(val.isEmpty()){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Empty tag is not allowed");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }
            Tag t = new Tag("person", val);
            if(!finalP.addTag(t)){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Duplicate tag already exists");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }
            LoadSaveController.sortedInsert(this.tags, t);
            ((BaseAdapter)this.tagListView.getAdapter()).notifyDataSetChanged();
            /*this.tagListView.setAdapter(new TagAdapter(this,
                    R.layout.view_album_list,
                    p.getTags(),
                    p));*/

        });
        builder.setNeutralButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            return;
        });
        builder.setNegativeButton("Add Location Tag", (dialog, which) -> {
            String val = input.getText().toString().trim();
            //No empty tags allowed
            if(val.isEmpty()){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Empty tag is not allowed");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }
            Tag t = new Tag("location", val);
            if(!finalP.addTag(t)){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Duplicate tag");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }
            LoadSaveController.sortedInsert(this.tags, t);
            ((BaseAdapter)this.tagListView.getAdapter()).notifyDataSetChanged();
            /*this.tagListView.setAdapter(new TagAdapter(this,
                    R.layout.view_album_list,
                    p.getTags(),
                    p));*/

        });
        builder.show();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        //Go to searched photos screen
        if(this.album == null){
            Intent i = new Intent(getApplicationContext(), SearchedPhotosActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("photos", this.foundPhotos);
            i.putExtras(b);
            startActivityForResult(i, 0);
            return true;
        }
        //Go to regular photo list screen
        Intent intent = new Intent(getApplicationContext(), PhotoListScreenActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("index", this.albumIndex);
        intent.putExtras(bundle);

        startActivityForResult(intent, 0);
        return true;
    }
}
