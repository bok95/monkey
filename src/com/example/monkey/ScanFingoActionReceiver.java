package com.example.monkey;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import fingo.addons.IExternalFingoAction;

public class ScanFingoActionReceiver extends BroadcastReceiver implements
		IExternalFingoAction {

	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		sendBroadcast(this);
	}

	private void sendBroadcast(IExternalFingoAction action) {
		Intent i = new Intent(Intent.ACTION_MAIN);

		i.setAction(IExternalFingoAction.INSTALLED_FINGO_ACTION);
		i.putExtra(IExternalFingoAction.Key.RESOURCE.name(), action.getIcon());
		i.putExtra(IExternalFingoAction.Key.PKG_NAME.name(),
				action.getPackageName());
		i.putExtra(IExternalFingoAction.Key.CLASS_NAME.name(),
				action.getClassName());
		i.putExtra(IExternalFingoAction.Key.TYPE.name(), action.getType()
				.ordinal());
		i.putExtra(IExternalFingoAction.Key.SUBJECT.name(), action.getSubject());
		i.putExtra(IExternalFingoAction.Key.DESCRIPTION.name(),
				action.getDescription());
		i.putExtra(IExternalFingoAction.Key.FOR_DEV.name(),
				action.getDescription());

		HashMap<State, String> icons = getIcons();
		for (State key : icons.keySet()) {
			i.putExtra(key.name(), icons.get(key));
		}
		context.sendBroadcast(i);
	}

	@Override
	public String getClassName() {
		return MonkeyAction.class.getName();
	}

	@Override
	public String getDescription() {
		return context.getResources().getString(R.string.action_description);
	}

	@Override
	public String getPackageName() {
		return context.getPackageName();
	}

	@Override
	public String getIcon() {
		return "monkey_pause";
	}

	@Override
	public String getSubject() {
		return context.getResources().getString(R.string.action_title);
	}

	@Override
	public Type getType() {
		return Type.TOGGLE;
	}

	@Override
	public HashMap<State, String> getIcons() {
		HashMap<State, String> icons = new HashMap<IExternalFingoAction.State, String>();
		icons.put(State.DEFAULT, "monkey_off");
		icons.put(State.TOGGLE_FIRST, "monkey_off");
		icons.put(State.TOGGLE_SECOND, "monkey_recording");
		icons.put(State.TOGGLE_THIRD, "monkey_pause");
		return icons;
	}

}
