package com.eyeofmidas.personallog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.xiph.vorbis.recorder.VorbisRecorder;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String TAG = "PersonalLog";
	private boolean isRecording = false;
	private String logDirectory;
	private Button recordingButton;
	private File appDirectory;
	private ListView logList;
	private VorbisRecorder vorbisRecorder;
	private Handler recordingHandler;
	private SimpleDateFormat simpleFileDateFormatter;
	private SimpleDateFormat simpleDisplayDateFormatter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		logDirectory = Environment.getExternalStorageDirectory().toString() + "/" + "PersonalLog";

		recordingButton = (Button) findViewById(R.id.recordNewLogButton);
		recordingButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (!isRecording) {
					isRecording = true;
					startRecording();

				} else {
					isRecording = false;
					stopRecording();
				}
			}
		});

		appDirectory = new File(logDirectory);
		// have the object build the directory structure, if needed.
		if (!appDirectory.mkdirs() && !appDirectory.isDirectory()) {
			Log.e(TAG, "The directory " + logDirectory + " does not exist and I could not create it.");
		}

		simpleFileDateFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
		simpleDisplayDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

		logList = (ListView) findViewById(R.id.logList);
		logList.setAdapter(getUpdatedAdapter());
		logList.setOnItemClickListener(getClickListener());

		recordingHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case VorbisRecorder.START_ENCODING:
					Log.d(TAG, "Starting to encode");
					break;
				case VorbisRecorder.STOP_ENCODING:
					Log.d(TAG, "Stopping the encoder");
					break;
				case VorbisRecorder.UNSUPPORTED_AUDIO_TRACK_RECORD_PARAMETERS:
					Log.d(TAG, "You're device does not support this configuration");
					break;
				case VorbisRecorder.ERROR_INITIALIZING:
					Log.d(TAG, "There was an error initializing.  Try changing the recording configuration");
					break;
				case VorbisRecorder.FAILED_FOR_UNKNOWN_REASON:
					Log.d(TAG, "The encoder failed for an unknown reason!");
					break;
				case VorbisRecorder.FINISHED_SUCCESSFULLY:
					Log.d(TAG, "The encoder has finished successfully");
					break;
				}
			}
		};

	}

	private ArrayAdapter<String> getUpdatedAdapter() {
		File[] logFiles = appDirectory.listFiles();
		Arrays.sort(logFiles, new Comparator<File>() {
			@Override
			public int compare(File object1, File object2) {
				return Long.valueOf(object1.lastModified()).compareTo(object2.lastModified());
			}
		});

		ArrayList<String> logData = new ArrayList<String>();

		if (logFiles.length > 0) {
			for (int i = 0; i < logFiles.length; i++) {
				// TODO: instead of using filenames, output database info

				//logData.add(simpleDisplayDateFormatter.format(logFiles[i].lastModified()));
				logData.add(logFiles[i].getName());
			}
		} else {
			logData.add("No logs!");
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, logData);
		return arrayAdapter;
	}

	protected void stopRecording() {
		vorbisRecorder.stop();
		vorbisRecorder = null;

		recordingButton.setText(R.string.record_new_log);
		logList.setAdapter(getUpdatedAdapter());
		logList.setOnItemClickListener(getClickListener());
	}

	private OnItemClickListener getClickListener() {
		return new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
				TextView clickedView = (TextView) view;
				Intent openIntent = new Intent();
				openIntent.setAction(Intent.ACTION_VIEW);
				File logFile = new File(logDirectory + "/" + clickedView.getText());
				openIntent.setDataAndType(Uri.fromFile(logFile), "audio/ogg");
				startActivity(openIntent);
			}
		};
	}

	protected void startRecording() {
		String currentDateandTime = simpleFileDateFormatter.format(new Date());
		recordingButton.setText(R.string.stop_recording);

		String outputFileName = logDirectory + "/log_" + currentDateandTime + ".ogg";
		vorbisRecorder = new VorbisRecorder(new File(outputFileName), recordingHandler);
		vorbisRecorder.start(44100, 2, 128000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
