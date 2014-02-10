package com.eyeofmidas.personallog;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button button = (Button) findViewById(R.id.recordNewLogButton);
		button.setOnClickListener(new View.OnClickListener() {
			private boolean isRecording = false;

			public void onClick(View v) {
				if (!this.isRecording) {
					this.isRecording = true;
					button.setText(R.string.stop_recording);
				} else {
					this.isRecording = false;
					button.setText(R.string.record_new_log);
				}
			}
		});

		ListView logList = (ListView) findViewById(R.id.logList);
		ArrayList<String> logData = new ArrayList<String>();
		logData.add("log 1");
		logData.add("log 2");
		logData.add("log 3");

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, logData);

		logList.setAdapter(arrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
