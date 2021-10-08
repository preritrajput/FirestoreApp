package com.preritrajput.firestoreapp;

public class ModelExperiments {
    String semester,uid,email,facultyName,subjectName,courseCode,expNo,aim,theory,outputCode,subTime,outputPic,timeStamp;

    public ModelExperiments() {
    }

    public ModelExperiments(String semester, String uid, String email, String facultyName, String subjectName, String courseCode, String expNo, String aim, String theory, String outputCode, String subTime, String outputPic, String timeStamp) {
        this.semester = semester;
        this.uid = uid;
        this.email = email;
        this.facultyName = facultyName;
        this.subjectName = subjectName;
        this.courseCode = courseCode;
        this.expNo = expNo;
        this.aim = aim;
        this.theory = theory;
        this.outputCode = outputCode;
        this.subTime = subTime;
        this.outputPic = outputPic;
        this.timeStamp = timeStamp;
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

    public String getExpNo() {
        return expNo;
    }

    public void setExpNo(String expNo) {
        this.expNo = expNo;
    }

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public String getTheory() {
        return theory;
    }

    public void setTheory(String theory) {
        this.theory = theory;
    }

    public String getOutputCode() {
        return outputCode;
    }

    public void setOutputCode(String outputCode) {
        this.outputCode = outputCode;
    }

    public String getSubTime() {
        return subTime;
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
    }

    public String getOutputPic() {
        return outputPic;
    }

    public void setOutputPic(String outputPic) {
        this.outputPic = outputPic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
