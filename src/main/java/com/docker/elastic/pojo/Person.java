package com.docker.elastic.pojo;


public class Person {
    String age ;
    String dateOfBirth ;
    String fullName ;

    public Person() {
    }

    public Person(String age, String dateOfBirth, String fullName) {
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.fullName = fullName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
