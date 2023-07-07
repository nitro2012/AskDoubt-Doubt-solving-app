package com.chaquo.AskDoubt.Model;

public class Answer {


    public Answer(){}
    public String getAnsby() {
        return ansby;
    }

    public void setAnsby(String ansby) {
        this.ansby = ansby;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerImage() {
        return answerImage;
    }

    public void setAnswerImage(String answerImage) {
        this.answerImage = answerImage;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    private String ansby,date,postId,publisher, answer, answerImage,topic;

    public Answer(String askedBy, String date, String postId, String publisher, String question, String answerImage, String topic) {
        this.ansby = askedBy;
        this.date = date;
        this.postId = postId;
        this.publisher = publisher;
        this.answer = question;
        this.answerImage = answerImage;
        this.topic = topic;
    }
}
