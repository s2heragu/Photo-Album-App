package com.example.androidphotos01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.androidphotos01.dialogs.ErrorDialogFragment;
import com.example.androidphotos01.dialogs.TextInputDialogFragment;
import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.model.Tag;
import com.example.androidphotos01.model.User;
import com.example.androidphotos01.adapters.RealAlbumAdapter;

import java.util.ArrayList;

//ALBUM LIST SCREEN
public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private User user;

    private TextView tag1name;
    private TextView tag2name;
    private Switch orAndSwitch;
    private Spinner tag1Name;
    private Spinner tag2Name;
    private Spinner conj;
    private EditText tag1Value;
    private EditText tag2Value;
    private ArrayList<String> tagNameChoices = new ArrayList<>();
    private ArrayList<String> conjChoices = new ArrayList<>();
    private boolean oneTag;
    private boolean or;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list_screen);

        if(!LoadSaveController.isFileEmpty()){
            LoadSaveController.getUser();
        }

        user = LoadSaveController.user();
        tag1name = (TextView) findViewById(R.id.tag1Name);
        tag2name = (TextView) findViewById(R.id.tag2Name);
        tag1Name = (Spinner) findViewById(R.id.tag1NameChoices);
        tag2Name = (Spinner) findViewById(R.id.tag2NameChoices);


        tag1Name.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("clicked the spinner choice box");
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(
                        (null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        tag2Name.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("clicked the spinner choice box");
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(
                        (null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });

        conj = (Spinner) findViewById(R.id.conjChoices);

        conj.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("clicked the spinner choice box");
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(
                        (null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });

        tag1Value = (EditText) findViewById(R.id.tag1Value);
        tag2Value = (EditText) findViewById(R.id.tag2Value);
        tagNameChoices.add("location");
        tagNameChoices.add("person");
        conjChoices.add("N/A");
        conjChoices.add("OR");
        conjChoices.add("AND");
        tag1Name.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tagNameChoices));
        tag2Name.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tagNameChoices));
        conj.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, conjChoices));
        conj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 0){
                    tag2Name.setVisibility(View.GONE);
                    tag2name.setVisibility(View.GONE);
                    tag2Value.setVisibility(View.GONE);
                    tag2Name.setSelection(0);
                    tag2Value.getText().clear();
                    oneTag = true;
                    or = true;
                }
                else{
                    if(position == 1){
                        or = true;
                    }
                    else{
                        or = false;
                    }
                    tag2Name.setVisibility(View.VISIBLE);
                    tag2name.setVisibility(View.VISIBLE);
                    tag2Value.setVisibility(View.VISIBLE);
                    oneTag = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing
            }

        });
        tag1Name.setSelection(0);
        tag2Name.setSelection(0);
        conj.setSelection(0);


        listView = (ListView) findViewById(R.id.album_list);
        listView.setAdapter(new RealAlbumAdapter(this, R.layout.view_album_list, user.albums(),user));

        listView.setOnItemClickListener(
                (AdapterView<?> parent, View view, int position, long id) -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    Album a = user.getAlbum(position);
                    builder.setTitle("Perform action on album '"+a.name()+"':");
                    builder.setPositiveButton("Rename", (dialog, which) -> {
                        albumName(position,"Enter new name for album '" + a.name()+"':",false).show();
                    });
                    builder.setNeutralButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    });
                    builder.setNegativeButton("Open", (dialog, which) -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", position);

                        Intent intent = new Intent(this, PhotoListScreenActivity.class);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    });
                    builder.show();
                }
        );


    }

    public void addAlbum(View view){
        albumName(1, "Enter new album name:",true).show();
    }

    public void searchPhotos(View view){
        String tag1N = ((String) tag1Name.getSelectedItem()).trim();
        String tag1V = tag1Value.getText().toString().trim();;
        Tag tag1 = null;
        Tag tag2 = null;
        if(oneTag){
            if(tag1V.isEmpty()){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Tag1 value cannot be empty.");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }
        }
        else{
            String tag2N = ((String) tag2Name.getSelectedItem()).trim();
            String tag2V = tag2Value.getText().toString().trim();
            if(tag1V.isEmpty() || tag2V.isEmpty()){
                Bundle bundle = new Bundle();
                bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Tag values cannot be empty.");
                DialogFragment fragment = new ErrorDialogFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "asdf");
                return;
            }
            tag2 = new Tag(tag2N, tag2V);
        }
        tag1 = new Tag(tag1N, tag1V);
        user.getPhotos(tag1, tag2, or, oneTag);
        if(user.searchedPhotos().isEmpty()){
            Bundle bundle = new Bundle();
            bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"No photos matched the search.");
            DialogFragment fragment = new ErrorDialogFragment();
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), "asdf");
            return;
        }
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, SearchedPhotosActivity.class);
        startActivity(intent);

    }

    public void onDestroy(){
        super.onDestroy();
        LoadSaveController.saveUser();
        System.out.println("MA Destroy");
    }

    public void onStop(){
        super.onStop();
        LoadSaveController.saveUser();
        System.out.println("MA Stop");
    }

    private AlertDialog.Builder albumName(int position, String title, boolean add){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        if(add){
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newAlbumName = input.getText().toString().trim();
                    if(newAlbumName.equalsIgnoreCase("Search Results")){
                        Bundle bundle = new Bundle();
                        bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Album name '" + newAlbumName+"' is illegal.");
                        DialogFragment fragment = new ErrorDialogFragment();
                        fragment.setArguments(bundle);
                        fragment.show(getSupportFragmentManager(), "asdf");
                        return;
                    }
                    if(newAlbumName.isEmpty()){
                        Bundle bundle = new Bundle();
                        bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Album name cannot be empty.");
                        DialogFragment fragment = new ErrorDialogFragment();
                        fragment.setArguments(bundle);
                        fragment.show(getSupportFragmentManager(), "asdf");
                        return;
                    }
                    Album newAlbum = new Album(newAlbumName);
                    int ind = user.addAlbum(newAlbum);
                    if(ind == -1){
                        Bundle bundle = new Bundle();
                        bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Album '" + newAlbumName + "' already exists.");
                        DialogFragment fragment = new ErrorDialogFragment();
                        fragment.setArguments(bundle);
                        fragment.show(getSupportFragmentManager(), "asdf");
                        return;
                    }
                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                }
            });
        }
        else{
            builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newAlbumName = input.getText().toString().trim();
                    if(newAlbumName.equalsIgnoreCase("Search Results")){
                        Bundle bundle = new Bundle();
                        bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Album name '" + newAlbumName+"' is illegal.");
                        DialogFragment fragment = new ErrorDialogFragment();
                        fragment.setArguments(bundle);
                        fragment.show(getSupportFragmentManager(), "asdf");
                        return;
                    }

                    if(newAlbumName.isEmpty()){
                        Bundle bundle = new Bundle();
                        bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Album name cannot be empty.");
                        DialogFragment fragment = new ErrorDialogFragment();
                        fragment.setArguments(bundle);
                        fragment.show(getSupportFragmentManager(), "asdf");
                        return;
                    }
                    int ind = user.renameAlbum(position, newAlbumName);
                    if(ind == -1){
                        Bundle bundle = new Bundle();
                        bundle.putString(ErrorDialogFragment.MESSAGE_KEY,"Album '" + newAlbumName + "' already exists.");
                        DialogFragment fragment = new ErrorDialogFragment();
                        fragment.setArguments(bundle);
                        fragment.show(getSupportFragmentManager(), "asdf");
                        return;
                    }

                    ((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
                }
            });
        }
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder;
    }


}