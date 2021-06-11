package io.github.homeworkcli.ui.apis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ApisViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ApisViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("To be finished");
    }

    public LiveData<String> getText() {
        return mText;
    }
}