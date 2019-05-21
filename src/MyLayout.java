import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import ij.ImagePlus;
import ij.gui.HistogramWindow;
import ij.gui.PlotWindow;
import ij.plugin.ContrastEnhancer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MyLayout extends JFrame{
	
	public enum Thresholding{TRES_OFF, AUTO_TRES, MANUAL_TRES};
	
	private int xSize = 1360; 
	private int ySize = 700;
	private Dimension actualDim = new Dimension(xSize, ySize);
	private JPanel mRowImagePanel, mFinalImagePanel, mSegmToolsPanel, mMainPanel;
	private JButton mBrowserButton, mMakeSeg, mSaveButton;
	private JFileChooser mJFileChooser;
	private FileFilter mFilter;
	private Image mImage;
	private DICOM_processing mDicomFile;
	private String mFilePath, mImageName;
	private JCheckBox mSmoothCB, mFindEdgesCB, mMedianBlurCB, mGaussianBlurCB, mFindContours, mOpenAndClose , mShowHistograms, mEmptyCB6;
	private JRadioButton mAutoModeRB, mManualModeRB, mAutoTresholdingRB, mManualTresholdingRB, mTresholdingOFF;
	private ButtonGroup mTresholdingBG, mModeBG;
	private JRadioButtonMenuItem mModeGroup, mTreshGroup;
	private JSlider mTresholdSdr;
	private boolean mIsAutoMode;
	private GroupLayout mRBsLayout, mCheckBoxLayout, mSegToolsLayout, mButtonsLayout;
	private JPanel mRBsLayoutPanel, mCheckBoxLayoutPanel, mButtonsPanel, mThresholdingPanel, mModePanel;
	private final int mXButSize=100;
	private final int mYButSize=25;
	private SegParameters mSegParameters=null;
	private Thresholding mThreshChoice;
	private JLabel mRowImageLabel, mFinalImageLabel;
	
	public MyLayout(){
		mThreshChoice = Thresholding.AUTO_TRES;
		mSegParameters = new SegParameters();
	}
	
    public void createFrame()
    {
    	this.setTitle("TRUS segmentation");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

    }
    
    public void createGui(){
    	createPanels();   	
    	setMainLayout();
    	addComponents();
    }
    
    private void createPanels(){
    	
    	this.add(this.mMainPanel = new JPanel());
    	mMainPanel.setBorder(BorderFactory.createEmptyBorder());
    	mMainPanel.setSize(actualDim);

    	this.add(this.mFinalImagePanel = new JPanel());
    	mFinalImagePanel.setBorder(BorderFactory.createTitledBorder("Final Image"));
    	mMainPanel.add(mFinalImagePanel);
    	
    	this.add(this.mRowImagePanel = new JPanel());
    	mRowImagePanel.setBorder(BorderFactory.createTitledBorder("Row Image"));
    	mMainPanel.add(mRowImagePanel);
    	
    	this.add(this.mSegmToolsPanel = new JPanel());
    	mSegmToolsPanel.setBorder(BorderFactory.createTitledBorder("Segmentation tools"));
    	mMainPanel.add(mSegmToolsPanel);
    	
    	mButtonsPanel = new JPanel();
		mRBsLayoutPanel = new JPanel();
		mCheckBoxLayoutPanel = new JPanel();
		mThresholdingPanel = new JPanel();
		mModePanel = new JPanel();
		mRBsLayoutPanel.setBorder(BorderFactory.createEmptyBorder());
		mCheckBoxLayoutPanel.setBorder(BorderFactory.createEmptyBorder());
		mButtonsPanel.setBorder(BorderFactory.createEmptyBorder());
		mThresholdingPanel.setBorder(BorderFactory.createTitledBorder("Threshold"));
		mModePanel.setBorder(BorderFactory.createTitledBorder("Segmantation mode"));
		mThresholdingPanel.setMaximumSize(new Dimension(1200, 40));
		mModePanel.setMaximumSize(new Dimension(1200, 40));
    }
    
    private void addComponents(){
    	addFileChooser();
    	addMakeSegmatationButton();
    	addSaveButton();
    	setFilters();
    //	addCheckBoxes();
    //	addmMakeManulSegButton();
    	addSelectThresholding();
    	addSelectMode();
    	addTresholdSlider();
    	addCheckBoxes();
    	makeSegToolsLayout();
    	setManualToolsActive(false);
    	setSelectedCheckBoxes();
    }
    
    private void addFileChooser(){
    	mBrowserButton = new JButton("Open an image");
    	//mBrowserButton.setSize(mXButSize,mYButSize);
    	mBrowserButton.setMaximumSize(new Dimension(mXButSize,mYButSize));
    	mBrowserButton.setPreferredSize(new Dimension(mXButSize,mYButSize));
    	mButtonsPanel.add(mBrowserButton);
    	mBrowserButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				mJFileChooser = new JFileChooser("C:\\Users\\Andrzekj\\Desktop");
				mJFileChooser.addChoosableFileFilter(mFilter);
				
				if (mJFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					
					mFinalImagePanel.removeAll();
					mRowImagePanel.removeAll();
	
					File file = mJFileChooser.getSelectedFile(); //getting image to file  
					mFilePath = file.getAbsolutePath(); //getting file path 
					
					//getting image name 
					int index = mFilePath.lastIndexOf('/');
					mImageName = mFilePath.substring(index+1);
										
					mDicomFile = new DICOM_processing();	
					mImage = mDicomFile.getDICOMImage(mFilePath);
								
					//display selected image
					mRowImageLabel = new JLabel();
					mRowImageLabel.setIcon(new ImageIcon(mImage));
					mRowImagePanel.add(mRowImageLabel);
					
					mRowImagePanel.setVisible(true);
					mRowImageLabel.setSize(xSize/2-15, ySize/2-15);
				    setLocationRelativeTo(null);
				    setVisible(true);
//					pack();

				}
			}
		});
    }
  
    private void setFilters(){
    	mFilter = new FileFilter() {
			
			@Override
			public String getDescription() {
				 return "Just Images";
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
		            return true;
		        }

		        String extension = Utils.getExtension(f);
		        if (extension != null) {
		            if (extension.equals(Utils.tiff) ||
		                extension.equals(Utils.tif) ||
		                extension.equals(Utils.gif) ||
		                extension.equals(Utils.jpeg) ||
		                extension.equals(Utils.jpg) ||
		                extension.equals(Utils.dicom) ||
		                extension.equals(Utils.bmp) ||
		                extension.equals(Utils.png)) {
		                    return true;
		            } else {
		                return false;
		            }
		        }

		        return false;
		    }
		};
    } 
    
    private void setMainLayout(){
    	
    	GroupLayout layout = new GroupLayout(mMainPanel); 
    	layout.setAutoCreateGaps(true);
    	layout.setHorizontalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
    				.addGroup(layout.createSequentialGroup()
    	        		.addComponent(mRowImagePanel)
            			.addComponent(mFinalImagePanel))
    				.addComponent(mSegmToolsPanel)));
    	layout.setVerticalGroup(layout.createSequentialGroup()
        		.addGroup(layout.createSequentialGroup()
            		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	        		.addComponent(mRowImagePanel)
    	        		.addComponent(mFinalImagePanel))	 
            		.addComponent(mSegmToolsPanel)));
    	mMainPanel.setLayout((LayoutManager) layout);
    	pack();
    }
    

    private void addMakeSegmatationButton(){
    	mMakeSeg = new JButton("Make Segmantation");
    	mButtonsPanel.add(mMakeSeg);
    	mMakeSeg.setSize(mXButSize,mYButSize);
    	mMakeSeg.setMaximumSize(new Dimension(mXButSize,mYButSize));
    	mMakeSeg.setPreferredSize(new Dimension(mXButSize,mYButSize));
    	
    	mMakeSeg.addActionListener(new ActionListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				mFinalImagePanel.removeAll();
				if(!mAutoModeRB.isSelected()){
					getManualParameters();
				}
				else{
					mSegParameters = new SegParameters();
				}
				
				if(mFilePath!=null){
					Segmentation sg = new Segmentation(mImage);
					Image segmentedImage=mDicomFile.getDICOMImage(mFilePath);
					
					
					ImagePlus imp = new ImagePlus(mImageName, segmentedImage);
					mDicomFile.letsDoSth(imp,mSegParameters);
					segmentedImage=imp.getImage();

					
					HistogramWindow hist = new HistogramWindow(imp);
					int hh[] = hist.getHistogram();
					float[] ff = new float[256];
					float[] hhh = new float[256];
					for (int i=0; i<hh.length; ++i){
						hhh[i] = hh[i];
						ff[i] = i;
					}
					if(mSegParameters.ismShowHistograms()){
						PlotWindow PW = new PlotWindow("Row", "x", "y", ff, hhh);
						PW.draw();
					}
					ContrastEnhancer EQ = new ContrastEnhancer();
					EQ.equalize(imp);
					
					segmentedImage=imp.getImage();
					
					HistogramWindow hist2 = new HistogramWindow(imp);
					int hh2[] = hist2.getHistogram();
					float[] ff2 = new float[256];
					float[] hhh2 = new float[256];
					for (int i=0; i<hh2.length; ++i){
						hhh2[i] = hh2[i];
						ff2[i] = i;
						
					}
					
					if(mSegParameters.ismShowHistograms()){
						PlotWindow PW2 = new PlotWindow("Final", "x", "y", ff2, hhh2);
						PW2.draw();
					}

					segmentedImage=sg.letsDoSomeFlitration(segmentedImage,mSegParameters);
					segmentedImage=sg.callArgorithm(segmentedImage, mSegParameters);

					
					mFinalImageLabel = new JLabel();
					mFinalImageLabel.setIcon(new ImageIcon(segmentedImage));
	
					mFinalImagePanel.add(mFinalImageLabel);
					mFinalImagePanel.setVisible(true);
					
			//		mFinalImageLabel.setSize(xSize/2-15, ySize/2-15);
					mFinalImageLabel.setLocation(10, 20);
					setLocationRelativeTo(null);
				    setVisible(true);
				}
				else{
					JOptionPane.showMessageDialog(null,"Please select an image!", "There is no image selected",JOptionPane.ERROR_MESSAGE);
				}
			}
		});	
    }
    
    private void addCheckBoxes(){
    	    	
    	mSmoothCB = new JCheckBox("Smooth");
    	mFindEdgesCB = new JCheckBox("Canny edges");
    	mMedianBlurCB = new JCheckBox("Median blur");
    	mGaussianBlurCB = new JCheckBox("Gaussian blur");
    	mFindContours = new JCheckBox("Find contours");
    	mOpenAndClose = new JCheckBox("Open and close shapes");
    	mShowHistograms = new JCheckBox("Show Histograms");
    	mEmptyCB6 = new JCheckBox("mEmptyCB6");
    	
    	mSegmToolsPanel.add(mSmoothCB);
    	mSegmToolsPanel.add(mFindEdgesCB);
    	mSegmToolsPanel.add(mMedianBlurCB);
    	mSegmToolsPanel.add(mGaussianBlurCB);
    	mSegmToolsPanel.add(mFindContours);
    	mSegmToolsPanel.add(mOpenAndClose);
    	mSegmToolsPanel.add(mShowHistograms);
    	mSegmToolsPanel.add(mEmptyCB6);
    	
    	mCheckBoxLayoutPanel.add(mSmoothCB);
    	mCheckBoxLayoutPanel.add(mFindEdgesCB);
    	mCheckBoxLayoutPanel.add(mMedianBlurCB);
    	mCheckBoxLayoutPanel.add(mGaussianBlurCB);
    	mCheckBoxLayoutPanel.add(mFindContours);
    	mCheckBoxLayoutPanel.add(mOpenAndClose);
    	mCheckBoxLayoutPanel.add(mShowHistograms);
    	mCheckBoxLayoutPanel.add(mEmptyCB6); 	
    	
    }
    
    private void makemCheckBoxLayout(){
    	mCheckBoxLayout = new GroupLayout(mCheckBoxLayoutPanel); 
    	mCheckBoxLayoutPanel.setLayout(mCheckBoxLayout);
        mCheckBoxLayout.setAutoCreateGaps(true);
        mCheckBoxLayout.setHorizontalGroup(mCheckBoxLayout.createSequentialGroup()
	        		.addGroup(mCheckBoxLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        		.addComponent(mSmoothCB)
		        		.addComponent(mFindEdgesCB)
		        		.addComponent(mMedianBlurCB)
	        			.addComponent(mGaussianBlurCB))
	        		.addGroup(mCheckBoxLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        		.addComponent(mFindContours)
		        		.addComponent(mOpenAndClose)
		        		.addComponent(mShowHistograms)));
        
        mCheckBoxLayout.setVerticalGroup(mCheckBoxLayout.createSequentialGroup()
        		.addGroup(mCheckBoxLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        		.addComponent(mSmoothCB)
	        		.addComponent(mFindContours))
        		.addGroup(mCheckBoxLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    				.addComponent(mFindEdgesCB)
	        		.addComponent(mOpenAndClose))
        		.addGroup(mCheckBoxLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        		.addComponent(mMedianBlurCB)
	        		.addComponent(mShowHistograms))
        		.addGroup(mCheckBoxLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	        		.addComponent(mGaussianBlurCB)));
        pack();
    }
    
    private void makemRBsLayout(){
    	mRBsLayout = new GroupLayout(mRBsLayoutPanel); 
    	mRBsLayoutPanel.setLayout(mRBsLayout);
    	mRBsLayout.setAutoCreateGaps(true);
    	mRBsLayout.setHorizontalGroup(mRBsLayout.createSequentialGroup()
	        		.addGroup(mRBsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        		.addComponent(mModePanel)
		        		.addComponent(mThresholdingPanel)
		        		.addComponent(mTresholdSdr)));
    	
    	mRBsLayout.setVerticalGroup(mRBsLayout.createSequentialGroup()
	        		.addGroup(mRBsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        		.addComponent(mModePanel))
	        		.addGroup(mRBsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	    				.addComponent(mThresholdingPanel))
    				.addGroup(mRBsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    					.addComponent(mTresholdSdr)));
    	pack();
    }
    
    private void makeButtonsLayout(){
    	mButtonsLayout = new GroupLayout(mButtonsPanel); 
    	mButtonsPanel.setLayout(mButtonsLayout);
    	mButtonsLayout.setAutoCreateGaps(true);
    	mButtonsLayout.setHorizontalGroup(mButtonsLayout.createSequentialGroup()
	        		.addGroup(mButtonsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        		.addComponent(mBrowserButton)
		        		.addComponent(mMakeSeg)
		        		.addComponent(mSaveButton)));
    	
    	mButtonsLayout.setVerticalGroup(mButtonsLayout.createSequentialGroup()
	        		.addGroup(mButtonsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        		.addComponent(mBrowserButton))
    				.addGroup(mButtonsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    					.addComponent(mMakeSeg))
					.addGroup(mButtonsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)	
    					.addComponent(mSaveButton)));
    	pack();
    }
    
    private void makeSegToolsLayout(){
    	makeButtonsLayout();
    	makemRBsLayout();
    	makemCheckBoxLayout();
    	
    	mSegToolsLayout = new GroupLayout(mSegmToolsPanel); 
    	mSegmToolsPanel.setLayout(mSegToolsLayout);
    	mSegToolsLayout.setAutoCreateGaps(true);
    	mSegToolsLayout.setHorizontalGroup(mSegToolsLayout.createSequentialGroup()
	        		.addGroup(mSegToolsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        		.addComponent(mRBsLayoutPanel))
	        		.addGroup(mSegToolsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        		.addComponent(mCheckBoxLayoutPanel))
	        		.addGroup(mSegToolsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        		.addComponent(mButtonsPanel)));
    	
    	mSegToolsLayout.setVerticalGroup(mSegToolsLayout.createSequentialGroup()
	        		.addGroup(mSegToolsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		        		.addComponent(mRBsLayoutPanel)
	    				.addComponent(mCheckBoxLayoutPanel)
            			.addComponent(mButtonsPanel)));
    	pack();
    }
    
    private void addSelectMode(){
    	mModeGroup = new JRadioButtonMenuItem();
    	mModeBG = new ButtonGroup();
    	
    	mAutoModeRB = new JRadioButton("Auto", false);
    	mManualModeRB = new JRadioButton("Manual", false);
    	
    	mModeGroup.add(mAutoModeRB);
    	mModeGroup.add(mManualModeRB);
    	
        mModeBG.add(mAutoModeRB);
        mModeBG.add(mManualModeRB);
        
        mModeGroup.setLayout(new BoxLayout(mModeGroup,BoxLayout.X_AXIS));
        mSegmToolsPanel.add(mModeGroup);
        mAutoModeRB.setSelected(true);
        mRBsLayoutPanel.add(mModeGroup);

		mModePanel.add(mManualModeRB);
		mModePanel.add(mAutoModeRB);
        
        mAutoModeRB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mIsAutoMode = false;
				setManualToolsActive(mIsAutoMode);
			}
		});
        mManualModeRB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mIsAutoMode = true;
				setManualToolsActive(mIsAutoMode);
			}
		});
        
    }
    
    private void addSelectThresholding(){
    	mTreshGroup = new JRadioButtonMenuItem();
    	mTresholdingBG = new ButtonGroup();
    	
    	mAutoTresholdingRB = new JRadioButton("Auto", false);
    	mManualTresholdingRB = new JRadioButton("Manual", false);
    	mTresholdingOFF = new JRadioButton("OFF", false);
    	
    	mTreshGroup.add(mAutoTresholdingRB);
    	mTreshGroup.add(mManualTresholdingRB);
    	mTreshGroup.add(mTresholdingOFF);
    	
    	mTresholdingBG.add(mAutoTresholdingRB);
    	mTresholdingBG.add(mManualTresholdingRB);
    	mTresholdingBG.add(mTresholdingOFF);
    	
    	mTreshGroup.setLayout(new BoxLayout(mTreshGroup,BoxLayout.X_AXIS));
    	
//        mSegmToolsPanel.add(mTreshGroup);
        mRBsLayoutPanel.add(mTreshGroup);
        
        mAutoTresholdingRB.setSelected(true);
        
        mThresholdingPanel.add(mAutoTresholdingRB);
        mThresholdingPanel.add(mManualTresholdingRB);
        mThresholdingPanel.add(mTresholdingOFF);
        
        mAutoTresholdingRB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mAutoTresholdingRB.isSelected()){
					mThreshChoice = Thresholding.AUTO_TRES;
				}
				else{
					JOptionPane.showMessageDialog(null, "Sellection error");
				}
				
				mTresholdSdr.setEnabled(false);
			}
		});
        mManualTresholdingRB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mManualTresholdingRB.isSelected()){
					mThreshChoice = Thresholding.MANUAL_TRES;
				}
				else{
					JOptionPane.showMessageDialog(null, "Sellection error");
				}
				mTresholdSdr.setEnabled(true);
			}
		});
        
        mTresholdingOFF.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mTresholdingOFF.isSelected()){
					mThreshChoice = Thresholding.TRES_OFF;
				}
				else{
					JOptionPane.showMessageDialog(null, "Sellection error");
				}
				mTresholdSdr.setEnabled(false);
			}
		});
    }
    
    private void addTresholdSlider(){
    	final int TRS_MIN = 0;
    	final int TRS_MAX = 255;
    	final int TRS_INIT = 127; 

    	mTresholdSdr = new JSlider(JSlider.HORIZONTAL, TRS_MIN, TRS_MAX, TRS_INIT);

    	mTresholdSdr.setMajorTickSpacing(100);
    	mTresholdSdr.setMinorTickSpacing(1);
    	mTresholdSdr.setPaintTicks(true);
    	mTresholdSdr.setPaintLabels(true);
    	
    	Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
    	labelTable.put( new Integer( 0 ), new JLabel("White") );
    	labelTable.put( new Integer( TRS_MAX ), new JLabel("Balck") );
    	mTresholdSdr.setLabelTable( labelTable );
    	mTresholdSdr.setPaintLabels(true);
    	mTresholdSdr.setMaximumSize(new Dimension(1000, 40));
    	mRBsLayoutPanel.add(mTresholdSdr);
    }
    
    private void setManualToolsActive(boolean isActive){
    	mAutoTresholdingRB.setEnabled(isActive);
    	mManualTresholdingRB.setEnabled(isActive);
    	mTresholdingOFF.setEnabled(isActive);
    	mSmoothCB.setEnabled(isActive);
    	mFindEdgesCB.setEnabled(isActive);
    	mMedianBlurCB.setEnabled(isActive);
    	mGaussianBlurCB.setEnabled(isActive);
    	mFindContours.setEnabled(isActive);
    	mOpenAndClose.setEnabled(isActive);
    	mShowHistograms.setEnabled(isActive);
    	mEmptyCB6.setEnabled(isActive);
    	
    	if(isActive==true){
	    	if(mAutoTresholdingRB.isSelected()){
	    		mTresholdSdr.setEnabled(false);
			}
			else if(mManualTresholdingRB.isSelected()){
				mTresholdSdr.setEnabled(true);
			}
			else{
				mTresholdSdr.setEnabled(false);
			}
    	}
    	else{
    		mTresholdSdr.setEnabled(false);
    	}
    }
    
    private void getManualParameters(){
    	
    	mSegParameters.setmIsSmooth(mSmoothCB.isSelected());
    	mSegParameters.setmIsFindEdges(mFindEdgesCB.isSelected());
    	mSegParameters.setmBlur(mMedianBlurCB.isSelected());
    	mSegParameters.setmGaussianBlurCB(mGaussianBlurCB.isSelected());
    	
    	if(mThreshChoice==Thresholding.MANUAL_TRES){
    		mSegParameters.setmTreshValue(mTresholdSdr.getValue());
    		mSegParameters.setmThresholding(Thresholding.MANUAL_TRES);
    	}
    	else{
    		mSegParameters.setmThresholding(mThreshChoice);
    	}
    	mSegParameters.setmShowHistograms(mShowHistograms.isSelected());
    	mSegParameters.setmFindContours(mFindContours.isSelected());
    	mSegParameters.setmOpenAndClose(mOpenAndClose.isSelected());
    	
    }
    
    private void addSaveButton(){
    	mSaveButton = new JButton("Save the image");
    	mButtonsPanel.add(mSaveButton);
    	mSaveButton.addActionListener(new ActionListener() {
    		
    		@Override
			public void actionPerformed(ActionEvent e) {

    			JFileChooser fs = new JFileChooser(new File("C:\\Users\\Andrzekj\\Desktop"));
    			fs.setDialogTitle("Save the segmented image");
    			fs.addChoosableFileFilter(new FileNameExtensionFilter("*.png", "png"));
    			if (fs.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
    			

    				Icon icon= mFinalImageLabel.getIcon();  				
    				int w = icon.getIconWidth();
					int h = icon.getIconHeight();
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice gd = ge.getDefaultScreenDevice();
					GraphicsConfiguration gc = gd.getDefaultConfiguration();
					BufferedImage image2 = gc.createCompatibleImage(w, h);
					Graphics2D g = image2.createGraphics();
					icon.paintIcon(null, g, 0, 0);
					g.dispose();
			
	    			try{

	    				ImageIO.write(image2, "png", new File(fs.getSelectedFile().toString()));
	    				JOptionPane.showMessageDialog(null, "Imege successfully saved!","Image saved",JOptionPane.INFORMATION_MESSAGE);
	    			} catch (Exception e1) {
	    				e1.printStackTrace();
	    				JOptionPane.showMessageDialog(null, "Saving fail","Saving error",JOptionPane.ERROR_MESSAGE);

	    			}
    			}
    			else{
    				JOptionPane.showMessageDialog(null, "There is no image for save","Saving error",JOptionPane.ERROR_MESSAGE);
    			}
			}
    		
 		});
    }
    
 private void setSelectedCheckBoxes() {
    	
    	if(mSegParameters.ismBlur()){
    		mMedianBlurCB.setSelected(true);
    	}
    	else{
    		mMedianBlurCB.setSelected(false);
    	}
    	
    	if(mSegParameters.ismIsSmooth()){
    		mSmoothCB.setSelected(true);
    	}
    	else{
    		mSmoothCB.setSelected(false);
    	}
    	
    	if(mSegParameters.ismIsFindEdges()){
    		mFindEdgesCB.setSelected(true);
    	}
    	else{
    		mFindEdgesCB.setSelected(false);
    	}
    	
    	if(mSegParameters.ismGaussianBlurCB()){
    		mGaussianBlurCB.setSelected(true);
    	}
    	else{
    		mGaussianBlurCB.setSelected(false);
    	}
    	
    	if(mSegParameters.ismOpenAndClose()){
    		mOpenAndClose.setSelected(true);
    	}
    	else{
    		mOpenAndClose.setSelected(false);
    	}
    	
    	if(mSegParameters.ismFindContours()){
    		mFindContours.setSelected(true);
    	}
    	else{
    		mFindContours.setSelected(false);
    	}
    	
    	if(mSegParameters.ismShowHistograms()){
    		mShowHistograms.setSelected(true);
    	}
    	else{
    		mShowHistograms.setSelected(false);
    	}
    	
	}
}

    
