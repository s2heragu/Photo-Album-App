package com.example.androidphotos01.adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidphotos01.R;
import com.example.androidphotos01.model.User;
import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.LoadSaveController;

import java.util.List;

public class OtherAlbumAdapter extends ArrayAdapter<Album> {

    private int resource;
    private Context context;
    private User user;

    public OtherAlbumAdapter(Context context, int resource, List<Album>albums,User user) {
        super(context, resource, albums);
        this.user = user;
        this.resource = resource;
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(context);
            view = inflater.inflate(resource, null);
        }

        Album a = (Album) getItem(position);

        if (a != null) {
            TextView tt1 = (TextView) view.findViewById(R.id.album_name);

            if (tt1 != null) {
                tt1.setText(a.toString());
            }
        }

        return view;
    }

}

