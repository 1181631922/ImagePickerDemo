package com.fanyafeng.imagepicker.view.photoview.fresco;

import com.facebook.drawee.drawable.ProgressBarDrawable;

public class CustomProgressbarDrawable extends ProgressBarDrawable {

	private ImageDownloadListener mListener;

	public CustomProgressbarDrawable(ImageDownloadListener listener) {
		mListener = listener;
	}

	@Override
	protected boolean onLevelChange(int level) {
		int progress = (int) ((level / 10000.0) * 100);
		if (mListener != null) {
			mListener.onUpdate(progress);
		}
		return super.onLevelChange(level);
	}
}
