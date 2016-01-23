package com.endro32.proadmin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FileGenerator {
	
	public static boolean generateEULA(String path, boolean acc) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		Date date = calendar.getTime();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
					FileManager.appdir + "/" + path + "/eula.txt"));
			out.write("#By changing the setting below to TRUE you are indicating your agreement "
					+ "to our EULA (https://account.mojang.com/documents/minecraft_eula).");
			out.newLine();
			out.write("#"+date);
			out.newLine();
			out.write("eula="+acc);
			out.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean updateEULA(String path, boolean acc) {
		File eula = new File(FileManager.appdir + "/" + path + "/eula.txt");
		if(!eula.exists()) return false;
		try {
			BufferedReader in = new BufferedReader(new FileReader(eula));
			in.readLine();
			String date = in.readLine();
			in.close();
			BufferedWriter out = new BufferedWriter(new FileWriter(eula));
			out.write("#By changing the setting below to TRUE you are indicating your agreement "
					+ "to our EULA (https://account.mojang.com/documents/minecraft_eula).");
			out.newLine();
			out.write(date);
			out.newLine();
			out.write("eula="+acc);
			out.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
}
