package com.bbudzowski.homeautoandroid.ui.automaton.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams;
import androidx.lifecycle.Observer;

import com.bbudzowski.homeautoandroid.R;
import com.bbudzowski.homeautoandroid.databinding.FragmentListBinding;
import com.bbudzowski.homeautoandroid.tables.AutomatonEntity;
import com.bbudzowski.homeautoandroid.tables.SensorEntity;
import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.ui.fragments.BasicFragment;

import org.json.JSONException;

import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutomatonListFragment extends BasicFragment {
    protected FragmentListBinding binding;
    private AutomatonListViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        binding = FragmentListBinding.inflate(inflater, container, false);
        ScrollView root = binding.getRoot();
        model = new AutomatonListViewModel(mainActivity);
        getViewModelStore().put("automatonList", model);
        final Observer<List<AutomatonEntity>> automatonObserver = automatons -> {
            createAutomatonsUi(root.findViewById(R.id.units_list), automatons);
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
                    model.getAutomatons().postValue(mainActivity.getAutomatons());
                    model.setLastUpdateTime(updateTime);
                }
            }
        }, 0, updatePeriod);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createAutomatonsUi(ConstraintLayout root, List<AutomatonEntity> automatons) {
        root.removeAllViews();
        if (automatons == null || automatons.size() == 0) {
            handleError(root, getString(R.string.no_results));
            return;
        }
        for (int i = 0; i < automatons.size(); ++i) {
            ConstraintLayout view = createAutomatonView(root, automatons.get(i));
            view.setId(View.generateViewId());
            root.addView(view);
            view.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT));
        }
        constraintViewsToRoot(root);
    }

    private ConstraintLayout createAutomatonView(ConstraintLayout root, AutomatonEntity automaton) {
        ConstraintLayout view = new ConstraintLayout(root.getContext());
        view.setBackgroundResource(R.drawable.layout_border);

        addTextView(view, automaton.name, 28f);
        addTextView(view, "", 20f);
        addTextView(view, automaton.sens.name,20f);
        addTextView(view, automaton.sens.device.location,20f);

        String unit = "";
        if(automaton.sens.json_desc != null)
            try { unit = automaton.sens.json_desc.getString("unit");
            } catch (JSONException e) { unit = ""; }
        ConstraintLayout textLine = new ConstraintLayout(view.getContext());
        textLine.setId(View.generateViewId());
        addTextView(textLine, "Górna: ", 20f);
        addTextView(textLine, automaton.val_top.toString() + unit,  20f);
        addTextView(textLine, " - ", 20f);
        addTextView(textLine, "Dolna: ", 20f);
        addTextView(textLine, automaton.val_bot.toString() + unit,  20f);
        constraintTextInLine(textLine);
        view.addView(textLine);
        textLine.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        addTextView(view, "", 20f);
        SensorEntity acts = automaton.acts;
        addTextView(view, acts.name,20f);
        addTextView(view, acts.device.location,20f);

        textLine = new ConstraintLayout(view.getContext());
        textLine.setId(View.generateViewId());
        String stateDesc = automaton.state_up.toString();
        if(acts.current_val != null && acts.json_desc != null)
            try { stateDesc = acts.json_desc.getString(automaton.state_up.toString());
            } catch (JSONException e) { stateDesc = automaton.state_up.toString(); }
        addTextView(textLine, stateDesc,  20f);
        addTextView(textLine, " - ", 20f);
        stateDesc = automaton.state_down.toString();
        if(acts.current_val != null && acts.json_desc != null)
            try { stateDesc = acts.json_desc.getString(automaton.state_down.toString());
            } catch (JSONException e) { stateDesc = automaton.state_down.toString(); }
        addTextView(textLine, stateDesc, 20f);
        constraintTextInLine(textLine);
        view.addView(textLine);
        textLine.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        constraintTextToView(view, 0);
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