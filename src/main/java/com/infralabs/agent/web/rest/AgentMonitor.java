package com.infralabs.agent.web.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.infralabs.agent.scheduledjobs.Reports;
import com.infralabs.agent.util.GeneralUtils;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;

@RestController
@RequestMapping("/")
public class AgentMonitor {
	@Value("${agent.scripts.dir:}")
	public String scriptsDirecotry;
	@Value("${agent.export.dir:}")
	public String statusOutputDirectory;
	public static final String EXTENSION = "csv";

	public AgentMonitor(@Value("${alphaagent.monitor.dir}") final String filename) {
	}

	@GetMapping("/monitor")
	@Timed
	public Map<String, Object> getList() {
		Map<String, Object> retval = new HashMap<String, Object>();
		if (statusOutputDirectory.equals("")) {
			statusOutputDirectory = new File("").getAbsolutePath() + "\\src\\main\\resources\\output\\";
		}
		String[] exportFiles = GeneralUtils.listFile(statusOutputDirectory, EXTENSION);
		for (String exportFile : exportFiles) {
			StringBuilder output = new StringBuilder();
			String agentOutputFile = exportFile.substring(exportFile.lastIndexOf('\\')+1, exportFile.lastIndexOf('.'));
			Stream<String> stream;
			try {
				stream = Files.lines(Paths.get(exportFile));
				stream.forEach((each -> {
					output.append(each).append("\n");
				}));
				stream.close();
				JSONArray array = null;
				if(output.length() > 0){
				      array = CDL.toJSONArray(output.toString());
				      retval.put(agentOutputFile, array.toString());
				}
				
			} catch (IOException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		return retval;
	}
	
	@GetMapping("/alerts")
	@Timed
	public Map<String, Object> getAlerts() {
		Map<String, Object> retval = new HashMap<String, Object>();
		if (statusOutputDirectory.equals("")) {
			statusOutputDirectory = new File("").getAbsolutePath() + "\\src\\main\\resources\\output\\";
		}
		String [] scriptFiles = GeneralUtils.listFile(statusOutputDirectory, EXTENSION);
		StringBuilder output = new StringBuilder();
		for (String agentFile : scriptFiles) {
			if(!agentFile.contains("_alert.csv"))
				continue;
			List<String> alerts = GeneralUtils.ReadCsvFile(agentFile);
			for(String each:alerts) {
				output.append(each.toString()).append(System.lineSeparator());
			}
			if(output.length() >0)
			   retval.put(agentFile, output.toString());

			GeneralUtils.clearFile(agentFile);
		}
		
		return retval;
	}
	
	@RequestMapping("/performAction/{hostname}/{service}/{action}")
	 public String processServiceAction(@PathVariable(value="hostname") String hostname,@PathVariable(value="service") String service,@PathVariable(value="action") String action){
	  PowerShell powerShell=null;
	   PowerShellResponse response = null;
	   String status = "";
	      try {
	          //Creates PowerShell session
	           powerShell = PowerShell.openSession();
	          //Increase timeout to give enough time to the script to finish
	          Map<String, String> config = new HashMap<String, String>();
	          config.put("maxWait", "80000");

	          //Execute script
	          if(action.equals("stop"))
	        	  response = powerShell.executeCommand(action+"-service -Force "+service);
	          else
	        	  response = powerShell.executeCommand(action+"-service "+service);

	          //Print results if the script
	          status = "executed";
	      } catch(PowerShellNotAvailableException ex) {
	          //Handle error when PowerShell is not available in the system
	          //Maybe try in another way?
	       status = "executedFail";
	      } 
	   if (powerShell != null)
	          powerShell.close();
	  return status;
	 }

}
