package com.bbudzowski.homeautoandroid.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bbudzowski.homeautoandroid.ui.MainActivity;
import com.bbudzowski.homeautoandroid.databinding.FragmentLoadBinding;

public class LoadFragment extends Fragment {
    FragmentLoadBinding binding;
    MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoadBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        return binding.getRoot();
    }
}
