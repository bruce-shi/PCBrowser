package com.jupiter.pcbrowser;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jupiter.Adapter.NavDrawerListAdapter;
import com.jupiter.Helper.Helper;
import com.jupiter.Service.NewsPullService;

import com.jupiter.model.CachedNews;
import com.jupiter.model.NavDrawerItem;
import com.orm.SugarRecord;
import org.apache.http.message.HeaderValueParser;

import java.util.*;

public class MainActivity extends Activity  implements ListFragment.OnFragmentInteractionListener {



    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;
    // application title
    private CharSequence mAppTitle;

    //nav slide menu items
    private String[]  navMenuTitles;
    //Icons for slide menu
    private TypedArray navMenuIcons;

    private String[]  navMenuCategory;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    //private ArrayList<NewsListItem> listItems;


    private int currentNavPosition;
    private ListFragment fragment;

    private static final int SETTING_ACTIVITY_REQUEST_CODE = 101;
    public static String PREFERENCE_CHANGED = "preference_changed";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuCategory = getResources().getStringArray(R.array.nav_drawer_catogory);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.container);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuCategory[0],navMenuIcons.getResourceId(0, 0)));
        // Sports
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],navMenuCategory[1], navMenuIcons.getResourceId(1, 0)));
        // Entertainment
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuCategory[2],navMenuIcons.getResourceId(2, 0)));
        // Technologies
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuCategory[3],navMenuIcons.getResourceId(3, 0)));
        // Politics
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuCategory[4], navMenuIcons.getResourceId(4, 0)));
        // My Favorite
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuCategory[5], navMenuIcons.getResourceId(5, 0)));

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuCategory[6], navMenuIcons.getResourceId(6, 0)));

        navMenuIcons.recycle();

        adapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems);
        mDrawerList.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name,
                R.string.app_name
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mAppTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        fragment = new ListFragment();

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
            displayFragment(0);
        }


        final Intent mServiceIntent = new Intent(this,NewsPullService.class);
        startService(mServiceIntent);
        mServiceIntent.setAction("com.jupiter.pcbrowser.NewsPullService");
        //final Intent eintent = new Intent(Helper.createExplicitFromImplicitIntent(this, mServiceIntent));
        // mServiceIntent.setPackage(getPackageName());
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

        //set the default pref file and mode for preference
        PreferenceManager.setDefaultValues(getApplicationContext(), getString(R.string.sharedpref), Context.MODE_MULTI_PROCESS, R.xml.pref_general, false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            this.startActivityForResult(intent, SETTING_ACTIVITY_REQUEST_CODE);
            return true;
        }
        if(id == R.id.action_logout){
            SharedPreferences sp = getSharedPreferences(getString(R.string.sharedpref),Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor= sp.edit();
            editor.putString(getString(R.string.appkey), "");
            editor.putBoolean(getString(R.string.islogin),false);
            editor.commit();
            Intent login = new Intent(this,LoginActivity.class);
            this.startActivity(login);
            this.finish();

        }
        if(id == R.id.action_delete_test){
            CachedNews.executeQuery("delete from "+ SugarRecord.getTableName(CachedNews.class)+" where title like 'Fake%'");

        }
        if(id == R.id.action_delete_all_cache){
            CachedNews.executeQuery("delete from "+ SugarRecord.getTableName(CachedNews.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title){
        mAppTitle = title;
        getActionBar().setTitle(title);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        //hide the action menu if drawer is opened
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void  onItemClick(Bundle param){
        Intent intent = new Intent(this, NewsContentActivity.class);

        //intent.putExtra(PCConstant.NEWS_TITLE,message);
        //intent.putExtra(PCConstant.NEWS_ID, currentItem.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtras(param);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case SETTING_ACTIVITY_REQUEST_CODE : {
                if (resultCode == Activity.RESULT_OK && data.getBooleanExtra(PREFERENCE_CHANGED,false)) {
                    try {
                        mService.restartServer();
                    }catch(RemoteException re){
                        re.printStackTrace();
                    }

                }
                break;
            }
        }
    }

    @Override
    public void finish(){
        unbindService(mConnection);
        super.finish();
    }

    private void displayFragment(int position){

        if(fragment !=null){

            fragment.setCategory(navMenuCategory[position]);
            // update selected item and title, then close the drawer

            //loadNewsList(navMenuCategory[position], fragment);

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

    }
    private class SlideMenuClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayFragment(position);
        }
    }



    /***************************************************************
     * code to communicate with the server
     *
     *
     ***************************************************************/

    ILocalService mService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = ILocalService.Stub.asInterface(service);

            try {
                mService.startServer();
            }catch (RemoteException re){
                re.printStackTrace();
            }

            Log.d(this.getClass().toString(),"Server Disconnect");

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            Log.d(this.getClass().toString(),"Server Disconnect");
        }
    };

}
