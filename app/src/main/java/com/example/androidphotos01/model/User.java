package com.example.androidphotos01.model;

import com.example.androidphotos01.LoadSaveController;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private ArrayList<Album>albums; //List of albums
    private ArrayList<Photo>photos; //List of unique photos
    private ArrayList<Photo>searched; //List of currently searched photos
    static final long serialVersionUID = 1L;

    //CONSTRUCTOR

    public User(){
        this.photos = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.searched = new ArrayList<>();
    }

    //ALBUM GETTERS

    //Returns album list
    public ArrayList<Album> albums(){
        return this.albums;
    }
    //Returns album given index
    public Album getAlbum(int index){
        return this.albums.get(index);
    }
    //Returns number of albums
    public int numAlbums(){
        return this.albums.size();
    }


    //UNIQUE PHOTO GETTERS

    //Returns unique photo list
    public ArrayList<Photo> photos() {
        return this.photos;
    }
    //Returns number of unique photos
    public int numPhotos(){
        return this.photos.size();
    }
    //Return searched photo list
    public ArrayList<Photo> searchedPhotos() {return this.searched;}


    //PHOTO MOVE
    public int movePhotoToAlbum(int photoIndex, Album previous, Album current){
        Photo p = previous.get(photoIndex);
        int toReturn = addPhoto(p,current);
        if(toReturn != -1){
            deletePhoto(photoIndex,previous);
        }
        return toReturn;
    }


    //PHOTO SEARCHER

    //Returns photos from a tag search
    public void getPhotos(Tag t1, Tag t2, boolean or, boolean oneTag) {
        this.searched = new ArrayList<Photo>();
        for(Photo p: this.photos){
            if(p.fitsFilter(t1,t2,or,oneTag)){
                this.searched.add(p);
            }
        }
    }



    //ALBUM MANAGING

    //Attempts to add photo album and returns index position (-1) if add is invalid
    public int addAlbum(Album a){
        return LoadSaveController.sortedInsert(this.albums,a);
    }
    //Attempts to rename album and reorder it in the list
    public int renameAlbum(int index, String newName){
        Album temp = new Album(newName);
        if(LoadSaveController.binSearch(this.albums,temp)==null){
            Album toEdit = this.albums.get(index);
            LoadSaveController.sortedDelete(this.albums,toEdit);
            for(Photo p: toEdit.photos()){
                p.removeAlbum(toEdit);
            }
            toEdit.rename(newName);
            for(Photo p: toEdit.photos()){
                p.addAlbum(toEdit);
            }
            return LoadSaveController.sortedInsert(this.albums,toEdit);
        }
        return -1;
    }
    //Deletes album given index position; assumes valid deletion
    public void deleteAlbum(int index){
        Album toRemove = this.albums.remove(index);
        for(Photo p: toRemove.photos()){
            p.removeAlbum(toRemove);
            if(p.noAlbums()){
                int [] res = LoadSaveController.intBinSearch(this.photos,p);
                this.photos.remove(res[0]);
            }
        }
    }


    //PHOTO MANAGING

    //Add photo p to album a
    public int addPhoto(Photo p, Album a){
        Photo p1 = LoadSaveController.binSearch(this.photos,p);
        if(p1 == null){
            this.addUniquePhoto(p);
        }
        else{
            p = p1;
        }
        return a.addPhoto(p);
    }
    //Delete photo from album a given index
    public void deletePhoto(int index, Album a){
        Photo p = a.deletePhoto(index);
        p.removeAlbum(a);
        if(p.noAlbums()){
            LoadSaveController.sortedDelete(this.photos,p);
        }
    }


    //PRIVATE METHODS

    //Adds unique photos to general photo list
    private void addUniquePhoto(Photo p){
        LoadSaveController.sortedInsert(this.photos,p);
    }
}

