package com.example.umayado;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class InitialActivity extends Activity {
    private boolean srv_mode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        Switch mode_switch = (Switch) findViewById(R.id.mode_switch);
        Button start_btn = (Button) findViewById(R.id.startButton);

        mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    srv_mode = true;
                    findViewById(R.id.server_url).setVisibility(View.VISIBLE);
                } else {
                    srv_mode = false;
                    findViewById(R.id.server_url).setVisibility(View.GONE);
                }
            }
        });

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, MainActivity.class);
                if (srv_mode) {
                    EditText srv_url = (EditText) findViewById(R.id.server_url);
                    intent.putExtra("srv_url", srv_url.getText().toString());
                }
                //遷移先の画面を起動
                startActivity(intent);
            }
        });
    }
}
