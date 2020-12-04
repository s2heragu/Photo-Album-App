package com.example.androidphotos01.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.androidphotos01.R;
import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.model.Photo;

import java.io.IOException;
import java.util.List;


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

        //get photo
        Photo p = (Photo) getItem(position);
        String fileString = p.fileDir();
        Uri uri = Uri.parse(fileString);
        Bitmap bitmapImage = this.loadFromUri(uri);
        ImageView thumbnail = null;

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

        //Get image here and do set bitmap image, return to grid view to display


        //Do this in the activity?
        /*
        if(thumbnail != null){
            thumbnail.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Choose Action");
                    builder.setMessage("Display or Delete this photo");
                    builder.setPositiveButton("Display", (dialog, id) -> {

                    });
                    builder.setNegativeButton("Delete", (dialog, id) -> {

                    });
                    builder.setNegativeButton("Cancel", (dialog, id) -> {
                        dialog.cancel();
                        return;
                    });
                    builder.show();
                }
            });
        }
        */



        return v;
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

