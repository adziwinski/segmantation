import java.awt.Image;
import javax.swing.JOptionPane;

import org.opencv.core.Core;

import ij.*;
import ij.ImagePlus;
import ij.gui.HistogramWindow;
import ij.process.ImageProcessor;



/**_
 * @author Andrzekj
 *
 */
public class DICOM_processing extends ImagePlus{

	/**
	 * default constructor
	 */
	public DICOM_processing(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public void letsDoSth(ImagePlus imp, SegParameters segP){
		ImageProcessor ip = imp.getProcessor();
		
		if(segP!=null){
			if(segP.ismIsSmooth()){
				ip.smooth();
			}
			if(segP.getmThresholding()==MyLayout.Thresholding.AUTO_TRES){
				ip.autoThreshold();
			}
			if(segP.ismIsFindEdges()){
				ip.findEdges();
				
			}
			
		}
		else{
			JOptionPane.showMessageDialog(null, "No segementation parameters selected");
		}

	}
	
	public Image getAndProcess(String path){
		Image myImage=null;

		ImagePlus imp = IJ.openImage(path); 

		letsDoSth(imp,null);
		myImage = imp.getImage();
		return myImage;
	}
	
	public Image getDICOMImage(String path){
		Image myImage=null;

		ImagePlus imp = IJ.openImage(path); 
		myImage = imp.getImage();
		return myImage;
	}
	
	public void getHist(ImagePlus imp){
		new HistogramWindow(imp);
	}

}
