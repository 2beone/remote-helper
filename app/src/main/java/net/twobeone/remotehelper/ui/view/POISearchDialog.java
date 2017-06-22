package net.twobeone.remotehelper.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.ui.BaseActivity;

public class POISearchDialog extends BaseActivity {

    private EditText poi_name;
    private Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_poi);

        poi_name = (EditText) findViewById(R.id.edit_poi);
        search = (Button) findViewById(R.id.search);

        search.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_close));
        search.setTextColor(getColorStateList(this, R.drawable.btn_text));

        search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle extra = new Bundle();
                Intent intent = new Intent();
                extra.putString("data", poi_name.getText().toString());
                intent.putExtras(extra);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private ColorStateList getColorStateList(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColorStateList(id, context.getTheme());
        } else {
            return context.getResources().getColorStateList(id);
        }
    }
}