package com.spericorn.scotran.fragments;

import android.annotation.SuppressLint;

import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.spericorn.scotran.Helper.AppConst;
import com.spericorn.scotran.R;
import com.spericorn.scotran.api.Api;
import com.spericorn.scotran.models.BaseResponse.BaseResponse;
import com.spericorn.scotran.models.PersonalInfo;
import com.spericorn.scotran.network.Connectivity;
import com.spericorn.scotran.utils.ScotranUtils;
import com.spericorn.scotran.utils.StorageUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BiometricAuthenticationFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        return inflater.inflate(R.layout.fragment_biometric_authentiction, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initializing all views within the layout here by calling this function.
        initViews(view);

    }

    //Function that handles the initialization of all views within this fragment.
    @Override
    public void initViews(View view) {

        //Initializing the progress bar & StorageUtils class's object to handle stored preferences.
        progressDialog = ScotranUtils.createProgressDialog(getActivity());
        storageUtils = StorageUtils.getInstance(getActivity());

        if (progressDialog.isShowing()){

            progressDialog.dismiss();
        }

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        LinearLayout layout_back = toolbar.findViewById(R.id.layout_back);
        layout_back.setVisibility(View.VISIBLE);

            //Performing device's hardware compatibility check for setting up biometrics.
            if (!ScotranUtils.checkBiometricStatus(getActivity())) {

                //Navigating back to the Settings screen as the device doesn't have any hardware support for biometrics.
                loadFragment(R.id.content_frame, new com.spericorn.scotran.fragments.Settings());
            } else {

                Switch biometricControlSwitch = view.findViewById(R.id.switchBiometricEnable);

                //Calling the function that fetching the biometric status and setting switch state according to it.
                getPersonalInfo(biometricControlSwitch);

            }
    }

    //Function that handles the turning on & turning off of biometric authentication.
    @SuppressLint("HardwareIds")
    private void setBiometricLogin(String biometricStatus){

        //Checking the internet connectivity before making network request.
        if (!Connectivity.checkConnection(getActivity())){

            ScotranUtils.showSnackbar(getActivity(), AppConst.NO_INTERNET,R.color.red);
        }else {

            //Making network request with the necessary parameters as the internet connection is active & showing the progress bar to indicate request is performing.
            progressDialog.show();
            Api.scotran().setBiometricLogin(AppConst.CONTENT_TYPE,AppConst.BEARER+storageUtils.getTokenValue(),storageUtils.getUserId(),
                    Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID) ,biometricStatus).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

                    //Dismissing the progress bar as we've received the response.
                    if (progressDialog.isShowing()){

                        progressDialog.dismiss();
                    }

                    //Checking whether the response is null or not.
                    if (response != null){

                        //Checking whether the response body is null or not.
                        if (response.body() != null){

                            //Checking whether the response body status returns success or not.
                            if (response.body().isSuccess()){

                                //Acknowledging the user about the response of the network request.
                                ScotranUtils.showSnackbar(getActivity(),response.body().getMSG(),R.color.scotranGreenColor);
                            }else {

                                ScotranUtils.showSnackbar(getActivity(),response.body().getMSG(),R.color.red);
                            }
                        }else {

                            ScotranUtils.showSnackbar(getActivity(),getString(R.string.something_went_wrong),R.color.red);
                        }
                    }else {

                        ScotranUtils.showSnackbar(getActivity(),getString(R.string.something_went_wrong),R.color.red);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {

                    //Dismissing the progress bar as the network resulted in an error.
                    if (progressDialog.isShowing()){

                        progressDialog.dismiss();
                    }
                    //Discovering the error encountered while the network request.
                    t.printStackTrace();
                }
            });
        }
    }

    //Function that handles the biometric status of the user.
    @SuppressLint("HardwareIds")
    private void getPersonalInfo(final Switch biometricSwitch){

        //Checking the internet connectivity before making network request.
        if (!Connectivity.checkConnection(getActivity())){

            ScotranUtils.showSnackbar(getActivity(),AppConst.NO_INTERNET,R.color.red);
        }else {

                //Making network request with the necessary parameters as the internet connection is active & showing the progress bar to indicate request is performing.
                progressDialog.show();
                Api.scotran().getPersonalInfo(AppConst.CONTENT_TYPE,AppConst.BEARER+storageUtils.getTokenValue(),storageUtils.getUserId(),
                        android.provider.Settings.Secure.getString(getActivity().getApplicationContext()
                        .getContentResolver(), android.provider.Settings.Secure.ANDROID_ID)).enqueue(new Callback<PersonalInfo>() {
                    @Override
                    public void onResponse(Call<PersonalInfo> call, Response<PersonalInfo> response) {

                        //Dismissing the progress bar as we've received the response.
                        if (progressDialog.isShowing()){

                            progressDialog.dismiss();
                        }

                        //Checking whether the response is null or not.
                        if (response != null){

                            //Checking whether the response body is null or not.
                            if (response.body() != null){

                                //Checking whether the response body status returns success or not.
                                if (response.body().isSuccess()){

                                    //Checking the biometric status of the user
                                    if (response.body().getDATA().getBiometricStatus().equals("1")){

                                        //Enabling the biometric switch if the status is 1.
                                        biometricSwitch.setChecked(true);
                                    }else {

                                        //Disabling the biometric switch if the status is 0.
                                        biometricSwitch.setChecked(false);
                                    }

                                    //Implementing biometric switch action .
                                    biometricSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                            //Performing device's hardware compatibility check for setting up biometrics.
                                            if (ScotranUtils.checkBiometricStatus(getActivity())){

                                                if (isChecked) {

                                                    //Checking the switch will call the function that handles network request
                                                    // for setting/disabling biometric login and passes 1 as argument.
                                                    setBiometricLogin("1");

                                                } else {

                                                    //Checking the switch will call the function that handles network request
                                                    // for setting/disabling biometric login and passes 0 as argument.
                                                    setBiometricLogin("0");
                                                }
                                            }
                                        }
                                    });

                                }else {

                                    ScotranUtils.showSnackbar(getActivity(),response.body().getMSG(),R.color.red);
                                }
                            }else {

                                ScotranUtils.showSnackbar(getActivity(),getString(R.string.something_went_wrong),R.color.red);
                            }
                        }else {

                            ScotranUtils.showSnackbar(getActivity(),getString(R.string.something_went_wrong),R.color.red);
                        }
                    }

                    @Override
                    public void onFailure(Call<PersonalInfo> call, Throwable t) {

                        //Dismissing the progress bar as the network resulted in an error.
                        if (progressDialog.isShowing()){

                            progressDialog.dismiss();
                        }
                        //Discovering the error encountered while the network request.
                        t.printStackTrace();
                    }
                });
        }
    }

}
