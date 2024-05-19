package com.example.notesapp;

public class firebasemodel {

    private String title;
    private String content;

    public firebasemodel(){

    }

    public firebasemodel (String title,String content){
//        initializing title content input by user
        this.title=title;
        this.content=content;
    }

    //    alt+insert getter and setter dono select krke implemet kiye
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}


