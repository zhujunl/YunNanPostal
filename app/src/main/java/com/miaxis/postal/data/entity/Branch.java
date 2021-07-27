package com.miaxis.postal.data.entity;


public class Branch {

    public String id;
    public String name;

    public Branch() {
    }

    public Branch(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}
