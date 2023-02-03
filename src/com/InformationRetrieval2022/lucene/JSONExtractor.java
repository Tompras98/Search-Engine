package com.InformationRetrieval2022.lucene;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONExtractor {

	String sourcePath;
	
	public JSONExtractor(String source)
	{
		sourcePath = source;
	}
	
	public static String newline = System.getProperty("line.separator");
	
	public void extract(boolean deleteExisting)
	{
		try 
		{
			//Source file. Within this DIRECTORY, all JSON files are contained.
			File source = new File(sourcePath);
			File[] files = source.listFiles(); //Fetch all JSON documents within
			
			File docdir = new File(Paths.get("").toAbsolutePath().toString() + "\\Documents");
			if (!docdir.exists()) { Files.createDirectories(Paths.get(docdir.getPath())); }	
			
			if (deleteExisting)
			{
				for (File i : docdir.listFiles())
				{
					i.delete();
				}
			}
			else
			{
				if (docdir.listFiles().length > 0) { return; }
			}
			
			
			for (int i = 0; i < files.length; i++) //Run each JSON document
			{
				//Create a new TXT file for each doc
				File docs = new File(docdir.getPath() + "\\" + i + ".txt");
				if (!docs.createNewFile()) //If it exists already, delete and recreate it
				{
					docs.delete();
					docs.createNewFile();
				}

				FileWriter writer = new FileWriter(docs); //Writer for the Txt file
				
				System.out.flush();
				System.out.println("Loading Progress: " + i + "/" + files.length);
				System.out.println("Loading: " + i + " for source: " + files[i].getName()); 
				
				Scanner reader = new Scanner(files[i]); //First, we fetch the entire JSON text
				String str = ""; //in this string.
				while (reader.hasNext()) 
				{
					str += reader.nextLine();
				}
				reader.close();
				
				String documentText = ""; //Text to be written in our TXT
				JSONObject obj = new JSONObject(str); //Convert text into a Json object
				documentText += obj.getString("title") + newline; //Fetch and write title field
				
				documentText += obj.getString("overview") + newline; //Fetch and write overview field
				
				documentText += obj.getString("original_language") + newline; //Fetch and write language field
				
				documentText += obj.getString("vote_average") + newline;//Fetch and write vote avg field
				writer.write(documentText);	 //Write and close
				writer.close();
			}
			
			System.out.println("DONE!"); 
			

			
		}
		catch (Exception e)
		{
			System.out.println("Error" + e);
			
		} 
	}
	
}
