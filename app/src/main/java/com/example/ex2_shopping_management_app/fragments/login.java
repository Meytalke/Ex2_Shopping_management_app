package com.example.ex2_shopping_management_app.fragments;

import static com.example.ex2_shopping_management_app.R.*;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ex2_shopping_management_app.MainActivity;
import com.example.ex2_shopping_management_app.R;

public class login extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public login() {
    }

    public static login newInstance(String param1, String param2) {
        login fragment = new login();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button registrationButtonInLogin=view.findViewById(id.registrationButtonInLoginPage);
        registrationButtonInLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(id.action_login_to_registration);
            }
        });


        Button login = view.findViewById(R.id.LogInButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = true;
                EditText userNameField = view.findViewById(R.id.TextUserNameAddressLogin);
                EditText passwordField = view.findViewById(R.id.TextPasswordLogin);
                String userName = userNameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (userName.isEmpty()) {
                    userNameField.setError("Username is required");
                    isValid = false;
                }

                if (password.isEmpty()) {
                    passwordField.setError("Password is required");
                    isValid = false;
                }
                if (isValid) {
                    getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE)
                            .edit()
                            .putString("username", userName)
                            .apply();
                    MainActivity mainActivity= (MainActivity) getActivity();
                    mainActivity.login(userName,password,v);
                }
            }
        });
        return view;
    }
}