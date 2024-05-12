package com.example.meditrack;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_some);

        // Button that triggers the bottom sheet dialog
        Button button = findViewById(R.id.showDialogButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChoiceDialogFragment dialogFragment = new ImageChoiceDialogFragment();
                dialogFragment.setImageChoiceListener(new ImageChoiceDialogFragment.ImageChoiceListener() {
                    @Override
                    public void onImageChosen(Uri imageUri) {
                        // Handle the chosen image URI
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "imagePicker");
            }
        });
    }
}
