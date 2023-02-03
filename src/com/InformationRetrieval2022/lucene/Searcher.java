package com.InformationRetrieval2022.lucene;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

	static IndexReader ireader;
	static IndexSearcher isearcher;
	
	
	static QueryParser queryParser;
	static Query query;

	
	public Searcher()
	{
		try
		{
			String tempPath = Paths.get("").toAbsolutePath().toString() + "\\Indexes"; //Path for Indexes
			Directory dir = FSDirectory.open(Paths.get(tempPath)); //Fetch above directory for file storing
			ireader = DirectoryReader.open(dir); //Same directory for reading
			isearcher = new IndexSearcher(ireader); //Use above reader for searching
			
			
			
		}
		catch (Exception e)
		{
			System.out.println("EXCEPTION: " + e);
		}
	}
	

	
	public String docToString(ScoreDoc sd, String info)
	{
		try
		{ 
			
			return isearcher.doc(sd.doc).get(info);
		}
		catch (Exception e)
		{
			System.out.println("ERROR!");
			return null;
		}
	}
	
	public TopDocs search(String s, String field)
	{
		try
		{
			//SMART SEARCH
			if (field.equals("Advanced"))
			{
				//Proceeds to do multiple searches on 3 sections (title, overview, original language)
				//It finds all unique finds between them, adding up their scores with special modifiers.
				//For instance, a document that hits on both title and overview will go higher than one that hits only on overview.
				//Each field has a modifier to affect its weight. Title has x0.5, Overview x5 and Original Language x10, 
				QueryParser qPTitle = new QueryParser("title", new StandardAnalyzer());
				QueryParser qPOverview = new QueryParser("overview", new StandardAnalyzer());
				QueryParser qPLanguage = new QueryParser("original_language", new StandardAnalyzer());
			
				Query qTitle = qPTitle.parse(s);
				Query qOverview = qPOverview.parse(s);
				Query qLanguage = qPLanguage.parse(s);
			
				
				TopDocs dTitle = isearcher.search(qTitle, 100);
				TopDocs dOverview = isearcher.search(qOverview, 100);
				TopDocs dLanguage = isearcher.search(qLanguage, 100);
				
				//we are using a Hashmap to keep the score results
				HashMap<ScoreDoc,Float> results = new HashMap<ScoreDoc,Float>();
				
				
				for (ScoreDoc i : dTitle.scoreDocs)
				{
					results.put(i, i.score*0.5f);					
				}
				
				for (ScoreDoc i : dOverview.scoreDocs)
				{
					boolean exists = false;
					ScoreDoc temp = null;
					for (ScoreDoc j : results.keySet())
					{
						if (docToString(j, "title").equals(docToString(i,"title")))
						{
							exists = true;
							temp = j;
							break;
						}
					}
					
					if (exists)
					{
						results.put(temp, temp.score + i.score*5.0f);
					}
					else
					{
						results.put(i, i.score*5.0f);
					}
					
				}
				
				
				for (ScoreDoc i : dLanguage.scoreDocs)
				{
					boolean exists = false;
					ScoreDoc temp = null;
					for (ScoreDoc j : results.keySet())
					{
						if (docToString(j, "title").equals(docToString(i,"title")))
						{
							exists = true;
							temp = j;
							break;
						}
					}
					
					if (exists)
					{
						results.put(temp, temp.score + i.score*10f);
					}
					else
					{
						results.put(i, i.score*10f);
					}
					
				}
				
				//
				ScoreDoc[] tempResults = new ScoreDoc[results.keySet().size()];
				
				int ii = 0;
				for (ScoreDoc i : results.keySet())
				{
					tempResults[ii] = i;
					ii++;
				}
				
				for (int i = 0; i < tempResults.length-1; i++)
				{
					for (int j = 0; j < tempResults.length-1-i; j++)
					{
						if (tempResults[j] == null || tempResults[j+1] == null) { continue; }
						if (results.get(tempResults[j]) < results.get(tempResults[j+1]))
						{
							ScoreDoc temp2 = tempResults[j];
							tempResults[j] = tempResults[j+1];
							tempResults[j+1] = temp2;
						}
					}
				}

				TopDocs top = new TopDocs(null, tempResults);
				return top;
								 
			}
			//this is for the simple search of each field 		
			else{
				queryParser = new QueryParser(field, new StandardAnalyzer());
				query = queryParser.parse(s); //Form a query of given string s.
				return isearcher.search(query,100); //Fetch and return the top (100) results.
			}
		}
		catch (Exception e)
		{
			System.out.println("Error!" + e);
			return null;
		}
	}
}

