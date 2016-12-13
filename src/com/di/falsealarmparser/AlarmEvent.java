package com.di.falsealarmparser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AlarmEvent {
    static final Pattern USERNAME_PATTERN = Pattern.compile("(?<=user ).+?(?= )");
    static final Pattern COMMAND_PATTERN = Pattern.compile("(chown|chmod|chgrp).*");
    static final Pattern DATE_PATTERN = Pattern.compile("^.*(\\d*:\\d*:\\d*)[ ]+[A-Za-z][A-Za-z][A-Za-z][ ]+\\d+");
    static final Pattern FILE_ACTION_PATTERN = Pattern.compile("^.* (file)");

    String id;
    Date date;
    String action;
    String param;
    String userName;
    String filePath;
    String valid;

    public static List<AlarmEvent> parse(String rawEvent){
        if(rawEvent.isEmpty())
            return Collections.emptyList();

        final String id = System.nanoTime() + Thread.currentThread().getName() + rawEvent.hashCode();

        final String[] rawEventLines = rawEvent.split("\r\n");
        final List<AlarmEvent> alarmEventList = new ArrayList<AlarmEvent>();

        for(final String rawEventLine : rawEventLines){
            AlarmEvent alarmEvent = new AlarmEvent(rawEventLine);
            alarmEvent.setId(id);
            alarmEventList.add(alarmEvent);
        }

        String validAlarm = "";

        for(final AlarmEvent alarmEvent : alarmEventList){
            if(alarmEvent.isValid() != null && !alarmEvent.isValid().toLowerCase().equals("null")){
                validAlarm = alarmEvent.isValid();
                break;
            }
        }

        for(final AlarmEvent alarmEvent : alarmEventList){
            alarmEvent.setValid(validAlarm);
        }
        return alarmEventList;
    }

    public AlarmEvent(String rawEventLine){
        /*
            FIND DATE
         */
        Matcher matcher = DATE_PATTERN.matcher(rawEventLine);
        if(matcher.find()){
            String dateString = matcher.group().replaceAll("\\s+", " ");
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");

            try {
                Date date = dateFormat.parse(dateString);
                setDate(date);
            }catch(ParseException e){
                e.printStackTrace();
                System.out.println("Continuing...");
            }
        }

        /*
            FIND ALARM WARNING (... was not created)
         */
        if(rawEventLine.endsWith("was not created")){
            setAction("was not created");
            String parts[] = rawEventLine.trim().split("\\s+");

            if(parts.length > 1) {
                String filePath = parts[0];
                setFilePath(filePath);

                //TRUE ALARMS end with lowercase extension
                //FALSE ALARMS end with uppercase
                String extension = filePath.substring(filePath.length() - 4);
                setValid(extension.equals(extension.toLowerCase()));
            }
            return;
        }

        /*
            FIND USERNAME
         */
        matcher = USERNAME_PATTERN.matcher(rawEventLine);
        if(matcher.find()){
            String userName = matcher.group();
            setUserName(userName);

            //Only user names are coupled with file upload attempts.
            matcher = FILE_ACTION_PATTERN.matcher(rawEventLine.substring(rawEventLine.indexOf(userName) + userName.length() + 1));

            if(matcher.find()){
                String fileAction = matcher.group();
                setAction(fileAction);

                setFilePath(rawEventLine.substring(rawEventLine.indexOf(fileAction) + fileAction.length() + 1));
            }
            return;
        }

        /*
            FIND UNIX COMMANDS
         */
        matcher = COMMAND_PATTERN.matcher(rawEventLine);
        if(matcher.find()){
            final String parts[] = matcher.group().split(" ");
            if(parts.length > 0)
                setAction(parts[0]);
            if(parts.length > 1)
                setParam(parts[1]);
            if(parts.length > 2)
                setFilePath(parts[2]);
        }
    }

    public String getId(){ return id; }

    public void setId(String id){ this.id = id;
    }
    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public String getAction() { return action; }

    public void setAction(String action) { this.action = action; }

    public String getParam() { return param; }

    public void setParam(String param) { this.param = param; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getFilePath() { return filePath; }

    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String isValid(){ return valid; }

    public void setValid(String valid){ this.valid = valid; }
    public void setValid(boolean valid){ this.valid = valid ? "TRUE" : "FALSE"; }

    @Override
    public String toString(){
        return String.format("%s,%s,%s,%s,%s,%s,%s\r\n", getId(), getDate(), getAction(), getParam(), getUserName(), getFilePath(), isValid());
    }
}
