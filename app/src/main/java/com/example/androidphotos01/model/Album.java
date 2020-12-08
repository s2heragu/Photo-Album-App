package com.example.androidphotos01.model;

import com.example.androidphotos01.LoadSaveController;
import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Comparable<Album>, Serializable {

    private String name;
    private ArrayList<Photo>photos;
    static final long serialVersionUID = 1L;

    //CONSTRUCTOR

    public Album(String name){
        this.name = name;
        this.photos = new ArrayList<>();
    }


    //NAME GETTERS AND SETTERS

    //Returns album name
    public String name(){
        return this.name;
    }
    //Renames album with input name
    public void rename(String newName) {
        this.name = newName;
    }


    //PHOTOS GETTERS AND SETTERS

    //Returns album photos
    public ArrayList<Photo> photos(){
        return this.photos;
    }
    //Gets photo given index
    public Photo get(int index){
        return this.photos.get(index);
    }
    //Returns number of photos in album
    public int size(){
        return this.photos.size();
    }



    //ADDING AND DELETING PHOTOS

    //Adds photo, and returns index of photo's position (-1 if invalid add)
    public int addPhoto(Photo p){
        p.addAlbum(this);
        return LoadSaveController.sortedInsert(this.photos,p);
    }
    //Deletes photo from list given index, assumes valid deletion
    public Photo deletePhoto(int index){
        return this.photos.remove(index);
    }

    public Photo getPhoto(int index){
        return this.photos.get(index);
    }


    //MISC

    //toString method
    public String toString(){
        return this.name;
    }
    //compareTo method
    public int compareTo(Album a){
        if(this.name.compareToIgnoreCase(a.name)==0){
            return this.name.compareTo(a.name);
        }
        return this.name.compareToIgnoreCase(a.name);
    }
}

