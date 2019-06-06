package com.example.recipe.model;

public class User {



    private int user_id;
    private String fname;
    private String mname;
    private String lname;
    private String address;
    private String phone;
    private String email;
    private String image;
    private String password;



    public User(int user_id, String phone, String email, String password) {

        this.user_id=user_id;
        this.phone = phone;
        this.email = email;
        this.password=password;
    }

    public User(int user_id, String fname,String mname,String lname,String address, String phone, String email, String password,String image) {

        this.user_id=user_id;
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password=password;
        this.image=image;

    }



    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
