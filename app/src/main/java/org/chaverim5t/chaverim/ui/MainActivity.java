package org.chaverim5t.chaverim.ui;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

//import com.astuetz.PagerSlidingTabStrip;

import org.chaverim5t.chaverim.data.CallManager;
import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.UserManager;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private UserManager userManager = UserManager.getUserManager(this);
  private CallManager callManager = CallManager.getCallManager(this);

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  SectionsPagerAdapter mSectionsPagerAdapter;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  ViewPager mViewPager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(mSectionsPagerAdapter);
/*
    PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    tabs.setViewPager(mViewPager);
    tabs.setShouldExpand(true);
    */
    TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(mViewPager);
    setTabsFromIconTabProvider(tabLayout, mSectionsPagerAdapter);
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
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void setTabsFromIconTabProvider(TabLayout tabLayout, IconTabProvider iconTabProvider) {
    for (int i = 0; i < iconTabProvider.getCount(); i++) {
      tabLayout.getTabAt(i).setIcon(iconTabProvider.getPageIconResId(i));
      tabLayout.getTabAt(i).setText("");
    }
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */

  interface IconTabProvider {
    int getCount();
    int getPageIconResId(int i);
  }

  public class SectionsPagerAdapter extends FragmentPagerAdapter implements IconTabProvider {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      //return PlaceholderFragment.newInstance(position + 1);
      // d + r
      // responding, calls, new, callerid, settings
      // d
      // calls, new, callerid, settings
      // r
      // responding, calls, settings
      switch (position) {
        case 0:
          if (userManager.isResponder()) return new RespondingFragment();
          return new CallsFragment();
        case 1:
          if (userManager.isResponder()) return new CallsFragment();
          return new NewCallFragment();
        case 2:
          if (userManager.isResponder() && userManager.isDispatcher()) return new NewCallFragment();
          if (userManager.isDispatcher()) return new CallerIDFragment();
          if (userManager.isResponder()) return new SettingsFragment();
          throw new AssertionError("Not dispatcher or responder");
        case 3:
          if (userManager.isResponder() && userManager.isDispatcher()) return new CallerIDFragment();
          return new SettingsFragment();
        case 4:
          return new SettingsFragment();
        default:
          throw new IllegalArgumentException("Don't know how to instantiate tab " + position);
      }
    }

    @Override
    public int getCount() {
      // d+r:5
      // d: 4
      // r: 3
      if (userManager.isResponder() && userManager.isDispatcher()) return 5;
      if (userManager.isDispatcher()) return 4;
      if (userManager.isResponder()) return 3;
      throw new AssertionError("Must be either dispatcher or responder");
    }

    @Override
    public CharSequence getPageTitle(int position) {
      String[] titles = {"responding", "calls", "new", "callerids", "settings"};
      int skip = 0;
      if (userManager.isDispatcher() && userManager.isResponder()) {
        skip = 0;
      } else {
        if (userManager.isDispatcher()) {
          skip = 1;
        } else {
          if (position > 1) skip = 2;
        }
      }
      return titles[position + skip];
    }

    @Override
    public int getPageIconResId(int i) {
      int[] resources = {R.drawable.ic_directions_run_black_24dp, R.drawable.ic_list_black_24dp, R.drawable.ic_create_black_24dp, R.drawable.ic_call_black_24dp, R.drawable.ic_settings_black_24dp};
      int skip = 0;
      if (userManager.isDispatcher() && userManager.isResponder()) {
        skip = 0;
      } else {
        if (userManager.isDispatcher()) {
          skip = 1;
        } else {
          if (i > 1) skip = 2;
        }
      }
      return resources[i + skip];
    }
  }
  //*/

}
