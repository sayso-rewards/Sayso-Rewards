package com.offerwall;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sayso.ui.activities.SaySo;

public class MainActivity extends Activity {

    private EditText etRidId, etPartnerId;

    private SaySo saySo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saySo = new SaySo();

        initView();
    }

    private void initView() {
        etRidId = findViewById(R.id.etRid);
        etPartnerId = findViewById(R.id.etPartnerId);
    }

    public void onConfigureClick(View view) {
        if (etRidId.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter RId", Toast.LENGTH_SHORT).show();
            return;
        }
        if (etPartnerId.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter partner id", Toast.LENGTH_SHORT).show();
            return;
        }

        saySo.isSurveyAvailable(this, etPartnerId.getText().toString().trim(), etRidId.getText().toString().trim());
    }

    public void onStartClick(View view) {
        if (saySo.isSurveyAvailable(this)) {
            saySo.displaySurveyInWebView();
        }else{
            Toast.makeText(this, "Survey not Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (saySo != null)
            Toast.makeText(this, String.valueOf(saySo.getRewardValue(this)), Toast.LENGTH_SHORT).show();
    }
}
