# SwipeNumberPicker

The library provides simple number picker. The number is selected with the swipe gesture, to right - increase, to left - decrease value. Also, by click NumberPickerDialog will be shown.

# Usage

In layout:

```xml
<com.vi.swipenumberpicker.SwipeNumberPicker
		android:id="@+id/number_picker"
		android:layout_width="wrap_content"
		android:layout_height="wrap_content"
		android:layout_alignParentBottom="true"
		android:layout_alignParentLeft="true"
		snp:activeTextColor="@android:color/white"
		snp:arrowColor="@android:color/white"
		snp:backgroundColor="@color/colorAccent"
		snp:max="100"
		snp:min="50"
		snp:value="95"/>
```
