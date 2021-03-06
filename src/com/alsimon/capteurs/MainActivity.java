package com.alsimon.capteurs;

import android.content.Context;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.alsimon.filter.HighPassFilter;
import com.alsimon.filter.LowPassFilter;
import com.alsimon.filter.MedianFilter;
import com.alsimon.filter.SimpleFilter;
import com.alsimon.sensor.MySensorManager;
import com.alsimon.ui.model.ActionBarDrawerActivity;
import com.alsimon.ui.model.NavDrawerItem;
import com.alsimon.utils.Logg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends ActionBarDrawerActivity {
    private final int NAVDRAWER_GROUP_ADD_SENSOR = 0;
    private final int NAVDRAWER_GROUP_REMOVE_SENSOR = 1;
    private final int NAVDRAWER_GROUP_MANAGE_FILTER = 2;
    private String NAVDRAWER_STRING_ADD_SENSOR;
    private String NAVDRAWER_STRING_REMOVE_SENSOR;
    private String NAVDRAWER_STRING_MANAGE_FILTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initString();
        super.onCreate(savedInstanceState);
        changeFragment(new GraphFragment());
        if (savedInstanceState == null)
            mDrawerLayout.openDrawer(mDrawerList);
    }

    public void initString() {
        NAVDRAWER_STRING_ADD_SENSOR = this.getResources().getString(R.string.addSensor);
        NAVDRAWER_STRING_REMOVE_SENSOR = this.getResources().getString(R.string.removeSensor);
        NAVDRAWER_STRING_MANAGE_FILTER = this.getResources().getString(R.string.manageFilter);
    }

    @Override
    public void prepareListData() {
        MySensorManager.getInstance().initialize((android.hardware.SensorManager)
                getSystemService(Context.SENSOR_SERVICE));
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<NavDrawerItem>>();

        listDataHeader.add(NAVDRAWER_STRING_ADD_SENSOR);
        listDataHeader.add(NAVDRAWER_STRING_REMOVE_SENSOR);
        listDataHeader.add(NAVDRAWER_STRING_MANAGE_FILTER);

        // Adding child data
        List<NavDrawerItem> add_sensors = new ArrayList<NavDrawerItem>();
        List<Sensor> sensors = MySensorManager.getInstance().getSensorList();
        for (Sensor s : sensors) {
            add_sensors.add(sensor2NavDrawerItem(s));
        }

        List<NavDrawerItem> remove_sensor = new ArrayList<NavDrawerItem>();

        List<NavDrawerItem> add_filters = new ArrayList<NavDrawerItem>();

        listDataChild.put(listDataHeader.get(NAVDRAWER_GROUP_ADD_SENSOR), add_sensors);
        listDataChild.put(listDataHeader.get(NAVDRAWER_GROUP_REMOVE_SENSOR), remove_sensor);
        listDataChild.put(listDataHeader.get(NAVDRAWER_GROUP_MANAGE_FILTER), add_filters);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MySensorManager.getInstance().unregisterListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MySensorManager.getInstance().registerListener();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Fragment f = getSupportFragmentManager().findFragmentById(
                R.id.container);
        switch (item.getItemId()) {
            case R.id.action_pause:
                if (f instanceof GraphFragment)
                    ((GraphFragment) f).changePause();
                return true;
            case R.id.action_change_speed:
                MySensorManager.getInstance().swapSensorDelay();
                return true;
            case R.id.action_toggle_write_sensor_data:
                if (!SensorWriter.getInstance().isWriting()) {
                    SensorWriter.getInstance().initWriter(this);
                    MySensorManager.getInstance().registerObserver(SensorWriter.getInstance());
                } else {
                    MySensorManager.getInstance().removeObserver(SensorWriter.getInstance());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeFragment(Fragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void onNavDrawerClickListener(int groupPosition, int childPosition) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof GraphFragment) {
            if (groupPosition == NAVDRAWER_GROUP_ADD_SENSOR) {
                List<Sensor> sensors = MySensorManager.getInstance().getSensorList();
                MySensorManager.getInstance().addSensor(sensors.get(childPosition), new LowPassFilter(3), new SimpleFilter(3), new MedianFilter(3, 10), new HighPassFilter(3, 30));
                //Add sensor to the "remove sensors" group
                List<NavDrawerItem> sensorsAlreadyAdded = listDataChild.get(NAVDRAWER_STRING_REMOVE_SENSOR);
                if (sensorsAlreadyAdded == null)
                    sensorsAlreadyAdded = new ArrayList<NavDrawerItem>();
                sensorsAlreadyAdded.add(sensor2NavDrawerItem(sensors.get(childPosition)));
                listDataChild.put(listDataHeader.get(NAVDRAWER_GROUP_REMOVE_SENSOR), sensorsAlreadyAdded);
                listDataChild.put(listDataHeader.get(NAVDRAWER_GROUP_MANAGE_FILTER), sensorsAlreadyAdded);
                adapter.notifyDataSetChanged();
            } else if (groupPosition == NAVDRAWER_GROUP_REMOVE_SENSOR) {
                List<NavDrawerItem> sensorsAlreadyAdded = listDataChild.get(NAVDRAWER_STRING_REMOVE_SENSOR);
                if (sensorsAlreadyAdded != null) {
                    MySensorManager.getInstance().removeSensor(navDrawerItem2Sensor(sensorsAlreadyAdded.get(childPosition)));
                    sensorsAlreadyAdded.remove(childPosition);
                    adapter.notifyDataSetChanged();
                }
            } else if (groupPosition == NAVDRAWER_GROUP_MANAGE_FILTER) {
                showFilterDialog(MySensorManager.getInstance().getSensorAtPosition(childPosition), childPosition);
            }
            if (fragment != null) {
                mDrawerList.setItemChecked(childPosition, true);
                mDrawerList.setSelection(childPosition);
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                Logg.e("Error in creating fragment");
            }
        }
    }

    public NavDrawerItem sensor2NavDrawerItem(Sensor sensor) {
        return new NavDrawerItem(sensor.getName(), NavDrawerItem.NO_ICON, NavDrawerItem.SECOND_TEXT_VISIBLE, sensor.getPower() + " mA");
    }

    public Sensor navDrawerItem2Sensor(NavDrawerItem navDrawerItem) {
        List<Sensor> sensors = MySensorManager.getInstance().getSensorList();
        for (Sensor s : sensors) {
            if (s.getName().equals(navDrawerItem.getTitle()))
                return s;
        }
        return null;
    }

    void showFilterDialog(Sensor sensor, int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FilterDialogFragment newFragment = FilterDialogFragment.newInstance(sensor.getName(), position);
        newFragment.show(ft, "dialog");
    }

}