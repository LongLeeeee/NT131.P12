package com.example.project_app;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_app.Mapdata.SharedViewModel;
import com.example.project_app.data.userModel.ApiService;
import com.example.project_app.data.userModel.NormalUser;
import com.example.project_app.data.userModel.UpdatePreferencesRequest;
import com.example.project_app.data.userModel.UserSignInWithFacebook;
import com.example.project_app.data.userModel.UserSignInWithGoogle;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private String token = MainActivity.token;

    private RelativeLayout sign_out, change_password, data_sharing, profile, detection_sensitivity, alert_references;
    private Button btn_close, btn_sign_out;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    RelativeLayout label4_des, label3_des;
    Switch active_sharing, active_alert;

    static Boolean isSharing;
    static Boolean isAlert;
    private NormalUser userdata;
    private UserSignInWithGoogle userSignInWithGoogle;
    private UserSignInWithFacebook userSignInWithFacebook;
    String user_id;
    private SharedViewModel sharedViewModel;


//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Bundle bundle = getArguments();
//
//        if (bundle != null) {
//            // Nhận dữ liệu từ Bundle
//            userdata = (NormalUser) bundle.getSerializable("user_data");
//
//            if (userdata != null) {
//                // Gán dữ liệu từ userdata vào các biến hoặc xử lý logic
//                isSharing = userdata.getIsSharing();
//                isAlert = userdata.getIsAlert();
//
//                // Debug log để kiểm tra giá trị nhận được
//                Log.d("SettingsFragment", "isSharing: " + isSharing + ", isAlert: " + isAlert);
//
//            } else {
//                // Log lỗi và thông báo nếu userdata null
//                Log.e("SettingsFragment", "userdata is null");
//                Toast.makeText(getActivity(), "Dữ liệu người dùng không được tìm thấy", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            // Log lỗi nếu Bundle null
//            Log.e("SettingsFragment", "Bundle is null");
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sign_out = view.findViewById(R.id.sign_out);
        change_password = view.findViewById(R.id.change_password);
        data_sharing = view.findViewById(R.id.data_sharing);
        active_sharing = view.findViewById(R.id.active_sharing);
        active_alert = view.findViewById(R.id.active_alert);
        profile = view.findViewById(R.id.profile);
        detection_sensitivity = view.findViewById(R.id.detection_sensitivity);
        label3_des = view.findViewById(R.id.label3_describe);
        label4_des = view.findViewById(R.id.label4_describe);
        alert_references = view.findViewById(R.id.alert_preferences);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        if (MainActivity.type.equals("normal")){
            userdata = MainActivity.userdata;
            isSharing = userdata.getIsSharing();
            isAlert = userdata.getIsAlert();
            user_id = userdata.get_id();
            active_sharing.setChecked(userdata.getIsSharing());
            active_alert.setChecked(userdata.getIsAlert());
        }
        else if (MainActivity.type.equals("google")){
            userSignInWithGoogle = MainActivity.userSignInWithGoogle;
            isSharing = userSignInWithGoogle.isSharing();
            isAlert = userSignInWithGoogle.isAlert();
            user_id = userSignInWithGoogle.get_id();
            active_sharing.setChecked(userSignInWithGoogle.isSharing());
            active_alert.setChecked(userSignInWithGoogle.isAlert());
        }
        else if (MainActivity.type.equals("facebook")){
            userSignInWithFacebook = MainActivity.userSignInWithFacebook;
            isSharing = userSignInWithFacebook.isSharing();
            isAlert = userSignInWithFacebook.isAlert();
            user_id = userSignInWithFacebook.get_id();
            active_sharing.setChecked(userSignInWithFacebook.isSharing());
            active_alert.setChecked(userSignInWithFacebook.isAlert());
        }

        if (!MainActivity.type.equals("normal")){
            change_password.setVisibility(View.GONE);
        }

        // Set listener for data sharing switch
        active_sharing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (MainActivity.type.equals("normal")){
                    userdata.setIsSharing(isChecked);
                    sharedViewModel.setIsSharing(isChecked);
                    isSharing = userdata.getIsSharing();
                    updateSharingPreference(user_id,isSharing, isAlert, "normal");
                }
                else if (MainActivity.type.equals("google")){
                    userSignInWithGoogle.setSharing(isChecked);
                    sharedViewModel.setIsSharing(isChecked);
                    isSharing = userSignInWithGoogle.isSharing();
                    updateSharingPreference(user_id,isSharing, isAlert, "google");
                }
                else if (MainActivity.type.equals("facebook")){
                    userdata.setIsSharing(isChecked);
                    sharedViewModel.setIsSharing(isChecked);
                    isSharing = userdata.getIsSharing();
                    updateSharingPreference(user_id,isSharing, isAlert, "facebook");
                }
            }
        });

        // Set the switch state based on userdata.isAlert

        active_alert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (MainActivity.type.equals("normal")){
                    userdata.setIsAlert(isChecked);
                    sharedViewModel.setIsAlert(isChecked);
                    isAlert = userdata.getIsAlert();
                    updateSharingPreference(user_id,isSharing, isAlert, "normal");
                }
                else if (MainActivity.type.equals("google")){
                    userSignInWithGoogle.setAlert(isChecked);
                    sharedViewModel.setIsAlert(isChecked);
                    isAlert = userSignInWithGoogle.isAlert();
                    updateSharingPreference(user_id,isSharing, isAlert, "google");
                }
                else if (MainActivity.type.equals("facebook")){
                    userSignInWithFacebook.setSharing(isChecked);
                    sharedViewModel.setIsSharing(isChecked);
                    isSharing = userSignInWithFacebook.isSharing();
                    updateSharingPreference(user_id,isSharing, isAlert, "facebook");
                }
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                Bundle bundle = ActivityOptions.makeCustomAnimation(getContext(), R.anim.animation1, R.anim.animation2).toBundle();
                startActivity(intent, bundle);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("user_data", userdata);
                Bundle bundle = ActivityOptions.makeCustomAnimation(getContext(), R.anim.animation1, R.anim.animation2).toBundle();
                startActivity(intent, bundle);
            }
        });

        detection_sensitivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = ActivityOptions.makeCustomAnimation(getContext(), R.anim.animation1, R.anim.animation2).toBundle();
                startActivity(new Intent(getActivity(), DetectionSensitivityActivity.class), bundle);
            }
        });

        alert_references.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (label4_des.getVisibility() == View.GONE){
                    label4_des.setVisibility(View.VISIBLE);
                }
                else {
                    label4_des.setVisibility(View.GONE);
                }
            }
        });

        data_sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (label3_des.getVisibility() == View.GONE){
                    label3_des.setVisibility(View.VISIBLE);
                }
                else {
                    label3_des.setVisibility(View.GONE);
                }
            }
        });

        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(requireContext());
                dialog.setContentView(R.layout.dialog_confirmed_sign_out);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                btn_close = dialog.findViewById(R.id.btn_close);
                btn_sign_out = dialog.findViewById(R.id.btn_sign_out);
                dialog.getWindow().setBackgroundDrawable(null);

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btn_sign_out.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String type = MainActivity.type;
                        if (MainActivity.type.equals("google")){
                            signOutGoogle();
                        }
                        else if (MainActivity.type.equals("facebook")){
                            signOutFacebook();
                        }
                        else {
                            SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            Resources resources = getResources();
                            Configuration config = resources.getConfiguration();
                            DisplayMetrics dm = resources.getDisplayMetrics();
                            String currentLanguage = config.locale.getLanguage();
                            editor.clear();
                            editor.apply();

                            editor.putString("lang_code",currentLanguage);
                            editor.apply();

                            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Bundle bundle = ActivityOptions.makeCustomAnimation(getContext(), R.anim.animation1, R.anim.animation2).toBundle();
                            startActivity(intent, bundle);
                            getActivity().finish();
                        }
                    }
                });

                dialog.show();

            }
        });

        return view;
    }

    private void updateSharingPreference(String user_id, boolean isSharing, boolean isAlert, String type) {
        ApiService.apiService.updatePreferences(type,new UpdatePreferencesRequest(user_id,isSharing, isAlert)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
    void signOutGoogle(){
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                Resources resources = getResources();
                Configuration config = resources.getConfiguration();
                DisplayMetrics dm = resources.getDisplayMetrics();
                String currentLanguage = config.locale.getLanguage();
                editor.clear();
                editor.apply();

                editor.putString("lang_code",currentLanguage);
                editor.apply();

                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
    void signOutFacebook(){
        LoginManager.getInstance().logOut();
        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        String currentLanguage = config.locale.getLanguage();
        editor.clear();
        editor.apply();

        editor.putString("lang_code",currentLanguage);
        editor.apply();

        Intent intent = new Intent(getActivity(), WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}