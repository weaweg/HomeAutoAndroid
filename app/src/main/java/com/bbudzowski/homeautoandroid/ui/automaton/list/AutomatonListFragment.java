package com.bbudzowski.homeautoandroid.ui.automaton.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bbudzowski.homeautoandroid.MainActivity;
import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.api.MeasurementApi;
import com.bbudzowski.homeautoandroid.api.SensorApi;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.bbudzowski.homeautoandroid.ui.ListFragment;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutomatonListFragment extends ListFragment {
    private MainActivity mainActivity;
    private AutomatonListViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentListBinding.inflate(inflater, container, false);
        ConstraintLayout root = binding.getRoot();
        model = new ViewModelProvider(this).get(AutomatonListViewModel.class);
        final Observer<List<AutomatonEntity>> automatonObserver = automatons -> {
            root.removeAllViews();
            setAutomatons(automatons);
            createAutomatonsUi(root, automatons);
        };
        model.getAutomatons().observe(getViewLifecycleOwner(), automatonObserver);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timestamp updateTime = mainActivity.getAutomatonsLastUpdate();
                if(updateTime.compareTo(model.getLastUpdateTime()) > 0) {
                    model.getAutomatons().setValue(mainActivity.getAutomatons());
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    private void setAutomatons(List<AutomatonEntity> automatons) {
        for(AutomatonEntity auto : automatons) {
            auto.sens = SensorApi.getSensor(auto.device_id_sens, auto.sensor_id_sens);
            auto.sens.lastMeasurement = MeasurementApi.
                    getLastMeasurementForSensor(auto.device_id_sens, auto.sensor_id_sens);
            auto.acts = SensorApi.getSensor(auto.device_id_acts, auto.sensor_id_acts);
        }
    }

    private void createAutomatonsUi(ConstraintLayout root, List<AutomatonEntity> automatons) {
        if (automatons == null || automatons.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }
        for (int i = 0; i < automatons.size(); ++i) {
            ConstraintLayout view = createAutomatonView(root, automatons.get(i));
            view.setId(("box" + i).hashCode());
            root.addView(view);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
        }
        constraintViewsToRoot(root);
    }

    private ConstraintLayout createAutomatonView(View root, AutomatonEntity automaton) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);

        addTextView(view, automaton.name, "autoName", 24f, R.color.teal_700);
        //addTextView(view, automaton., "autoSensName", 20f, R.color.teal_700);
        addTextView(view, "Próg: " + automaton.val + "Histereza: " + automaton.hysteresis,
                "autoVal", 20f, R.color.teal_700);


        constraintTextToView(view);
        view.setOnClickListener(onAutomatonClick(automaton.name));
        return view;
    }

    private View.OnClickListener onAutomatonClick(String name) {
        return view -> {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            //replaceFragment(new AutomatonUnitFragment(), bundle);
        };
    }
}