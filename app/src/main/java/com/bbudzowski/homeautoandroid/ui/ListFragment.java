package com.bbudzowski.homeautoandroid.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.tables.DeviceEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.device.DeviceUnitFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class ListFragment<T> extends Fragment {
    public void updateUI(ConstraintLayout root, List<T> units) {
        if (units == null || units.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }

        int constraint = ConstraintSet.TOP;
        int guideVertId = R.id.guideline;
        int prevViewId = root.getId();

        Class<?> unitClass = units.get(0).getClass();
        List<Field> displayFields;
        try {
            if (unitClass.equals(DeviceEntity.class))
                displayFields = getDeviceDisplayFields();
            else if(unitClass.equals(SensorEntity.class))
                displayFields = getSensorDisplayFields();
            else
                throw new ClassCastException();
        } catch (NoSuchFieldException e) {
            handleError(root, e.getMessage());
            return;
        }

        for (int i = 0; i < units.size(); ++i) {
            ConstraintLayout view = new ConstraintLayout(root.getContext());
            view.setId(("cl" + i).hashCode());
            view.setBackgroundResource(R.drawable.layout_border);

            int prevTextId = view.getId();
            int nextConstraint = ConstraintSet.TOP;

            List<String> fieldsValue = new ArrayList<>();
            for (int j = 0; j < displayFields.size(); ++j) {
                TextView textView = new TextView(view.getContext());
                textView.setId(("tv" + j).hashCode());
                try {
                    String tmp = String.valueOf(displayFields.get(j).get(units.get(i)));
                    fieldsValue.add(tmp);
                    textView.setText(tmp);
                } catch (IllegalAccessException e) {
                    handleError(root, e.getMessage());
                    return;
                }
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                view.addView(textView);
                textView.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                ConstraintSet set = new ConstraintSet();
                set.clone(view);
                set.connect(textView.getId(), ConstraintSet.TOP, prevTextId, nextConstraint);
                set.applyTo(view);
                nextConstraint = ConstraintSet.BOTTOM;
                prevTextId = textView.getId();
            }
            if (unitClass.equals(DeviceEntity.class))
                view.setOnClickListener(onDeviceClick(fieldsValue.get(0)));
            else if(unitClass.equals(SensorEntity.class))
                view.setOnClickListener(onSensorClick(fieldsValue.get(0), fieldsValue.get(1)));
            else
                throw new ClassCastException();

            root.addView(view);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
            ConstraintSet set = new ConstraintSet();
            float px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
            set.clone(root);
            set.connect(view.getId(), ConstraintSet.TOP, prevViewId, constraint, (int) px);
            set.constrainPercentWidth(view.getId(), .4f);
            if (i % 2 == 0) {
                set.center(view.getId(), ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0,
                        guideVertId, ConstraintSet.LEFT, 0, 2 / 3f);
            } else {
                set.center(view.getId(), guideVertId, ConstraintSet.RIGHT, 0,
                        ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, 1 / 3f);
                prevViewId = view.getId();
            }

            set.applyTo(root);
            constraint = ConstraintSet.BOTTOM;
        }
    }

    private void handleError(ConstraintLayout root, String text) {
        TextView emptyList = new TextView(root.getContext());
        emptyList.setText(text);
        emptyList.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        root.addView(emptyList);
        emptyList.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void replaceFragment(View view, Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(view.getRootView().getId(), fragment, fragment.getClass().getName());
        fragmentTransaction.addToBackStack(view.getRootView().getTag().toString());
        fragmentTransaction.commit();
    }

    private List<Field> getDeviceDisplayFields() throws NoSuchFieldException {
        List<Field> fields = new ArrayList<>();
        fields.add(DeviceEntity.class.getDeclaredField("name"));
        fields.add(DeviceEntity.class.getDeclaredField("location"));
        return fields;
    }

    public View.OnClickListener onDeviceClick(String device_id) {
        return view -> {
            Bundle bundle = new Bundle();
            bundle.putString("device_id", device_id);
            replaceFragment(view.getRootView(), new DeviceUnitFragment(), bundle);
        };
    }

    private List<Field> getSensorDisplayFields() throws NoSuchFieldException {
        List<Field> fields = new ArrayList<>();
        fields.add(SensorEntity.class.getDeclaredField("device_id"));
        fields.add(SensorEntity.class.getDeclaredField("sensor_id"));
        fields.add(SensorEntity.class.getDeclaredField("current_state"));
        fields.add(SensorEntity.class.getDeclaredField("units"));
        return fields;
    }

    public View.OnClickListener onSensorClick(String device_id, String sensor_id) {
        return v -> {
            Bundle bundle = new Bundle();

        };
    }
}
