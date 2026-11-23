package com.kpfl.model;

import com.kpfl.exception.KPFLValidationException;
import java.io.Serializable;

public abstract class Person implements Serializable {
    private static final long serialVerionUID = 1L;

    private String id;
    private String name;
    private int age;
    private String gender;


    public Person(String id, String name, int age, String gender) throws KPFLValidationException{
        setID(id);
        setName(name);
        setAge(age);
        setGender(gender);
    }

    //Abstract methods to enforce polymorphism across all subclass
    public abstract String getRole();
    public abstract String getSummaryDetails();

    //Getters 
    public String getID() {
        return id;
    }

    public void setID(String id) throws KPFLValidationException {
        if (id == null || id.trim().isEmpty()) {
        throw new KPFLValidationException("Person ID cannot be empty.");
        } 
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws KPFLValidationException {
        if(name == null || name.trim().isEmpty()) {
            throw new KPFLValidationException("Name cannot be empty.");
        }
        this.name = name.trim();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) throws KPFLValidationException {
        if(age < 0 || age > 120) {
            throw new KPFLValidationException("Invalid Age provided: " + age);
        }
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) throws KPFLValidationException {
        if(gender == null || gender.trim().isEmpty()) {
            throw new KPFLValidationException("Gender cannot be empty.");
        }
        this.gender = gender.trim();
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Name: %s | Age: %d | Gender: %s | %s",
                getRole(), id, name, age, gender, getSummaryDetails());
    }
}
