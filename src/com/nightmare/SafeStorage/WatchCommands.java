package com.nightmare.SafeStorage;
//package com.nightmare.Stalker;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//
//public class WatchCommands{
//
//	/**
//	 * @param args
//	 * @throws IOException
//	 * 
//	 */
//
//	public static Set<String> lines1;
//	public static Set<String> lines2;
//	public static Set<String> symmetricDifference;
//	public static int numberOfLines1, numberOfLines2;
//
//	public static int watchNumber = 1;
//	public static String file1Path, file2Path, watchPath;
//	public static File checkFile1, checkFile2;
//
//	public static void makeWatchFile() throws IOException {
//		// File directory = new File("c:\\Users\\trent_rand\\Pictures\\");
//		if (watchPath != null) {
//
//			File watchFile = new File(watchPath);
//
//			File[] files = watchFile.listFiles();
//			// TODO
//			 File file = new File("/sdcard/file" + watchNumber + ".txt");
////			File file = new File("file" + watchNumber + ".txt");
//
//			BufferedWriter writter = new BufferedWriter(new FileWriter(file));
//			for (int index = 0; index < files.length; index++) {
//				writter.write(files[index].toString());
//				writter.newLine();
//				// Print out the name of files in the directory
//				// System.out.println(files[index].toString());
//			}
//			watchNumber = (watchNumber == 1) ? 2 : 1;
//
//			if (watchNumber == 1) {
//				file1Path = file.getAbsolutePath();
//			} else {
//				file2Path = file.getAbsolutePath();
//			}
//			// setClipboardData(file.getAbsolutePath());
//			writter.close();
//		} else {
//			System.out.println("Watch Path Not Set");
//		}
//		// System.out.println("File 1 path: " + file1Path);
//		// System.out.println("File 2 path: " + file2Path);
//	}
//
//	public static boolean checkIfComparable() {
//		// TODO
//		 checkFile1 = new File("/mnt/sdcard/file1.txt");
//		 checkFile2 = new File("/mnt/sdcard/file2.txt");
////		checkFile1 = new File("C:/Workspace/Stalker-Test/file1.txt");
////		checkFile2 = new File("C:/Workspace/Stalker-Test/file2.txt");
//		if (checkFile1.exists() && checkFile2.exists()) {
//			System.out.println("true");
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public static void deleteDuplicates() {
//		 List<String> linesList1 = new ArrayList<String>();
//		 List<String> linesList2 = new ArrayList<String>();
//
//		try {
//			// Open the file that is the first
//			// command line parameter
//			FileInputStream fstream1 = new FileInputStream(checkFile1);
//			// Get the object of DataInputStream
//			DataInputStream in1 = new DataInputStream(fstream1);
//			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
//
//			FileInputStream fstream2 = new FileInputStream(checkFile2);
//			// Get the object of DataInputStream
//			DataInputStream in2 = new DataInputStream(fstream2);
//			BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));
//
////			String[] lineString1 = new String[numberOfLines1];
////			String[] lineString2 = new String[numberOfLines2];
//		        String line1 = null;
//		        while ((line1 = br1.readLine()) != null) {
//		            linesList1.add(line1);
//		        }
//		        System.out.println(linesList1.toString());
//		        //TODO Seperate readers
//		        String line2 = null;
//		        while ((line2 = br2.readLine()) != null) {
//		            linesList2.add(line2);
//		        }
//		        System.out.println(linesList2.toString());
////			for (int x = 0; x < numberOfLines1; x++) {
////				lineString1[x] = br1.readLine();
////				System.out.println(br1.readLine());
////			}
////			
////			for (int y = 0; y < numberOfLines2; y++) {
////				lineString2[y] = br2.readLine();
////			}
//			in1.close();
//			in2.close();
//			br1.close();
//			br2.close();
//
//			Set<String> notInSet1 = new HashSet<String>(linesList2);
//	        notInSet1.removeAll(linesList1);
//	        Set<String> notInSet2 = new HashSet<String>(linesList1);
//	        notInSet2.removeAll(linesList2);
//	 
//	        // The symmetric difference is the concatenation of the two individual differences
//	        symmetricDifference = new HashSet<String>(notInSet1);
//	        symmetricDifference.addAll(notInSet2);
//	        
//	        System.out.println(symmetricDifference);
//	        
//	        System.out.println("Sending email");
//
//	        StalkerActivity.sendEmail();
//	        
//	        
//		} catch (Exception e) {// Catch exception if any
//			System.err.println("Error: " + e.getMessage());
//		}
//	}
//}
