package com.InformationRetrieval2022.lucene;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SortedNumericSortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

	static IndexWriter iwriter;

	
	
	public Indexer(String source, boolean deleteAll)
	{
		try
		{
			String tempPath = Paths.get("").toAbsolutePath().toString() + "\\" + source;
			File f = new File(tempPath);
			if (!f.exists()) { Files.createDirectories(Paths.get(tempPath)); }
			
			if (deleteAll)
			{
				for (File i : f.listFiles())
				{
					i.delete();
				}
			}
			else
			{
				if (f.listFiles().length > 0) { return; }
			}
			
			
			Directory dir = FSDirectory.open(Paths.get(tempPath)); //Fetch above directory for file storing
			StandardAnalyzer analyzer = new StandardAnalyzer(); //Using Standard Analyzer
			IndexWriterConfig config = new IndexWriterConfig(analyzer); 
		
			iwriter = new IndexWriter(dir,config); //Writer
			
			
		}
		catch (Exception e)
		{
			System.out.println("EXCEPTION: " + e);
		}
	}
	
	public void writeIndexes(String path)
	{
		if (iwriter == null) { return; }
		try
		{
			//Source file of our document library
			String source = Paths.get("").toAbsolutePath().toString() + "\\" + path;
			File dir = new File(source);
			
			
			
			File[] files = dir.listFiles(); //Fetch all JSON documents 
			
			
			String line;
			
			for (int i = 0; i < files.length; i++)
			{	
				Scanner reader = new Scanner(files[i]);
				
				if (!reader.hasNextLine()) { continue; }
				
				line = reader.nextLine();
				if (line.trim().equals("") || line.trim().equals("\n")) { continue; }
				System.out.println("Writing " + i + " : " + line + " : " + files[i].getName());
				
				
				Document d = new Document(); //Create a new document
				
				d.add(new TextField("title", line, Store.YES)); //Store each input accordingly
				
				if (!reader.hasNextLine()) { continue; } 
				line = reader.nextLine();
				if (line.trim().equals("") || line.trim().equals("\n")) { continue; }
				
				d.add(new TextField("overview", line, Store.YES));// add the overview

				
				if (!reader.hasNextLine()) { continue; } 
				line = reader.nextLine();
				if (line.trim().equals("") || line.trim().equals("\n")) { continue; }
				
				d.add(new TextField("original_language", line, Store.YES));	// add the language
				
				if (!reader.hasNextLine()) { continue; } 
				line = reader.nextLine();
				if (line.trim().equals("") || line.trim().equals("\n")) { continue; }
				
				d.add(new TextField("vote_average", line, Store.YES));// add the votes
				iwriter.addDocument(d); //Create Index Document
				
				reader.close();
				iwriter.flush();
			}
			iwriter.close();
			
			
			
		}
		catch (Exception e)
		{
			System.out.println("Problem! " + e);
		}
	}
}
