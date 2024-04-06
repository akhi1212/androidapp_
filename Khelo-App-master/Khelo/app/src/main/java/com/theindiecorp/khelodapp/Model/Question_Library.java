package com.theindiecorp.khelodapp.Model;

public class Question_Library {public static String mQuestions [] = {
        "Who is the captain of indian odi team",
        "This is some random question of one line",
        "This is a random question of multiple lines it has minimum of two lines",
        "Which animal do you want ?"

};


    private String mChoices [][] = {
            {"Dhoni", "Ashwin", "Virat","Jadeja"},
            {"Fruit", "Leaves", "Seeds","Bark"},
            {"Bark", "Flower", "Roots","Leaves"},
            {"Dog", "Cat", "Puppy","Kitten"}
    };



    private String mCorrectAnswers[] = {"Virat", "Leaves", "Flower", "Puppy"};




    public String getQuestion(int a) {
        String question = mQuestions[a];
        return question;
    }


    public String getChoice1(int a) {
        String choice0 = mChoices[a][0];
        return choice0;
    }


    public String getChoice2(int a) {
        String choice1 = mChoices[a][1];
        return choice1;
    }

    public String getChoice3(int a) {
        String choice2 = mChoices[a][2];
        return choice2;
    }
    public String getChoice4(int a) {
        String choice4 = mChoices[a][3];
        return choice4;
    }
    public String getCorrectAnswer(int a) {
        String answer = mCorrectAnswers[a];
        return answer;
    }

}
