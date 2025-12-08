package com.example.hanout_app.model;

public class User {

    private int id_User;
    private String name;
    private String email;
    private String password;
    private String phone;

//    public User( String name, String email, String password, String phone) {
////    this.id_User = id_User; hda tal db onsybo khdmto
//   this.name = name;
//    this.email = email;
//    this.password = password;
//    this.phone = phone;
//    }

    public int getId_User() {
        return id_User;
    }

    public void setId_User(int id_User) {
        this.id_User = id_User;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return this.email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPhone(){
        return this.phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public boolean isEmailValid(String email) {
        if (!email.contains("@")) {
            return false;
        }
        return email.contains(".");  //return the bool state if yes or no
    }
    public boolean isPasswordValid(String password) {
        return password.length() >= 8;  //return the bool state if yes or no
    }
    public boolean isPhoneValid(String phone) {
        return  phone.length() == 10;
    }

}
