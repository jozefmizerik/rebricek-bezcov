package assigningModule;

import GeneratingModule.HtmlResultGenerator;
import InputFileClasses.ClassResult;
import InputFileClasses.Event;
import InputFileClasses.PersonResult;
import InputFileClasses.ResultList;
import InputFileClasses.Class;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.Normalizer;
import java.text.Normalizer.Form;


public class TotalResults {

	Map<String, Map<String, RunnerOverall>> dictionary = new HashMap<>();
	List<String> events = new ArrayList<>();
	List<String> dates = new ArrayList<>();
	RunnerOverall runnerOverall;
	

	public Map<String, Map<String, RunnerOverall>> getDictionary() {
		return dictionary;
	}

	public List<String> getEvents() {
		return events;
	}

	public List<String> getDates() {
		return dates;
	}

	public void resultsAssignment(List<ResultList> results) {
		for(ResultList rl : results) {
			Event event = rl.getEvent();
			events.add(event.getName());
			String localDate = rl.getEvent().getStartTime().getDate();
			dates.add(localDate);
			int roundNumber = event.getRank();
			for(ClassResult cr : rl.getClassResult()) {
				Class cd = cr.getClazz();
				for(PersonResult pr : cr.getPersonResult()) {
					System.out.println("idem asignovat hraca: " + pr.getPerson().getName().getFamily());
					assignRunner(cd, pr, roundNumber);
				}
			}
		}
		for (Map<String, RunnerOverall> entry : dictionary.values()) {
			System.out.println("Stav po kole::::::::::::::::::::::::::::::::::::::::::");
		    for(RunnerOverall runnerOverall : entry.values()) {
				runnerOverall.setPoints(runnerOverall.sumOfPoints());
				while(runnerOverall.getRounds().size() < events.size()) {
					runnerOverall.getRounds().add(0);
				}
				System.out.println(runnerOverall.identificator + "......." + runnerOverall.rounds.toString());
		    }
		}
		new HtmlResultGenerator(dictionary, events, dates);
	}
	
	private void assignRunner(Class cd, PersonResult pr, int roundNumber) {
		String classId = cd.getName();
		String personId = pr.getPerson().getId().get(0).getValue();
		String fullName = pr.getPerson().getName().getFamily() + ' ' + pr.getPerson().getName().getGiven();
		int points = pr.getResult().getPoints();
		if(dictionary.containsKey(classId)) {
			if(dictionary.get(classId).containsKey(personId)) {
				RunnerOverall runnerOverall = dictionary.get(classId).get(personId);
				while(roundNumber - 1 > runnerOverall.rounds.size()) {
					runnerOverall.getRounds().add(0);
				}
				runnerOverall.getRounds().add(points);
				//dictionary.get(classId).put(personId, runnerOverall);
			}
			else if(dictionary.get(classId).containsKey(normalizeString("#" + fullName))) {
				RunnerOverall runnerOverall = dictionary.get(classId).get(normalizeString("#" + fullName));
				while(roundNumber - 1 > runnerOverall.rounds.size()) {
					runnerOverall.getRounds().add(0);
				}
				runnerOverall.getRounds().add(points);
				//dictionary.get(classId).put(personId, runnerOverall);
				
			}
			else if(! personId.equals("")) {
				RunnerOverall runnerOverall = new RunnerOverall(personId, pr.getPerson().getName().getGiven(), pr.getPerson().getName().getFamily());
				while(roundNumber - 1 > runnerOverall.rounds.size()) {
					runnerOverall.getRounds().add(0);
				}
				runnerOverall.getRounds().add(points);
				dictionary.get(classId).put(personId, runnerOverall);
			}
				
			else {
				RunnerOverall runnerOverall = new RunnerOverall("", pr.getPerson().getName().getGiven(), pr.getPerson().getName().getFamily()); //first_parameter: "#" + fullName
				//dictionary.get(classId).put("#" + fullName, new RunnerOverall(personId, pr.getPerson().getGivenName(), pr.getPerson().getFamilyName()));
				while(roundNumber - 1 > runnerOverall.rounds.size()) {
					runnerOverall.getRounds().add(0);
				}
				runnerOverall.getRounds().add(points);
				dictionary.get(classId).put(normalizeString("#" + fullName), runnerOverall);
			}
		}
		else {
			Map<String, RunnerOverall> newMap = new HashMap<String, RunnerOverall>();
			RunnerOverall runnerOverall =  new RunnerOverall(personId, pr.getPerson().getName().getGiven(), pr.getPerson().getName().getFamily());
			while (roundNumber - 1 > runnerOverall.rounds.size()) {
				runnerOverall.getRounds().add(0);
			}
			runnerOverall.getRounds().add(points);
			if(! personId.equals("")) {
				System.out.println("som v if");
				newMap.put(personId, runnerOverall);
			}
			else {
				System.out.println("som v else");
				//runnerOverall.setIdentificator("#" + fullName);
				newMap.put("#" + fullName, runnerOverall);
			}
			dictionary.put(classId, newMap);
		}
	}

	public String normalizeString(String str){
		return Normalizer.normalize(str, Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
	
	
	
}
