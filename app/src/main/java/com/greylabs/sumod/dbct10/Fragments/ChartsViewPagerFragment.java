package com.greylabs.sumod.dbct10.Fragments;

import com.blunderer.materialdesignlibrary.handlers.ViewPagerHandler;
import com.greylabs.sumod.dbct10.Fragments.Charts.BarChart1Fragment;
import com.greylabs.sumod.dbct10.Fragments.Charts.BarChart2Fragment;
import com.greylabs.sumod.dbct10.Fragments.Charts.SpiderChartFragment;

/**
 * Created by Sumod on 03-Dec-15.
 */
public class ChartsViewPagerFragment extends com.blunderer.materialdesignlibrary.fragments.ViewPagerFragment {



    @Override
    public ViewPagerHandler getViewPagerHandler() {
        return new ViewPagerHandler(getActivity())
                .addPage("Incidents vs Pincode", new BarChart1Fragment())
                .addPage("Incidents vs Category", new BarChart2Fragment())
                .addPage("SpiderChart", new SpiderChartFragment());
    }


    @Override
    public boolean showViewPagerIndicator() {
        return true;
    }

    @Override
    public boolean replaceActionBarTitleByViewPagerPageTitle() {
        return true;
    }

    @Override
    public int defaultViewPagerPageSelectedPosition() {
        return 0;
    }
}
