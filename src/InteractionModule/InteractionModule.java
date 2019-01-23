package InteractionModule;

import InputFileHandlingModule.InputFileHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InteractionModule {
    private static String inputFileAddress;

    public void run() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Variant A: a new input file is given, then the point computation is executed.");
        System.out.println("Variant B: the point computation is executed, using actual input files, without inserting a new one.");
        selection:  while (true) {
            System.out.print("Choose variant A/B by typing \"a\" or \"b\": ");
            switch (br.readLine().toLowerCase()){
                case "a":
                    runA(null);
                    break;
                case "b":
                    runB();
                    break;
                default:
                    System.out.println("Wrong selection!");
                    continue selection;
            }
            break selection;
        }
    }
    public void runA(String inputAddress){
        inputFileAddress = requestInputFileAddress(inputAddress);
        // TODO - adresa vstupneho suboru je nahrana, mozes ju posunut modulu, ktory s nou bude pracovat (ten asi bude pustat moduly, ktore budu robit vypocet)

        InputFileHandler.addInputFile(inputFileAddress);
    }
    public void runB(){
        System.out.println("running variant B");    // temp
        // TODO - zavolat metodu do Tomasovho modulu

        InputFileHandler.loadInputFiles();
    }
    // --
    static public String requestInputFileAddress(String givenAddress){
        // metoda je pouzivana aj pri vypytani uplne novej adresy, aj pri pytani adresy v pripade, ze defaultne zadana adresa je zla
        if (givenAddress != null) {
            String address = InputCheckModule.checkInputAddress(givenAddress);
            if (address != null) {
                return address;
            }
            System.out.println("Input address is not correct.");
        }
        enterAddress: while (true) {
            String inputAddress = InputCheckModule.checkInputAddress(requestStringInput("Enter input file address: "));
            if (inputAddress == null){  // kontrola
                continue enterAddress;
            }
            return inputAddress;
        }
    }

    private static String requestStringInput(String message) {
        System.out.print(message);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // --
    public static void printMessage(String message) {
        System.out.println(message);
    }
    // --
    public static Integer requestRaceRank() {
        String message = "Enter race rank: ";
        while (true) {
            try {
                String input = requestStringInput(message);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Wrong race rank format!");
                continue;
            }
        }
    }
    public static String requestRaceName() {
        String message = "Enter race name: ";
        return requestStringInput(message);
    }
    public static String requestRaceStartTime() {
        String message = "Enter race start time: ";
        while (true) {
            try {
                String input = requestStringInput(message);
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");  // yyyy-MM-dd hh:mm:ss.SSS
                Date parsedDate = dateFormat.parse(input);
                new java.sql.Timestamp(parsedDate.getTime());
                return input;
            } catch (ParseException e) {
                System.out.println("Wrong race start_time format!");
                continue;
            }
        }
    }
    // --
    public static String getInputFileAddress() {
        return inputFileAddress;
    }

}

