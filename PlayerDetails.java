package com.lflus.com.lflus.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lflus.com.lflus.R;
import com.lflus.com.lflus.activities.MainActivity;
import com.lflus.com.lflus.api.Api;
import com.lflus.com.lflus.helper.AppConst;
import com.lflus.com.lflus.models.PlayerDetailsModel;
import com.lflus.com.lflus.network.NetworkConnectionDetector;
import com.lflus.com.lflus.utils.LFLUtils;

import org.json.JSONObject;

import java.lang.reflect.Field;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerDetails extends Fragment implements View.OnClickListener {


    NetworkConnectionDetector network_connection_detector;
    boolean is_internet_present = false;
    PlayerDetailsModel playerDetailsModel;
    LinearLayout linear_layout, layout_offense_rushing, layout_offense_receiving, layout_offense_passing,
            layout_offense_rushing_two, layout_offense_fumbles, layout_defense_trackles,
            layout_defense_interceptions, layout_defense_fumbles, offense_rushing_data,
            offense_receiving_data, offense_passing_data, offense_rushing_two_data,
            offense_fumbles_data, defense_trackles_data, defense_interceptions_data, defense_fumbles_data,
            layout_offense_rushing_three, layout_offense_receiving_two, layout_defense_trackles_two,
            layout_defense_turnovers, offense_rushing_three_data, offense_receiving_two_data,defense_trackles_two_data,
            defense_turnovers_data,llMainLayout;
    TextView offense_rushing_G, offense_rushing_AVG, offense_rushing_ATT, offense_rushing_YDS,
            offense_rushing_TD, offense_receiving_G, offense_receiving_REC,
            offense_receiving_YDS, offense_receiving_AVG, offense_receiving_LONG, offense_receiving_TD,
            offense_passing_G, offense_passing_COMP, offense_passing_AT, offense_passing_PCT,
            offense_passing_YDS, offense_passing_AVG_G, offense_passing_TD, offense_passing_INT,
            offense_rushing_two_G, offense_rushing_two_ATT, offense_rushing_two_YDS, offense_rushing_two_AVG,
            offense_rushing_two_TD, offense_fumbles_G, offense_fumbles_FUM, offense_fumbles_LOST,
            defense_trackles_G, defense_trackles_TOTAL, defense_trackles_SOLO, defense_trackles_AST,
            defense_trackles_SACK, defense_trackles_TFL, defense_trackles_PDEF, defense_interceptions_G,
            defense_interceptions_INT, defense_interceptions_YDS, defense_interceptions_AVG,
            defense_interceptions_LONG, defense_interceptions_TD, defense_fumbles_G, defense_fumbles_FF,
            defense_fumbles_FR, player_number, team_name, player_other_details, offense_rushing_three_G,
            offense_rushing_three_ATT,offense_rushing_three_YDS,offense_rushing_three_AVG,offense_rushing_three_TD,
            defense_trackles_two_G,defense_trackles_two_TOTAL,defense_trackles_two_SOLO,defense_trackles_two_AST,
            defense_trackles_two_SACK,defense_trackles_two_TFL,defense_trackles_two_PDEF,
            defense_turnoversG,defense_turnovers_INT,defense_turnovers_YDS,defense_turnovers_AVG,
            defense_turnovers_LNG,defense_turnovers_TD,defense_turnovers_FF,defense_turnovers_FR,
            offense_rushing_three_LNG, offense_rushing_three_FUM, offense_rushing_three_LOST,
            offense_receiving_two_G,offense_receiving_two_ATT,offense_receiving_two_YDS,
            offense_receiving_two_AVG,offense_receiving_two_LNG,offense_receiving_two_TD, offense_rushing_two_LONG;
    ImageView player_image;
    private ProgressDialog progress_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_player_details, container, false);

        linear_layout = (LinearLayout) view.findViewById(R.id.linear_layout);

        /***************** Layout List ****************************/
        layout_offense_rushing = (LinearLayout) view.findViewById(R.id.layout_offense_rushing);
        layout_offense_receiving = (LinearLayout) view.findViewById(R.id.layout_offense_receiving);
        layout_offense_passing = (LinearLayout) view.findViewById(R.id.layout_offense_passing);
        layout_offense_rushing_two = (LinearLayout) view.findViewById(R.id.layout_offense_rushing_two);
        layout_offense_fumbles = (LinearLayout) view.findViewById(R.id.layout_offense_fumbles);
        layout_defense_trackles = (LinearLayout) view.findViewById(R.id.layout_defense_trackles);
        layout_defense_interceptions = (LinearLayout) view.findViewById(R.id.layout_defense_interceptions);
        layout_defense_fumbles = (LinearLayout) view.findViewById(R.id.layout_defense_fumbles);
        layout_offense_rushing_three = (LinearLayout) view.findViewById(R.id.layout_offense_rushing_three);
        layout_offense_receiving_two = (LinearLayout) view.findViewById(R.id.layout_offense_receiving_two);
        layout_defense_trackles_two = (LinearLayout) view.findViewById(R.id.layout_defense_trackles_two);
        layout_defense_turnovers = (LinearLayout) view.findViewById(R.id.layout_defense_turnovers);
        offense_rushing_three_data = (LinearLayout) view.findViewById(R.id.offense_rushing_three_data);
        offense_receiving_two_data = (LinearLayout) view.findViewById(R.id.offense_receiving_two_data);
        defense_trackles_two_data = (LinearLayout) view.findViewById(R.id.defense_trackles_two_data);
        defense_turnovers_data = (LinearLayout) view.findViewById(R.id.defense_turnovers_data);
        llMainLayout=(LinearLayout)view.findViewById(R.id.ll_main);

        /***************** Setting on click Listener ****************************/
        layout_offense_rushing.setOnClickListener(this);
        layout_offense_receiving.setOnClickListener(this);
        layout_offense_passing.setOnClickListener(this);
        layout_offense_rushing_two.setOnClickListener(this);
        layout_offense_rushing_three.setOnClickListener(this);
        layout_offense_fumbles.setOnClickListener(this);
        layout_defense_trackles.setOnClickListener(this);
        layout_defense_interceptions.setOnClickListener(this);
        layout_defense_fumbles.setOnClickListener(this);
        layout_offense_receiving_two.setOnClickListener(this);
        layout_defense_trackles_two.setOnClickListener(this);
        layout_defense_turnovers.setOnClickListener(this);

        /***************** Visibility hiding Layout List ****************************/

        offense_rushing_data = (LinearLayout) view.findViewById(R.id.offense_rushing_data);
        offense_receiving_data = (LinearLayout) view.findViewById(R.id.offense_receiving_data);
        offense_passing_data = (LinearLayout) view.findViewById(R.id.offense_passing_data);
        offense_rushing_two_data = (LinearLayout) view.findViewById(R.id.offense_rushing_two_data);
        offense_fumbles_data = (LinearLayout) view.findViewById(R.id.offense_fumbles_data);
        defense_trackles_data = (LinearLayout) view.findViewById(R.id.defense_trackles_data);
        defense_interceptions_data = (LinearLayout) view.findViewById(R.id.defense_interceptions_data);
        defense_fumbles_data = (LinearLayout) view.findViewById(R.id.defense_fumbles_data);

        /***************** Player common deatils ****************************/
        player_image = (ImageView) view.findViewById(R.id.player_image);
        player_number = (TextView) view.findViewById(R.id.player_number);
        team_name = (TextView) view.findViewById(R.id.team_name);
        player_other_details = (TextView) view.findViewById(R.id.player_other_details);

        /***************** OFFENSE-RUSHING ****************************/
        offense_rushing_G = (TextView) view.findViewById(R.id.offense_rushing_G);
        offense_rushing_ATT = (TextView) view.findViewById(R.id.offense_rushing_ATT);
        offense_rushing_YDS = (TextView) view.findViewById(R.id.offense_rushing_YDS);
        offense_rushing_AVG = (TextView) view.findViewById(R.id.offense_rushing_AVG);
        offense_rushing_TD = (TextView) view.findViewById(R.id.offense_rushing_TD);

        /***************** OFFENSE-RECEIVING ****************************/
        offense_receiving_G = (TextView) view.findViewById(R.id.offense_receiving_G);
        offense_receiving_REC = (TextView) view.findViewById(R.id.offense_receiving_REC);
        offense_receiving_YDS = (TextView) view.findViewById(R.id.offense_receiving_YDS);
        offense_receiving_AVG = (TextView) view.findViewById(R.id.offense_receiving_AVG);
        offense_receiving_LONG = (TextView) view.findViewById(R.id.offense_receiving_LONG);
        offense_receiving_TD = (TextView) view.findViewById(R.id.offense_receiving_TD);

        /***************** OFFENSE-PASSING ****************************/
        offense_passing_G = (TextView) view.findViewById(R.id.offense_passing_G);
        offense_passing_COMP = (TextView) view.findViewById(R.id.offense_passing_COMP);
        offense_passing_AT = (TextView) view.findViewById(R.id.offense_passing_AT);
        offense_passing_PCT = (TextView) view.findViewById(R.id.offense_passing_PCT);
        offense_passing_YDS = (TextView) view.findViewById(R.id.offense_passing_YDS);
        offense_passing_AVG_G = (TextView) view.findViewById(R.id.offense_passing_AVG_G);
        offense_passing_TD = (TextView) view.findViewById(R.id.offense_passing_TD);
        offense_passing_INT = (TextView) view.findViewById(R.id.offense_passing_INT);

        /***************** OFFENSE-RUSHING-2 ****************************/
        offense_rushing_two_G = (TextView) view.findViewById(R.id.offense_rushing_two_G);
        offense_rushing_two_ATT = (TextView) view.findViewById(R.id.offense_rushing_two_ATT);
        offense_rushing_two_YDS = (TextView) view.findViewById(R.id.offense_rushing_two_YDS);
        offense_rushing_two_AVG = (TextView) view.findViewById(R.id.offense_rushing_two_AVG);
        offense_rushing_two_TD = (TextView) view.findViewById(R.id.offense_rushing_two_TD);
        offense_rushing_two_LONG = (TextView) view.findViewById(R.id.offense_rushing_two_LONG);

        /***************** OFFENSE-FUMBLES ****************************/
        offense_fumbles_G = (TextView) view.findViewById(R.id.offense_fumbles_G);
        offense_fumbles_FUM = (TextView) view.findViewById(R.id.offense_fumbles_FUM);
        offense_fumbles_LOST = (TextView) view.findViewById(R.id.offense_fumbles_LOST);

        /***************** DEFENCE-TRACKLES ****************************/
        defense_trackles_G = (TextView) view.findViewById(R.id.defense_trackles_G);
        defense_trackles_TOTAL = (TextView) view.findViewById(R.id.defense_trackles_TOTAL);
        defense_trackles_SOLO = (TextView) view.findViewById(R.id.defense_trackles_SOLO);
        defense_trackles_AST = (TextView) view.findViewById(R.id.defense_trackles_AST);
        defense_trackles_SACK = (TextView) view.findViewById(R.id.defense_trackles_SACK);
        defense_trackles_TFL = (TextView) view.findViewById(R.id.defense_trackles_TFL);
        defense_trackles_PDEF = (TextView) view.findViewById(R.id.defense_trackles_PDEF);

        /***************** DEFENCE-INTERCEPTIONS ****************************/
        defense_interceptions_G = (TextView) view.findViewById(R.id.defense_interceptions_G);
        defense_interceptions_INT = (TextView) view.findViewById(R.id.defense_interceptions_INT);
        defense_interceptions_YDS = (TextView) view.findViewById(R.id.defense_interceptions_YDS);
        defense_interceptions_AVG = (TextView) view.findViewById(R.id.defense_interceptions_AVG);
        defense_interceptions_LONG = (TextView) view.findViewById(R.id.defense_interceptions_LONG);
        defense_interceptions_TD = (TextView) view.findViewById(R.id.defense_interceptions_TD);

        /***************** DEFENCE-FUMBLES ****************************/
        defense_fumbles_G = (TextView) view.findViewById(R.id.defense_fumbles_G);
        defense_fumbles_FF = (TextView) view.findViewById(R.id.defense_fumbles_FF);
        defense_fumbles_FR = (TextView) view.findViewById(R.id.defense_fumbles_FR);

        /***************** OFFENSE-RUSHING-3 ****************************/
        offense_rushing_three_G = (TextView) view.findViewById(R.id.offense_rushing_three_G);
        offense_rushing_three_ATT = (TextView) view.findViewById(R.id.offense_rushing_three_ATT);
        offense_rushing_three_YDS = (TextView) view.findViewById(R.id.offense_rushing_three_YDS);
        offense_rushing_three_AVG = (TextView) view.findViewById(R.id.offense_rushing_three_AVG);
        offense_rushing_three_LNG = (TextView) view.findViewById(R.id.offense_rushing_three_LNG);
        offense_rushing_three_TD = (TextView) view.findViewById(R.id.offense_rushing_three_TD);
        offense_rushing_three_FUM = (TextView) view.findViewById(R.id.offense_rushing_three_FUM);
        offense_rushing_three_LOST = (TextView) view.findViewById(R.id.offense_rushing_three_LOST);

        /***************** OFFENSE-RECEIVING-2 ****************************/
        offense_receiving_two_G = (TextView) view.findViewById(R.id.offense_receiving_two_G);
        offense_receiving_two_ATT = (TextView) view.findViewById(R.id.offense_receiving_two_ATT);
        offense_receiving_two_YDS = (TextView) view.findViewById(R.id.offense_receiving_two_YDS);
        offense_receiving_two_AVG = (TextView) view.findViewById(R.id.offense_receiving_two_AVG);
        offense_receiving_two_LNG = (TextView) view.findViewById(R.id.offense_receiving_two_LNG);
        offense_receiving_two_TD = (TextView) view.findViewById(R.id.offense_receiving_two_TD);

        /***************** DEFENCE-TRACKLES-2 ****************************/
        defense_trackles_two_G = (TextView) view.findViewById(R.id.defense_trackles_two_G);
        defense_trackles_two_TOTAL = (TextView) view.findViewById(R.id.defense_trackles_two_TOTAL);
        defense_trackles_two_SOLO = (TextView) view.findViewById(R.id.defense_trackles_two_SOLO);
        defense_trackles_two_AST = (TextView) view.findViewById(R.id.defense_trackles_two_AST);
        defense_trackles_two_SACK = (TextView) view.findViewById(R.id.defense_trackles_two_SACK);
        defense_trackles_two_TFL = (TextView) view.findViewById(R.id.defense_trackles_two_TFL);
        defense_trackles_two_PDEF = (TextView) view.findViewById(R.id.defense_trackles_two_PDEF);

        /***************** DEFENSE - TURNOVERS ****************************/
        defense_turnoversG = (TextView) view.findViewById(R.id.defense_turnoversG);
        defense_turnovers_INT = (TextView) view.findViewById(R.id.defense_turnovers_INT);
        defense_turnovers_YDS = (TextView) view.findViewById(R.id.defense_turnovers_YDS);
        defense_turnovers_AVG = (TextView) view.findViewById(R.id.defense_turnovers_AVG);
        defense_turnovers_LNG = (TextView) view.findViewById(R.id.defense_turnovers_LNG);
        defense_turnovers_TD = (TextView) view.findViewById(R.id.defense_turnovers_TD);
        defense_turnovers_FF = (TextView) view.findViewById(R.id.defense_turnovers_FF);
        defense_turnovers_FR = (TextView) view.findViewById(R.id.defense_turnovers_FR);

        network_connection_detector = new NetworkConnectionDetector(getActivity());

        Bundle args = getArguments();
        String team_id = args.getString("team_id");
        String player_id = args.getString("player_id");
        try {
            is_internet_present = false;
            if (is_internet_present = network_connection_detector.isConnectingToInternet()) {
                Getting_Player_details(team_id, player_id);
            } else {
                LFLUtils.showMessage(getActivity(), getString(R.string.nointernet), linear_layout, R.color.red);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;

    }

    private void Getting_Player_details(String team_id, String player_id) {
        progress_bar = LFLUtils.createProgressDialog(getActivity());
        progress_bar.show();
        Call<PlayerDetailsModel> call = Api.scotran().PlayerDetails(
                AppConst.CONTENT_TYPE,
                team_id,
                player_id);

        call.enqueue(new Callback<PlayerDetailsModel>() {
            @Override
            public void onResponse(Call<PlayerDetailsModel> call, Response<PlayerDetailsModel> response) {
                /*dismiss progress bar*/
                JSONObject obj = new JSONObject();
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                if (response.isSuccessful()) {
                    /* get the parsed response from api to a model class objet and pass it to
                    another medthod to consume*/

                    llMainLayout.setVisibility(View.VISIBLE);
                    PlayerDetailsModel PlayerDetailsModel = response.body();
                    consumeApiData1(PlayerDetailsModel);
                } else {
                }
            }

            @Override
            public void onFailure(Call<PlayerDetailsModel> call, Throwable t) {
                /*dismiss progress bar*/
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                // showErrorMessage(StringHelper.REQUESTFAILED);
            }

            private void consumeApiData1(final PlayerDetailsModel playerDetailsModel) {
                try {
                    if (playerDetailsModel.getStatus().equalsIgnoreCase("success")) {

                        /***************** Setting Player Details ****************************/


                        Glide.with(getActivity())
                                .load(playerDetailsModel.getData().get(0).getThumb_image())
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(player_image);
                        player_number.setText(playerDetailsModel.getData().get(0).getPlayer_number());
                        team_name.setText(playerDetailsModel.getData().get(0).getTeam());
                        player_other_details.setText(playerDetailsModel.getData().get(0).getPlayer_name() + " "
                                + playerDetailsModel.getData().get(0).getPlayer_height() + "|"
                                + playerDetailsModel.getData().get(0).getPlayer_weight() + "|"
                                + playerDetailsModel.getData().get(0).getPlayer_age());

                        /***************** OFFENSE-RUSHING ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_1().getStatus().equals("0")) {
                            layout_offense_rushing.setVisibility(View.GONE);
                        } else {
                            offense_rushing_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_1().getG());
                            offense_rushing_ATT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_1().getATT());
                            offense_rushing_YDS.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_1().getYDS());
                            offense_rushing_AVG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_1().getAVG());
//                            offense_rushing_LONG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_1().getLONG());
                            offense_rushing_TD.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_1().getTD());
                        }

                        /***************** OFFENSE-RECEIVING ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving().getStatus().equals("0")) {
                            layout_offense_receiving.setVisibility(View.GONE);

                        } else {
                            offense_receiving_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving().getG());
                            offense_receiving_REC.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving().getREC());
                            offense_receiving_YDS.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving().getYDS());
                            offense_receiving_AVG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving().getAVG());
                            offense_receiving_LONG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving().getLONG());
                            offense_receiving_TD.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving().getTD());
                        }

                        /***************** OFFENSE-PASSING ****************************/
                        Log.e("off_passing:", playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getStatus());
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getStatus().equals("0")) {
                            layout_offense_passing.setVisibility(View.GONE);
                        } else {
                            offense_passing_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getG());
                            offense_passing_COMP.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getCOMP());
                            offense_passing_AT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getATT());
                            offense_passing_PCT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getPCT());
                            offense_passing_YDS.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getYDS());
                            offense_passing_AVG_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getAVG());
                            offense_passing_TD.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getTD());
                            offense_passing_INT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_passing().getInt());
                        }

                        /***************** OFFENSE-RUSHING-2 ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_2().getStatus().equals("0")) {
                            layout_offense_rushing_two.setVisibility(View.GONE);

                        } else {
                            offense_rushing_two_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_2().getG());
                            offense_rushing_two_ATT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_2().getATT());
                            offense_rushing_two_YDS.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_2().getYDS());
                            offense_rushing_two_AVG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_2().getAVG());
                            offense_rushing_two_LONG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_2().getLONG());
                            offense_rushing_two_TD.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_2().getTD());
                        }

                        /***************** OFFENSE-FUMBLES ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_fumbles().getStatus().equals("0")) {
                            layout_offense_fumbles.setVisibility(View.GONE);
                        } else {
                            offense_fumbles_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_fumbles().getG());
                            offense_fumbles_FUM.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_fumbles().getFUM());
                            offense_fumbles_LOST.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_fumbles().getLOST());
                        }

                        /***************** DEFENCE-TRACKLES ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles().getStatus().equals("0")) {
                            layout_defense_trackles.setVisibility(View.GONE);
                        } else {
                            defense_trackles_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles().getG());
                            defense_trackles_TOTAL.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles().getTOTAL());
                            defense_trackles_SOLO.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles().getSOLO());
                            defense_trackles_AST.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles().getAST());
                            defense_trackles_SACK.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles().getSACK());
                            defense_trackles_TFL.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles().getTFL());
                            defense_trackles_PDEF.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles().getPDEF());
                        }

                        /***************** DEFENCE-INTERCEPTIONS ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_interceptions().getStatus().equals("0")) {
                            layout_defense_interceptions.setVisibility(View.GONE);
                        } else {
                            defense_interceptions_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_interceptions().getG());
                            defense_interceptions_INT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_interceptions().getINT());
                            defense_interceptions_YDS.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_interceptions().getYDS());
                            defense_interceptions_AVG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_interceptions().getAVG());
                            defense_interceptions_LONG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_interceptions().getLONG());
                            defense_interceptions_TD.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_interceptions().getTD());
                        }

                        /***************** DEFENCE-FUMBLES ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_fumbles().getStatus().equals("0")) {
                            layout_defense_fumbles.setVisibility(View.GONE);
                        } else {
                            defense_fumbles_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_fumbles().getG());
                            defense_fumbles_FF.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_fumbles().getFF());
                            defense_fumbles_FR.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_fumbles().getFr());
                        }

                        /***************** OFFENSE-RUSHING-3 ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getStatus().equals("0")) {
                            layout_offense_rushing_three.setVisibility(View.GONE);

                        } else {
                            offense_rushing_three_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getG());
                            offense_rushing_three_ATT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getATT());
                            offense_rushing_three_YDS.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getYDS());
                            offense_rushing_three_AVG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getAVG());
                            offense_rushing_three_LNG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getLNG());
                            offense_rushing_three_TD.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getTD());
                            offense_rushing_three_FUM.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getFUM());
                            offense_rushing_three_LOST.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_rushing_3().getLOST());
                        }

                        /***************** OFFENSE-RECEIVING-2 ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving_2().getStatus().equals("0")) {
                            layout_offense_receiving_two.setVisibility(View.GONE);

                        } else {
                            offense_receiving_two_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving_2().getG());
                            offense_receiving_two_ATT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving_2().getATT());
                            offense_receiving_two_YDS.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving_2().getYDS());
                            offense_receiving_two_AVG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving_2().getAVG());
                            offense_receiving_two_LNG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving_2().getLNG());
                            offense_receiving_two_TD.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getOffense_receiving_2().getTD());
                        }

                        /***************** DEFENCE-TRACKLES-2 ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles_2().getStatus().equals("0")) {
                            layout_defense_trackles_two.setVisibility(View.GONE);
                        } else {
                            defense_trackles_two_G.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles_2().getG());
                            defense_trackles_two_TOTAL.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles_2().getTotal());
                            defense_trackles_two_SOLO.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles_2().getSolo());
                            defense_trackles_two_AST.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles_2().getAST());
                            defense_trackles_two_SACK.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles_2().getSACK());
                            defense_trackles_two_TFL.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles_2().getTFL());
                            defense_trackles_two_PDEF.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_tackles_2().getPDEF());
                        }

                        /***************** DEFENSE-TURNOVERS ****************************/
                        if (playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getStatus().equals("0")) {
                            layout_defense_turnovers.setVisibility(View.GONE);
                        } else {
                            defense_turnoversG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getG());
                            defense_turnovers_INT.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getINT());
                            defense_turnovers_YDS.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getYDS());
                            defense_turnovers_AVG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getAVG());
                            defense_turnovers_LNG.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getLNG());
                            defense_turnovers_TD.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getTD());
                            defense_turnovers_FF.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getFF());
                            defense_turnovers_FR.setText(playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getFR());
                        }

                        Class getDefense_turnovers = playerDetailsModel.getData().get(0).getPlayer_stats().getDefense_turnovers().getClass();
                        Field[] f = getDefense_turnovers.getDeclaredFields();
                        System.out.println("--------------------");
                        for(int i = 0; i < f.length; i++) {
                            System.out.println("Field = " + f[i].toString());
                        }

                    } else {
                        LFLUtils.showMessage(getActivity(), playerDetailsModel.getMessage(),
                                linear_layout, R.color.red);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.layout_offense_rushing:
                if (offense_rushing_data.getVisibility() == View.VISIBLE) {
                    offense_rushing_data.setVisibility(View.GONE);
                } else {
                    offense_rushing_data.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_offense_receiving:
                if (offense_receiving_data.getVisibility() == View.VISIBLE) {
                    offense_receiving_data.setVisibility(View.GONE);
                } else {
                    offense_receiving_data.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_offense_passing:
                if (offense_passing_data.getVisibility() == View.VISIBLE) {
                    offense_passing_data.setVisibility(View.GONE);
                } else {
                    offense_passing_data.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_offense_rushing_two:
                if (offense_rushing_two_data.getVisibility() == View.VISIBLE) {
                    offense_rushing_two_data.setVisibility(View.GONE);
                } else {
                    offense_rushing_two_data.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_offense_fumbles:
                if (offense_fumbles_data.getVisibility() == View.VISIBLE) {
                    offense_fumbles_data.setVisibility(View.GONE);
                } else {
                    offense_fumbles_data.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_defense_trackles:
                if (defense_trackles_data.getVisibility() == View.VISIBLE) {
                    defense_trackles_data.setVisibility(View.GONE);
                } else {
                    defense_trackles_data.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_defense_interceptions:
                if (defense_interceptions_data.getVisibility() == View.VISIBLE) {
                    defense_interceptions_data.setVisibility(View.GONE);
                } else {
                    defense_interceptions_data.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_defense_fumbles:
                if (defense_fumbles_data.getVisibility() == View.VISIBLE) {
                    defense_fumbles_data.setVisibility(View.GONE);
                } else {
                    defense_fumbles_data.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.layout_offense_rushing_three:
                if (offense_rushing_three_data.getVisibility() == View.VISIBLE) {
                    offense_rushing_three_data.setVisibility(View.GONE);
                } else {
                    offense_rushing_three_data.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.layout_offense_receiving_two:
                if (offense_receiving_two_data.getVisibility() == View.VISIBLE) {
                    offense_receiving_two_data.setVisibility(View.GONE);
                } else {
                    offense_receiving_two_data.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.layout_defense_trackles_two:
                if (defense_trackles_two_data.getVisibility() == View.VISIBLE) {
                    defense_trackles_two_data.setVisibility(View.GONE);
                } else {
                    defense_trackles_two_data.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.layout_defense_turnovers:
                if (defense_turnovers_data.getVisibility() == View.VISIBLE) {
                    defense_turnovers_data.setVisibility(View.GONE);
                } else {
                    defense_turnovers_data.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
    public void onResume()
    {
        super.onResume();
        MainActivity.title_name.setText("PLAYER DETAILS");
    }
}
