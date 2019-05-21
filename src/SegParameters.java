
/**
 * @author Andrzekj
 *
 */
public class SegParameters {
	
	private boolean mIsSmooth, mIsFindEdges, mBlur,mGaussianBlurCB, mOpenAndClose, mFindContours, mShowHistograms;
	private MyLayout.Thresholding mThresholding;
	private int mTreshValue;
	
	/**
	 * default constructor
	 */
	public SegParameters() {
		mIsSmooth=true;
		mIsFindEdges=false;
		mThresholding=MyLayout.Thresholding.AUTO_TRES;
		mBlur=true;
		mGaussianBlurCB=true;
		mTreshValue=127;
		mOpenAndClose = true;
		mFindContours = true;
		mShowHistograms = false;
	}

	/**
	 * @return the mIsSmooth
	 */
	public boolean ismIsSmooth() {
		return mIsSmooth;
	}

	/**
	 * @param mIsSmooth the mIsSmooth to set
	 */
	public void setmIsSmooth(boolean mIsSmooth) {
		this.mIsSmooth = mIsSmooth;
	}

	/**
	 * @return the mIsFindEdges
	 */
	public boolean ismIsFindEdges() {
		return mIsFindEdges;
	}

	/**
	 * @param mIsFindEdges the mIsFindEdges to set
	 */
	public void setmIsFindEdges(boolean mIsFindEdges) {
		this.mIsFindEdges = mIsFindEdges;
	}

	/**
	 * @return the mThresholding
	 */
	public MyLayout.Thresholding getmThresholding() {
		return mThresholding;
	}

	/**
	 * @param mThresholding the mThresholding to set
	 */
	public void setmThresholding(MyLayout.Thresholding mThresholding) {
		this.mThresholding = mThresholding;
	}

	/**
	 * @return the mTreshValue
	 */
	public int getmTreshValue() {
		return mTreshValue;
	}

	/**
	 * @param mTreshValue the mTreshValue to set
	 */
	public void setmTreshValue(int mTreshValue) {
		this.mTreshValue = mTreshValue;
	}

	/**
	 * @return the mBlur
	 */
	public boolean ismBlur() {
		return mBlur;
	}

	/**
	 * @param mBlur the mBlur to set
	 */
	public void setmBlur(boolean mBlur) {
		this.mBlur = mBlur;
	}

	/**
	 * @return the mGaussianBlurCB
	 */
	public boolean ismGaussianBlurCB() {
		return mGaussianBlurCB;
	}

	/**
	 * @param mGaussianBlurCB the mGaussianBlurCB to set
	 */
	public void setmGaussianBlurCB(boolean mGaussianBlurCB) {
		this.mGaussianBlurCB = mGaussianBlurCB;
	}

	/**
	 * @return the mOpenAndClose
	 */
	public boolean ismOpenAndClose() {
		return mOpenAndClose;
	}

	/**
	 * @param mOpenAndClose the mOpenAndClose to set
	 */
	public void setmOpenAndClose(boolean mOpenAndClose) {
		this.mOpenAndClose = mOpenAndClose;
	}

	/**
	 * @return the mFindContours
	 */
	public boolean ismFindContours() {
		return mFindContours;
	}

	/**
	 * @param mFindContours the mFindContours to set
	 */
	public void setmFindContours(boolean mFindContours) {
		this.mFindContours = mFindContours;
	}

	/**
	 * @return the mShowHistograms
	 */
	public boolean ismShowHistograms() {
		return mShowHistograms;
	}

	/**
	 * @param mShowHistograms the mShowHistograms to set
	 */
	public void setmShowHistograms(boolean mShowHistograms) {
		this.mShowHistograms = mShowHistograms;
	}

}
