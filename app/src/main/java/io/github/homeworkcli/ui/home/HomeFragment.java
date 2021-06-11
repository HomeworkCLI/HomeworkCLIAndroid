package io.github.homeworkcli.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import io.github.homeworkcli.core.HomeworkCLICore;
import io.github.homeworkcli.databinding.FragmentHomeBinding;
import io.github.homeworkcli.models.clientLoginModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView ettpAccount = binding.ettpAccount;
        final TextView ettpPassword = binding.ettpPassword;
        final Button btnLogin =  binding.btnLogin;

        btnLogin.setOnClickListener(v -> {
            HomeworkCLICore homeworkCLICore = new HomeworkCLICore();
            try {
                homeworkCLICore.clientLogin(ettpAccount.getText().toString(), ettpPassword.getText().toString(), true, 1).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Snackbar.make(root, "Error sending login request", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        File file = new File(container.getContext().getFilesDir(), "loginInfo");
                        clientLoginModel clientLoginResult = new Gson().fromJson(response.body().string(), clientLoginModel.class);
                        if (clientLoginResult.isSuccess()) {
                            Snackbar.make(root, "Successfully logged in", Snackbar.LENGTH_LONG).show();
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(new Gson().toJson(clientLoginResult).getBytes());
                            fos.close();
                        } else {
                            Snackbar.make(root, "Error sending login request with message: " + clientLoginResult.getMsg(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            } catch (InvalidKeySpecException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
                Snackbar.make(root, "Error encrypting password", Snackbar.LENGTH_LONG).show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}