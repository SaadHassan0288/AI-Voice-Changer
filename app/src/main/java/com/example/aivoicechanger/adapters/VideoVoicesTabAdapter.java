package com.example.aivoicechanger.adapters;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.aivoicechanger.fragments.VideoBackgrounds;
import com.example.aivoicechanger.fragments.VideoVoices;

public class VideoVoicesTabAdapter extends FragmentPagerAdapter {

    Context mContext;
    int mTotalTabs;
    String data;

    public VideoVoicesTabAdapter(Context context, FragmentManager fragmentManager, int totalTabs) {
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
                return new VideoVoices();
            case 1:
                return new VideoBackgrounds();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}
