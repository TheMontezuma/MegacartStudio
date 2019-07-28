package org.montezuma.megacart;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

@SuppressWarnings("unused")
public class Manual extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final int WINDOW_WIDTH = 500;
	private static final int WINDOW_HEIGHT = 300;
	private JTextPane mTp;
	
	public void createDialog()
	{
		mTp = new JTextPane();
		mTp.setEditable(false);
		try
		{
			String manual_file_name = "manual_"+UserPreferences.sLanguage+".html";
			URL url = ClassLoader.getSystemResource(manual_file_name);
			if(null==url)
			{
				File f = new File(manual_file_name);
				url = f.toURI().toURL();
			}
			mTp.setPage(url);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		HyperlinkListener hl = new HyperlinkListener() 
		{
	        public void hyperlinkUpdate(HyperlinkEvent e)
	        {
	            if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType())
	            {
	                try
	                {
	                	Desktop.getDesktop().browse(e.getURL().toURI());
	                }
	                catch (IOException e1)
	                {
	                    e1.printStackTrace();
	                }
	                catch (URISyntaxException e2)
	                {
						e2.printStackTrace();
					}
	            }
	        }
		};
		mTp.addHyperlinkListener(hl);
		JScrollPane pane = new JScrollPane(mTp);
		add(pane);
		setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		setVisible(true);
	}
}
