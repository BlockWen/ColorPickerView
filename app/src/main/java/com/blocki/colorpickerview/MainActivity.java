package com.blocki.colorpickerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blocki.mylibrary.ColorPickerView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView showColorView;
    private ColorPickerView pickerView;
    private Button confirmBtn;

    private int[] rgbArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showColorView = findViewById(R.id.textview_showColor);
        showColorView.setVisibility(View.VISIBLE);
        pickerView = findViewById(R.id.main_colorPickerView);
        confirmBtn = findViewById(R.id.btn_confirm);

        Log.d(TAG, "onCreate: pickerview settings start");

        pickerView.setDrawMagnifyCircle(true);
        pickerView.setDrawMagnifyBounds(true);
        pickerView.setCornorCircleType(ColorPickerView.TYPE_FILL);

        Log.d(TAG, "onCreate: pickerview settings end");

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = pickerView.getCurRGBColor();
                showColorView.setBackgroundColor(color);
                rgbArray = pickerView.getRGBArray();
                showColorView.setText(rgbArray[0] +" "+ rgbArray[1] +" "+ rgbArray[2]);
            }
        });
    }

}
