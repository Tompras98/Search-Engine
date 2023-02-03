package com.InformationRetrieval2022.main;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.InformationRetrieval2022.lucene.Load;
import com.InformationRetrieval2022.lucene.MyHighlightPainter;
import com.InformationRetrieval2022.lucene.Searcher;

public class UserInterface implements ActionListener{
	public static String newline = System.getProperty("line.separator");

	static JList<String> list; 
	static JComboBox<String> cbox;
	static JTextField textField;
	
	static Searcher searcher; //searcher class
	static String[] results = new String[5001]; //results of  top max titles
	static float[] votes = new float[5001]; //results of votes
	static String[] vote_String = new String[5001]; // for votes manipulation
	static ScoreDoc[] documents = new ScoreDoc[5001]; //documents of  top max results

	
	
	static int currentPanel = -1;	//used for navigation between result pages
	
	
	
	public static void main(String[] args)
	{
		
		//GUI
		JFrame frame = new JFrame();
		frame.setSize(700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//main panel
		JPanel panel = new JPanel();
		panel.setBounds(800,800, 200, 200);
		panel.setLayout(null);
		panel.setBackground(new Color(153, 153, 204));
		frame.add(panel);
		
		//title
		JLabel label = new JLabel("MovieIt");
		label.setFont(label.getFont().deriveFont(64.0f));
		label.setBounds(25, -180, 500, 500);
		panel.add(label);
		
		//text field for user input
		textField = new JTextField();
		textField.setBounds(25,120,320,30);
		textField.setFont(textField.getFont().deriveFont(20.0f));
		panel.add(textField);
		
		//search button
		JButton button = new JButton();
		button.setBounds(350,120,150,30);
		button.setText("Search");
		button.setFont(button.getFont().deriveFont(25.0f));
		button.addActionListener(new UserInterface());
		panel.add(button);
		
		//previous page button
		JButton Larrow_button = new JButton();
		Larrow_button.setBounds(25,460,150,20);
		Larrow_button.setText("Previous page");
		Larrow_button.setFont(Larrow_button.getFont().deriveFont(15.0f));
		Larrow_button.addActionListener(new UserInterface());
		panel.add(Larrow_button); 
		
		//next page button
		JButton Rarrow_button = new JButton();
		Rarrow_button.setBounds(345,460,150,20);
		Rarrow_button.setText("Next page");
		Rarrow_button.setFont(Rarrow_button.getFont().deriveFont(15.0f));
		Rarrow_button.addActionListener(new UserInterface());
		panel.add(Rarrow_button); 
		
		//open button
		JButton readButton = new JButton();
		readButton.setBounds(505,405,150,30);
		readButton.setText("Open");
		readButton.setFont(readButton.getFont().deriveFont(25.0f));
		readButton.addActionListener(new UserInterface());
		panel.add(readButton);
		
		//sorts by history button
		JButton sortRead = new JButton();
		sortRead.setBounds(185,165,150,30);
		sortRead.setText("Sort by History");
		sortRead.setFont(sortRead.getFont().deriveFont(14.0f));
		sortRead.addActionListener(new UserInterface());
		panel.add(sortRead);
		
		//sorts by vote average button
		JButton sortAvg = new JButton();
		sortAvg.setBounds(350,165,150,30);
		sortAvg.setText("Sort by Vote Avg");
		sortAvg.setFont(sortAvg.getFont().deriveFont(14.0f));
		sortAvg.addActionListener(new UserInterface());
		panel.add(sortAvg);
		
		
		//load documents button
		JButton documents_load = new JButton();
		documents_load.setBounds(505,450,150,30);
		documents_load.setText("Load");
		documents_load.setFont(documents_load.getFont().deriveFont(20.0f));
		documents_load.addActionListener(new UserInterface());
		panel.add(documents_load); 
		
		//list of result. outputs titles based on input
		String[] data = {}; 
		list = new JList<String>(data);
		list.setFont(list.getFont().deriveFont(15.0f)); 
		list.setBounds(25, 220, 470, 230);
		panel.add(list);
		
		//Combo Box. Lets the user choose type of search
		String[] options = {"Title","Overview","Original_Language","Vote_Average","Advanced Search"};
		cbox = new JComboBox<String>(options);
		cbox.setBounds(25,165,140,30);
		cbox.setFont(cbox.getFont().deriveFont(15.0f));
		panel.add(cbox);
		
		
		
		
		frame.setVisible(true);

			
	}

		//button event based on which action is being performed
	@Override
	public void actionPerformed(ActionEvent e) 
	{

		
		switch (((JButton)e.getSource()).getText())
		{
		
			//According to the name of the button, we choose what to do
			case "Search":
				
				if (textField.getText().equals("")) { System.out.println("Search field is empty"); return; } //checks if field is empty
				
				searcher = new Searcher();
				
				String temp = (String)cbox.getSelectedItem(); //get the type of search and do so accordingly
				if (temp.equals("Advanced Search"))
				{ 
					temp = "Advanced";
				}
				else { temp = temp.toLowerCase(); }
				
				TopDocs top = searcher.search(textField.getText(), temp); //Take top scores
				if (top == null) { System.out.println("Error (TopDocs)"); return;}
				
				
				String[] temp2 = new String[10]; //top 10 scores for output
				currentPanel = 0; 
				for (int i = 0; i < 100; i++)
				{
					if (i < top.scoreDocs.length) //max results will be copied by title
					{
						results[i] = (i+1) + ") " + searcher.docToString(top.scoreDocs[i], "title");
						documents[i] = top.scoreDocs[i]; //copy of each doc for later use
						
					}
					else
					{
						results[i] = ""; //all unused slots are turned into empty
					}
										
					if (i < 10)
					{
						temp2[i] = results[i];//top 10 results for immediate display
					
					}
				}
		
				list.setListData(temp2); //Display!
				 try{
			            // Create new file
			            String content = textField.getText() + newline ;
			            String Path = Paths.get("").toAbsolutePath().toString() + "/history.txt";
			            File file = new File(Path);
			
			            if (!file.exists()) {
			                file.createNewFile();
			            }
			
			            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			            BufferedWriter bw = new BufferedWriter(fw);
			    
			            // Write in file
			            bw.write(content );

			            // Close connection
			            bw.close();
		   
			        }
			        catch(Exception r){
			            System.out.println(r);
			        }
				break;
			case "Next page":
				if (currentPanel < 0) { System.out.println("No other page to navigate"); return; } //Can't move pages if no search has happened
				
				currentPanel++; //Go one page forward
				
				if (currentPanel == 20) { 
					currentPanel--; System.out.println("100 results reached"); 
					return;
				} 
				
				if (results[currentPanel*10].equals("")) {
					currentPanel--; 
					System.out.println("(!)No further results found."); 
					return;
					}
				
				//temp for display of top 10 on the new page
				String[] temp3 = new String[10];
				
				//Compute correct position in results
				int j = 0;
				for (int i = 10*currentPanel; i < (10*currentPanel)+10; i++)
				{
					temp3[j] = results[i];
					j++;
				}
				list.setListData(temp3); //display
				
				break;
			case "Previous page": 
				if (currentPanel < 0) { 
					System.out.println("No other page to navigate");
					return; 
				}
				
				currentPanel--;
				if (currentPanel == -1) {
					currentPanel++; System.out.println("First page reached"); 
					return; 
				}
				
				String[] temp4 = new String[10];
				int j2 = 0;
				for (int i = 10*currentPanel; i < (10*currentPanel)+10; i++)
				{
					temp4[j2] = results[i];
					j2++;
				}
				list.setListData(temp4);
				
				
				
				break;
			
			case "Open": //opens a selected document!
				if (list.getSelectedValue() == null) { System.out.println("Select a title first"); return; } //No selection case
				
				ScoreDoc selected;
				
				selected = documents[currentPanel*10 + list.getSelectedIndex()]; //Fetch chosen ScoreDoc
				
				String[] text = searcher.docToString(selected, "overview").toLowerCase().split(""); //Break it for modification
				String rText = ""; //Text that will be displayed
				
				//title.
				rText += searcher.docToString(selected, "title") + "\n ";
				rText = rText.toLowerCase();
				
				int bound = 0; //better title display
				for (int i = 0; i < text.length; i++)
				{
					
					if (bound > 100 && text[i].equals(" "))
					{
						bound = 0;
						rText += " \n  ";
					}
					else
					{
						bound++;
						rText += text[i];
					}
				}
				//for the language
				 rText += "\n " + searcher.docToString(selected, "original_language") + "\n " ;
				 //at the end we paste the vote average
				 rText += searcher.docToString(selected, "vote_average") + "\n " ;
				
				//pop-up when a title is selected
				JFrame secondaryFrame = new JFrame(); 
				secondaryFrame.setSize(1200, 800);
				
				JPanel panel2 = new JPanel(); 
				panel2.setBounds(1200,800, 200, 200);
				panel2.setLayout(null);
				secondaryFrame.add(panel2);
			

				
				JTextArea textArea = new JTextArea(rText); 
				textArea.setEditable(false);
				textArea.setFont(textArea.getFont().deriveFont(20.0f));
				
				//incase it needs some scrolling
				JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollPane.setBounds(0,0,1150,750);
				panel2.add(scrollPane);
				try {
					MyHighlightPainter.highlight(textArea,textField.getText().toLowerCase() + " ");
				} catch (Exception e1) {
		
					e1.printStackTrace();
				}
				secondaryFrame.setVisible(true);
			
				
				break;
			case "Load": //Load files
				
				JFileChooser chooser = new JFileChooser(Paths.get("").toString()); //pop-up window
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //choose directory
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					Load lo = new Load();
					
					JFrame f = new JFrame();
					boolean a,b; 
					int option = JOptionPane.showConfirmDialog(f, "Do you want to override existing documentation?");
					if (option == JOptionPane.YES_OPTION) { a = true; }
					else { a = false; }
					
					option = JOptionPane.showConfirmDialog(f, "Do you want to override existing indexes?");
					if (option == JOptionPane.YES_OPTION) { b = true; }
					else { b = false; }
					
					//setup
					lo.load(chooser.getSelectedFile().getAbsolutePath(), a, b);
					
					JOptionPane.showMessageDialog(f, "Loading proccess was successful. Welcome to MovieIt!");
				}
				
				break;
		
			case  "Sort by History":
			  
				
		        String line, word = "";  
		        int count = 0, maxCount = 0;  
		        ArrayList<String> words = new ArrayList<String>();  
		       try 
		       {   
		       //opens file 
		        FileReader file = new FileReader("history.txt");  
		        BufferedReader br = new BufferedReader(file);  
		   
		          
		        //line reader
		        while((line = br.readLine()) != null) {  
		            String string[] = line.toLowerCase().split("([,.\\s]+)");  
		            //adds all words created into words  
		            for(String s : string){  
		                words.add(s);  
		            }  
		        }
		       
		        //find the most repeated word in a file  
		        for(int i = 0; i < words.size(); i++){  
		            count = 1;  
		            //counter for each word 
		            for(int k = i+1; k < words.size(); k++){  
		                if(words.get(i).equals(words.get(k))){  
		                    count++;  
		                }   
		            }  
		            //if maxCount is less than count then store value of count in maxCount   
  
		            if(count > maxCount){  
		                maxCount = count;  
		                word = words.get(i);  
		            }
		        }
		          
		        br.close();  
		       }
		       catch (Exception p)
				{
					
				}
		       String temp7 = (String)cbox.getSelectedItem(); //gets type of search
				if (temp7.equals("Advanced Search"))
				{ 
					temp7 = "Advanced";
				}
				else { temp7 = temp7.toLowerCase(); }
		       TopDocs top2 = searcher.search(word, temp7); //top scores
				if (top2 == null) { System.out.println("(X) Something went wrong."); return;}
				
				
				String[] temp8 = new String[10]; //Top 10 scores for basic output
				currentPanel = 0; 
				for (int i = 0; i < 100; i++)
				{
					if (i < top2.scoreDocs.length) //max results by title
					{
						results[i] = (i+1) + ") " + searcher.docToString(top2.scoreDocs[i], "title");
						documents[i] = top2.scoreDocs[i]; //Also fetch a static copy of each "document" for later use
						
					}
					else
					{
						results[i] = ""; //all unused slots turned into empty
					}
										
					if (i < 10)
					{
						temp8[i] = results[i]; //top 10 results for immediate display
					}
				}
				
				
				list.setListData(temp8); 
				
				break;

		
			case "Sort by Vote Avg": //Sorts depending on search using vote average
				  
			       String temp12 = (String)cbox.getSelectedItem(); //get the type of search and do so accordingly
					if (temp12.equals("Advanced Search"))
					{ 
						temp12 = "Advanced";
					}
					else { temp12 = temp12.toLowerCase(); }
			       TopDocs top3 = searcher.search(textField.getText(), temp12); // top scores
					if (top3 == null) { System.out.println("(X) Something went wrong."); return;}
					
					
					String[] temp13 = new String[10]; //Top 10 scores for basic output
					currentPanel = 0; //Set current panel to a valid 0 value
					for (int i = 0; i < 100; i++)
					{
						if (i < top3.scoreDocs.length) //Up to 100 max results will be copied by title
						{
							results[i] =searcher.docToString(top3.scoreDocs[i], "title");
							votes[i] = Float.parseFloat(searcher.docToString(top3.scoreDocs[i], "vote_average"));
							documents[i] = top3.scoreDocs[i]; //Also fetch a static copy of each "document" for later use
							//Arrays.sort(votes );
									
							
						}
						else
						{
							results[i] = ""; //all unused slots are turned into empty
						}
											
						if (i < 10)
						{
							for(int k = 0; k < votes.length; k++) {
								 

							}
							temp13[i] =vote_String[i]; //top 10 results for immediate display
						}
					}
					//bubble sort based on votes 
					boolean swapped = true;
				    int j1 = 0;
				    float tmpSort;
				    String tmpSortR ;
				    ScoreDoc tmpSortD ;
				    while (swapped) {
				        swapped = false;
				        j1++;
				        for (int i1 = 0; i1 < votes.length - j1; i1++) {
				            if (votes[i1] > votes[i1 + 1]) {
				            	tmpSort = votes[i1];
				                tmpSortR = results[i1];
				                tmpSortD = documents[i1];
				                votes[i1] = votes[i1 + 1];
				                results[i1] = results[i1+1];
				                documents[i1] = documents[i1+1];
				                votes[i1 + 1] = tmpSort;
				                results[i1+1] = tmpSortR;
				                documents[i1+1] = tmpSortD;
				                swapped = true;
				            }
				        }
				    }
				    //reversing so list can be descending 
				    int q = 0;
				      int w = votes.length - 1;
				      float tmpMax;
				      String tmpMaxR;
				      ScoreDoc tmpMaxD ;
				      while (w > q) {
				          tmpMax = votes[w];
				          tmpMaxR = results[w];
				          tmpMaxD = documents[w];
				          votes[w] = votes[q];
				          results[w]= results[q];
				          documents[w]= documents[q];
				          votes[q] = tmpMax;
				          results[q]= tmpMaxR;
				          documents[q]= tmpMaxD;
				          w--;
				          q++;
				      }
					 
					//System.out.println(Arrays.toString(results));
					list.setListData(results); 
					break;
					}
	}
}