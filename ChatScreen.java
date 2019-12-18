package com.spericorn.chat.chat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.spericorn.chat.R;
import com.spericorn.chat.activity.AttachmentActivity;
import com.spericorn.chat.activity.MainActivity;
import com.spericorn.chat.activity.VideoViewActivity;
import com.spericorn.chat.adapter.ChatMessageAdapter;
import com.spericorn.chat.apicall.API;
import com.spericorn.chat.interfaces.KeyboardAppearanceListener;
import com.spericorn.chat.interfaces.RecyclerViewAdapterListener;
import com.spericorn.chat.model.CustmerDetailsFetch;
import com.spericorn.chat.model.DatumHistoryNew;
import com.spericorn.chat.model.LeaveChatModel;
import com.spericorn.chat.model.Location;
import com.spericorn.chat.model.SocketNewHistoryModel;
import com.spericorn.chat.model.SocketNewModel;
import com.spericorn.chat.utils.AppConst;
import com.spericorn.chat.utils.SpericornUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatScreen extends BaseActivity implements View.OnClickListener, KeyboardAppearanceListener, DialogInterface {
    LinearLayout llAddLayout;
    AlertDialog.Builder alertDialog;
    String strDate;
    AlertDialog dialog_customer_detials;
    RecyclerView recyclerView;
    public LinearLayoutManager mLayoutManager;
    public View llmain;
    ChatMessageAdapter chatMessageAdapter;
    DatumHistoryNew datumHistory;
    private ProgressDialog progress_bar;
    SocketNewHistoryModel chatSocketModel;
    Location locationModel;
    public com.github.nkzawa.socketio.client.Socket mSocket;
    public String strCustomername = "";
    String strFormattedDate;
    public TextView textViewName;
    LinearLayout linearLayoutBack;
    SocketNewModel socketModel;
    SocketNewModel socketModelTyping;
    TextView txtOnline;
    String strName, agentFname, agentFname_chathistory;
    String firstLetter;
    String agentprofileName;
    String nametxt;
    LinearLayout layoutFullType;
    public ImageView imgPerson;
    boolean isbackground = true;
    List<DatumHistoryNew> datumHistoryNew;
    Handler handler = new Handler();
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    String socketbusinessid;
    public boolean isseen = true;
    public boolean ismesge_customer = false;
    public boolean ismesge_agent = false;
    public boolean ismesge_agent_new = false;
    public boolean booldate = false;
    String formatdate, socketdate;
    String currentdate;
    boolean isagent = false;
    public String agentchtuid;
    SocketNewHistoryModel currntlistmodel_seen;
    LinearLayout layout_bottom;

    {
        try {
            mSocket = IO.socket(API.soket_url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public LinearLayout llMessageSent;
    public EditText etTyping;
    public String strCustomerId = "";
    public String agentUid = "";
    public String api = API.soket_url;
    public String agentname = "";
    LinearLayout layout_parent, layout_header;
    public boolean istype = true;
    ImageView ivChat;
    public boolean isstate = true;
    String strfrom;
    String businessuid;
    LinearLayout layout_images, layout_details;
    LinearLayout layout_no_internet, layout_no_history_found;
    String cusid = "";
    String oldatae, oldateViaSocket;
    boolean isKeyboardShowing = false;
    String Authorization_token;
    public ImageView iv_DataImage;
    public TextView tvMainMessage, tvSubMessage, tvLastMessage;
    public Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 30000)) {
                // TODO: do what you need here
                // ............
                // ............
                istype = true;
                nottyping();
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Authorization_token = SpericornUtils.getDefaults(AppConst.TOKEN, ChatScreen.this);
        ReceiveData();
        mSocket.connect();
        iniview();
        keyboardListener(R.id.layout_main, ChatScreen.this, this);
        socketbusinessid = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, ChatScreen.this);
        businessuid = SpericornUtils.getDefaults(AppConst.ISBUSINESSADMIN, ChatScreen.this);
        agentFname = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
        agentFname_chathistory = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        formatdate = df.format(c.getTime());
        if (SpericornUtils.hasInternetAccess(ChatScreen.this)) {
            if (strCustomerId != null) {
                getChatHistory(strCustomerId);
            }

        } else {
            noInternet();
        }
        SocketConnectionEstablished();
        typingNotTypingMode();

        //--------------------------------- Scrollview scrolldown code--------------------------//
        llmain.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        llmain.getWindowVisibleDisplayFrame(r);
                        int screenHeight = llmain.getRootView().getHeight();
                        int keypadHeight = screenHeight - r.bottom;
                        if (keypadHeight > screenHeight * 0.15) {

                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                            }
                        } else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
                            }
                        }
                    }
                });
    }

    void onKeyboardVisibilityChanged(boolean opened) {
        if (opened) {
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        }
    }

    public void ReceiveData() {
        agentUid = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, ChatScreen.this);
        strfrom = getIntent().getStringExtra(AppConst.FROM);
        strCustomerId = getIntent().getStringExtra("customerUid");
    }

    public void iniview() {

        //---------------------------------- View Declaration--------------------------------//

        llAddLayout = findViewById(R.id.ll_add);
        ivChat = findViewById(R.id.iv_sent);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        layout_images = findViewById(R.id.layout_image);
        layout_details = findViewById(R.id.layout_details);
        layoutFullType = findViewById(R.id.viewB);
        recyclerView = findViewById(R.id.messages_view);
        llMessageSent = findViewById(R.id.ll_messageSent);
        recyclerView.setLayoutManager(mLayoutManager);
        etTyping = findViewById(R.id.et_Typing);
        llAddLayout.setOnClickListener(this);
        llMessageSent.setOnClickListener(this);
        progress_bar = SpericornUtils.createProgressDialog(ChatScreen.this);
        llmain = findViewById(R.id.layout_main);
        textViewName = findViewById(R.id.textName);
        linearLayoutBack = findViewById(R.id.layout_back);
        linearLayoutBack.setOnClickListener(this);
        txtOnline = findViewById(R.id.text_online);
        imgPerson = findViewById(R.id.imgContact);
        layout_parent = findViewById(R.id.viewA);
        layout_header = findViewById(R.id.layout_header);
        layout_header.setOnClickListener(this);
        layout_images.setOnClickListener(this);
        layout_details.setOnClickListener(this);
        layout_no_internet = findViewById(R.id.layout_no_internet);
        layout_no_history_found = findViewById(R.id.no_chathistory_found);
        iv_DataImage = findViewById(R.id.iv_DataImage);
        tvMainMessage = findViewById(R.id.tv_Main_Message);
        tvSubMessage = findViewById(R.id.tv_submessage);
        tvLastMessage = findViewById(R.id.tvlastmessage);
        recyclerView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpericornUtils.hideKeyboard(v, getApplicationContext());
            }
        });
        //-------------------------- Textwatcher code -----------------------------//

        etTyping.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0) {
                    if (istype) {
                        typing();
                    }
                } else {
                    istype = true;
                    nottyping();
                }
                handler.removeCallbacks(input_finish_checker);
            }
        });
        NameSet(strCustomername, false);
        layout_parent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpericornUtils.hideKeyboard(v, getApplicationContext());
            }
        });

    }

    public void NameSet(String customerNmae, boolean value) {
        if (customerNmae != null && !customerNmae.equalsIgnoreCase("")) {
            if (customerNmae.contains(" ")) {
                String[] arrSplit = customerNmae.split(" ");
                String firstletter = arrSplit[0];
                String secondletter = arrSplit[1];
                if (firstletter != null && secondletter != null && !firstletter.equalsIgnoreCase("") && !secondletter.equalsIgnoreCase("")) {
                    firstLetter = firstletter.charAt(0) + String.valueOf(secondletter.charAt(0));
                }
            } else {
                firstLetter = String.valueOf(customerNmae.charAt(0));
            }
            nametxt = customerNmae;
            //-------------------------------- Round Textview ---------------------------------------//
            if (firstLetter != null) {
                if (!nametxt.contains("user_") && !nametxt.contains("#")) {

                    @SuppressLint({"NewApi", "LocalSuppress"}) TextDrawable drawable = TextDrawable.builder()
                            .beginConfig()
                            .textColor(Color.WHITE)
                            .useFont(Typeface.DEFAULT)
                            .fontSize(50)
                            .bold()
                            .toUpperCase()
                            .endConfig()
                            .buildRoundRect(firstLetter, getApplicationContext().getColor(R.color.color_chat_header_image), 100);
                    imgPerson.setImageDrawable(drawable);
                }
            }
        }
        if (value) {
            if (SpericornUtils.hasInternetAccess(ChatScreen.this)) {
                if (strCustomerId != null) {
                    getChatHistory(strCustomerId);
                }

            } else {
                noInternet();
            }
        }
    }

    // -------------------------------------  View Click function ---------------------------------//
    public void onClick(View v) {
        if (v == llAddLayout) {
            SpericornUtils.hideKeyboard(v, ChatScreen.this);
            menuoption(v);
        } else if (v == llMessageSent) {
            agentchtuid = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
            if (isstate) {
                if (etTyping.getText().toString() != null && !etTyping.getText().toString().equalsIgnoreCase(" ")) {
                    nottyping();
                    messageSent(etTyping.getText().toString().trim(), agentchtuid);
                }
            } else {
                showdialog(getString(R.string.closechat));
            }
        } else if (v == linearLayoutBack) {
            SpericornUtils.hideKeyboard(v, getApplicationContext());

            if (dialog_customer_detials != null) {
                if (dialog_customer_detials.isShowing()) {
                    dialog_customer_detials.dismiss();
                }
            }
            if (strfrom != null) {
                if (strfrom.equalsIgnoreCase("from") || strfrom.equalsIgnoreCase("complete")) {
                    finish();
                } else {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } else if (v == layout_header) {
            SpericornUtils.hideKeyboard(v, getApplicationContext());
        } else if (v == layout_details) {
            SpericornUtils.hideKeyboard(v, getApplicationContext());
        }
    }
    //----------------------- Alert Dialog box ------------------------------//

    public void showdialog(String txt) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(txt);
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        isstate = true;
                        etTyping.setVisibility(View.VISIBLE);
                        ivChat.setImageResource(R.drawable.ic_sent);

                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //--------------------------- Popup Menu ------------------------------------//

    public void menuoption(View view) {
        final PopupMenu popup = new PopupMenu(ChatScreen.this, view);

        popup.getMenuInflater().inflate(R.menu.poupup_menu, popup.getMenu());
        Menu popupMenu = popup.getMenu();
        if (chatSocketModel != null) {
            if (chatSocketModel.getData() != null && chatSocketModel.getData().size() > 0) {
                if (chatSocketModel.getData().get(0) != null) {
                    if (chatSocketModel.getData().get(0).getIsPicked().size() == 0) {
                        popupMenu.findItem(R.id.leave_chat).setVisible(false);
                        popupMenu.findItem(R.id.customerDetails).setVisible(false);
                        popupMenu.findItem(R.id.location).setVisible(false);
                    } else {
                        if (chatSocketModel.getData().get(0).getIsPicked().contains(socketbusinessid)) {
                            popupMenu.findItem(R.id.leave_chat).setVisible(true);
                            popupMenu.findItem(R.id.customerDetails).setVisible(true);
                            popupMenu.findItem(R.id.location).setVisible(true);
                        } else if (!chatSocketModel.getData().get(0).getIsPicked().contains(socketbusinessid)) {
                            popupMenu.findItem(R.id.leave_chat).setVisible(false);
                            popupMenu.findItem(R.id.customerDetails).setVisible(false);
                        }
                    }
                }
            }
        }
        if (strfrom.equalsIgnoreCase("complete")) {
            popupMenu.findItem(R.id.leave_chat).setVisible(false);
            popupMenu.findItem(R.id.location).setVisible(false);
        }
        MenuItem item_one = popupMenu.findItem(R.id.customerDetails);
        MenuItem item_two = popupMenu.findItem(R.id.leave_chat);
        MenuItem item_three = popupMenu.findItem(R.id.attachment);
        MenuItem item_four = popupMenu.findItem(R.id.location);
        item_one.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                getCustomerDetails();
                return true;
            }
        });
        item_two.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                InputMethodManager inputManager = (InputMethodManager) ChatScreen.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(llmain.getWindowToken(), 0);
                new android.app.AlertDialog.Builder(ChatScreen.this)
                        .setTitle(getString(R.string.alert))
                        .setMessage(getString(R.string.endchat_alert))
                        .setPositiveButton("Yes", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (SpericornUtils.hasInternetAccess(ChatScreen.this)) {
                                    leaveChat();
                                } else {
                                    noInternet();
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                //leaveChat();
                return true;
            }
        });

        item_three.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                redirectattachment();
                return true;
            }
        });
        item_four.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                getlocation();
                return false;
            }
        });

        popup.show();
    }
    //-------------------------------------- Location Api fetch -------------------------------//

    private void getlocation() {
        progress_bar.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AppConst.BUSINESS_UID, businessuid);
        jsonObject.addProperty(AppConst.AGENT_UID, agentUid);
        Call<Location> call = API.spericorn().getlocation(AppConst.APPLICATION_JSON, Authorization_token, jsonObject);
        call.enqueue(new Callback<Location>() {
            public void onResponse(Call<Location> call, Response<Location> response) {
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                if (response.isSuccessful()) {
                    locationModel = response.body();
                    consumeDataLocation(locationModel);
                } else {
                    networkerror();
                }
            }

            public void onFailure(Call<Location> call, Throwable t) {
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                networkerror();
            }
        });
    }

    private void consumeDataLocation(Location locationModel) {
        if (locationModel != null) {
            if (locationModel.getData() != null) {
                Toast.makeText(getApplicationContext(), "succesfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //-----------------------------------------  redirect to attachement page --------------------------------------//
    private void redirectattachment() {
        Intent intent = new Intent(ChatScreen.this, AttachmentActivity.class);
        intent.putExtra(AppConst.CUSTOMER_UID, strCustomerId);
        intent.putExtra(AppConst.CUSTOMER_name, textViewName.getText().toString());
        intent.putExtra(AppConst.CUSTOMER_name, textViewName.getText().toString());
        intent.putExtra(AppConst.COUNTRY_NAME, chatSocketModel.getData().get(0).getCountry());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slideleft, R.anim.slide_right);
    }


    //----------------------------------------------- customerdetails api --------------------------------------------//

    private void getCustomerDetails() {
        progress_bar.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AppConst.UID, strCustomerId);
        jsonObject.addProperty(AppConst.BUSINESS_UID, businessuid);
        Call<CustmerDetailsFetch> call = API.spericorn().getCustomerDetailsNew(AppConst.APPLICATION_JSON, Authorization_token, jsonObject);
        call.enqueue(new Callback<CustmerDetailsFetch>() {
            public void onResponse(Call<CustmerDetailsFetch> call, Response<CustmerDetailsFetch> response) {
                /*dismiss progress bar*/
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                if (response.isSuccessful()) {
                    CustmerDetailsFetch customerDetails = response.body();
                    consumeData_details(customerDetails);
                } else {
                    networkerror();
                }
            }

            public void onFailure(Call<CustmerDetailsFetch> call, Throwable t) {
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                networkerror();
            }
        });
    }

    private void consumeData_details(CustmerDetailsFetch customerDetails) {
        if (customerDetails != null) {
            if (customerDetails.getStatus().equalsIgnoreCase("success")) {
                if (customerDetails.getData() != null) {
                    popup(customerDetails);
                }
            } else {
                SpericornUtils.showDialog(ChatScreen.this, customerDetails.getStatus());
            }
        }
    }

    private void popup(CustmerDetailsFetch customerDetails) {
        alertDialog = new AlertDialog.Builder(ChatScreen.this, R.style.AppBaseTheme);
        LayoutInflater layoutInflater = LayoutInflater.from(ChatScreen.this);
        final View view = layoutInflater.inflate(R.layout.layout_popup, null);
        alertDialog.setView(view);
        dialog_customer_detials = alertDialog.create();
        dialog_customer_detials.setCanceledOnTouchOutside(true);
        Window window = dialog_customer_detials.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog_customer_detials.show();
        dialog_customer_detials.setCancelable(true);
        dialog_customer_detials.setCanceledOnTouchOutside(true);
        final EditText etUserName = view.findViewById(R.id.et_Username);
        final EditText etLastName = view.findViewById(R.id.et_LastName);
        final EditText etEmail = view.findViewById(R.id.et_EmailId);
        final EditText etCountry = view.findViewById(R.id.et_Country);
        final EditText etPhoneNumber = view.findViewById(R.id.et_phoneNumber);
        final EditText etBrowser = view.findViewById(R.id.et_Browser);
        final EditText etDeviceId = view.findViewById(R.id.et_Deviceid);
        final EditText etIP = view.findViewById(R.id.et_Ip);
        final ImageView ivClose = view.findViewById(R.id.iv_close);
        final ScrollView scrollView = view.findViewById(R.id.srcollview);
        final LinearLayout layout_header = view.findViewById(R.id.layout_header);
        layout_bottom = view.findViewById(R.id.layout_bottom);
        layout_header.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpericornUtils.hideKeyboard(v, getApplicationContext());
            }
        });
        layout_bottom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpericornUtils.hideKeyboard(v, getApplicationContext());
            }
        });
        final TextView tvLogin = view.findViewById(R.id.tv_Update);
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etEmail.requestFocus();
                    scrollView.smoothScrollTo(0, etLastName.getTop());
                }
            }
        });
        etCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etCountry.requestFocus();
                    scrollView.smoothScrollTo(0, etEmail.getTop());
                }
            }
        });
        etPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etPhoneNumber.requestFocus();
                    scrollView.smoothScrollTo(0, etCountry.getTop());
                }
            }
        });
        etBrowser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etBrowser.requestFocus();
                    scrollView.smoothScrollTo(0, etPhoneNumber.getTop());
                }
            }
        });
        etDeviceId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etDeviceId.requestFocus();
                    scrollView.smoothScrollTo(0, etBrowser.getTop());
                }
            }
        });
        etIP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etIP.requestFocus();
                    scrollView.smoothScrollTo(0, etDeviceId.getTop());
                }
            }
        });
        if (customerDetails != null) {
            if (customerDetails.getData() != null) {
                if (customerDetails.getData().getFirstName() != null) {
                    etUserName.setText(customerDetails.getData().getFirstName());
                }
                if (customerDetails.getData().getLastName() != null) {
                    etLastName.setText(customerDetails.getData().getLastName());
                }
                if (customerDetails.getData().getEmail() != null) {
                    etEmail.setText(customerDetails.getData().getEmail());
                }
                if (customerDetails.getData().getCountry() != null) {
                    etCountry.setText(customerDetails.getData().getCountry());
                }
                if (customerDetails.getData().getPhone() != null) {
                    etPhoneNumber.setText(customerDetails.getData().getPhone());
                }
                if (customerDetails.getData().getBrowser() != null) {
                    etBrowser.setText(customerDetails.getData().getBrowser());
                    etBrowser.setEnabled(false);
                }
                if (customerDetails.getData().getIpaddress() != null) {
                    etIP.setText(customerDetails.getData().getIpaddress());
                    etIP.setEnabled(false);
                }
            }
        }
        tvLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpericornUtils.hideKeyboard(v, getApplicationContext());
                if (validation(etUserName, etEmail, etPhoneNumber)) {
                    customerdetailsUpdat(etUserName.getText().toString().trim(), etLastName.getText().toString().trim(), etEmail.getText().toString().trim(), etPhoneNumber.getText().toString().trim(), etCountry.getText().toString().trim());
                }
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SpericornUtils.hideKeyboard(v, getApplicationContext());
                dialog_customer_detials.dismiss();
            }
        });
    }

    //---------------------------------------------  Customer Details Update API -------------------------------------------------//
    private void customerdetailsUpdat(final String username, final String lastname, String email, String phonenumber, String country) {
        progress_bar.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AppConst.UID, strCustomerId);
        jsonObject.addProperty(AppConst.FIRSTNAME, username);
        jsonObject.addProperty(AppConst.LASTNAME, lastname);
        jsonObject.addProperty(AppConst.PHONE, phonenumber);
        jsonObject.addProperty(AppConst.EMAIL, email);
        jsonObject.addProperty(AppConst.COUNTRY, country);
        jsonObject.addProperty(AppConst.BUSINESS_UID, businessuid);
        Call<CustmerDetailsFetch> call = API.spericorn().customerUpdate(AppConst.APPLICATION_JSON, Authorization_token, jsonObject);
        call.enqueue(new Callback<CustmerDetailsFetch>() {
            public void onResponse(Call<CustmerDetailsFetch> call, Response<CustmerDetailsFetch> response) {
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                if (response.isSuccessful()) {
                    CustmerDetailsFetch customerDetails = response.body();
                    consumeData_details_update(customerDetails, username, lastname);
                } else {
                    SpericornUtils.showDialog(ChatScreen.this, getString(R.string.errormessage));
                }
            }

            public void onFailure(Call<CustmerDetailsFetch> call, Throwable t) {
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                SpericornUtils.showDialog(ChatScreen.this, getString(R.string.errormessage));
            }
        });
    }

    private void consumeData_details_update(CustmerDetailsFetch customerDetails, String firstname, String lastName) {
        if (customerDetails != null) {
            if (customerDetails.getStatus().equalsIgnoreCase("success")) {
                if (firstname != null && !firstname.equalsIgnoreCase("")) {
                    strName = firstname;
                    if (lastName != null && !lastName.equalsIgnoreCase("")) {
                        strName = strName + " " + lastName;
                    }
                }
                textViewName.setText(strName);
                NameSet(strName, true);
                dialog_customer_detials.dismiss();
            }
        }
    }
    //-------------------------------------- Socket Io connection -----------------------------------------//

    private void SocketConnectionEstablished() {
        ismesge_agent_new = false;
        mSocket.on("new message", new Emitter.Listener() {
            public void call(final java.lang.Object... args) {
                ChatScreen.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            JSONObject data = new JSONObject(String.valueOf(args[0]));
                            String jsonString = data.toString();
                            Gson gson = new Gson();
                            if (jsonString != null) {
                                socketModel = gson.fromJson(jsonString, SocketNewModel.class);
                                datumHistory = new DatumHistoryNew();
                                datumHistory.setCusFirstname(socketModel.getMessage().getCusFirstname());
                                datumHistory.setSocketId(socketModel.getMessage().getSocketId());
                                datumHistory.setRoomId(String.valueOf(socketModel.getMessage().getRoomId()));
                                datumHistory.setAgentUid(String.valueOf(socketModel.getMessage().getAgentUid()));
                                datumHistory.setCusUid(socketModel.getMessage().getCusUid());
                                datumHistory.setType(socketModel.getMessage().getType());
                                datumHistory.setChatMessage(socketModel.getMessage().getChatMessage());
                                datumHistory.setTime(socketModel.getMessage().getTime());
                                datumHistory.setFileLink(socketModel.getMessage().getFileLink());
                                datumHistory.setFile_ext(socketModel.getMessage().getFile_ext());
                                datumHistory.setFile_type(socketModel.getMessage().getFile_type());
                                datumHistory.setBusiness_uid(socketModel.getMessage().getBusiness_uid());
                                datumHistory.setAgentName(socketModel.getMessage().getAgentName());
                                datumHistory.setAgentUid(socketModel.getMessage().getAgentUid());
                                strDate = socketModel.getMessage().getDate();
                                if (socketModel.getMessage().getDate() != null) {
                                    if (strDate != null) {
                                        DateFormat originalFormat = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
                                        DateFormat targetFormat = new SimpleDateFormat("yyyy-dd-mm", Locale.ENGLISH);
                                        Date date = null;
                                        try {
                                            date = originalFormat.parse(strDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        if (date != null) {
                                            strFormattedDate = targetFormat.format(date);
                                        }
                                        if (strFormattedDate != null) {
                                            datumHistory.setDate(strFormattedDate);
                                        }
                                    }
                                }
                                if (agentFname.equalsIgnoreCase(datumHistory.getAgentUid())) {
                                    if (ismesge_agent == false) {
                                        ismesge_agent = false;
                                    } else {
                                        ismesge_agent = true;
                                    }
                                }
                                if (strCustomerId != null) {
                                    if (strCustomerId.equalsIgnoreCase(socketModel.getMessage().getCusUid())) {
                                        if (chatSocketModel.getData() != null) {
                                            if (businessuid != null) {
                                                visibleRecyclerview();
                                                if (socketModel.getMessage().getBusiness_uid().equalsIgnoreCase(businessuid)) {
                                                    if (datumHistory.getType().equalsIgnoreCase("customer")) {
                                                        agentFname = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
                                                        ismesge_agent = false;
                                                        cusid = datumHistory.getCusUid();

                                                        if (!ismesge_customer) {
                                                            datumHistory.setIsfirstmessage(true);
                                                            datumHistory.setNextMessage(false);
                                                            ismesge_customer = true;
                                                        } else {
                                                            datumHistory.setNextMessage(true);
                                                        }
                                                    } else {
                                                        ismesge_customer = false;
                                                        if (agentFname.equalsIgnoreCase(datumHistory.getAgentUid())) {
                                                            if (!ismesge_agent) {
                                                                datumHistory.setIsfirstmessage(true);
                                                                datumHistory.setNextMessage(false);
                                                                ismesge_agent = true;
                                                                agentFname = datumHistory.getAgentUid();
                                                            } else {
                                                                datumHistory.setNextMessage(true);
                                                                agentFname = datumHistory.getAgentUid();
                                                            }
                                                        } else {
                                                            datumHistory.setIsfirstmessage(true);
                                                            datumHistory.setNextMessage(false);
                                                            ismesge_agent = false;
                                                            agentFname = datumHistory.getAgentUid();
                                                            ismesge_agent = true;
                                                        }

                                                    }
                                                    chatSocketModel.getData().add(chatSocketModel.getData().size(), datumHistory);


                                                    //------------------------------------------------------- date wise filtering -------------------------------------------------------------//

                                                    if (chatSocketModel != null) {
                                                        if (chatSocketModel.getData() != null && chatSocketModel.getData().size() > 0) {
                                                            oldateViaSocket = chatSocketModel.getData().get(chatSocketModel.getData().size() - 2).getDate();
                                                        }
                                                    }
                                                    if (oldateViaSocket != null) {
                                                        if (!oldateViaSocket.equalsIgnoreCase(datumHistory.getDate())) {
                                                            if (chatSocketModel.getData() != null && chatSocketModel.getData().size() > 0) {
                                                                chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setToday(true);
                                                                oldateViaSocket = chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).getDate();
                                                            }
                                                        }
                                                    }


                                                    //--------------------------------------------------------- message grouping----------------------------------------------------------------------------//
                                                    if (chatSocketModel != null) {
                                                        if (chatSocketModel.getData() != null && chatSocketModel.getData().size() > 0) {
                                                            for (int i = 0; i < chatSocketModel.getData().size(); i++) {
                                                                if (chatSocketModel.getData().get(i).isIsfirstmessage()) {
                                                                    if (i != 0) {
                                                                        if (chatSocketModel.getData().get(i - 1).isNextMessage()) {
                                                                            chatSocketModel.getData().get(i - 1).setLsttime(true);
                                                                        } else if (chatSocketModel.getData().get(i - 1).isIsfirstmessage()) {
                                                                            chatSocketModel.getData().get(i - 1).setLsttime(true);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            chatMessageAdapter.notifyDataSetChanged();
                                                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                                        }
                                                    }
                                                    seen(chatSocketModel);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }


    //---------------------------------------------- typing not typing socket io connection-----------------------------------------//

    private void typingNotTypingMode() {
        mSocket.on("typing", new Emitter.Listener() {
            public void call(final java.lang.Object... args) {
                ChatScreen.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            JSONObject data = new JSONObject(String.valueOf(args[0]));
                            String jsonString = data.toString();
                            Gson gson = new Gson();
                            if (jsonString != null) {
                                socketModelTyping = gson.fromJson(jsonString, SocketNewModel.class);
                                // currentChatListModel
                                if (socketModelTyping != null) {
                                    if (socketModelTyping.getMessage() != null) {
                                        if (socketModelTyping.getMessage().getCusUid() != null) {
                                            if (chatSocketModel != null) {
                                                if (chatSocketModel != null) {
                                                    if (chatSocketModel.getData() != null && chatSocketModel.getData().size() > 0) {
                                                        if (socketModelTyping.getMessage().getCusUid().equalsIgnoreCase(chatSocketModel.getData().get(0).getCusUid())) {
                                                            if (!strfrom.equalsIgnoreCase(AppConst.COMPLETE)) {
                                                                txtOnline.setText(getString(R.string.typing));
                                                            } else {
                                                                txtOnline.setText(getString(R.string.offline));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        mSocket.on(AppConst.STOPTYPING, new Emitter.Listener() {
            public void call(final java.lang.Object... args) {
                ChatScreen.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            JSONObject data = new JSONObject(String.valueOf(args[0]));
                            String jsonString = data.toString();
                            if (jsonString != null) {
                                if (!strfrom.equalsIgnoreCase(AppConst.COMPLETE)) {
                                    txtOnline.setText(getString(R.string.online));
                                } else {
                                    txtOnline.setText(getString(R.string.offline));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    //----------------------------------------  Chatscreen listview adapter ---------------------------------------//
    private void setAdapter(SocketNewHistoryModel chatSocketModel) {
        if (chatSocketModel != null) {
            if (chatSocketModel.getData() != null && chatSocketModel.getData().size() > 0) {
                agentFname = chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).getAgentUid();
                for (int i = 0; i < chatSocketModel.getData().size(); i++) {
                    if (chatSocketModel.getData().get(i).getType().equalsIgnoreCase("customer")) {
                        agentFname = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
                        agentFname_chathistory = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
                        ismesge_agent = false;
                        ismesge_agent_new = false;
                        cusid = chatSocketModel.getData().get(i).getCusUid();
                        if (!ismesge_customer) {
                            chatSocketModel.getData().get(i).setIsfirstmessage(true);
                            chatSocketModel.getData().get(i).setNextMessage(false);
                            ismesge_customer = true;

                        } else {
                            chatSocketModel.getData().get(i).setNextMessage(true);

                        }
                    } else {
                        ismesge_customer = false;
                        if (chatSocketModel.getData().get(i).getAgentUid().equalsIgnoreCase(agentFname_chathistory)) {
                            ismesge_agent_new = false;
                            if (!ismesge_agent) {
                                chatSocketModel.getData().get(i).setIsfirstmessage(true);
                                chatSocketModel.getData().get(i).setNextMessage(false);
                                ismesge_agent = true;
                                ismesge_agent_new = false;
                                agentFname_chathistory = chatSocketModel.getData().get(i).getAgentUid();
                                agentFname = chatSocketModel.getData().get(i).getAgentUid();
                            } else {
                                chatSocketModel.getData().get(i).setNextMessage(true);
                                agentFname = chatSocketModel.getData().get(i).getAgentUid();

                            }
                        } else {
                            chatSocketModel.getData().get(i - 1).setLsttime(true);
                            chatSocketModel.getData().get(i).setIsfirstmessage(true);
                            chatSocketModel.getData().get(i).setNextMessage(false);
                            ismesge_agent_new = true;
                            ismesge_agent = true;
                            agentFname_chathistory = chatSocketModel.getData().get(i).getAgentUid();
                            agentFname = chatSocketModel.getData().get(i).getAgentUid();
                        }
                    }
                }

            }
        }
//-------------------------------- date  ----------------------------------------------
        if (chatSocketModel != null) {
            if (chatSocketModel.getData() != null && chatSocketModel.getData().size() > 0) {
                currentdate = chatSocketModel.getData().get(0).getDate();
                for (int i = 0; i < chatSocketModel.getData().size(); i++) {
                    if (i == 0) {
                        chatSocketModel.getData().get(0).setToday(true);
                    }
                    if (!currentdate.equalsIgnoreCase(chatSocketModel.getData().get(i).getDate())) {
                        if (!booldate) {
                            chatSocketModel.getData().get(i).setToday(true);
                            currentdate = chatSocketModel.getData().get(i).getDate();
                            //  booldate=true;

                        } else {
                            chatSocketModel.getData().get(i).setToday(false);
                        }
                    }
                }
            }
        }

        //----------------------------------- last chat time ---------------------------//
        if (chatSocketModel != null) {
            if (chatSocketModel.getData() != null) {
                for (int i = 0; i < chatSocketModel.getData().size(); i++) {
                    if (chatSocketModel.getData().get(i).isIsfirstmessage()) {
                        if (i != 0) {
                            if (chatSocketModel.getData().get(i - 1).isNextMessage()) {
                                chatSocketModel.getData().get(i - 1).setLsttime(true);
                            } else if (chatSocketModel.getData().get(i - 1).isIsfirstmessage()) {
                                chatSocketModel.getData().get(i - 1).setLsttime(true);
                            }
                        }
                    }
                }
            }
        }
        //----------------------------------- last chat time ---------------------------//
        if (agentprofileName == null || agentprofileName.equalsIgnoreCase("") || agentprofileName.equalsIgnoreCase(" ")) {
            agentprofileName = agentname;
        }
        chatMessageAdapter = new ChatMessageAdapter(this, chatSocketModel, firstLetter, agentprofileName, new RecyclerViewAdapterListener() {
            public void onCallBack(View v, int position, Object item) {
                datumHistoryNew = (List<DatumHistoryNew>) item;
                if (datumHistoryNew != null) {
                    if (datumHistoryNew.get(position) != null) {
                        if (datumHistoryNew.get(position).getFileLink() != null) {
                            Intent intent = new Intent(getApplicationContext(), VideoViewActivity.class);
                            intent.putExtra(AppConst.ATTACHMENT_FILE, datumHistoryNew.get(position).getFileLink());
                            startActivity(intent);
                        }
                    }
                }
            }

            public void emptyView(boolean isEmpty) {
            }
        });
        recyclerView.setAdapter(chatMessageAdapter);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    //-------------------------------------------  Message senting function ----------------------------------------------//
    private void messageSent(String text, String agentuid) {
        if (!text.equalsIgnoreCase("")) {
            DateFormat df = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            String date = df.format(Calendar.getInstance().getTime());
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dfs = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            socketdate = dfs.format(c.getTime());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(AppConst.CUSTOMER_FIRST_NAME, chatSocketModel.getData().get(0).getCusFirstname());
                jsonObject.put(AppConst.CUSTOMER_LAST_NAME, chatSocketModel.getData().get(0).getCusLastname());
                jsonObject.put(AppConst.CUSTOMER_UID, strCustomerId);
                jsonObject.put(AppConst.AGENT_NAME, agentname);
                jsonObject.put(AppConst.AGENT_UID, agentUid);
                jsonObject.put(AppConst.ONLINE_OFFLINE_STATUS, "");
                jsonObject.put(AppConst.TYPE, "Agent");
                jsonObject.put(AppConst.SOCKETID, chatSocketModel.getData().get(0).getSocketId());
                jsonObject.put(AppConst.ROOM_ID, chatSocketModel.getData().get(0).getRoomId());
                jsonObject.put(AppConst.COUNTRY, chatSocketModel.getData().get(0).getCountry());
                jsonObject.put(AppConst.CHAT_MESSAGE, text);
                jsonObject.put(AppConst.LAST_MESSAGE, text);
                jsonObject.put(AppConst.UNREAD_MESSAGE_COUNT, "");
                jsonObject.put(AppConst.DATE, socketdate);
                jsonObject.put(AppConst.TIME, date);
                jsonObject.put(AppConst.FILE_LINK, "");
                jsonObject.put(AppConst.BUSINESS_UID, businessuid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("new message", jsonObject);
            JSONObject jsonObject_message = new JSONObject();
            try {
                jsonObject_message.put(AppConst.CHAT_MESSAGE, text);
                jsonObject_message.put(AppConst.TYPE, "Agent");
                jsonObject_message.put(AppConst.TIME, date);
                jsonObject_message.put(AppConst.AGENT_NAME, agentname);
                jsonObject_message.put(AppConst.DATE, formatdate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<String> isPicked = new ArrayList<>();
            isPicked.add(socketbusinessid);
            if (chatSocketModel != null) {
                if (chatSocketModel.getData() != null) {
                    chatSocketModel.getData().get(0).setIsPicked(isPicked);
                }
            }
            String jsonString = jsonObject_message.toString();
            Gson gson = new Gson();
            datumHistory = gson.fromJson(jsonString, DatumHistoryNew.class);


            //--------------------------- date wise filter-----------------------------------//

            chatSocketModel.getData().add(chatSocketModel.getData().size(), datumHistory);
            oldatae = chatSocketModel.getData().get(chatSocketModel.getData().size() - 2).getDate();
            isagent = true;
            if (oldatae != null) {
                if (!oldatae.equalsIgnoreCase(formatdate)) {
                    chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setToday(true);
                    oldatae = chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).getDate();
                }
            }
            ismesge_customer = false;
            //-------------------------------- messge grouping----------------------------------------------------------------//
            if (chatSocketModel.getData().get(chatSocketModel.getData().size() - 2).getType().equalsIgnoreCase("customer")) {
                if (!ismesge_agent) {
                    chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setIsfirstmessage(true);
                    chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setNextMessage(false);
                    ismesge_agent = true;
                    agentFname = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
                } else {
                    chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setNextMessage(true);
                    agentFname = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
                }
            } else {
                if (agentFname.equalsIgnoreCase(agentuid)) {
                    if (!ismesge_agent) {
                        chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setIsfirstmessage(true);
                        chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setNextMessage(false);
                        ismesge_agent = true;
                        agentFname = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
                    } else {
                        chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setNextMessage(true);
                        agentFname = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
                    }
                } else {
                    chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setIsfirstmessage(true);
                    chatSocketModel.getData().get(chatSocketModel.getData().size() - 1).setNextMessage(false);
                    ismesge_agent = true;
                    agentFname = SpericornUtils.getDefaults(AppConst.CUSTOMER_AGENTID, getApplicationContext());
                }
            }
            //-------------------------------- messge grouping----------------------------------------------------------------//
            for (int i = 0; i < chatSocketModel.getData().size(); i++) {
                if (chatSocketModel.getData().get(i).isIsfirstmessage()) {
                    if (i != 0) {
                        if (chatSocketModel.getData().get(i - 1).isNextMessage()) {
                            chatSocketModel.getData().get(i - 1).setLsttime(true);

                        } else if (chatSocketModel.getData().get(i - 1).isIsfirstmessage()) {
                            chatSocketModel.getData().get(i - 1).setLsttime(true);
                        }
                    }
                } else if (chatSocketModel.getData().get(i).isNextMessage()) {
                    if (chatSocketModel.getData().get(i - 1).isNextMessage()) {
                        chatSocketModel.getData().get(i - 1).setLsttime(false);
                    }
                }
            }
            layout_no_history_found.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (chatMessageAdapter != null) {
                chatMessageAdapter.notifyDataSetChanged();
                recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

            } else {
                setAdapter(chatSocketModel);
            }
            etTyping.setText("");
        }
    }

    public void onBackPressed() {
        if (dialog_customer_detials != null) {
            if (dialog_customer_detials.isShowing()) {
                dialog_customer_detials.dismiss();
            }
        }
        if (strfrom != null) {
            if (strfrom.equalsIgnoreCase("from") || strfrom.equalsIgnoreCase("complete")) {
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    //--------------------------------------------------- chatHistory api -------------------------------------------//
    private void getChatHistory(String strCustomerId) {
        progress_bar.show();
        visibleRecyclerview();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cus_uid", strCustomerId);
        jsonObject.addProperty("business_uid", businessuid);
        jsonObject.addProperty("agent_uid", agentUid);
        Call<SocketNewHistoryModel> call = API.spericorn().chathistory(AppConst.APPLICATION_JSON, Authorization_token, jsonObject);
        call.enqueue(new Callback<SocketNewHistoryModel>() {
            public void onResponse(Call<SocketNewHistoryModel> call, Response<SocketNewHistoryModel> response) {
                /*dismiss progress bar*/
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                if (response.isSuccessful()) {
                    chatSocketModel = response.body();
                    consumeData(chatSocketModel);
                } else {
                    networkerror();
                }
            }

            public void onFailure(Call<SocketNewHistoryModel> call, Throwable t) {
                /*dismiss progress bar*/
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                networkerror();
            }
        });
    }

    private void consumeData(SocketNewHistoryModel currentChatListModel) {
        this.currntlistmodel_seen = currentChatListModel;
        if (currentChatListModel != null) {
            if (currentChatListModel.getStatus().equalsIgnoreCase("Success")) {
                if (currentChatListModel.getData() != null && currentChatListModel.getData().size() > 0) {
                    setAdapter(currentChatListModel);
                    seen(currentChatListModel);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    layout_no_history_found.setVisibility(View.VISIBLE);
                }
            }
            if (currentChatListModel.getData() != null && currentChatListModel.getData().size() > 0) {
                if (currentChatListModel.getData().get(currentChatListModel.getData().size() - 1).getIsPicked() != null) {
                    if (currentChatListModel.getData().get(currentChatListModel.getData().size() - 1).getIsPicked().size() == 0) {
                        ivChat.setImageResource(R.drawable.ic_sent);
                    } else if (currentChatListModel.getData().get(currentChatListModel.getData().size() - 1).getIsPicked().size() > 0) {
                        if (currentChatListModel.getData().get(currentChatListModel.getData().size() - 1).getIsPicked().contains(socketbusinessid)) {
                            ivChat.setImageResource(R.drawable.ic_sent);
                        } else if (!currentChatListModel.getData().get(currentChatListModel.getData().size() - 1).getIsPicked().contains(socketbusinessid)) {
                            ivChat.setImageResource(R.drawable.ic_join_chat);
                            etTyping.setVisibility(View.VISIBLE);
                            isstate = false;
                        }
                    }
                }
            }
            newdata(currentChatListModel);
        }
    }

    private void newdata(SocketNewHistoryModel currentChatListModel) {
        if (currentChatListModel != null) {
            agentname = SpericornUtils.getDefaults(AppConst.AGENT_FIRSTnAME, ChatScreen.this);
            if (currentChatListModel.getData() != null && currentChatListModel.getData().size() > 0) {
                if (currentChatListModel.getData().get(0).getCusFirstname() != null) {
                    textViewName.setText(currentChatListModel.getData().get(0).getCusFirstname());
                }
                if (currentChatListModel.getData().get(0).getCusFirstname() != null && !currentChatListModel.getData().get(0).getCusFirstname().equalsIgnoreCase("")) {
                    strCustomername = currentChatListModel.getData().get(0).getCusFirstname();
                    if (currentChatListModel.getData().get(0).getCusLastname() != null && !currentChatListModel.getData().get(0).getCusLastname().equalsIgnoreCase("")) {

                        if (!strCustomername.contains("user")) {
                            strCustomername = strCustomername + " " + currentChatListModel.getData().get(0).getCusLastname();
                        } else {
                            strCustomername = currentChatListModel.getData().get(0).getCusFirstname();
                        }
                    }
                }
                if (currentChatListModel.getData().get(0).getAgentName() != null && !currentChatListModel.getData().get(0).getAgentName().equalsIgnoreCase("") && !currentChatListModel.getData().get(0).getAgentName().equalsIgnoreCase(" ")) {
                    agentprofileName = currentChatListModel.getData().get(0).getAgentName();
                }
                if (currentChatListModel != null) {
                    if (strfrom != null) {
                        if (!strfrom.equalsIgnoreCase("complete")) {
                            if (currentChatListModel.getData().get(0).getOnlineOfflineStatus() != null && !currentChatListModel.getData().get(0).getOnlineOfflineStatus().equalsIgnoreCase("")) {
                                if (currentChatListModel.getData().get(0).getOnlineOfflineStatus().equalsIgnoreCase("online")) {
                                    txtOnline.setText(getString(R.string.online));
                                } else if (currentChatListModel.getData().get(0).getOnlineOfflineStatus().equalsIgnoreCase("offline")) {
                                    txtOnline.setText(getString(R.string.offline));
                                } else {
                                    txtOnline.setText(getString(R.string.online));
                                }
                            }
                            if (currentChatListModel != null) {
                                if (currentChatListModel.getPlan_status().equalsIgnoreCase("Active")) {
                                    layoutFullType.setVisibility(View.VISIBLE);
                                } else {
                                    layoutFullType.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            txtOnline.setText(getString(R.string.offline));
                            layoutFullType.setVisibility(View.GONE);

                        }
                    }
                }
            }
        }
    }


    //-----------------------------------------------  Socket io seen function ------------------------------------//

    private void seen(SocketNewHistoryModel chatSocketModel) {
        if (isseen) {
            if (chatSocketModel.getData() != null && chatSocketModel.getData().size() > 0) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(AppConst.CUSTOMER_FIRST_NAME, chatSocketModel.getData().get(0).getCusFirstname());
                    jsonObject.put(AppConst.CUSTOMER_LAST_NAME, "");
                    jsonObject.put(AppConst.CUSTOMER_UID, strCustomerId);
                    jsonObject.put(AppConst.AGENT_NAME, agentname);
                    jsonObject.put(AppConst.AGENT_UID, agentUid);
                    jsonObject.put(AppConst.ONLINE_OFFLINE_STATUS, "picked");
                    jsonObject.put(AppConst.TYPE, "Agent");
                    jsonObject.put(AppConst.SOCKETID, chatSocketModel.getData().get(0).getSocketId());
                    jsonObject.put(AppConst.ROOM_ID, chatSocketModel.getData().get(0).getRoomId());
                    jsonObject.put(AppConst.COUNTRY, "");
                    jsonObject.put(AppConst.CHAT_MESSAGE, "");
                    jsonObject.put(AppConst.LAST_MESSAGE, "");
                    jsonObject.put(AppConst.UNREAD_MESSAGE_COUNT, "");
                    jsonObject.put(AppConst.DATE, chatSocketModel.getData().get(0).getDate());
                    jsonObject.put(AppConst.TIME, chatSocketModel.getData().get(0).getTime());
                    jsonObject.put(AppConst.FILE_LINK, chatSocketModel.getData().get(0).getFileLink());
                    jsonObject.put(AppConst.BUSINESS_UID, businessuid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit(AppConst.MESSAGE_SEEN, jsonObject);/**/
            }
        }
    }


    //-------------------------------------------------- leave chat api call ------------------------------------//
    private void leaveChat() {
        visibleRecyclerview();
        progress_bar.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AppConst.CUSTOMER_AGENTID, agentUid);
        jsonObject.addProperty(AppConst.CUSTOMER_ID, strCustomerId);
        jsonObject.addProperty(AppConst.BUSINESS_UID, businessuid);
        Call<LeaveChatModel> call = API.spericorn().leaveChat(AppConst.APPLICATION_JSON, Authorization_token, jsonObject);
        call.enqueue(new Callback<LeaveChatModel>() {
            public void onResponse(Call<LeaveChatModel> call, Response<LeaveChatModel> response) {
                /*dismiss progress bar*/
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                if (response.isSuccessful()) {
                    LeaveChatModel chatSocketModel = response.body();
                    consumeDataAttachment(chatSocketModel);
                } else {
                    SpericornUtils.showDialog(ChatScreen.this, getString(R.string.errormessage));
                }
            }

            public void onFailure(Call<LeaveChatModel> call, Throwable t) {
                /*dismiss progress bar*/
                if (progress_bar.isShowing()) {
                    progress_bar.dismiss();
                }
                SpericornUtils.showDialog(ChatScreen.this, getString(R.string.errormessage));


            }

        });
    }

    private void consumeDataAttachment(LeaveChatModel attachmentModel) {
        if (attachmentModel != null) {
            if (attachmentModel.getStatus() != null && !attachmentModel.getStatus().equalsIgnoreCase("")) {
                if (attachmentModel.getStatus().equalsIgnoreCase("success")) {
                    //finish();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    //---------------------------------------------   Typing socket io function----------------------------------//
    private void typing() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppConst.CUSTOMER_FIRST_NAME, chatSocketModel.getData().get(0).getCusFirstname());
            jsonObject.put(AppConst.CUSTOMER_LAST_NAME, chatSocketModel.getData().get(0).getCusLastname());
            jsonObject.put(AppConst.CUSTOMER_UID, strCustomerId);
            jsonObject.put(AppConst.AGENT_NAME, agentname);
            jsonObject.put(AppConst.AGENT_UID, agentUid);
            jsonObject.put(AppConst.ONLINE_OFFLINE_STATUS, "picked");
            jsonObject.put(AppConst.TYPE, "Agent");
            jsonObject.put(AppConst.SOCKETID, chatSocketModel.getData().get(0).getSocketId());
            jsonObject.put(AppConst.ROOM_ID, chatSocketModel.getData().get(0).getRoomId());
            jsonObject.put(AppConst.COUNTRY, "");
            jsonObject.put(AppConst.CHAT_MESSAGE, "");
            jsonObject.put(AppConst.LAST_MESSAGE, "");
            jsonObject.put(AppConst.UNREAD_MESSAGE_COUNT, "");
            jsonObject.put(AppConst.DATE, chatSocketModel.getData().get(0).getDate());
            jsonObject.put(AppConst.TIME, chatSocketModel.getData().get(0).getTime());
            jsonObject.put(AppConst.FILE_LINK, chatSocketModel.getData().get(0).getFileLink());
            jsonObject.put(AppConst.BUSINESS_UID, businessuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit(AppConst.TYPING, jsonObject);
        istype = false;

    }
    //----------------------------------------------------- Not typing socket io ------------------------------------//

    private void nottyping() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppConst.CUSTOMER_FIRST_NAME, chatSocketModel.getData().get(0).getCusFirstname());
            jsonObject.put(AppConst.CUSTOMER_LAST_NAME, chatSocketModel.getData().get(0).getCusLastname());
            jsonObject.put(AppConst.CUSTOMER_UID, strCustomerId);
            jsonObject.put(AppConst.AGENT_NAME, agentname);
            jsonObject.put(AppConst.AGENT_UID, agentUid);
            jsonObject.put(AppConst.ONLINE_OFFLINE_STATUS, "picked");
            jsonObject.put(AppConst.TYPE, "Agent");
            jsonObject.put(AppConst.SOCKETID, chatSocketModel.getData().get(0).getSocketId());
            jsonObject.put(AppConst.ROOM_ID, chatSocketModel.getData().get(0).getRoomId());
            jsonObject.put(AppConst.COUNTRY, "");
            jsonObject.put(AppConst.CHAT_MESSAGE, "");
            jsonObject.put(AppConst.LAST_MESSAGE, "");
            jsonObject.put(AppConst.UNREAD_MESSAGE_COUNT, "");
            jsonObject.put(AppConst.DATE, chatSocketModel.getData().get(0).getDate());
            jsonObject.put(AppConst.TIME, chatSocketModel.getData().get(0).getTime());
            jsonObject.put(AppConst.FILE_LINK, chatSocketModel.getData().get(0).getFileLink());
            jsonObject.put(AppConst.BUSINESS_UID, businessuid);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("stop typing", jsonObject);
    }

    public void onResume() {
        isbackground = true;
        super.onResume();
        isseen = true;
    }

    protected void onPause() {
        isbackground = false;
        super.onPause();
        isseen = false;

    }

    //--------------------------------------  validation -----------------------------------//

    private boolean validation(EditText etUserName, EditText etEmail, EditText etPhoneNumber) {
        boolean isValid = true;
        if (etUserName.getText().toString().length() == 0) {
            etUserName.setError(getString(R.string.firstname_required));
            etUserName.requestFocus();
            isValid = false;
        }
        if (etEmail.getText().toString() != null && !etEmail.getText().toString().equalsIgnoreCase("")) {
            if (!etEmail.getText().toString().matches(AppConst.EMAIL_PATTERN)) {
                etEmail.setError(getString(R.string.invalid_email));
                etEmail.requestFocus();
                isValid = false;
            }
        }
        if (etPhoneNumber.getText().toString() != null && !etPhoneNumber.getText().toString().equalsIgnoreCase("")) {
            if (!etPhoneNumber.getText().toString().matches(AppConst.MobilePattern)) {
                etPhoneNumber.setError(getString(R.string.invalid_phone_number));
                etPhoneNumber.requestFocus();
                isValid = false;
            }
        }
        return isValid;
    }

    public void OnKeyBoardAppear(Boolean appeared, int keyboardHeight) {
    }

    public void cancel() {
    }

    public void dismiss() {
    }

    private void networkerror() {
        recyclerView.setVisibility(View.GONE);
        layout_no_internet.setVisibility(View.GONE);
        layout_no_history_found.setVisibility(View.VISIBLE);
        iv_DataImage.setImageResource(R.drawable.ic_network_error);
        tvMainMessage.setText(getString(R.string.network_error_msg));
        tvSubMessage.setText(getString(R.string.something_wrong));
        tvLastMessage.setText(getString(R.string.error_sub));
    }

    private void visibleRecyclerview() {
        recyclerView.setVisibility(View.VISIBLE);
        layout_no_history_found.setVisibility(View.GONE);
        layout_no_internet.setVisibility(View.GONE);
    }


    private void noInternet() {
        if (progress_bar != null) {
            if (progress_bar.isShowing()) {
                progress_bar.dismiss();
            }
        }
        layout_no_internet.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

}
