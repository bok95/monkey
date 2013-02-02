package com.example.monkey;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.example.monkey.fingo.AbstractActionReceiver;
import com.example.monkey.fingo.FingoApplication;

public class MonkeyActionReceiver extends AbstractActionReceiver implements
		OnTouchListener {

	private boolean isRecording;
	private ImageView pointerView;
	private MarginLayoutParams pointerMargin;
	private int pointerOffsetX;
	private int pointerOffsetY;
	private LayoutInflater inflater;
	
	@Override
	public void action1() {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout container = (LinearLayout) inflater.inflate(
				R.layout.activity_monkey_action, null);
		FingoApplication.getInstance().setContainer(container);

		container.setOnTouchListener(this);
		// container.setBackgroundColor(Color.YELLOW); // for debugging
		container.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View arg0, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight,
					int oldBottom) {
				window.updateViewLayout(container, makeLayoutParams(context));
			}
		});

		window.addView(container, makeLayoutParams(context));
		isRecording = true;

		LinearLayout touchContainer = (LinearLayout) container
				.findViewById(R.id.fingersContainer);

		pointerView = new ImageView(context);
		pointerView.setBackgroundResource(R.drawable.pointer2);
		BitmapDrawable bd = (BitmapDrawable) pointerView.getBackground();
		LayoutParams layoutParam = new LinearLayout.LayoutParams(bd.getBitmap()
				.getWidth(), bd.getBitmap().getHeight());
		pointerView.setLayoutParams(layoutParam);

		pointerView.setVisibility(View.GONE);
		touchContainer.addView(pointerView);

		pointerMargin = new ViewGroup.MarginLayoutParams(
				pointerView.getLayoutParams());

		pointerOffsetX = bd.getBitmap().getWidth() / 2;
		pointerOffsetY = bd.getBitmap().getHeight() / 2;
		isRecording = true;
	}

	private android.view.WindowManager.LayoutParams makeLayoutParams(
			Context context) {
		int w = getWindowWidth(context)
				- context.getResources().getDimensionPixelSize(
						R.dimen.edge_detection_width);
		android.view.WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
				w, LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
						| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		return layoutParams;
	}

	@Override
	public void action2() {
		unblock();
		isRecording = true;
	}

	@Override
	public void action3() {
		unblock();
		save("monkey.txt");
		isRecording = false;
		FingoApplication.getInstance().setWaitTime(0);
	}

	@Override
	protected String getClassName() {
		return MonkeyAction.class.getName();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		long eventTime = event.getEventTime();
		int action = event.getAction();
		int deviceId = event.getDeviceId();
		float x = event.getX();
		float y = event.getY() + 50;
		if (FingoApplication.getInstance().getWaitTime() == 0) {
			FingoApplication.getInstance().setWaitTime(eventTime);
		}

		Log.d("external", "onTouch : " + event);
		if (action == MotionEvent.ACTION_OUTSIDE)
			return false;
		long wtime = Math.abs(eventTime - FingoApplication.getInstance().getWaitTime());
		// Log.d("monkey", "action = " + action + " pause(" + wtime
		// + ") : eventTime = " + eventTime + " waitTime = "
		// + waitTime);
		pause(wtime);
		FingoApplication.getInstance().getEvents()
				.add(dispatchPointer(action, x, y, deviceId));
		FingoApplication.getInstance().setWaitTime(eventTime);

		int margin_x = (int) event.getX() - pointerOffsetX;
		int margin_y = (int) event.getY() - pointerOffsetY;
		pointerMargin.setMargins(margin_x, margin_y, 0, 0);
		pointerView
				.setLayoutParams(new LinearLayout.LayoutParams(pointerMargin));
		pointerView.setVisibility(View.VISIBLE);

		return true;
	}

	private void unblock() {
		LinearLayout container = FingoApplication.getInstance().getContainer();
		try {
			if (container != null) {
				window.removeView(container);
			}
		} catch (IllegalArgumentException e) {
			Log.e("monkey", e.toString());
		} finally {
			FingoApplication.getInstance().setContainer(null);
		}
	}

	private void pause(long time) {
		FingoApplication.getInstance().getEvents()
				.add("UserWait(" + time + ")\n");
	}

	private String dispatchPointer(int action, float x, float y, int deviceId) {
		String cmd = "DispatchPointer(0, 0" + ", " + action + ", " + x + " ,"
				+ y + ", 0,0,0,0,0," + deviceId + ",0)\n";
		return cmd;

	}

	private void save(String fileName) {
		String root = Environment.getExternalStorageDirectory().toString();
		File file = new File(root + "/" + fileName);
		if (file.exists())
			file.delete();
		try {
			FileOutputStream out = new FileOutputStream(file);
			for (String startCmd : getStartScript()) {
				out.write(startCmd.getBytes());
			}
			for (String cmd : FingoApplication.getInstance().getEvents()) {
				out.write(cmd.getBytes());
			}
			for (String endCmd : getEndScript()) {
				out.write(endCmd.getBytes());
			}

			out.flush();
			out.close();

			resetEvents();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> getStartScript() {
		ArrayList<String> prefix = new ArrayList<String>();
		prefix.add("type= user\n");
		prefix.add("speed= 1000\n");
		prefix.add("start data >>\n");
		prefix.add("UserWait(3000)\n");
		return prefix;
	}

	public ArrayList<String> getEndScript() {
		ArrayList<String> endCmd = new ArrayList<String>();
		endCmd.add("quit\n");
		return endCmd;
	}

	private void resetEvents() {
		FingoApplication.getInstance().getEvents().clear();
	}

	private int getWindowWidth(Context context) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		window.getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}

	private int getWindowHeight(Context context) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		window.getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

}
