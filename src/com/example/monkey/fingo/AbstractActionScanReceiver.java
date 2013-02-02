package com.example.monkey.fingo;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import fingo.addons.IExternalFingoAction;

public abstract class AbstractActionScanReceiver extends BroadcastReceiver
		implements IExternalFingoAction {

	protected Context context;

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

}
