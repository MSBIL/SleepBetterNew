package com.bulut.sleepbetter;

public class MusicItem {

    private String user;
    private String title;
    private String answerOne;
    private String answerTwo, answerThree, answerFour;

    public MusicItem() {}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getUser() {
        return title;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAnswerOne() {
        return answerOne;
    }
    public void setAnswerOne(String answerOne) {
        this.answerOne = answerOne;
    }

    public String getAnswerTwo() {
        return answerTwo;
    }
    public void setAnswerTwo(String answerTwo) {
        this.answerTwo = answerTwo;
    }

    public String getAnswerThree() {
        return answerThree;
    }
    public void setAnswerThree(String answerThree) {
        this.answerThree = answerThree;
    }

    public String getAnswerFour() {
        return answerFour;
    }
    public void setAnswerFour(String answerFour) {
        this.answerFour = answerFour;
    }


    public MusicItem(
                             String title,
                             String user,
                             String answerOne,
                             String answerTwo,
                             String answerThree,
                             String answerFour) {
        this.title = title;
        this.user = user;
        this.answerOne = answerOne;
        this.answerTwo = answerTwo;
        this.answerThree = answerThree;
        this.answerFour = answerFour;
    }

    /*
    public QuestionnaireItem(
                             String title,
                             String user,
                             String answer_1) {
        this.title = title;
        this.user = user;
        this.answerOne = answer_1;
    }
    */

}
