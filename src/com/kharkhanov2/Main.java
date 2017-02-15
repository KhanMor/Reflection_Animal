package com.kharkhanov2;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        //People john = new People("John", 20, 20.0);

        XMLSerializer xmlSerializer = new XMLSerializer();
        /*xmlSerializer.serializeToXML(john,"john.xml");

        List<Object> objects = xmlSerializer.deserializeFromXML("john.xml");
        System.out.println("Objects deserialized: ");
        for (Object object:objects) {
            System.out.println(object);
        }

        Animal animal = new Animal("Змея");
        animal.whoAmI();
        xmlSerializer.serializeToXML(animal, "animal.xml");
        */
        List<Object> objects = xmlSerializer.deserializeFromXML("animal.xml");
        System.out.println(objects.size() + " Animals deserialized: ");
        for (Object object:objects) {
            System.out.println(object);
        }
    }
}
