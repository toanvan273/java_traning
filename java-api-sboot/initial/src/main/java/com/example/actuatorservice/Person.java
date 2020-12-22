package com.example.actuatorservice;

public class Person{
    private final long IDP;
    private final String fullName;
    private final String birthDay;

    public Person(long IDP, String fullName, String birthDay) {
        this.IDP = IDP;
        this.fullName = fullName;
        this.birthDay = birthDay;
    }

    public long getIDP() {
        return IDP;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBirthDay() {
        return birthDay;
    }
}