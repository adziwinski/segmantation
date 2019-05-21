import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.*;
import org.opencv.imgcodecs.Imgcodecs;

public class Segmentation{
	
	private Image mImage;
	
	public Segmentation(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * @param mImage
	 */
	public Segmentation(Image mImage) {
		super();
		this.mImage = mImage;
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public Image segmentProcess(Image im){
		
		BufferedImage img = (BufferedImage) im;
		BufferedImage imageCopy = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
		imageCopy.getGraphics().drawImage(im, 0, 0, null);

		byte[] data = ((DataBufferByte) imageCopy.getRaster().getDataBuffer()).getData();
		Mat gray = new Mat(im.getHeight(null), im.getWidth(null), CvType.CV_8UC1);
		Mat binary = new Mat(im.getHeight(null), im.getWidth(null), CvType.CV_8UC1);
		Mat mat = new Mat(im.getHeight(null), im.getWidth(null), CvType.CV_8UC1);
		mat.put(0, 0, data);  
		System.out.println("Image width = " + mat.width());
		Imgproc.cvtColor(mat, gray,  Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(gray,binary,0,255,Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
		
		//Imgproc.watershed(binary, markers);
		MatOfByte bytemat = new MatOfByte();
		
		Imgcodecs.imencode(".png", binary, bytemat);
		
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			
			e.printStackTrace();
			System.out.println("Can't read input stream");
		}
		

		return (Image) img;
	}
	
	public Mat image2Mat(Image im){
		

		BufferedImage imageCopy = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
		imageCopy.getGraphics().drawImage(im, 0, 0, null);
		byte[] data = ((DataBufferByte) imageCopy.getRaster().getDataBuffer()).getData();

		
		Mat mat1 = new Mat(imageCopy.getHeight(null), imageCopy.getWidth(null), CvType.CV_8UC3);
		mat1.put(0,0,data);
		
		return mat1;
	}
	
	public Image mat2Image(Mat mat){
	
		BufferedImage img = null;
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".png", mat, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
	
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Problem with image reading");
			JOptionPane.showMessageDialog(null, "Problem with image reading");
		}
		
		return (Image) img;
	}
	
	public Image letsDoSomeFlitration(Image img, SegParameters segP){
				
		Mat out;
		out = image2Mat(img);

		Imgproc.cvtColor(out, out,  Imgproc.COLOR_BGR2GRAY);
		
		if(segP!=null){
			
			if(segP.ismBlur()){
				Imgproc.medianBlur(out, out, 5);
			}
			if(segP.ismGaussianBlurCB()){
				Imgproc.GaussianBlur(out, out, new Size(3,3), 3);
			}
			if(segP.getmThresholding()==MyLayout.Thresholding.MANUAL_TRES){
				Imgproc.threshold(out,out,segP.getmTreshValue(),255,Imgproc.THRESH_BINARY);
			}
		}
		else{
			JOptionPane.showMessageDialog(null, "No segementation parameters are selected!");
		}
		
		img = mat2Image(out);
		
		return img;
	} 
	
	public Image callArgorithm(Image img, SegParameters segP){
		Mat mat1, out;
		mat1 = out = image2Mat(img);
				
		Imgproc.cvtColor(mat1,mat1,Imgproc.COLOR_BGR2GRAY);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>(10);
		Mat hierarchy = new Mat(200, 200, CvType.CV_8UC1, new Scalar(0));

		if(segP!=null){
			if(segP.ismOpenAndClose()){
				Mat kernel = Mat.ones(new Size(5, 5) ,CvType.CV_8UC1);
				Imgproc.morphologyEx(mat1, mat1, Imgproc.MORPH_CLOSE, kernel);
				Imgproc.morphologyEx(mat1, mat1, Imgproc.MORPH_OPEN, kernel);
			}
			if(segP.ismFindContours()){
				Imgproc.findContours( mat1, contours, hierarchy , Imgproc.RETR_TREE ,Imgproc.CHAIN_APPROX_SIMPLE);

				Imgproc.cvtColor(mat1,mat1,Imgproc.COLOR_BayerGR2RGB_EA);
				for (int i = 0; i < contours.size(); i++) {
				    Imgproc.drawContours(mat1, contours, i, new Scalar(0, 255, 0), 2);
				}
			}
			out=mat1;
		}
		else{
			JOptionPane.showMessageDialog(null, "No segementation parameters are selected!");
		}
		img = mat2Image(out);
		return img;
	}

}
