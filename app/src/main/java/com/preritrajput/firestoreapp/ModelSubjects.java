package com.preritrajput.firestoreapp;

public class ModelSubjects {
    String semester,uid,email,facultyName,subjectName,courseCode,timeStamp,name,phone;

    public ModelSubjects() {
    }

    public ModelSubjects(String semester, String uid, String email, String facultyName, String subjectName, String courseCode, String timeStamp,String name,String phone) {
        this.semester = semester;
        this.uid = uid;
        this.email = email;
        this.facultyName = facultyName;
        this.subjectName = subjectName;
        this.courseCode = courseCode;
        this.timeStamp = timeStamp;
        this.name = name;
        this.phone = phone;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
