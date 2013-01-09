package com.rayudu.flakes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;


public class FlakeyRS extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        final FlakeyView mView = new FlakeyView(this);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.addView(mView);
        
        CheckBox showFPS = (CheckBox) findViewById(R.id.showFPS);
        showFPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mView.showFPS(isChecked);	
			}
		});
        
        Button more = (Button) findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				mView.changeNumFlakes(true);
				
			}
		});
        
        Button less = (Button) findViewById(R.id.less);
        less.setOnClickListener(new View.OnClickListener() {	
			public void onClick(View v) {
				mView.changeNumFlakes(false);
				
			}
		});
    }
}