package com.kharkhanov;

/**
 * Created by Mordr on 15.02.2017.
 */
public class Animal {
    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void whoAmI(){
        System.out.println("I am " + this.name);
    }
}
