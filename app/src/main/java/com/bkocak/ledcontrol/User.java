package com.bkocak.ledcontrol;

/**
 * Created by BurakCan on 13/07/2016.
 */
public class User {
    private String userName;
    private String pass;


    private User(){
    }


    User(String userName,String pass){
        this.userName=userName;
        this.pass=pass;
    }

    public String getUserName(){return userName;}
    public String getPass() {return pass;}


}
