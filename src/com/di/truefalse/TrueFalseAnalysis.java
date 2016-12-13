package com.di.truefalse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrueFalseAnalysis {

    public static void main(String[] args) {
        try{
            Scanner scanner = new Scanner(new File("projectdoc.txt"));
            final String data = scanner.useDelimiter("\\Z").next();
            scanner.close();

            final String[] items = data.split("=+");
            final Pattern   chUserPattern = Pattern.compile("(chown|chmod|chgrp).+(?=[\n\r])"),
                            uploadedPattern = Pattern.compile("(?<=user ).+(?=[\r\n])");
            StringBuilder sb = new StringBuilder("commandName,userId,filePath,falsePositive\r\n");

            System.out.printf("Parsing %d entries...", items.length);

            int lines = 0;
            for(final String item : items){
                Matcher matcher = chUserPattern.matcher(item);

                //Search for FILE PERMISSION CHANGES
                while(matcher.find()){
                    String line[] = matcher.group().split(" ");
                    sb	    .append(line[0])
                            .append(",")
                            .append(line[1])
                            .append(",")
                            .append(line[2])
                            .append(",")
                            .append("")
                            .append("\r\n");
                    lines++;
                }

                //SEARCH FOR USER UPLOADS
                matcher = uploadedPattern.matcher(item);
                while(matcher.find()){
                    String line = matcher.group(),
                           action;
                    String lineSegments[];
                    if(line.contains(" started to upload ")){
                        lineSegments = line.split(" started to upload ");
                        action = "started to upload";
                    }else{
                        lineSegments = line.split(" uploaded ");
                        action = "uploaded";
                    }
                    sb	    .append(action)
                            .append(",")
                            .append(lineSegments[0])
                            .append(",")
                            .append(lineSegments[1])
                            .append(",")
                            .append("")
                            .append("\r\n");
                    lines++;
                }
            }

            System.out.printf("Processed %d lines of user actions", lines);

            final FileWriter writer = new FileWriter("output.csv");
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(sb.toString());
            bufferedWriter.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
