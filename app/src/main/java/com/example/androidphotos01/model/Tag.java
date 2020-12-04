package com.example.androidphotos01.model;

import java.io.Serializable;

public class Tag implements Comparable<Tag>, Serializable {

    private String name;    //name of tag
    private String value;   //value of tag
    static final long serialVersionUID = 1L;

    //CONSTRUCTOR

    public Tag(String name, String value){
        this.name = name;
        this.value = value;
    }


    //GETTERS

    //Returns tag value
    public String name(){
        return this.name;
    }
    //Returns tag name
    public String value(){
        return this.value;
    }


    //MISC

    //toString method
    public String toString() {
        return this.name + ": " + this.value;
    }

    //Implements compareTo
    public int compareTo(Tag t){
        if(this.name.equals(t.name)){
            return this.value.compareTo(t.value);
        }
        return this.name.compareTo(t.name);
    }


}

