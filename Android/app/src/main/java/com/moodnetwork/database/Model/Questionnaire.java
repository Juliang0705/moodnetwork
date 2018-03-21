package com.moodnetwork.database.Model;

public class Questionnaire {
    private String mQuestion;
    private String mAnswer;

    public Questionnaire(String question, String answer) {
        setQuestion(question);
        setAnswer(answer);
    }
    public Questionnaire() {

    }
    public String getQuestion() {
        if (mQuestion == null) throw new RuntimeException("Question is not set to any value yet");
        return mQuestion;
    }
    public String getAnswer() {
        if (mAnswer == null) throw new RuntimeException("Answer is not set to any value yet");
        return mAnswer;
    }

    public void setQuestion(String question) {
        if (question == null) throw new RuntimeException("Question cannot be null");
        mQuestion = question;
    }

    public void setAnswer(String answer) {
        if (answer == null) throw new RuntimeException("Answer cannot be null");
        mAnswer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Questionnaire that = (Questionnaire) o;

        if (!mQuestion.equals(that.mQuestion)) return false;
        return mAnswer.equals(that.mAnswer);
    }

    @Override
    public int hashCode() {
        int result = mQuestion.hashCode();
        result = 31 * result + mAnswer.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Questionnaire{" +
                "mQuestion='" + mQuestion + '\'' +
                ", mAnswer='" + mAnswer + '\'' +
                '}';
    }
}
