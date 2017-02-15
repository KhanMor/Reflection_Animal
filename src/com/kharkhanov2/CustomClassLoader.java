package com.kharkhanov2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Mordr on 15.02.2017.
 */
public class CustomClassLoader extends ClassLoader{
    private String jarFileURL = "https://github.com/KhanMor/Reflection_Animal/raw/master/src/com/Animal.jar";
    private String jarFileLocal = "Animal.jar";
    private Hashtable classes = new Hashtable(); //used to cache already defined classes

    public CustomClassLoader() {
        super(CustomClassLoader.class.getClassLoader()); //calls the parent class loader's constructor
    }


    public Class loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    public Class findClass(String className) {
        System.out.println("entered to loadClass");
        Class result = null;

        result = (Class) classes.get(className); //checks in cached classes
        if (result != null) {
            System.out.println(classes);
            return result;
        }

        try {
            return findSystemClass(className);
        } catch (Exception e) {
        }

        try {
            try(
                    InputStream is = new URL(jarFileURL).openStream();
                    FileOutputStream fos = new FileOutputStream(new File(jarFileLocal));
            ) {
                int inByte;
                while((inByte = is.read()) != -1)
                    fos.write(inByte);
            }

            JarFile jar = new JarFile(jarFileLocal);
            System.out.println("className = " + className);
            JarEntry entry = jar.getJarEntry(className + ".class");
            InputStream is = jar.getInputStream(entry);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int nextValue = is.read();
            while (-1 != nextValue) {
                byteStream.write(nextValue);
                nextValue = is.read();
            }

            byte classByte[] = byteStream.toByteArray();
            result = defineClass(className, classByte, 0, classByte.length, null);
            classes.put(className, result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
