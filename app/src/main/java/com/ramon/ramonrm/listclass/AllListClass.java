package com.ramon.ramonrm.listclass;

import android.net.Uri;

import java.net.URI;

public class AllListClass {
    private String txt1;
    private String txt2;
    private String txt3;
    private String txt4;
    private String txt5;
    private String txt6;
    private String txt7;
    private String txt8;
    private String txt9;
    private Boolean bolean1;
    private int int1;
    private Uri uri1;

    public AllListClass(Uri uri){
        this.uri1=uri;
        txt1="";
        txt2="";
        txt3="";
        txt4="";
        txt5="";
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(int int1){
        this.int1=int1;
        txt1="";
        txt2="";
        txt3="";
        txt4="";
        txt5="";
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(String txt1){
        this.txt1=txt1;
        txt2="";
        txt3="";
        txt4="";
        txt5="";
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(String txt1, String txt2){
        this.txt1=txt1;
        this.txt2=txt2;
        txt3="";
        txt4="";
        txt5="";
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(String txt1, String txt2,String txt3,int int1){
        this.txt1=txt1;
        this.txt2=txt2;
        this.txt3=txt3;
        this.int1=int1;
        txt4="";
        txt5="";
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(String txt1, String txt2,int int1){
        this.txt1=txt1;
        this.txt2=txt2;
        this.int1=int1;
        txt3="";
        txt4="";
        txt5="";
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(String txt1, String txt2, String txt3, String txt4 , String txt5, String txt6, String txt7, String txt8){
        this.txt1=txt1;
        this.txt2=txt2;
        this.txt3=txt3;
        this.txt4=txt4;
        this.txt5=txt5;
        this.txt6=txt6;
        this.txt7=txt7;
        this.txt8=txt8;
    }

    public AllListClass(String txt1, String txt2, String txt3, String txt4 , String txt5, String txt6, String txt7, String txt8 , String txt9){
        this.txt1=txt1;
        this.txt2=txt2;
        this.txt3=txt3;
        this.txt4=txt4;
        this.txt5=txt5;
        this.txt6=txt6;
        this.txt7=txt7;
        this.txt8=txt8;
        this.txt9=txt9;
    }

    public AllListClass(String txt1, String txt2, String txt3){
        this.txt1=txt1;
        this.txt2=txt2;
        this.txt3=txt3;
        txt4="";
        txt5="";
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(String txt1, String txt2, Boolean boolean1){
        this.txt1=txt1;
        this.txt2=txt2;
        this.bolean1=boolean1;
        txt3="";
        txt4="";
        txt5="";
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(String txt1, String txt2, String txt3, String txt4, String txt5){
        this.txt1=txt1;
        this.txt2=txt2;
        this.txt3=txt3;
        this.txt4=txt4;
        this.txt5=txt5;
        txt6="";
        txt7="";
        txt8="";
        txt9="";
    }

    public AllListClass(String txt1, String txt2, String txt3, String txt4, String txt5, String txt6, String txt7){
        this.txt1=txt1;
        this.txt2=txt2;
        this.txt3=txt3;
        this.txt4=txt4;
        this.txt5=txt5;
        this.txt6=txt6;
        this.txt7=txt7;
        txt8="";
        txt9="";
    }
    public AllListClass(String txt1, String txt2, String txt3, String txt4, String txt5, String txt6){
        this.txt1=txt1;
        this.txt2=txt2;
        this.txt3=txt3;
        this.txt4=txt4;
        this.txt5=txt5;
        this.txt6=txt6;
        txt7="";
        txt8="";
        txt9="";
    }
    public String getTxt1(){
        return txt1;
    }

    public String getTxt2(){
        return txt2;
    }

    public String getTxt3(){
        return txt3;
    }

    public String getTxt4(){
        return txt4;
    }

    public String getTxt5(){
        return txt5;
    }

    public String getTxt6(){
        return txt6;
    }

    public String getTxt7(){
        return txt7;
    }

    public String getTxt8(){
        return txt8;
    }

    public String getTxt9(){
        return txt9;
    }

    public Boolean getBolean1(){
        return bolean1;
    }

    public int getInt1() {
        return int1;
    }

    public Uri getUri() {
        return uri1;
    }
}
