package com.actoon.actoon.util;



// 0 1 2 3 ...
public enum WebtoonProgressState {

    COMPLETE("COMPLETE", 0),
    REJECT("REJECT", 1),
    CONTINUE("CONTINUE", 2);

    private String state;
    private int progress;
    WebtoonProgressState(String state, int progress) {
        this.state = state;
        this.progress = progress;
    }

    public int getProgress(){
        return progress;
    }

    //Int
    public static int getProgress(String name){
        for (WebtoonProgressState state : WebtoonProgressState.values()){
            if(state.name().equals(name.toUpperCase()))
                return state.getProgress();
        }
        return -1;
    }

    //String
    public static String getState(Integer num){
        for (WebtoonProgressState state : WebtoonProgressState.values()){
            if(state.progress == num)
                return state.state.toUpperCase();
        }
        return "";
    }
}
