package com.example.androidphotos01.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.androidphotos01.LoadSaveController;
import com.example.androidphotos01.PhotoSlideshowActivity;
import com.example.androidphotos01.R;
import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.model.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;


public class PhotoAdapter extends ArrayAdapter<Photo> {
    private Context mContext;
    private Album album;
    private int resourceLayout;

    public PhotoAdapter(@NonNull Context context, int resource, List<Photo> photos, Album album) {
        super(context, resource, photos);
        this.mContext = context;
        this.album = album;
        this.resourceLayout = resource;
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = view;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        //Getting photo and thumbnail references
        ImageView thumb = v.findViewById(R.id.thumbnail);
        //Photo p = (Photo) getItem(position);

        //Retrieve view's relativeLayout
        RelativeLayout rL = (RelativeLayout)v.findViewById(R.id.imgLayout);
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            if(checkPermission()){
                //get photo
                Photo p = (Photo) getItem(position);
                String fileString = p.fileDir();



                Uri uri = Uri.parse(fileString);
                Bitmap bitmapImage = this.loadFromUri(uri);
                ImageView thumbnail = null;

                System.out.println("adding photo printing URI STrING");
                System.out.println("THE GRIDVIEW URI STRING: " + uri.toString());

                System.out.println("trying to get view");


                if(bitmapImage != null){
                    System.out.println("got bit map image");
                    thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
                    System.out.println(thumbnail.toString());
                    thumbnail.setImageBitmap(bitmapImage);
                }
                else{
                    System.out.println("did not get bit map image");
                }
            }
        }

        //Using viewTreeObserver to know when layout is generated: avoids NullPointer
        /*ViewTreeObserver vto = rL.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rL.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int width  = rL.getMeasuredWidth();
                int height = rL.getMeasuredHeight();
                try {
                    //Note that thumbnail is as big as relative layout.
                    thumb.setImageBitmap(mContext.getContentResolver().loadThumbnail(Uri.parse(p.fileDir()),new Size(width,height),null));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });*/

        return v;
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this.mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            ImageDecoder.Source source = ImageDecoder.createSource(this.mContext.getContentResolver(), photoUri);
            image = ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}

