package com.example.androidphotos01;

import com.example.androidphotos01.model.Album;
import com.example.androidphotos01.model.User;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

//Class for serializing
public class LoadSaveController {
    //file path serialize to
    public static final String filePath = "/src/main/java/com.example.androidphotos01/user.dat";

    private static User user = new User();

    public static User user(){
        return user;
    }

    public static void saveUser(){
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filePath));
            oos.writeObject(user);
            oos.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    //Checks if user.dat is empty
    public static boolean isFileEmpty(){
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            try {
                if(br.readLine() == null){
                    return false;
                }
                else{
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    //Deserialize user to get list of albums
    public static void getUser(){
        try {
            //Just use the new user already implemented
            if(isFileEmpty()){
                return;
            }
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
            try {
                user = (User) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int selectNew(int newIndex, int prevIndex, boolean isAdd, int lengthBeforeAction) {
        if(isAdd) {
            return newIndex;
        }
        //Deleted an item
        else {
            int newLength = lengthBeforeAction - 1;
            //Deleted all items
            if(newLength <= 0) {
                return -1;
            }
            //Deleted last item
            else if(prevIndex == newLength) {
                return prevIndex - 1;
            }
            else {
                return prevIndex;
            }
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

