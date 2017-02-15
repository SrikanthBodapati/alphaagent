package com.infralabs.agent.scheduledjobs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.infralabs.agent.util.GeneralUtils;

@Component
public class AgentAlertScheduler {
	@Value("${agent.scripts.dir:}")
	public String scriptsDirecotry;
	@Value("${agent.export.dir:}")
	public String statusOutputDirectory;
	public static final String EXTENSION = "ps1";

	@Scheduled(fixedRateString = "${agent.scripts.run.frequency:5000}")
	public void reportCurrentTime() {
		if (scriptsDirecotry.equals("")) {
			scriptsDirecotry = new File("").getAbsolutePath() + "\\src\\main\\resources\\scripts\\";
		}
		if (statusOutputDirectory.equals("")) {
			statusOutputDirectory = new File("").getAbsolutePath() + "\\src\\main\\resources\\output\\";
		}

		String[] scriptFiles = GeneralUtils.listFile(scriptsDirecotry, EXTENSION);
		Reports reportInstance = Reports.INSTANCE;
		for (String agentScript : scriptFiles) {
			String agentOutputFile = statusOutputDirectory
					+ agentScript.substring(agentScript.lastIndexOf('\\'), agentScript.lastIndexOf('.')) + ".csv";
			String alertOutputFile = statusOutputDirectory
					+ agentScript.substring(agentScript.lastIndexOf('\\'), agentScript.lastIndexOf('.')) + "_alert.csv";
			List<String> oldreports = reportInstance.getInstance();
			List<String> newreports = GeneralUtils.ReadCsvFile(agentOutputFile);
			writeAlerts(getAlerts(newreports, oldreports), alertOutputFile);
			populateReportsInmemory(newreports, reportInstance);
		}
	}

	private List<Alert> getAlerts(List<String> newReports, List<String> oldReports) {
		List<Alert> alerts = new ArrayList<Alert>();
		if (oldReports.size() == 0) {
			for (String each : newReports) {
				alerts.add(new Alert(null, each));
			}
		} else {
			int index = 0;
			for (String each : newReports) {
				String oldvalue = oldReports.get(index);
				if (!oldvalue.equals(each)) {
					alerts.add(new Alert(oldvalue, each));
				}
				index++;
			}
		}
		
		return alerts;
	}

	private void writeAlerts(List<Alert> alerts, String fileName) {
		try {
			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(fileName); // this erases previous
														// content
			fw = new FileWriter(fileName, true); // this reopens file for
													// appending
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			for (Alert each : alerts) {
				pw.println(each.toString());
//				pw.println(each.newvalue);
			}
			pw.flush();
			pw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void populateReportsInmemory(List<String> reports, Reports reportInstance) {
		reportInstance.setInstance(reports);
	}

}
