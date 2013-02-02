package fingo.plugin.action.monkey;

import java.util.HashMap;

import com.example.monkey.R;

import fingo.addons.IExternalFingoAction;
import fingo.plugin.action.AbstractActionScanReceiver;

public class MonkeyActionScanReceiver extends AbstractActionScanReceiver
		implements IExternalFingoAction {
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
