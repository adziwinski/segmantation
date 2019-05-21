/**
 * 
 */

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.opencv.core.Core;


/**
 * @author Andrzekj
 *
 */
public class MainClass {

	/**
	 * 
	 */
	public MainClass() {
		MyLayout l= new MyLayout();
        l.createFrame();
        l.createGui();
        l.setVisible(true);
        l.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try 
	    { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); 
	    } 
	    catch(Exception e){ 
	    }
		
		new MainClass();
		
	}

}

