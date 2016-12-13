package com.di.truefalse;

import java.util.Date;

public class TrueFalseEntry {
    Date date;
    String filePath;
    String errorMessage;

    String userID;
    String commandName;

    public void setDate(Date date){
        this.date = date;
    }
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public void setUserId(String userId){
        this.userID = userId;
    }

    public void setCommandName(String commandName){
        this.commandName = commandName;
    }

    public Date getDate(){ return this.date; }
    public String getFilePath(){ return this.filePath; }
    public String getErrorMessage(){ return this.errorMessage; }
    public String getUserId(){ return this.userID; }
    public String getCommandName(){ return this.commandName; }

    public boolean isValid(){ return this.filePath.endsWith(".txt"); }
}

