package com.bulut.sleepbetter;

import android.widget.TextView;

public class QuestionnaireItem {

    private String user;
    private String title;
    private String answerOne;
    private String answerTwo, answerThree, answerFour, answerFive, answerSix;

    public QuestionnaireItem() {}
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

    public String getAnswerFive() {
        return answerFive;
    }
    public void setAnswerFive(String answerFive) {
        this.answerFive = answerFive;
    }

    public String getAnswerSix() {
        return answerSix;
    }
    public void setAnswerSix(String answerSix) {
        this.answerSix = answerSix;
    }

    public QuestionnaireItem(
                             String title,
                             String user,
                             String answerOne,
                             String answerTwo,
                             String answerThree,
                             String answerFour,
                             String answerFive,
                             String answerSix) {
        this.title = title;
        this.user = user;
        this.answerOne = answerOne;
        this.answerTwo = answerTwo;
        this.answerThree = answerThree;
        this.answerFour = answerFour;
        this.answerFive = answerFive;
        this.answerSix = answerSix;
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
