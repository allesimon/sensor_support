package com.alsimon.capteurs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alsimon.filter.AbstractFilter;
import com.alsimon.filter.FilterUI;
import com.alsimon.sensor.MySensorManager;
import com.alsimon.utils.Logg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterDialogFragment extends DialogFragment {
    private static final String FIRST_KEY = "text1";
    private static final String SECOND_KEY = "text2";
    private static final String ARG_SENSOR_NAME = "sensor";
    private static final String ARG_SENSOR_NUMBER = "sensorNumber";
    private int sensorNumber;

    public static FilterDialogFragment newInstance(String sensorName, int sensorNumber) {
        FilterDialogFragment dialog = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SENSOR_NAME, sensorName);
        args.putInt(ARG_SENSOR_NUMBER, sensorNumber);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(getArguments().getString(ARG_SENSOR_NAME));
        return inflater.inflate(R.layout.filter_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        sensorNumber = getArguments().getInt(ARG_SENSOR_NUMBER);
        List<HashMap<String, String>> listFilterAlreadyAdded = new ArrayList<HashMap<String, String>>();

        List<AbstractFilter> filters = MySensorManager.getInstance().getFiltersForSensor(sensorNumber);
        for (AbstractFilter filter : filters) {
            if (filter instanceof FilterUI) {
                addElement(((FilterUI) filter).getName(), ((FilterUI) filter).getCoefficient(), listFilterAlreadyAdded);
            }
        }

        ListAdapter adapter = new SimpleAdapter(getActivity(), listFilterAlreadyAdded,
                android.R.layout.simple_list_item_2, new String[]{FIRST_KEY,
                SECOND_KEY}, new int[]{android.R.id.text1,
                android.R.id.text2}
        );
        ListView lvFilterAlreadyAdded = (ListView) getView().findViewById(R.id.lvRemoveFilter);
        lvFilterAlreadyAdded.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MySensorManager.getInstance().removeFilter(sensorNumber, position);
                dismiss();
            }
        });
        lvFilterAlreadyAdded.setAdapter(adapter);
    }


    public void addElement(Object text1, Object text2, List<HashMap<String, String>> list) {
        HashMap<String, String> element = new HashMap<String, String>();
        element.put(FIRST_KEY, text1.toString());
        element.put(SECOND_KEY, text2.toString());
        list.add(element);
    }
}
