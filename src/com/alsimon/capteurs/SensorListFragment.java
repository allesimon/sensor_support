package com.alsimon.capteurs;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensorListFragment extends Fragment {
    private static final String FIRST_KEY = "text1";
    private static final String SECOND_KEY = "text2";
    private OnSensorSelectedListener listener;
    private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private ListView lv;

    public static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th",
                "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_list_fragment, container,
                false);
        lv = (ListView) view.findViewById(R.id.listView);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        showSensorList();

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long id) {
                listener.onSensorSelected(position);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnSensorSelectedListener) {
            listener = (OnSensorSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet MyListFragment.OnItemSelectedListener");
        }
    }

    public void showSensorList() {
        SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(
                Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int i = 0; i < sensors.size(); i++) {
            addElement(ordinal(i + 1) + " sensor", sensors.get(i).getName());
        }

        ListAdapter adapter = new SimpleAdapter(getActivity(), list,
                android.R.layout.simple_list_item_2, new String[]{FIRST_KEY,
                SECOND_KEY}, new int[]{android.R.id.text1,
                android.R.id.text2}
        );
        lv.setAdapter(adapter);
    }

    public void addElement(Object text1, Object text2) {
        HashMap<String, String> element = new HashMap<String, String>();
        element.put(FIRST_KEY, text1.toString());
        element.put(SECOND_KEY, text2.toString());
        list.add(element);
    }

    public interface OnSensorSelectedListener {
        public void onSensorSelected(int position);
    }
}