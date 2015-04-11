package com.airbrush.airbrushrecorder;

import java.util.ArrayList;
import java.util.List;

import com.airbrush.airbrushrecorder.fragments.FragmentFlightBrowser;
import com.airbrush.airbrushrecorder.fragments.FragmentRecorder;
import com.airbrush.airbrushrecorder.fragments.FragmentAccountData;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class AirbrushPagerAdapter extends FragmentPagerAdapter
{
	private List<Fragment> fragments;

	public AirbrushPagerAdapter(FragmentManager fm)
    {
		super(fm);
		this.fragments = new ArrayList<Fragment>();
		fragments.add(new FragmentRecorder());
		fragments.add(new FragmentFlightBrowser());
		fragments.add(new FragmentAccountData());
	}

	@Override
	public Fragment getItem(int position)
    {
		return fragments.get(position);
	}

	@Override
	public int getCount()
    {
		return fragments.size();
	}
}
