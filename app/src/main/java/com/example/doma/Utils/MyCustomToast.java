package com.example.doma.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doma.R;

public class MyCustomToast {

    public static void createToast(Context context, String message, boolean error) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.my_custom_toast, null);
        TextView toastText = view.findViewById(R.id.mycustomotasttext);

        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, spannableString.length(), 0);
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);

        toastText.setText(spannableString);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);

        if (error) {
            toastText.setTextColor(Color.parseColor("#a60400"));
        } else {
            toastText.setTextColor(Color.parseColor("#036e03"));
        }

         toast.setGravity(Gravity.BOTTOM, 32, 32);
        toast.show();
    }

}
