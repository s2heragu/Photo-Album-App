package com.example.androidphotos01;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.model.Photo;
import com.example.androidphotos01.model.User;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;

import java.util.UUID;


//Class for serializing
public class LoadSaveController {
    //file path serialize to
    private static boolean b = false;

    private static String path = "";

    private static User user = new User();

    public static User user(){
        return user;
    }

    public static void setUser(User User){
        user = User;
    }

    public static boolean started(){
        return b;
    }

    public static void start(){
        b = true;
    }

    public static String path(){
        return path;
    }

    public static void setPath(String Path){
        path = Path;
    }

    public static void saveUser(){
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
            ObjectOutputStream os = new ObjectOutputStream(bos);
            os.writeObject(user);
            os.flush();
            os.close();
        }
        catch (Exception e) {
            System.out.println("OOOOOOOOOOOOOOOOF");
        }
    }

    //Checks if user.dat is empty
    public static void isFileEmpty(Activity activity){
        try {
            File file = new File(activity.getFilesDir(), "user.dat");
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                if(br.readLine() == null){

                }
                else{
                    b = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
    }

    //Binary search of a generic T object, returns object
    public static <T extends Comparable<T>> T binSearch(ArrayList<T> items, T searched){
        int [] res = intBinSearch(items,searched);
        if(res[1] != 1){
            return null;
        }
        return items.get(res[0]);
    }

    //Binary search of a generic T object, returns array with index position, and found status
    public static <T extends Comparable<T>> int[] intBinSearch(ArrayList<T> items, T searched){
        int [] out = new int[2];
        if(items.isEmpty()){
            out[1] = 2;
            return out;
        }

        int left = 0;
        int right = items.size()-1;
        int mid = (left+right)/2;

        while(left < right){
            int comp = searched.compareTo(items.get(mid));
            if(comp == 0){
                out[0] = mid;
                out[1] = 1;
                return out;
            }
            else if(comp < 0){
                if(mid == left){
                    right = left;
                    break;
                }
                right = mid-1;
                mid = (left+right)/2;
            }
            else{
                if(mid == right){
                    left = right;
                    break;
                }
                left = mid+1;
                mid = (left+right)/2;
            }
        }

        if(left == right){
            out[0] = mid;
            if(searched.compareTo(items.get(mid))==0){
                out[1] = 1;
                return out;
            }
        }

        return out;
    }

    public static <T extends Comparable<T>> int sortedInsert(ArrayList<T>items, T toInsert){
        int [] res = intBinSearch(items,toInsert);
        if(res[1] == 1){
            return -1;
        }
        if(res[1] == 2){
            items.add(toInsert);
            return 0;
        }
        T toCompare = items.get(res[0]);
        int comp = toInsert.compareTo(toCompare);
        if(comp < 0){
            items.add(res[0],toInsert);
            return res[0];
        }
        if(res[0] == items.size()-1){
            items.add(toInsert);
            return items.size()-1;
        }
        items.add(res[0]+1,toInsert);
        return res[0]+1;
    }

    public static <T extends Comparable<T>> void sortedDelete(ArrayList<T>items, T toInsert){
        int [] res = intBinSearch(items,toInsert);
        if(res[1] != 1){
            return;
        }
        items.remove(res[0]);
    }

}

