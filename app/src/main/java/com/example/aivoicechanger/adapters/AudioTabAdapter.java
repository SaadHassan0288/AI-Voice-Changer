package com.example.aivoicechanger.adapters;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.aivoicechanger.fragments.RecordAudio;
import com.example.aivoicechanger.fragments.UploadAudio;

public class AudioTabAdapter extends FragmentPagerAdapter {

    Context mContext;
    int mTotalTabs;
    String data;

    public AudioTabAdapter(Context context, FragmentManager fragmentManager, int totalTabs) {
        super(fragmentManager);
        mContext = context;
        mTotalTabs = totalTabs;
    }

    public void sendData(String data) {
        this.data = data;
        getItem(0);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("asasas", position + "");
        switch (position) {
            case 0:
                return new RecordAudio();
            case 1:
                return new UploadAudio();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}
