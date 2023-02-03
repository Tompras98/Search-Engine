package com.InformationRetrieval2022.lucene;

import java.awt.event.WindowAdapter;


import com.InformationRetrieval2022.lucene.Indexer;
import com.InformationRetrieval2022.lucene.JSONExtractor;

public class Load extends WindowAdapter{

	
	
	public void load(String sourcePath, boolean jsonOverride, boolean indexOverride)
	{
		//JSON Extraction from source
		try
		{
			System.out.println("Starting Extraction Proccess.");
			JSONExtractor jextract = new JSONExtractor(sourcePath); //Extract JSON from selected folder
			jextract.extract(jsonOverride); //True: Delete all, create new. False: Create only if none exists
			
			Indexer im = new Indexer("Indexes", indexOverride); 
			im.writeIndexes("Documents"); //Write all indexes from the Documents file
			
			
		}
		catch (Exception e)
		{
			System.out.println("Something went wrong while loading data!");
		}
	}
}
