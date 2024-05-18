package com.example.meditrack;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.Map;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class OptionsDialogFragment extends DialogFragment {

    private static final String ARG_PLAN_ID = "plan_id";
    private static final String ARG_PLAN_DATA = "plan_data";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static OptionsDialogFragment newInstance(String planId, Map<String, Object> planData) {
        OptionsDialogFragment fragment = new OptionsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAN_ID, planId);
        args.putSerializable(ARG_PLAN_DATA, (Serializable) planData);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_options, null);
        builder.setView(view);

        String planId = getArguments().getString(ARG_PLAN_ID);
        Map<String, Object> planData = (Map<String, Object>) getArguments().getSerializable(ARG_PLAN_DATA);

        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditPlan.class);
            intent.putExtra("PLAN_ID", planId);
            intent.putExtra("planData", (Serializable) planData);
            startActivity(intent);
            dismiss();
        });

        Button removeButton = view.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> {
            String imageUrl = (String) planData.get("imageURL");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                deleteImageFromStorage(imageUrl);
            }
            if (getActivity() instanceof Plans) {
                ((Plans) getActivity()).deletePlan(planId);
            }
            dismiss();
        });


        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    private void deleteImageFromStorage(String imageUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);

        imageRef.delete().addOnSuccessListener(aVoid -> {
            Log.d("Storage", "Image successfully deleted from Firebase Storage.");
        }).addOnFailureListener(e -> {
            Log.e("Storage", "Error deleting image from Firebase Storage.", e);
        });
    }
}
