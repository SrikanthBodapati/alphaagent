package com.infralabs.agent.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.infralabs.agent.scheduledjobs.Alert;


public class GeneralUtils {
	public static String[] listFile(String folder, String ext) {
		GenericExtFilter filter = new GenericExtFilter(ext);

		File dir = new File(folder);

		if(dir.isDirectory()==false){
			System.out.println("Directory does not exists : " + dir);
			return new String[0];
		}

		// list out all the file name and filter by the extension
		String[] list = dir.list(filter);

		if (list.length == 0) {
			System.out.println("no files end with : " + ext);
			return list;
		}
		
		int count=0;
		for (String file : list) {
			String temp = new StringBuffer(dir.getAbsolutePath()).append(File.separator)
					.append(file).toString();
			list[count]=temp;
			count++;
		}
		return list;
	}
	

		public static List<String> ReadCsvFile(String fileName) {

			List<String> list = new ArrayList<>();

			try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

				//1. filter line 3
				//2. convert all content to upper case
				//3. convert it into a List
				list = stream.collect(Collectors.toList());

			} catch (IOException e) {
				e.printStackTrace();
			}
			list.forEach(System.out::println);
			return list;
		}
		
		public static void clearFile(String fileName) {
			try {
				File file = new File(fileName);

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(fileName); //this erases previous content
				fw = new FileWriter(fileName); 
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw);
				pw.write("");
				pw.flush();
				pw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	// inner class, generic extension filter
	public static class GenericExtFilter implements FilenameFilter {

		private String ext;

		public GenericExtFilter(String ext) {
			this.ext = ext;
		}

		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}
}
