package com.example.kickunity.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.widget.EditText;

import com.example.kickunity.Auth.AuthApiService;
import com.example.kickunity.Auth.CheckResponse;
import com.example.kickunity.Auth.Login;
import com.example.kickunity.R;
import com.example.kickunity.Auth.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private TextView textProfileName, textTeamInfo, textEmail, textBirthdate;
    private Button editProfileButton, logoutButton, deleteAccountButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        initializeUI(view);

        // Get user details from SharedPreferences
        String userEmail = getUserEmailFromSharedPreferences();
        String accessToken = getAccessTokenFromSharedPreferences();

        if (userEmail != null && accessToken != null) {
            loadUserProfile(userEmail, accessToken); // Load user profile data
        } else {
            Log.e(TAG, "User email or access token is missing.");
        }

        setupButtons();
        return view;
    }

    // Initialize UI elements
    private void initializeUI(View view) {
        textProfileName = view.findViewById(R.id.TextprofileName);
        textTeamInfo = view.findViewById(R.id.TextTeamInfo);
        textEmail = view.findViewById(R.id.Textemail);
        textBirthdate = view.findViewById(R.id.TextBirthdate);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);
    }

    // Setup button click listeners
    private void setupButtons() {
        editProfileButton.setOnClickListener(v -> openProfileEditActivity());
        logoutButton.setOnClickListener(v -> handleLogout());
        deleteAccountButton.setOnClickListener(v -> showPasswordDialog());
    }

    // Open profile edit activity with user data
    private void openProfileEditActivity() {
        Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
        intent.putExtra("userEmail", textEmail.getText().toString());
        intent.putExtra("userName", textProfileName.getText().toString());
        startActivity(intent);
    }

    // Show password input dialog for account deletion
    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter Password");

        EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String password = input.getText().toString().trim();
            if (!password.isEmpty()) {
                deleteAccount(getAccessTokenFromSharedPreferences(), password);
            } else {
                Toast.makeText(getActivity(), "Please enter a password.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Handle user logout
    private void handleLogout() {
        String accessToken = getAccessTokenFromSharedPreferences();

        if (accessToken != null) {
            logout(accessToken);
        } else {
            Log.e(TAG, "Access token is missing.");
            redirectToLoginScreen();
        }
    }

    private void logout(String accessToken) {
        AuthApiService authApiService = RetrofitClient.getRetrofitInstance().create(AuthApiService.class);
        Call<Void> call = authApiService.logout("Bearer " + accessToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    clearSharedPreferences();
                    redirectToLoginScreen();
                } else {
                    Toast.makeText(getActivity(), "Logout failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Logout request failed: " + t.getMessage());
                Toast.makeText(getActivity(), "Logout failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAccount(String token, String password) {
        // Create a request object with the password
        DeleteMemberRequest request = new DeleteMemberRequest(password);

        // Retrofit instance to make the request
        AuthApiService authApiService = RetrofitClient.getRetrofitInstance().create(AuthApiService.class);

        // Send a PUT request to the server to delete the account
        Call<CheckResponse> call = authApiService.deleteAccount("Bearer " + token, request);
        call.enqueue(new Callback<CheckResponse>() {
            @Override
            public void onResponse(Call<CheckResponse> call, Response<CheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Successfully deleted the account
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    clearSharedPreferences();
                    redirectToLoginScreen();
                } else {
                    // Handle failure response
                    Toast.makeText(getContext(), "Account deletion failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckResponse> call, Throwable t) {
                // Network or other errors
                Toast.makeText(getContext(), "Error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    private void redirectToLoginScreen() {
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void loadUserProfile(String userEmail, String accessToken) {
        AuthApiService authApiService = RetrofitClient.getRetrofitInstance().create(AuthApiService.class);
        Call<MypageResponse> call = authApiService.getMyPageInfo("Bearer " + accessToken, userEmail);

        call.enqueue(new Callback<MypageResponse>() {
            @Override
            public void onResponse(Call<MypageResponse> call, Response<MypageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Log.e(TAG, "Failed to load profile: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MypageResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch profile: " + t.getMessage());
            }
        });
    }

    private void updateUI(MypageResponse response) {
        textProfileName.setText(response.getName() != null ? response.getName() : "");
        textTeamInfo.setText(response.getTeam() != null ? response.getTeam() : "");
        textEmail.setText(response.getEmail() != null ? response.getEmail() : "");
        textBirthdate.setText(response.getBirth() != null ? response.getBirth() : "");
    }

    private String getUserEmailFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("userEmail", null);
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("auth", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }
}
