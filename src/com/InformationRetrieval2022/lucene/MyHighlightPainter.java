package com.InformationRetrieval2022.lucene;

import java.awt.Color;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter;

	  

public class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
	static MyHighlightPainter myHighlightPainter = new MyHighlightPainter(
		      Color.yellow);

	  public MyHighlightPainter(Color color) {
	    super(color);
	  }


	  public static void highlight(JTextArea textArea, String pattern)
		      throws Exception {
		   removeHighlights(textArea);

		    Highlighter hilite = (textArea).getHighlighter();
		    Document doc = textArea.getDocument();
		    String text = doc.getText(0, doc.getLength());
		    int pos = 0;

		    while ((pos = text.indexOf(pattern, pos)) >= 0) {
		      hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
		      pos += pattern.length();
		    }

	  }

		  public static void removeHighlights(JTextArea textArea) {
		    Highlighter hilite = textArea.getHighlighter();
		    Highlighter.Highlight[] hilites = hilite.getHighlights();

		    for (int i = 0; i < hilites.length; i++) {
		      if (hilites[i].getPainter() instanceof MyHighlightPainter) {
		        hilite.removeHighlight(hilites[i]);
		      }
		    }
		  }
		
}

		