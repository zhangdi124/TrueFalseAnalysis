package com.di.falsealarmparser;

import java.io.*;
import java.util.*;


public class FalseAlarmParser {
    final static String INPUT_FILEPATH = "projectdoc.txt";
    final static String OUTPUT_FILEPATH = "output.csv";

    public static String fileToString(String filePath){
        String output = "";

        try{
            final Scanner scanner = new Scanner(new File(filePath));
            output = scanner.useDelimiter("\\Z").next();
            scanner.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

        return output;
    }

    public static List<AlarmEvent> parseAlarmLog(String filePath){
        final String rawText = fileToString(filePath);
        if(rawText.isEmpty())
            return Collections.emptyList();

        final String[] rawEvents = rawText.split("=+");

        final List<AlarmEvent> alarmEventList = new ArrayList<AlarmEvent>();
        for(final String rawEvent : rawEvents){
            alarmEventList.addAll(AlarmEvent.parse(rawEvent));
        }
        return alarmEventList;
    }

    public static void writeAlarmLogCSV(String filePath, Collection<AlarmEvent> alarmEventCollection) throws IOException{
        final StringBuilder sb = new StringBuilder("ID,DATE,ACTION,PARAM,USERNAME,FILEPATH,VALID\r\n");
        for(final AlarmEvent alarmEvent : alarmEventCollection){
            sb.append(alarmEvent.toString());
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
        bufferedWriter.write(sb.toString());
        bufferedWriter.close();
    }

    public static void main(String[] args) {
        final List<AlarmEvent> alarmEventList = parseAlarmLog(INPUT_FILEPATH);

        if(alarmEventList.isEmpty()){
            System.out.println("Could not parse any alarm events - exiting");
            return;
        }

        System.out.printf("Parsed %d individual events\r\n", alarmEventList.size());

        try {
            writeAlarmLogCSV(OUTPUT_FILEPATH, alarmEventList);
            System.out.printf("Successfully wrote %s\r\n", OUTPUT_FILEPATH);
        }catch(IOException e){
            System.out.println("Could not write to file");
            e.printStackTrace();
        }
    }
}
