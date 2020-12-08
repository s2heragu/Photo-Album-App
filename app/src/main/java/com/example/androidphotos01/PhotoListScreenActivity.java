package com.example.androidphotos01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Size;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidphotos01.adapters.PhotoAdapter;
import com.example.androidphotos01.dialogs.ErrorDialogFragment;
import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.model.Photo;
import com.example.androidphotos01.model.User;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoListScreenActivity extends AppCompatActivity {

    private GridView photoGridView;
    public final static int PICK_PHOTO_CODE = 1046;

    private Album album;
    private List<Photo> photos;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_list_screen);

        //Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView aName = (TextView)findViewById(R.id.album_name_txt_view);

        Intent other = this.getIntent();
        Bundle extras = other.getExtras();

        user = LoadSaveController.user();
        album = user.getAlbum(extras.getInt("index"));
        int albumIndex = extras.getInt("index");
        photos = album.photos();

        aName.setText(album.name());

        this.photoGridView = (GridView) findViewById(R.id.photo_grid_view);


        photoGridView.setAdapter(new PhotoAdapter(this, R.layout.view_photo_list, photos, album));
        ((BaseAdapter)photoGridView.getAdapter()).registerDataSetObserver(new DataSetObserver(){
            @Override
            public void onChanged()
            {
                //LoadSaveController.saveUser(MainActivity.this);
                PhotoListScreenActivity.this.SaveUser();
            }
        });

        photoGridView.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {
                    System.out.println("selected photo: " + position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Choose Action");
                    builder.setMessage("Display or Delete this photo");
                    builder.setPositiveButton("Display", (dialog, which) -> {
                        //Navigate to slide show screen
                        Bundle bundle = new Bundle();

                        bundle.putInt("albumIndex", albumIndex);
                        bundle.putInt("photoIndex", position);

                        Intent intent = new Intent(this, PhotoSlideshowActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    });
                    builder.setNeutralButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        return;
                    });
                    builder.setNegativeButton("Delete", (dialog, which) -> {
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(PhotoListScreenActivity.this);
                        deleteDialog.setTitle("Confirm Delete");
                        deleteDialog.setMessage("Are you sure you want to delete photo?");
                        deleteDialog.setPositiveButton("YES", (d, i) -> {
                            //Perform actual delete here
                            user.deletePhoto(position, this.album);
                            ((BaseAdapter) photoGridView.getAdapter()).notifyDataSetChanged();
                        });
                        deleteDialog.setNegativeButton("NO", (d, i) -> {
                            d.cancel();
                            return;
                        });
                        deleteDialog.show();
                    });
                    builder.show();
                }
        );



    }


    //File chooser for photo supposedly
    public void addPhotoClicked(View view){

        System.out.println("Trying to add photo");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If condition for choosing photo
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {

            //Getting the photoUri and filePath: Note, this is not the Uri we want
            Uri photoUri = data.getData();
            String filePath = "";
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();

            //Testing if we can add the photo: We do not need uri for this
            Photo newPhoto = new Photo(filePath, photoUri.toString());
            int Int = this.user.addPhoto(newPhoto, album);
            //Cannot add album, file chooser ends
            if(Int == -1){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Photo already exists in this album.");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }

            Photo p = this.album.get(Int);
            ((BaseAdapter) photoGridView.getAdapter()).notifyDataSetChanged();
            if(p.numAlbums()>1){
              SaveUser();
            }
            else{
                //Initializing file for scanning
                File f = new File(newPhoto.fileName());

                //Scanning for photo using filePath: this allows us to construct desired Uri
                MediaScannerConnection.scanFile(this,
                        new String[] { f.getAbsolutePath() }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                                //Hardcoding desired uri: generated uri gives us different path
                                String content = "content://media/external/images/media";
                                String initUriPath = uri.toString();
                                content += initUriPath.substring(initUriPath.lastIndexOf("/"));

                                //Updating desired uri for photo: uri remains constant so that we don't need to scan for this photo again
                                newPhoto.setFileDir(content);
                                PhotoListScreenActivity.this.SaveUser();

                            }
                        });
            }

            //Wait for the MediaScannerConnection thread to finish running
            /*Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Reset the photoGridView adapter for simplicity
                    PhotoListScreenActivity.this.SaveUser();
                }
            }, 500);*/
        }
    }


    private void hardReset(){
        PhotoListScreenActivity.this.SaveUser();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
        return true;
    }

    private void SaveUser(){
        try {

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(LoadSaveController.path()));
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(LoadSaveController.user());
            os.flush();
            os.close();
        }
        catch (Exception e) {
            System.out.println("OOOOOOOOOOOOOOOOF");
        }
    }

    /*public void onStop(){
        super.onStop();
        LoadSaveController.saveUser(this);
        System.out.println("PLS Stop");
    }

    public void onDestroy(){
        super.onDestroy();
        LoadSaveController.saveUser(this);
        System.out.println("PLS Destroy");
    }*/
}
