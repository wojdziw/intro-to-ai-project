/**
 * Created by erikandersson on 2017-03-13.
 */
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class saveToCsv {
    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    public static void writeCsvFile(String fileName, int[][] runs, double[][] weights) {


        FileWriter fileWriter = null;
        //CSV file header
        String FILE_HEADER = "";

        for (int i = 0; i<runs[0].length;i++ ){
                FILE_HEADER += "Fitness" + String.valueOf(i+1) + ',';
        }
        for (int i = 0; i<weights[0].length;i++ ){
            if (i<weights[0].length-1) {
                FILE_HEADER += "Weight" + String.valueOf(i) + ',';
            } else{
                FILE_HEADER += "Weight" + String.valueOf(i);
            }
        }
        String timestamp = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(Calendar.getInstance().getTime());

        try {
            fileName = fileName+"-"+timestamp+".csv";
            fileWriter = new FileWriter(fileName);
            //Write the CSV file header
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new student object list to the CSV file
            for (int i = 0; i<runs.length;i++) {
                for (int run : runs[i]){
                fileWriter.append(String.valueOf(run));
                fileWriter.append(COMMA_DELIMITER);
                }
                for (double weight : weights[i]){
                    fileWriter.append(String.valueOf(weight));
                    fileWriter.append(COMMA_DELIMITER);
                }
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully with name " + fileName);

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
