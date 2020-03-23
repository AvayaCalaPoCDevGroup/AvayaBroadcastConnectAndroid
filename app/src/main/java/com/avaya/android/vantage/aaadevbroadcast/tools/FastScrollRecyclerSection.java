package com.avaya.android.vantage.aaadevbroadcast.tools;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SectionIndexer;

import com.avaya.android.vantage.aaadevbroadcast.views.FastScrollRecyclerView;

import java.lang.ref.WeakReference;

/*
* Copyright 2017 MyInnos
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

public class FastScrollRecyclerSection extends RecyclerView.AdapterDataObserver {
    private static final String TAG = "FastScrollRecycler";
    private float mIndexbarWidth;
    private float mIndexbarMargin;
    private final float mPreviewPadding;
    private final float mDensity;
    private final float mScaledDensity;
    private int mListViewWidth;
    private int mListViewHeight;
    private int mCurrentSection = -1;
    private boolean mIsIndexing = false;
    private final RecyclerView mRecyclerView;
    private SectionIndexer mIndexer = null;
    private String[] mSections = null;
    private RectF mIndexbarRect;

    private int setIndexTextSize = FastScrollRecyclerView.setIndexTextSize;
    private int setPreviewPadding = FastScrollRecyclerView.mPreviewPadding;
    private int setIndexBarCornerRadius = FastScrollRecyclerView.mIndexBarCornerRadius;
    private Typeface setTypeface = null;
    private String indexbarBackgroudColor = FastScrollRecyclerView.mIndexbarBackgroudColor;
    private String indexbarTextColor = FastScrollRecyclerView.mIndexbarTextColor;
    private int indexbarBackgroudAlpha = convertTransparentValueToBackgroundAlpha(
            FastScrollRecyclerView.mIndexBarTransparentValue);
    private final float fontSize = FastScrollRecyclerView.mFontSize;

    AttributeSet attrs;
    private SwipeRefreshLayout mSwipeRefresh;

    public FastScrollRecyclerSection(Context context, RecyclerView rv) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        mRecyclerView = rv;
        setAdapter(mRecyclerView.getAdapter());

        float setIndexbarWidth = FastScrollRecyclerView.mIndexbarWidth;
        mIndexbarWidth = setIndexbarWidth * mDensity;
        float setIndexbarMargin = FastScrollRecyclerView.mIndexbarMargin;
        mIndexbarMargin = setIndexbarMargin * mDensity;
        mPreviewPadding = setPreviewPadding * mDensity;
        mHandler = new FadePreviewHandler(rv);
    }

    public void setSwipeRefresh(SwipeRefreshLayout mSwipeRefresh) {
        this.mSwipeRefresh = mSwipeRefresh;
    }

    public void draw(Canvas canvas) {
        Paint indexbarPaint = new Paint();
        indexbarPaint.setColor(Color.parseColor(indexbarBackgroudColor));
        indexbarPaint.setAlpha(indexbarBackgroudAlpha);
        indexbarPaint.setAntiAlias(true);
        canvas.drawRoundRect(mIndexbarRect, setIndexBarCornerRadius * mDensity, setIndexBarCornerRadius * mDensity, indexbarPaint);

        if (mSections != null && mSections.length > 0) {
            // Preview is shown when mCurrentSection is set
            if (mCurrentSection >= 0 && mCurrentSection < mSections.length) {
                Paint previewPaint = new Paint();
                previewPaint.setColor(Color.BLACK);
                previewPaint.setAlpha(96);
                previewPaint.setAntiAlias(true);
                previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

                Paint previewTextPaint = new Paint();
                previewTextPaint.setColor(Color.WHITE);
                previewTextPaint.setAntiAlias(true);
                previewTextPaint.setTextSize(fontSize * mScaledDensity);
                previewTextPaint.setTypeface(setTypeface);

                float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
                float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
                RectF previewRect = new RectF((mListViewWidth - previewSize) / 2
                        , (mListViewHeight - previewSize) / 2
                        , (mListViewWidth - previewSize) / 2 + previewSize
                        , (mListViewHeight - previewSize) / 2 + previewSize);

                canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
                canvas.drawText(mSections[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1
                        , previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1, previewTextPaint);
                fade(1000);
            }

            Paint indexPaint = new Paint();
            indexPaint.setColor(Color.parseColor(indexbarTextColor));
            indexPaint.setAntiAlias(true);
            indexPaint.setTextSize(setIndexTextSize * mScaledDensity);
            indexPaint.setTypeface(setTypeface);

            float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length;
            float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
            for (int i = 0; i < mSections.length; i++) {
                float paddingLeft = (mIndexbarWidth - indexPaint.measureText(mSections[i])) / 2;
                canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft
                        , mIndexbarRect.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // If down event occurs inside index bar region, start indexing

                // preventing unintentionl refshesh if list is on the top and user pressed first indexed letter
                if (contains(ev.getX(), ev.getY()) && ev.getX() < 50) {
                    if (mSwipeRefresh != null) {
                        mSwipeRefresh.setEnabled(false);
                    }

                    // It demonstrates that the motion event started from index bar
                    mIsIndexing = true;
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(ev.getY());
                    scrollToPosition();
                    return true;

                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsIndexing) {
                    // If this event moves inside index bar
                    if (contains(ev.getX(), ev.getY()) && ev.getX() < 50) {
                        // Determine which section the point is in, and move the list to that section
                        mCurrentSection = getSectionByPoint(ev.getY());
                        scrollToPosition();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing) {
                    mIsIndexing = false;
                    mCurrentSection = -1;
                }

                // enabling back swipe refresh
                if (mSwipeRefresh != null) {
                    mSwipeRefresh.setEnabled(true);
                }

                break;
        }
        return false;
    }

    private void scrollToPosition() {
        int position = mIndexer.getPositionForSection(mCurrentSection);
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
        } else {
            assert layoutManager != null;
            layoutManager.scrollToPosition(position);
        }
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;
        float left = 50 - mIndexbarMargin - mIndexbarWidth;
        float top = mIndexbarMargin;
        float right = 50 - mIndexbarMargin;
        float bottom = h - mIndexbarMargin;
        // Left: 724.71875, Top: 6.6562505, Right: 751.34375, Bottom: 753.34375
        mIndexbarRect = new RectF(
                left
                , top
                , right
                , bottom);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof SectionIndexer) {
            adapter.registerAdapterDataObserver(this);
            mIndexer = (SectionIndexer) adapter;
            mSections = (String[]) mIndexer.getSections();
        }
    }

    @Override
    public void onChanged() {
        super.onChanged();
        mSections = (String[]) mIndexer.getSections();
    }

    public boolean contains(float x, float y) {
        // Determine if the point is in index bar region, which includes the right margin of the bar
        return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top + mIndexbarRect.height());
    }

    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.length == 0)
            return 0;
        if (y < mIndexbarRect.top + mIndexbarMargin)
            return 0;
        if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
            return mSections.length - 1;
        return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.length));
    }

    private static final int WHAT_FADE_PREVIEW = 1;

    private void fade(long delay) {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageAtTime(WHAT_FADE_PREVIEW, SystemClock.uptimeMillis() + delay);
    }

    private final Handler mHandler;

    private int convertTransparentValueToBackgroundAlpha(float value) {
        return (int) (255 * value);
    }

    /**
     * @param value int to set the text size of the index bar
     */
    public void setIndexTextSize(int value) {
        setIndexTextSize = value;
    }

    /**
     * @param value float to set the width of the index bar
     */
    public void setIndexbarWidth(float value) {
        mIndexbarWidth = value;
    }

    /**
     * @param value float to set the margin of the index bar
     */
    public void setIndexbarMargin(float value) {
        mIndexbarMargin = value;
    }

    /**
     * @param value int to set preview padding
     */
    public void setPreviewPadding(int value) {
        setPreviewPadding = value;
    }

    /**
     * @param value int to set the radius of the index bar
     */
    public void setIndexBarCornerRadius(int value) {
        setIndexBarCornerRadius = value;
    }

    /**
     * @param value float to set the transparency of the color for index bar
     */
    public void setIndexBarTransparentValue(float value) {
        indexbarBackgroudAlpha = convertTransparentValueToBackgroundAlpha(value);
    }

    /**
     * @param typeface Typeface to set the typeface of the preview & the index bar
     */
    public void setTypeface(Typeface typeface) {
        setTypeface = typeface;
    }

    /**
     * @param color The color for the scroll track
     */
    public void setIndexBarColor(String color) {
        indexbarBackgroudColor = color;
    }

    /**
     * @param color The text color for the index bar
     */
    public void setIndexBarTextColor(String color) {
        indexbarTextColor = color;
    }

    private static class FadePreviewHandler extends Handler {
        private final WeakReference<RecyclerView> mRecyclerViewRef;
        FadePreviewHandler (RecyclerView recyclerView) {
            mRecyclerViewRef = new WeakReference<>(recyclerView);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == WHAT_FADE_PREVIEW) {
                mRecyclerViewRef.get().invalidate();
            }

        }

    }
}