package com.avaya.android.vantage.aaadevbroadcast.tutorial;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.avaya.android.vantage.aaadevbroadcast.R;

public class PageListener implements ViewPager.OnPageChangeListener {

    private final View[] indicators;
    private int current = 0;
    private final Screens first;
    private final Screens last;

    private final TextView leftButton;
    private final TextView rightButton;

    public PageListener(AppCompatActivity activity, TextView leftButton, TextView rightButton) {
        this.indicators = Screens.indicators(activity);
        this.leftButton = leftButton;
        this.rightButton = rightButton;

        first = Screens.firstScreen();
        first.enableCurrentIndicator(indicators);
        last = Screens.lastScreen();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {
        final Screens currentScreen = Screens.values()[position];
        if (current != position) {
            currentScreen.enableCurrentIndicator(indicators);
            switch (currentScreen) { // change, screen corresponding, button text, minimum times
                case QuickTutorial:
                    if (current > first.ordinal()) leftButton.setText(R.string.skip_button);
                    break;
                case History:
                    if (current < last.ordinal()) rightButton.setText(R.string.done);
                    break;
                default:
                    if (current == first.ordinal()) leftButton.setText(R.string.back);
                    else if (current == last.ordinal()) rightButton.setText(R.string.next);
            }
            current = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
