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

Attributes:

``` xml
	<attr name="min" format="integer"/>
	<attr name="max" format="integer"/>
	<attr name="value" format="integer"/>
	<attr name="arrowColor" format="color"/>
	<attr name="backgroundColor" format="color"/>
	<attr name="numberColor" format="color"/>
```

To set changed value implement the `OnValueChangeListener` listener and on `onValueChange` return `true`
Also via code you can:
- disabled/enabled NumberPickerDialog, set the dialog title. If the dialog disabled `View.OnClickListener` will be called;
- set min, max values;
- set value.
