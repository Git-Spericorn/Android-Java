package com.spericorn.chat.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.spericorn.chat.R;
import com.spericorn.chat.adapter.ViewpagerAdater;
import com.spericorn.chat.apicall.API;
import com.spericorn.chat.chat.BaseActivity;
import com.spericorn.chat.fragment.CompletedFragment;
import com.spericorn.chat.interfaces.FilterClose;
import com.spericorn.chat.interfaces.OpenCloseInterface;
import com.spericorn.chat.interfaces.TabCloseInterface;
import com.spericorn.chat.interfaces.UpdateFrag;
import com.spericorn.chat.utils.AppConst;
import com.spericorn.chat.utils.SpericornUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends BaseActivity implements View.OnClickListener, OpenCloseInterface, TabCloseInterface, ViewPager.PageTransformer {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView ivFilter;
    LinearLayout layout_header, layout_view_pager, linearLayoutHeader;
    final int[] ICONS = new int[]{
            R.drawable.selector_current_chat,
            R.drawable.selector_completedchat,
            R.drawable.selector_settings};
    UpdateFrag updatfrag;
    FilterClose filter;
    TextView tvHeader;
    ViewpagerAdater adapter;
    String refreshtoken;
    String businessuid;
    final Handler handler = new Handler();
    private com.github.nkzawa.socketio.client.Socket socket;

    {
        try {
            socket = IO.socket(API.soket_url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        socket.connect();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        refreshtoken = SpericornUtils.getDefaults(AppConst.EXTRA_REFRESHtOKEN, MainActivity.this);
        businessuid = SpericornUtils.getDefaults(AppConst.ISBUSINESSADMIN, MainActivity.this);
        notificationEmit();
        initview();
    }

    private void initview() {
        startService();
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewpager);
        layout_view_pager = findViewById(R.id.layout_view_pager);
        ivFilter = findViewById(R.id.iv_filter);
        linearLayoutHeader = findViewById(R.id.linearLayoutHeader);
        linearLayoutHeader.setOnClickListener(this);
        tvHeader = findViewById(R.id.tv_header);
        layout_header = findViewById(R.id.layout_header);
        layout_header.setOnClickListener(this);
        ivFilter.setOnClickListener(this);
        viewPager.setOnClickListener(this);
        layout_view_pager.setOnClickListener(this);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Current));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Completed));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Settings));
        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);
        adapter = new ViewpagerAdater(this, getSupportFragmentManager(), tabLayout.getTabCount(), MainActivity.this, (TabCloseInterface) MainActivity.this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#4D7CFE"));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int CurrentPossition = 0;

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }


            public void onPageSelected(int position) {
                CurrentPossition = position;
            }


            public void onPageScrollStateChanged(int state) {
            }
        });


        //-----------------------------------View pager animation---------------------------------//
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int i = 0; i < tabsCount; i++) {
            int delay = (i * 150) + 750; //this is starting delay
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
            vgTab.setScaleX(0f);
            vgTab.setScaleY(0f);
            vgTab.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(delay)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setDuration(450)
                    .start();
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(linearLayoutHeader.getWindowToken(), 0);
                if (tab.getText().equals(AppConst.TAB_COMPLETED)) {
                    ivFilter.setVisibility(View.VISIBLE);
                } else {
                    ivFilter.setVisibility(View.GONE);
                }
                if (tab.getText().equals(AppConst.TAB_SETTINGS)) {
                    tvHeader.setText(AppConst.TAB_SETTINGS);

                } else if (tab.getText().equals(AppConst.TAB_COMPLETED)) {
                    tvHeader.setText(getString(R.string.completed_chats));
                    if (filter != null) {
                        ivFilter.setImageResource(R.drawable.ic_filter_icon_new);
                        filter.filterclose();
                    }
                } else {
                    tvHeader.setText("Chats");
                }
                viewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }


            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.text_ashcolor));
            drawable.setSize(1, 1);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }

    public void updateApi(UpdateFrag listener) {
        updatfrag = listener;
    }

    public void filterclose(FilterClose filterClose) {
        filter = filterClose;
    }

    public void onClick(View v) {
        if (v == ivFilter) {
            if (updatfrag != null) {
                updatfrag.updatefrag();
            }
        } else if (v == layout_header) {
            SpericornUtils.hideKeyboard(v, getApplicationContext());
        } else if (v == linearLayoutHeader) {
            SpericornUtils.hideKeyboard(v, getApplicationContext());
        }
    }


    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof CompletedFragment) {
        }
    }


    public void openclose(boolean isvalue) {

    }

    public void tabclose(boolean isvalue) {
        if (isvalue == true) {
            tabLayout.setVisibility(View.GONE);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tabLayout.setVisibility(View.VISIBLE);

                }
            }, 100);
        }
    }

    public void onBackPressed() {

        finishAffinity();
    }

    @Override
    public void transformPage(@NonNull View view, float position) {


        view.setRotationY(position * -30);
    }

    private void startService() {

    }


    private void notificationEmit() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppConst.BUSINESS_UID, businessuid);
            jsonObject.put(AppConst.REFRESH_TOKEN, refreshtoken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(AppConst.SUBSCRIBE_NOTIFICATION, jsonObject);
    }

}
