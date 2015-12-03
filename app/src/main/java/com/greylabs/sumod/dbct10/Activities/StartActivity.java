package com.greylabs.sumod.dbct10.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.blunderer.materialdesignlibrary.activities.NavigationDrawerActivity;
import com.blunderer.materialdesignlibrary.handlers.ActionBarHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerAccountsHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerAccountsMenuHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerBottomHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerStyleHandler;
import com.blunderer.materialdesignlibrary.handlers.NavigationDrawerTopHandler;
import com.blunderer.materialdesignlibrary.models.Account;
import com.greylabs.sumod.dbct10.Fragments.ChartsViewPagerFragment;
import com.greylabs.sumod.dbct10.Fragments.MapsFragment;
import com.greylabs.sumod.dbct10.PrefManager;
import com.greylabs.sumod.dbct10.R;

public class StartActivity extends NavigationDrawerActivity {


    @Override
    protected boolean enableActionBarShadow() {
        return true;
    }

    @Override
    protected ActionBarHandler getActionBarHandler() {
        return null;
    }

    @Override
    public NavigationDrawerStyleHandler getNavigationDrawerStyleHandler() {
        return new NavigationDrawerStyleHandler();
    }

    @Override
    public NavigationDrawerAccountsHandler getNavigationDrawerAccountsHandler() {
        PrefManager pref = new PrefManager(this);
        return new NavigationDrawerAccountsHandler(this).addAccount(pref.getName(), pref.getEmail(),
                R.drawable.ic_image_user, R.drawable.navigation_drawer_bg);
    }

    @Override
    public NavigationDrawerAccountsMenuHandler getNavigationDrawerAccountsMenuHandler() {
        return null;
    }

    @Override
    public void onNavigationDrawerAccountChange(Account account) {

    }

    @Override
    public NavigationDrawerTopHandler getNavigationDrawerTopHandler() {
        return new NavigationDrawerTopHandler(this)
                .addItem("Charts", new ChartsViewPagerFragment())
                .addItem("Maps", new MapsFragment());
    }

    @Override
    public NavigationDrawerBottomHandler getNavigationDrawerBottomHandler() {
        return null;
    }

    @Override
    public boolean overlayActionBar() {
        return true;
    }

    @Override
    public boolean replaceActionBarTitleByNavigationDrawerItemTitle() {
        return true;
    }

    @Override
    public int defaultNavigationDrawerItemSelectedPosition() {
        return 0;
    }
}
