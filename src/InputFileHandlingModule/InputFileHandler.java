package InputFileHandlingModule;
/**
 * @author TomasTakacs
 */


import InputFileClasses.ResultList;
import InteractionModule.InteractionModule;
import computingModule.RoundPointComputation;
import configurationModule.ConfigurationFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class InputFileHandler {

    private final static String FILESTORAGE = "DATA_STORAGE";


    public static void addInputFile(String fileAddress){
        ResultList inputResultList = null;
        try {
            inputResultList = InputFileHandler.unmarshalInputFile(fileAddress);
        } catch (JAXBException e) {
            InteractionModule.printMessage("An Error has occurred while reading file: " + fileAddress);
            e.printStackTrace();
            return;
        }

        // daj skontrolovat InputFileVerificator-u
        ResultList resultList = InputFileVerificator.verifyInputFile(inputResultList);

        //String season = "2018";         // placeholder, dummy value
        String season = Integer.toString(new ConfigurationFile().getSeasonYear());

        // get FileStorage // TODO

        String newFileName = "kolo"+resultList.getEvent().getRank() + ".xml";

        String savePath = FILESTORAGE + '\\'+season + '\\' + newFileName;

        File fileStorageFolder = new File(FILESTORAGE);
        if (fileStorageFolder.isDirectory() == false) {
            new File(FILESTORAGE).mkdirs();
            System.out.println("created directory: "+fileStorageFolder.getPath());
            //System.out.println("created directory: "+FILESTORAGE);
        }

        File seasonFolder = new File(FILESTORAGE + '/'+season);
        if (seasonFolder.isDirectory() == false) {
            new File(FILESTORAGE + '\\'+season).mkdirs();
            System.out.println("created directory: "+FILESTORAGE + '\\'+season);
        }

        try {
            InputFileHandler.marshalInputFile(resultList, savePath);
            System.out.println("created file: "+savePath);
        } catch (JAXBException e) {
            InteractionModule.printMessage("An Error has occurred while creating file: " + savePath);
            e.printStackTrace();
            return;
        }
        //if (success == false)
        // InteractionModule.printMessage(MESSAGE_MARSHALLING_FAILED)
        // nastala chyba pri ukladaní vstupného súboru

        loadInputFiles();
    }


    public static void loadInputFiles(){
        // TODO get fileStorage, season

        //String season = "2018";      // placeholder, dummy value
        String season = Integer.toString(new ConfigurationFile().getSeasonYear());

        List<String> inputFilePaths = null;
        try {
            inputFilePaths = listFilesForFolder(FILESTORAGE+"\\"+season);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        List<ResultList> loadedResultLists = new ArrayList<>();

        for (String fileAddress : inputFilePaths){
            ResultList resultList = null;
            try {
                resultList = InputFileHandler.unmarshalInputFile(fileAddress);
                ResultList verifiedResultList = InputFileVerificator.verifyInputFile(resultList);
                loadedResultLists.add(verifiedResultList);
            } catch (JAXBException e) {
                InteractionModule.printMessage("An Error has occurred while reading file: " + fileAddress);
                e.printStackTrace();
                return;
            }
        }

        // spusti dalsi MODUL
        new RoundPointComputation(loadedResultLists);
    }


    private static ResultList unmarshalInputFile(String sourceFileAddress) throws JAXBException {
        ResultList resultList = null;

        JAXBContext jc = JAXBContext.newInstance(ResultList.class);

        System.out.println(sourceFileAddress);
        File sourceFile = new File(sourceFileAddress);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        resultList = (ResultList) unmarshaller.unmarshal(sourceFile);

        return resultList;
    }

    private static void marshalInputFile(ResultList resultList, String targetFileAdress) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(ResultList.class);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        File targetFile = new File(targetFileAdress);
        marshaller.marshal(resultList, targetFile);
    }


    // FILESTORAGE + season + filename + extension
    private static List<String> listFilesForFolder(final String folderAddress) throws FileNotFoundException {
        final File folder = new File(folderAddress);
        if (folder.isDirectory() == false){
            System.out.println("Directory "+folder.getPath()+" does not exist");
            throw new FileNotFoundException();
        }
        List<String> filePaths = new ArrayList<>();


        // <LOG>
        System.out.println("-------------");
        System.out.println("Files loaded:");
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                if (fileEntry.getName().endsWith(".xml")){
                    System.out.println(fileEntry.getName());
                    filePaths.add(fileEntry.getPath());
                }
            }
        }
        System.out.println("-------------");
        // </LOG>

        return filePaths;
    }

}
