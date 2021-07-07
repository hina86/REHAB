package org.intellij.sdk.util;

import com.opencsv.CSVWriter;
import javafx.util.Pair;
import org.intellij.sdk.ruleengine.UiModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyFileUtil {
    public static String exportDataToCsv(String packageName, String versionNo, ArrayList<UiModel> list) {
        if (packageName == null){
            packageName = "";
        }
        try {
            String fileName = "LibraryDetectionData.csv";
            File file = new File(System.getProperty("user.home") + File.separator + fileName);
            FileWriter outputfile ;
            boolean writeHeader = false;
            if (!file.exists()) {
               outputfile = new FileWriter(file, false);
               writeHeader = true;
            } else {
                outputfile = new FileWriter(file, true);
                writeHeader = false;
            }
            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> data = new ArrayList<>();
            if (writeHeader) {
                data.add(new String[] { "Package Name","Version", "Library", "Request Type", "Method Definitions", "Method Calls" });
            }
            for (UiModel entity: list){
                System.out.println("Writing " + entity.toString());
                Pair<String, String> pair = split(entity.ruleName);
                data.add(new String[] {
                        packageName,
                        versionNo,
                        pair.getKey(),
                        pair.getValue(),
                        entity.getCallString(),
                        entity.getExpressionString()
                });
            }
            writer.writeAll(data);
            writer.close();
            return "Data exported to: \n" + file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    //returns pair consisting of library name nad request type
    private static Pair<String, String> split(String string) {
        String[] arr = string.split("-");
        if (arr.length == 2) {
            return new Pair<>(arr[0], arr[1]);
        } else {
            return new Pair<>(string, "");
        }
    }


    // Method for getting the maximum value
    public static int getMax(int[] inputArray){
        int maxValue = inputArray[0];
        for(int i=1;i < inputArray.length;i++){
            if(inputArray[i] > maxValue){
                maxValue = inputArray[i];
            }
        }
        return maxValue;
    }

}
