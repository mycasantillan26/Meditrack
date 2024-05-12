package com.example.meditrack;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ImageOptionsDialogFragment extends BottomSheetDialogFragment {
    private ImageOptionsListener listener;

    public interface ImageOptionsListener {
        void onRemoveSelected();
        void onCancelSelected();
    }

    public void setImageOptionsListener(ImageOptionsListener listener) {
        this.listener = listener;
    }

    @Override
    public BottomSheetDialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image_choices, null);
        setupButtons(view);

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        dialog.setContentView(view);
        return dialog;
    }

    private void setupButtons(View view) {
        Button removeButton = view.findViewById(R.id.removeButton);
        Button cancelButton = view.findViewById(R.id.button_cancel);

        removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveSelected();
            }
            dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelSelected();
            }
            dismiss();
        });
    }
}
