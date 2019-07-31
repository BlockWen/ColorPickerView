package com.blocki.colorpickerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView showColorView;
    private ColorPickerView pickerView;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showColorView = findViewById(R.id.textview_showColor);
        showColorView.setVisibility(View.VISIBLE);
        pickerView = findViewById(R.id.main_colorPickerView);
        confirmBtn = findViewById(R.id.btn_confirm);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = pickerView.getPointColor();
                showColorView.setBackgroundColor(color);
            }
        });
    }

}
