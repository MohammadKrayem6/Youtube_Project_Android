package com.example.myapplication;

public class Owner {
    private String _id; // Change to String to match the received ObjectId as string
    private String name;
    private String email;
    private String password;
    private String picture;
    private int __v;

    // Getters and setters
    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getVersion() {
        return __v;
    }

    public void setVersion(int version) {
        this.__v = version;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", picture='" + picture + '\'' +
                ", version=" + __v +
                '}';
    }
}
