package org.montezuma.megacart;

public class Application implements Runnable {
	
    public static void main(String[] args) {
    	try
    	{
    		UserPreferences.read();
    		javax.swing.SwingUtilities.invokeLater(new Application());
    	}
    	catch(Exception e)
    	{
    		System.out.println(e);
    	}
    }

	public void run() {
		Gui gui = new Gui("MegaCart Studio");
		gui.createAndShow();
	}
    
}
