package com.vi.swipenumberpicker.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.vi.swipenumberpicker.OnValueChangeListener;
import com.vi.swipenumberpicker.SwipeNumberPicker;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SwipeNumberPicker swipeNumberPicker = (SwipeNumberPicker) findViewById(R.id.number_picker);

		swipeNumberPicker.setOnValueChangeListener(new OnValueChangeListener() {
			@Override
			public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
				return true;
			}
		});
		swipeNumberPicker.setShowNumberPickerDialog(false);
		swipeNumberPicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SwipeNumberPicker) v).setValue(666, false);
			}
		});
	}
}
