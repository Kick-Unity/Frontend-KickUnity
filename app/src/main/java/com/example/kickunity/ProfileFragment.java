package com.example.kickunity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView profileImageView;
    private TextView profileName, teamInfo, phoneNumber, gender, birthdate, email;
    private Button editProfileButton, logoutButton, deleteAccountButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // View 연결
        profileImageView = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);
        teamInfo = view.findViewById(R.id.teamInfo);
        phoneNumber = view.findViewById(R.id.activityField);
        gender = view.findViewById(R.id.gender);
        birthdate = view.findViewById(R.id.birthdate);
        email = view.findViewById(R.id.email);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton);

        // 프로필 이미지 클릭하여 편집
        profileImageView.setOnClickListener(v -> openGallery());

        // 내정보 편집 버튼 클릭
        editProfileButton.setOnClickListener(v -> {
            // 내정보 편집 액티비티 또는 다이얼로그 띄우기
        });

        // 로그아웃 버튼 클릭
        logoutButton.setOnClickListener(v -> {
            // 로그아웃 처리
        });

        // 회원탈퇴 버튼 클릭
        deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());

        return view;
    }

    // 갤러리 열기 (프로필 사진 편집)
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                profileImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 회원탈퇴 확인 다이얼로그
    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("회원탈퇴")
                .setMessage("정말로 회원탈퇴를 하시겠습니까?")
                .setPositiveButton("탈퇴", (dialog, which) -> {
                    // 회원탈퇴 처리
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
