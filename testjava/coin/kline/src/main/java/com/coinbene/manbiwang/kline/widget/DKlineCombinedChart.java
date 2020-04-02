package com.coinbene.manbiwang.kline.widget;//package com.coinbene.manbiwang.function.kline_new.mychart;
//

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.github.mikephil.coinbene.charts.CombinedChart;
import com.github.mikephil.coinbene.data.Entry;
import com.github.mikephil.coinbene.highlight.Highlight;
import com.github.mikephil.coinbene.interfaces.datasets.IDataSet;

import java.util.List;

/**
 * Created by loro on 2017/2/8.
 */
public class DKlineCombinedChart extends CombinedChart {
	private MyLeftMarkerView myMarkerViewLeft;
	private MyHMarkerView myMarkerViewH;//长按的十字路线，没有起作用，还得研究
	private MyBottomMarkerView myBottomMarkerView;
	private List<KLineBean> datas;

	public DKlineCombinedChart(Context context) {
		super(context);
	}

	public DKlineCombinedChart(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DKlineCombinedChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setMarker(MyLeftMarkerView markerLeft, MyBottomMarkerView markerBottom, MyHMarkerView hightMarkerView, List<KLineBean> datas) {
		this.myMarkerViewLeft = markerLeft;
		this.myBottomMarkerView = markerBottom;
//        this.myMarkerViewH = hightMarkerView;
		this.datas = datas;
		setDataSize(datas.size());
	}

	@Override
	protected void drawMarkers(Canvas canvas) {
//        super.drawMarkers(canvas);
		// if there is no marker view or drawing marker is disabled
		if (!valuesToHighlight())
			return;

		for (int i = 0; i < mIndicesToHighlight.length; i++) {

			Highlight highlight = mIndicesToHighlight[i];
			if (highlight.getDataIndex() == -1) {
				highlight.setDataIndex(0);
			}

			IDataSet set = mData.getDataSetByHighlight(highlight);

			Entry e = mData.getEntryForHighlight(highlight);
			if (e == null)
				continue;

			int entryIndex = set.getEntryIndex(e);

			// make sure entry not null
			if (entryIndex > set.getEntryCount() * mAnimator.getPhaseX())
				continue;

			float[] pos = getMarkerPosition(highlight);

			// check bounds
			if (!mViewPortHandler.isInBounds(pos[0], pos[1]))
				continue;

			if (myMarkerViewH != null) {
				//修改标记值
				int index = (int) e.getX();
				if (index < 0 || index > datas.size()) {
					return;
				}
				myMarkerViewH.setTvWidth((int) mViewPortHandler.contentWidth());
				myMarkerViewH.layout(0, 0, myMarkerViewH.getMeasuredWidth(),
						myMarkerViewH.getMeasuredHeight());
				myMarkerViewH.draw(canvas, pos[0], pos[1]);
			}

			if (null != myMarkerViewLeft) {
				//修改标记值
				int index = (int) e.getX();
				if (index < 0 || index > datas.size()) {
					return;
				}
				myMarkerViewLeft.setData(datas.get(index));

				myMarkerViewLeft.refreshContent(e, mIndicesToHighlight[i]);

				myMarkerViewLeft.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
						View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
				myMarkerViewLeft.layout(0, 0, myMarkerViewLeft.getMeasuredWidth(),
						myMarkerViewLeft.getMeasuredHeight());

				int width = myMarkerViewLeft.getWidth();
				if (pos[0] <= (width * 1.5)) {
					myMarkerViewLeft.draw(canvas, mViewPortHandler.contentRight() - width, pos[1] - myMarkerViewLeft.getHeight());
				} else {
					myMarkerViewLeft.draw(canvas, mViewPortHandler.contentLeft(), pos[1] - myMarkerViewLeft.getHeight());
				}
			}

			if (null != myBottomMarkerView) {
				int index = (int) e.getX();
				if (index < 0 || index > datas.size()) {
					return;
				}

				myBottomMarkerView.setData(datas.get(index));
				myBottomMarkerView.refreshContent(e, mIndicesToHighlight[i]);

				myBottomMarkerView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
						View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
				myBottomMarkerView.layout(0, 0, myBottomMarkerView.getMeasuredWidth(),
						myBottomMarkerView.getMeasuredHeight());

//                myBottomMarkerView.draw(canvas, pos[0] - myBottomMarkerView.getWidth() / 2, mViewPortHandler.contentBottom());


				int width = myBottomMarkerView.getWidth();
				if (mViewPortHandler.contentRight() - pos[0] <= width) {
					myBottomMarkerView.draw(canvas, mViewPortHandler.contentRight() - myBottomMarkerView.getWidth(), mViewPortHandler.contentBottom());
				} else if (pos[0] - mViewPortHandler.contentLeft() <= width) {
					myBottomMarkerView.draw(canvas, mViewPortHandler.contentLeft(), mViewPortHandler.contentBottom());
				} else {
					myBottomMarkerView.draw(canvas, pos[0] - myBottomMarkerView.getWidth() / 2, mViewPortHandler.contentBottom());
				}

			}
		}
	}

	public void setMarkerData(List<KLineBean> kLineDatas) {
		this.datas = kLineDatas;
		setDataSize(kLineDatas == null ? 0 : kLineDatas.size());
	}
}
