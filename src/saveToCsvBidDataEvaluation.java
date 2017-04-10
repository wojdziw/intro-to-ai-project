/**
 * Created by adam on 10/04/17.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class saveToCsvBidDataEvaluation {

    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    public static void writeCsvFile(String fileName, ListOfResults resultDump) {

        System.out.println("Writing over resultDump to csv");

        FileWriter fileWriter = null;
        //CSV file header
        String FILE_HEADER = "";

        FILE_HEADER += "Generation,";
        FILE_HEADER += "cores,";
        FILE_HEADER += "cols,";
        FILE_HEADER += "GenerationFitness,";
        FILE_HEADER += "GenerationTime";

        String timestamp = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Calendar.getInstance().getTime());

        try {
            fileName = fileName+"-"+timestamp+".csv";
            fileWriter = new FileWriter(fileName);
            //Write the CSV file header
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new student object list to the CSV file
            for (int i = 0; i<resultDump.generationCount.size();i++) {
                fileWriter.append(String.valueOf(resultDump.generationCount.get(i)));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(resultDump.generationsNumberOfCores.get(i)));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(resultDump.generationsColumns.get(i)));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(resultDump.generationsResults.get(i)));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(resultDump.generationsTime.get(i)));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully with name " + fileName + "\n");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }

        }
    }
}