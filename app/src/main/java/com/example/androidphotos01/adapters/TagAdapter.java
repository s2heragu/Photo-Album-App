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
import com.example.androidphotos01.model.Photo;
import com.example.androidphotos01.model.Tag;
import com.example.androidphotos01.model.User;
import com.example.androidphotos01.model.Album;

import java.util.List;

//By Shreyas Heragu and Jonathan Wong
public class TagAdapter extends ArrayAdapter<Tag> {

    private int resource;
    private Context context;
    private Photo p;
    private List<Tag> tags;

    public TagAdapter(Context context, int resource, List<Tag> tags, Photo p) {
        super(context, resource, tags);
        this.p = p;
        this.tags = tags;
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

        Tag t = (Tag) getItem(position);


        if (t != null) {
            TextView tagName = (TextView) view.findViewById(R.id.album_name);
            ImageView del = (ImageView) view.findViewById(R.id.image_delete);

            if (tagName != null) {
                tagName.setText(t.toString());
            }

            if(del != null){
                del.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirm Delete");
                        builder.setMessage("Are you sure you want to delete tag '"+t.toString()+"'?");
                        builder.setPositiveButton("YES", (dialog, id) -> {
                            //Perform actual delete here

                            tags.remove(position);
                            p.deleteTag(t);

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

        return view;
    }

}

