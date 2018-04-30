package com.midas.githubstars.ui.act;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.midas.githubstars.MyApp;
import com.midas.githubstars.R;
import com.midas.githubstars.ui.frag.FrApi;
import com.midas.githubstars.ui.frag.FrLocal;


/*
Main Activity
 */
public class ActMain extends AppCompatActivity
{
    /************************* Define *************************/
    final public static int PAGE_COUNT = 2;//viewpager item cnt
    final public static int PAGE_API = 0;//api fragment
    final public static int PAGE_LOCAL = 1;//local fragment
    /************************* Member *************************/
    private Context m_Context = null;
    private Activity m_Activity = null;
    private MyApp m_App = null;
    private RequestManager m_RequestManager = null;

    /************************* Controller *************************/
    private ViewPager m_ViewPager = null;
    private TextView m_tv_Api = null;
    private TextView m_tv_Local = null;
    /************************* System Function *************************/
    //--------------------------------------------------------------------------
    //
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        m_Context = this;
        m_Activity = this;
        m_App = (MyApp)getApplication();
        if(m_App.m_bInit == false)
            m_App.init(m_Context);

        m_RequestManager = Glide.with(m_Context);

        recvIntentData();
        initValue();
        setInitLayout();
    }
    //--------------------------------------------------------------------------
    //
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();


    }
    /************************* User Function *************************/
    //--------------------------------------------------------------------------
    //
    public void recvIntentData()
    {

    }
    //--------------------------------------------------------------------------
    //
    public void initValue()
    {

    }
    //--------------------------------------------------------------------------
    //
    public void setInitLayout()
    {
        m_ViewPager = (ViewPager)findViewById(R.id.viewPager);
        m_tv_Api = (TextView)findViewById(R.id.tv_Api);
        m_tv_Local = (TextView)findViewById(R.id.tv_Local);

        m_ViewPager.addOnPageChangeListener(onPageChangeListener);
        m_tv_Api.setOnClickListener(onClickApiTap);
        m_tv_Local.setOnClickListener(onClickLocalTap);

        settingView();
    }
    //--------------------------------------------------------------------------
    //
    public void settingView()
    {
        m_ViewPager.setAdapter(new pagerAdapter(getSupportFragmentManager()));

        m_ViewPager.setCurrentItem(PAGE_API);
        m_ViewPager.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                onPageChangeListener.onPageSelected(PAGE_API);
            }
        },100);
    }

    //--------------------------------------------------------------------------
    //
    public void refreshTopUi(int position)
    {
        switch (position)
        {
            case PAGE_API:
                m_tv_Api.setTextColor(m_Context.getResources().getColor(R.color.colorAccent));
                m_tv_Local.setTextColor(m_Context.getResources().getColor(R.color.color_black));
                break;
            case PAGE_LOCAL:
                m_tv_Api.setTextColor(m_Context.getResources().getColor(R.color.color_black));
                m_tv_Local.setTextColor(m_Context.getResources().getColor(R.color.colorAccent));
                break;
            default:
                break;
        }
    }

    /************************* Listener *************************/
    //---------------------------------------------------------------------------------------------------
    //
    View.OnClickListener onClickApiTap = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            m_ViewPager.setCurrentItem(PAGE_API);
        }
    };
    //---------------------------------------------------------------------------------------------------
    //
    View.OnClickListener onClickLocalTap = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            m_ViewPager.setCurrentItem(PAGE_LOCAL);
        }
    };
    //---------------------------------------------------------------------------------------------------
    //
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {

        }

        @Override
        public void onPageSelected(int position)
        {
            refreshTopUi(position);
        }

        @Override
        public void onPageScrollStateChanged(int state)
        {

        }
    };

    /************************* Inner Class *************************/

    /********************************** Inner Class **********************************/
    //---------------------------------------------------------------------------------------------------
    //
    public class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case PAGE_API:
                    return new FrApi();//api
                case PAGE_LOCAL:
                    return new FrLocal();
                default:
                    return null;
            }
        }

        @Override
        public int getCount()
        {
            return PAGE_COUNT;
        }
    }



    /************************* Interface *************************/
}
