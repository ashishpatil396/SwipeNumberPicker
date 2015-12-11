package com.vi.swipenumberpicker.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.vi.swipenumberpicker.OnValueChangeListener;
import com.vi.swipenumberpicker.SwipeNumberPicker;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final SwipeNumberPicker swipeNumberPicker = (SwipeNumberPicker) findViewById(R.id.snp_implemented);
		final SwipeNumberPicker custom = (SwipeNumberPicker) findViewById(R.id.snp_custom);

		custom.setEnabled(false);
		swipeNumberPicker.setEnabled(false);

		final TextView result1 = (TextView) findViewById(R.id.tv_result_1);
		final TextView result2 = (TextView) findViewById(R.id.tv_result_2);
		result2.setText(Integer.toString(custom.getValue()));

		swipeNumberPicker.setOnValueChangeListener(new OnValueChangeListener() {
			@Override
			public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
				boolean isValueOk = (newValue & 1) == 0;
				custom.setEnabled(true);
				if (isValueOk)
					result1.setText(Integer.toString(newValue));

				return isValueOk;
			}
		});
		swipeNumberPicker.setShowNumberPickerDialog(false);
		swipeNumberPicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SwipeNumberPicker) v).setValue(0, true);
			}
		});

		custom.setOnValueChangeListener(new OnValueChangeListener() {
			@Override
			public boolean onValueChange(SwipeNumberPicker view, int oldValue, int newValue) {
				result2.setText(Integer.toString(newValue));
				return true;
			}
		});


	}
}
