package com.alsimon.capteurs;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.alsimon.capteurs.SensorListFragment.OnSensorSelectedListener;
import com.alsimon.ui.model.ActionBarDrawerActivity;
import com.alsimon.ui.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarDrawerActivity implements
        OnSensorSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeFragment(new GraphFragment());
        List<NavDrawerItem> navItems = new ArrayList<NavDrawerItem>();
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            navItems.add(new NavDrawerItem(sensor.getName(),
                    NavDrawerItem.NO_ICON));
        }
        setNavDrawerItems(navItems);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment f = getSupportFragmentManager().findFragmentById(
                R.id.container);
        switch (item.getItemId()) {
            case R.id.action_pause:
                if (f instanceof GraphFragment)
                    ((GraphFragment) f).changePause();
                return true;
            case R.id.action_change_speed:
                if (f instanceof GraphFragment)
                    ((GraphFragment) f).swapSensorDelay();
                return true;
            case R.id.action_toggle_write_sensor_data:
                if (f instanceof GraphFragment)
                    ((GraphFragment) f).toggleWriteData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSensorSelected(int position) {
        GraphFragment gf = new GraphFragment();
        Bundle args = new Bundle();
        args.putInt(GraphFragment.ARG_POSITION, position);
        gf.setArguments(args);
        changeFragment(gf);
    }

    public void changeFragment(Fragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void displayView(int position) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof GraphFragment) {
            SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensors = mSensorManager
                    .getSensorList(Sensor.TYPE_ALL);
            ((GraphFragment) fragment).manageSensor(sensors.get(position));
        }
        switch (position) {
            case 0:
                fragment = new GraphFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
//            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}