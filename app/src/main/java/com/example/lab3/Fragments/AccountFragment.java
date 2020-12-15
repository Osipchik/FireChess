package com.example.lab3.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.lab3.Activities.SignInActivity;
import com.example.lab3.R;
import com.example.lab3.ViewModels.AccountViewModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {
    private Button applyButton, uploadButton;
    private EditText userNameEditText;
    private ImageView profileImage;

    private final int PICK_IMAGE_REQUEST = 22;
    public static final String STORAGE_PATH = "image/*";

    AccountViewModel accountViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accountViewModel = ViewModelProviders.of(requireActivity()).get(AccountViewModel.class);
        accountViewModel.loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        userNameEditText = view.findViewById(R.id.userName);
        TextView emailView = view.findViewById(R.id.userEmail);
        Switch imageSwitch = view.findViewById(R.id.image_switch);
        applyButton = view.findViewById(R.id.apply_button);
        uploadButton = view.findViewById(R.id.upload_image_button);

        applyButton.setOnClickListener(this::applyChanges);
        uploadButton.setOnClickListener(this::selectImage);
        imageSwitch.setOnCheckedChangeListener(this::onCheckChange);

        accountViewModel.getEmail().observe(requireActivity(), emailView::setText);
        accountViewModel.getUserName().observe(requireActivity(), i -> userNameEditText.setText(i));
        accountViewModel.getImageUri().observe(requireActivity(), i -> Picasso.get().load(i).into(profileImage));
        accountViewModel.getIsGravatar().observe(requireActivity(), imageSwitch::setChecked);

        accountViewModel.getMessageId().observe(requireActivity(), i -> Toast.makeText(requireActivity(), getString(i), Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.logout_button).setOnClickListener(i -> {
            accountViewModel.logout();
            Intent intent = new Intent(getContext(), SignInActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        applyButton.setVisibility(View.GONE);
    }

    private void onCheckChange(CompoundButton compoundButton, boolean b) {
        uploadButton.setVisibility(b ? View.GONE : View.VISIBLE);
        accountViewModel.setIsGravatar(b);
    }

    private void applyChanges(View view) {
        accountViewModel.setName(userNameEditText.getText().toString());
        accountViewModel.updateProfile();
    }

    private void selectImage(View view)
    {
        Intent intent = new Intent();
        intent.setType(STORAGE_PATH);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            accountViewModel.setImageUri(data.getData());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                profileImage.setImageBitmap(bitmap);
                applyButton.setVisibility(View.VISIBLE);

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}