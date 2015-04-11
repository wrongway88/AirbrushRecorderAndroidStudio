package com.airbrush.airbrushrecorder;

import com.airbrush.airbrushrecorder.fragments.FragmentFlightBrowser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class ScreenSlidePagerActivity extends FragmentActivity
{
	private static final int NUM_PAGES = 2;
	
	private ViewPager _pager;
	
	private PagerAdapter _pagerAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		_pager = (ViewPager)findViewById(R.id.viewPager);
		_pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		_pager.setAdapter(_pagerAdapter);
	}
	
	@Override
	public void onBackPressed()
	{
		if(_pager.getCurrentItem() == 0)
		{
			super.onBackPressed();
		}
		else
		{
			_pager.setCurrentItem(_pager.getCurrentItem()-1);
		}
	}
	
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
	{
		public ScreenSlidePagerAdapter(FragmentManager fm)
		{
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position)
		{
			return new FragmentFlightBrowser();
		}
		
		@Override
		public int getCount()
		{
			return NUM_PAGES;
		}
	}
}
