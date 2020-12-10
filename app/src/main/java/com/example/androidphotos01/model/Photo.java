package com.example.androidphotos01.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.androidphotos01.LoadSaveController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Photo implements Comparable<Photo>, Serializable {

    private String fileName;    //file name of photo
    private String fileDir;     //file path of photo
    private ArrayList<Album> albums;    //albums that photo is part of
    private HashMap<String,ArrayList<Tag>> tags;    //collection of photo tags arranged by type
    static final long serialVersionUID = 1L;

    //CONSTRUCTOR

    public Photo(String fileName, String fileDir){
        this.fileName = fileName;
        this.fileDir = fileDir;
        this.albums = new ArrayList<>();
        this.tags = new HashMap<>();
    }



    //GETTERS

    //Returns file name
    public String fileName(){
        return this.fileName;
    }
    //Returns file path
    public String fileDir(){
        return this.fileDir;
    }

    //SETTER

    //Sets file directory
    public void setFileDir(String uri){
        this.fileDir = uri;
    }



    //ADDING AND DELETING TAGS

    //Attempts to add a tag, and returns success of operation
    public boolean addTag(Tag t){
        if(this.containsTag(t)){
            return false;
        }
        if(this.tags.get(t.name())==null){
            this.tags.put(t.name(),new ArrayList<Tag>());
        }
        LoadSaveController.sortedInsert(this.tags.get(t.name()),t);
        return true;
    }
    //Deletes a tag, assuming valid deletion possible
    public void deleteTag(Tag t){
        ArrayList<Tag>retrieved = this.tags.get(t.name());
        LoadSaveController.sortedDelete(retrieved,t);
        if(retrieved.isEmpty()){
            this.tags.remove(t.name());
        }
    }


    //MANAGING ALBUMS

    //Adds an album to list of albums that photo belongs to
    public void addAlbum(Album a){
        LoadSaveController.sortedInsert(this.albums,a);
    }
    //Checks if photo is not associated with any albums
    public boolean noAlbums(){
        return this.albums.isEmpty();
    }
    //Disconnects photo from an album
    public void removeAlbum(Album a){
        LoadSaveController.sortedDelete(this.albums,a);
    }

    public int numAlbums(){
        return this.albums.size();
    }



    //SEARCHING

    //Checks if photo fits a tag filter
    public boolean fitsFilter(Tag t1, Tag t2, boolean or, boolean oneTag){
        if(oneTag){
            return this.containsTagCompletion(t1);
        }
        if(or){
            return this.containsTagCompletion(t1) || this.containsTagCompletion(t2);
        }
        return this.containsTagCompletion(t1) && this.containsTagCompletion(t2);
    }


    //RETRIEVAL

    //Returns list of tags
    public ArrayList<Tag> getTags(){
        ArrayList<Tag>out = new ArrayList<Tag>();
        TreeMap<String, ArrayList<Tag>> sortedTags = new TreeMap<>(this.tags);
        for (Entry<String, ArrayList<Tag>> mapElement : sortedTags.entrySet()) {
            ArrayList<Tag>curr = mapElement.getValue();
            out.addAll(curr);
        }
        return out;
    }


    //MISC

    //compareTo method
    public int compareTo(Photo p){
        if(this.fileName.compareToIgnoreCase(p.fileName)==0){
            return this.fileName.compareTo(p.fileName);
        }
        return this.fileName.compareToIgnoreCase(p.fileName);
    }


    //PRIVATE METHODS

    //Checks if photo contains a tag
    private boolean containsTag(Tag t){
        ArrayList<Tag>retrieved = this.tags.get(t.name());
        if(retrieved != null){
            if(LoadSaveController.binSearch(retrieved,t)!=null){
                return true;
            }
        }
        return false;
    }
    //Checks if photo contains a tag with the same type and a name completion
    private boolean containsTagCompletion(Tag t){
        ArrayList<Tag>retrieved = this.tags.get(t.name());
        if(retrieved != null){
            String ts = t.value();
            for(Tag t1: retrieved){
                String t1s = t1.value();
                if(t1s.startsWith(ts)){
                    return true;
                }
            }
        }
        return false;
    }



}

