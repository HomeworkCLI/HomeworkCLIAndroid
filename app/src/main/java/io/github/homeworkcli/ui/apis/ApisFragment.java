package io.github.homeworkcli.ui.apis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import io.github.homeworkcli.databinding.FragmentApisBinding;

public class ApisFragment extends Fragment {

    private ApisViewModel apisViewModel;
    private FragmentApisBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        apisViewModel =
                new ViewModelProvider(this).get(ApisViewModel.class);

        binding = FragmentApisBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textApis;
        apisViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}