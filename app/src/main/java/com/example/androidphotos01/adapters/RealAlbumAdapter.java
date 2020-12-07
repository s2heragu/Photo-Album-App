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

public class RealAlbumAdapter extends ArrayAdapter<Album> {

    private int resource;
    private Context context;
    private User user;

    public RealAlbumAdapter(Context context, int resource, List<Album>albums,User user) {
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

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(resource, null);
        }

        Album a = (Album) getItem(position);

        if (a != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.album_name);
            ImageView del = (ImageView) v.findViewById(R.id.image_delete);

            if (tt1 != null) {
                tt1.setText(a.toString());
            }

            if(del != null){
                del.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you want to delete album '"+a.toString()+"'?");
                        builder.setPositiveButton("YES", (dialog, id) -> {
                            //Perform actual delete here
                            user.deleteAlbum(position);
                            notifyDataSetChanged();
                        });
                        builder.setNegativeButton("NO", (dialog, id) -> {
                            dialog.cancel();
                            return;
                        });
                        builder.show();
                    }
                });
            }
        }

        return v;
    }

}

