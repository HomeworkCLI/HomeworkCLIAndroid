package io.github.homeworkcli.ui.homeworkupload;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;


public class HomeworkUploadViewModel extends ViewModel {
    private MutableLiveData<List<String>> mText;

    private List<String> stringList = new LinkedList<>();

    public MutableLiveData<List<String>> getText() {
        if (mText == null) {
            mText = new MutableLiveData<>();
            stringList.add(0, "No files Selected");
            stringList.add(1, "");
            mText.postValue(stringList);
        }
        return mText;
    }
}