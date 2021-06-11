package io.github.homeworkcli.ui.homeworkupload;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import io.github.homeworkcli.FileUtil;
import io.github.homeworkcli.MainActivity;
import io.github.homeworkcli.core.HomeworkCLICore;
import io.github.homeworkcli.databinding.FragmentHomeworkuploadBinding;
import io.github.homeworkcli.models.BaseModel;
import io.github.homeworkcli.models.clientLoginModel;
import io.github.homeworkcli.models.saveDocNewModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeworkUploadFragment extends Fragment {
    private Context context;

    private HomeworkUploadViewModel homeworkUploadViewModel;
    private FragmentHomeworkuploadBinding binding;

    private Uri selectedFile;
    private String selectedFilePath;
    private OSS ossClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeworkUploadViewModel =
                new ViewModelProvider(this).get(HomeworkUploadViewModel.class);
        ossClient = new OSSClient(container.getContext(),
                "http://oss-cn-hangzhou.aliyuncs.com",
                new OSSPlainTextAKSKCredentialProvider("LTAI4G8HWjQYmcTk735N1zxu",
                        "WnoFodPmNvhT1wjnh73CiMf3QTaNnB"));
        binding = FragmentHomeworkuploadBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button btnSelectFiles = binding.btnSelectFiles;
        final TextView tvSelectedFiles = binding.tvSelectedFiles;
        final Button btnUpload = binding.btnUpload;
        final TextView tvUploadState = binding.tvUploadState;

        homeworkUploadViewModel.getText().observe(getViewLifecycleOwner(), strings -> {
            tvSelectedFiles.setText(strings.get(0));
            tvUploadState.setText(strings.get(1));
        });

        ActivityResultLauncher<String> mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    selectedFile = uri;
                    selectedFilePath = FileUtil.getFileAbsolutePath(getContext(), selectedFile);
                    Log.d("HomeworkUpload", selectedFilePath);
                    List<String> tvs = homeworkUploadViewModel.getText().getValue();
                    tvs.set(0, selectedFilePath);
                    homeworkUploadViewModel.getText().postValue(tvs);
                });

        btnSelectFiles.setOnClickListener(v -> mGetContent.launch("*/*"));

        btnUpload.setOnClickListener(v -> {
            if (!new File(container.getContext().getFilesDir(), "loginInfo").exists()) {
                List<String> tvs = homeworkUploadViewModel.getText().getValue();
                tvs.set(1, "Login to upload");
                homeworkUploadViewModel.getText().postValue(tvs);
                return;
            }
            if (selectedFile == null) {
                List<String> tvs = homeworkUploadViewModel.getText().getValue();
                tvs.set(1, "Select file to upload");
                homeworkUploadViewModel.getText().postValue(tvs);
                return;
            }
            String uuid = UUID.randomUUID().toString();
            Log.d("HomeworkUpload", "uuid: " + uuid);
            File file = new File(selectedFilePath);
            String extname;
            if (file.getName().lastIndexOf(".") != -1) {
                extname = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                Log.d("HomeworkUpload", extname);
                String object = "aliba/upload/HomeworkUpload/" + uuid + "/0.0." + extname;
                PutObjectRequest put = new PutObjectRequest("yixuexiao-2", object, selectedFile);
                NumberFormat nt = NumberFormat.getPercentInstance();
                nt.setMinimumFractionDigits(2);
                put.setProgressCallback((request, currentSize, totalSize) -> {
                    List<String> tvs = homeworkUploadViewModel.getText().getValue();
                    tvs.set(1, "Uploading " + Math.floor((double)currentSize/(double)totalSize) + "%");
                    homeworkUploadViewModel.getText().postValue(tvs);
                });

                File fl = new File(container.getContext().getFilesDir(), "loginInfo");
                FileInputStream fin;
                StringBuilder sb = new StringBuilder();
                try {
                    fin = new FileInputStream(fl);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                HomeworkCLICore homeworkCLICore = new HomeworkCLICore(new Gson().fromJson(sb.toString(), clientLoginModel.class));
                HomeworkCLICore.DocInfo docInfo = new HomeworkCLICore.DocInfo();
                docInfo.setTitle(file.getName());
                docInfo.setDoctype(extname);
                docInfo.setDocsize(file.length());
                docInfo.setDir("aliba/upload/HomeworkUpload");
                docInfo.setKey("aliba/upload/HomeworkUpload/" + uuid + "/0.0." + extname);
                docInfo.setMd5code(uuid);
                docInfo.setGuid(uuid);
                docInfo.setIsconverth5(false);
                docInfo.setIspublish(false);
                docInfo.setAgent("android");
                docInfo.setIflyknowledge("");
                docInfo.setBankname("体育");
                docInfo.setCategory1("");
                docInfo.setCategory2("");
                docInfo.setCategoryid("ghnvak6jnkdoh0hg1pdowq");
                docInfo.setCategoryname("课件");
                docInfo.setIsschool(false);
                docInfo.setCreator(homeworkCLICore.getUserid());

                ossClient.asyncPutObject(put,
                        new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                            @Override
                            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                                List<String> tvs = homeworkUploadViewModel.getText().getValue();
                                tvs.set(1, "Upload finished");
                                homeworkUploadViewModel.getText().postValue(tvs);
                                Log.d("HomeworkUpload", "onSuccess: saveDocNew execute1");
                                homeworkCLICore.saveDocNew(docInfo).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        List<String> tvs = homeworkUploadViewModel.getText().getValue();
                                        tvs.set(1, "Error saving document with message: " + e.getMessage());
                                        homeworkUploadViewModel.getText().postValue(tvs);
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        Log.d("HomeworkUpload", "onResponse: saved");
                                        List<String> tvs = homeworkUploadViewModel.getText().getValue();
                                        saveDocNewModel saveDocNewResult = new Gson().fromJson(response.body().string(), saveDocNewModel.class);
                                        tvs.set(1, saveDocNewResult.isSuccess() ? "Successfully shared" : "Error svaing document with message: " + saveDocNewResult.getMsg());
                                        homeworkUploadViewModel.getText().postValue(tvs);
                                        Call shareDoc = homeworkCLICore.shareDoc("1", "", saveDocNewResult.getData().getDocid(), homeworkCLICore.getUserid());
                                        shareDoc.enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                List<String> tvs = homeworkUploadViewModel.getText().getValue();
                                                tvs.set(1, "Error sharing document: " + e.getMessage());
                                                homeworkUploadViewModel.getText().postValue(tvs);
                                            }

                                            @Override
                                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                Log.d("HomeworkUpload", "onResponse: shared");
                                                List<String> tvs = homeworkUploadViewModel.getText().getValue();
                                                BaseModel shareDocResult = new Gson().fromJson(response.body().string(), BaseModel.class);
                                                tvs.set(1, shareDocResult.isSuccess() ? "Successfully shared" : "Error sharing document with message: " + shareDocResult.getMsg());
                                                homeworkUploadViewModel.getText().postValue(tvs);
                                            }
                                        });
                                    }
                                });
                                Log.d("HomeworkUpload", "onSuccess: saveDocNew execute2");
                            }

                            @Override
                            public void onFailure(PutObjectRequest request, ClientException clientException,
                                                  ServiceException serviceException) {
                                    List<String> tvs = homeworkUploadViewModel.getText().getValue();
                                    tvs.set(1, "Upload failed with message: " + (clientException == null? serviceException.getRawMessage() : clientException.getMessage()));
                                    homeworkUploadViewModel.getText().postValue(tvs);
                                if (clientException == null) {
                                    serviceException.printStackTrace();
                                } else {
                                    clientException.printStackTrace();
                                }
                            }
                        });
            } else {
                List<String> tvs = homeworkUploadViewModel.getText().getValue();
                tvs.set(1, "Unsupported file provider, use another file manager");
                homeworkUploadViewModel.getText().postValue(tvs);
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