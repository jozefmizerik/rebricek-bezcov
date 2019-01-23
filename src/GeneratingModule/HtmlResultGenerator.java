package GeneratingModule;

import assigningModule.RunnerOverall;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class HtmlResultGenerator {
    private Map<String, Map<String, RunnerOverall>> results;
    private List<String> events;
    private List<String> dates;
    private int countOfEvents;
    private String numOfResults = "10"; //TODO v konstruktore menit
    private String scoring = "50-45-40-35-30-29-28-27-...3-2-1-1-1-1... DISK = 0"; //TODO v konstruktore menit
    private static String mainTemplate;
    private static String navTemplate;
    private static String categoryTemplate;
    private static String runnerTemplate;

    public HtmlResultGenerator(Map<String,  Map<String, RunnerOverall>> results, List<String> events, List<String> dates){
        this.results = results;
        this.events = events;
        this.dates = dates;
        this.countOfEvents = events.size();
        mainTemplate = readStringFromFile("mainTemplate.txt");
        navTemplate = readStringFromFile("navTemplate.txt");
        categoryTemplate = readStringFromFile("categTemplate.txt");
        runnerTemplate = readStringFromFile("runnerTemplate.txt");
        generateFile();
    }

    private String fillMainTemplate(){
        return mainTemplate
                .replaceAll("@bodovanie", scoring)
                .replaceAll("@pocetVysledkov", numOfResults)
                .replaceAll("@navigacia", fillNavTemplate())
                .replaceAll("@poradieKol", eventsOrder())
                .replaceAll("@datumyKol", eventsDates())
                .replaceAll("@nazvyKol", eventsNamesToTable())
                .replaceAll("@vysledky", allCategoriesTables());
    }

    private String fillNavTemplate(){
        StringBuilder result = new StringBuilder();
        for (String categoryName: results.keySet()) {
            result.append(navTemplate.replaceAll("@kategoria", categoryName));
        }
        return result.toString();
    }

    private String fillCategoryTemplate(String categoryName){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < countOfEvents; i++){
            s.append("\t\t\t\t<td></td>\n");
        }
        return categoryTemplate
                .replaceAll("@kategoria", categoryName)
                .replaceAll("@prazdneStlpceKol", s.toString())
                .replaceAll("@vysledkyBezcov", allRunnersCategoryTable(categoryName));
    }

    private String fillRunnerTemplate(RunnerOverall runner, int order){//volat pre kazdeho bezca
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < countOfEvents; i++){
            int points = runner.getRounds().get(i);
            if (points == 0) {
                s.append("\t\t\t\t<td></td>\n");
            }else if (points > 0) {
                s.append("\t\t\t\t<td>").append(points).append("</td>\n");
            }else  {
                s.append("\t\t\t\t<td>0</td>\n");
            }
        }
        return runnerTemplate
                .replaceAll("@poradie", Integer.toString(order))
                .replaceAll("@zmena", "=")//TODO
                .replaceAll("@meno", runner.getGivenName())
                .replaceAll("@priezvisko", runner.getFamilyName())
                .replaceAll("@id", runner.getIdentificator())
                .replaceAll("@bodyKol", s.toString())
                .replaceAll("@spolu", Integer.toString(runner.getPoints()));
    }

    private String eventsNamesToTable(){
        StringBuilder result = new StringBuilder();
        for (String eventName: events) {
            result.append("\t\t\t\t<td><b>").append(eventName).append("</b></td>\n");
        }
        return result.toString();
    }

    private String eventsOrder(){
        StringBuilder result = new StringBuilder();
        for (int i = 1; i <= countOfEvents; i++){
            result.append("\t\t\t\t<td>").append(i).append(".kolo").append("</td>\n");
        }
        return result.toString();
    }

    private String eventsDates(){
        StringBuilder result = new StringBuilder();
        for (String date : dates){
            result.append("\t\t\t\t<td><b>").append(date).append("</b></td>\n");
        }
        return result.toString();
    }

    private String allCategoriesTables(){
        boolean notFirst = false;
        StringBuilder result = new StringBuilder();
        for (String categoryName : results.keySet()) {
            if(notFirst){
                result.append("\t\t<table>\n");
            }
            result.append(fillCategoryTemplate(categoryName));
            result.append("\t\t</table>\n");
            notFirst = true;
        }
        return result.toString();
    }

    private String allRunnersCategoryTable(String categoryName){
        StringBuilder result = new StringBuilder();
        int i = 1;
        Set<RunnerOverall> runners = new TreeSet<>();
        for (String id : results.get(categoryName).keySet()){
            runners.add(results.get(categoryName).get(id));
        }
        for (RunnerOverall runner : runners){
            result.append(fillRunnerTemplate(runner, i));
            i++;
        }
        return result.toString();
    }

    private String readStringFromFile(String filePath){
        String fileContent = null;
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    private void generateFile() {
        PrintWriter resultFile = null;
        try {
            resultFile = new PrintWriter("vystup.html", "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assert resultFile != null;

        resultFile.print(fillMainTemplate());
        resultFile.close();
    }
}
