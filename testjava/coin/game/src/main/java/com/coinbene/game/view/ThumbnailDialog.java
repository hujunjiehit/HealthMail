package com.coinbene.game.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.coinbene.common.utils.GlideUtils;
import com.coinbene.game.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

/**
 * ding
 * 2019-09-26
 * com.coinbene.game.view
 */
public class ThumbnailDialog extends DialogFragment {
	private int thumbnailRes;
	private ImageView imgThumbnail;

	public static ThumbnailDialog init() {
		return new ThumbnailDialog();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, R.style.Dialog_FullScreen);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		QMUIStatusBarHelper.translucent(getDialog().getWindow());
		return inflater.inflate(R.layout.dialog_thumbnail, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		imgThumbnail = view.findViewById(R.id.img_Thumbnail);
		GlideUtils.asGif(imgThumbnail, thumbnailRes);
		view.setOnClickListener(v -> dismiss());
	}

	/**
	 * 设置缩略图资源
	 */
	public void setThumbnailRes(@DrawableRes int resID) {
		this.thumbnailRes = resID;
	}
}
