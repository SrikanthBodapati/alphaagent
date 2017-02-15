package com.infralabs.agent.scheduledjobs;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.infralabs.agent.util.GeneralUtils;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;

@Component
public class AgentScriptsScheduler {
	@Value("${agent.scripts.dir:}")
	public String scriptsDirecotry;
	@Value("${agent.export.dir:}")
	public String statusOutputDirectory;
	public static final String EXTENSION="ps1";
	@Scheduled(fixedRateString = "${agent.scripts.run.frequency:5000}")
	public void reportCurrentTime() {
		if (scriptsDirecotry.equals("")) {
			scriptsDirecotry = new File("").getAbsolutePath() + "\\src\\main\\resources\\scripts\\";
		}
		if (statusOutputDirectory.equals("")) {
			statusOutputDirectory = new File("").getAbsolutePath() + "\\src\\main\\resources\\output\\";
		}
		
		
		String [] scriptFiles = GeneralUtils.listFile(scriptsDirecotry, EXTENSION);
		PowerShell powerShell=null;
		for (String agentScript : scriptFiles) {
			String agentOutputFile = statusOutputDirectory+agentScript.substring(agentScript.lastIndexOf('\\'), agentScript.lastIndexOf('.'))+".csv";
			PowerShellResponse response = null;
			   try {
			       //Creates PowerShell session
			        powerShell = PowerShell.openSession();
			       //Increase timeout to give enough time to the script to finish
			       Map<String, String> config = new HashMap<String, String>();
			       config.put("maxWait", "80000");

			       //Execute script
			       response = powerShell.configuration(config).executeScript(agentScript,agentOutputFile);

			       //Print results if the script
			       System.out.println("Script output:" + response.getCommandOutput());
			   } catch(PowerShellNotAvailableException ex) {
			       //Handle error when PowerShell is not available in the system
			       //Maybe try in another way?
			   } 
		}
		 if (powerShell != null)
	         powerShell.close();
	}

}
