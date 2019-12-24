package com.lflus.com.lflus.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.lflus.com.lflus.R;
import com.lflus.com.lflus.activities.MainActivity;
import com.lflus.com.lflus.adapters.FeaturedAthletsAdapter;
import com.lflus.com.lflus.adapters.NewStoriesAdapter;
import com.lflus.com.lflus.api.Api;
import com.lflus.com.lflus.helper.AppConst;
import com.lflus.com.lflus.models.HomeModel;
import com.lflus.com.lflus.network.NetworkConnectionDetector;
import com.lflus.com.lflus.utils.LFLUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends Fragment implements View.OnClickListener, YouTubePlayer.OnInitializedListener {
    RecyclerView horizontal_recyclerview, horizontal_recyclerview2;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progress_bar;
    private RecyclerView.Adapter mAdapter;
    NetworkConnectionDetector network_connection_detector;
    boolean is_internet_present = false;
    LinearLayout linear_layout;
    YouTubePlayerSupportFragment youtubeFragment;
    YouTubePlayer youTubeplayer;
    TextView text_viewall;
    View view;
    protected boolean mIsVisibleToUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initview(view);
        return view;
    }

    //------------------------ View Initilalization------------------------------------//
    private void initview(View view) {
        horizontal_recyclerview = view.findViewById(R.id.horizontal_recyclerview);
        horizontal_recyclerview2 = view.findViewById(R.id.horizontal_recyclerview2);
        text_viewall = view.findViewById(R.id.text_viewall);
        MainActivity.title_name.setText(getString(R.string.legend_footbal_league));
        horizontal_recyclerview.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        horizontal_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        horizontal_recyclerview2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        linear_layout = (LinearLayout) view.findViewById(R.id.linear_layout);
        network_connection_detector = new NetworkConnectionDetector(getActivity());
        youtubeFragment = (YouTubePlayerSupportFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.youtubeFragment);
        text_viewall.setOnClickListener(this);
        try {
            is_internet_present = false;
            if (is_internet_present = network_connection_detector.isConnectingToInternet()) {
                Getting_Home_details();
            } else {
                LFLUtils.showMessage(getActivity(), getString(R.string.nointernet), linear_layout, R.color.red);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------------- Home details api -------------------------------------//
    private void Getting_Home_details() {
        progress_bar = LFLUtils.createProgressDialog(getActivity());
        progress_bar.show();
        Call<HomeModel> call = Api.scotran().GetHomePage();
        call.enqueue(new Callback<HomeModel>() {
            @Override
            public void onResponse(Call<HomeModel> call, Response<HomeModel> response) {
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                if (response.isSuccessful()) {
                    HomeModel homemodel = response.body();
                    consumeApiData1(homemodel);
                }
            }

            @Override
            public void onFailure(Call<HomeModel> call, Throwable t) {
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }

            }

            private void consumeApiData1(final HomeModel homeModel) {
                try {
                    if (homeModel.getstatus().equalsIgnoreCase("success")) {

                        Play_Youtube_Video(homeModel.getvideo().get(0).getvideo_link());
                        mAdapter = new NewStoriesAdapter(getActivity(), homeModel);
                        horizontal_recyclerview.setAdapter(mAdapter);
                        mAdapter = new FeaturedAthletsAdapter(getActivity(), homeModel);
                        horizontal_recyclerview2.setAdapter(mAdapter);
                    } else {
                        LFLUtils.showMessage(getActivity(), homeModel.getmessage(),
                                linear_layout, R.color.red);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //---------------------- youtube video play ---------------------------------------------------//
    private void Play_Youtube_Video(final String video_url) {
        final YouTubePlayerSupportFragment youtubeFragment = YouTubePlayerSupportFragment.newInstance();
        youtubeFragment.initialize(AppConst.YOUTUBE_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubeplayer = youTubePlayer;
                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        youTubePlayer.cueVideo(video_url);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtubeFragment, youtubeFragment).commit();

    }

    //--------------------------------------- Click listner action -------------------------------------//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_viewall:
                Fragment fragment = new LFL360();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.fragment_layout, fragment).addToBackStack(null);
                ft.commit();
                break;
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.title_name.setText(getString(R.string.legend_footbal_league));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        if (isResumed()) { // fragment have created
            if (mIsVisibleToUser) {
                onVisible();
            }
        }
    }

    public void onVisible() {
        MainActivity.title_name.setText(getString(R.string.legend_footbal_league));
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.title_name.setText(getString(R.string.legend_footbal_league));
        if (mIsVisibleToUser) {
            onVisible();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean getUserVisibleHint() {
        MainActivity.title_name.setText(getString(R.string.legend_footbal_league));
        return super.getUserVisibleHint();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

