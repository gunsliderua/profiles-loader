package denys.salikhov.exam01.profileloader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FetchStatusFragment extends Fragment {
	IntentFilter iff = new IntentFilter();
	View updatingView, resultView;
	TextView tvstatus, tvDatetime;

	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("FetchStatusFragment LocalBroadcast", "onReceive called " + intent.getAction());
			updateViews();
		}
	};

	public FetchStatusFragment() {
		for (ProfilesKeeper.State possiblevalue : ProfilesKeeper.State.values()) {
			iff.addAction(possiblevalue.toString());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fetch_status, container, false);
		resultView = rootView.findViewById(R.id.result_view);
		updatingView = rootView.findViewById(R.id.updating_view);
		tvstatus = (TextView) resultView.findViewById(R.id.update_status);
		tvDatetime = (TextView) resultView.findViewById(R.id.update_datetime);
		updateViews();
		return rootView;

	}

	private void updateViews() {
		ProfilesKeeper.State state = ProfilesKeeper.INSTANCE.getState();
		String fetchDateTime = ProfilesKeeper.INSTANCE.getFetchDateTime();
		switch (state) {
			case READY:
				tvstatus.setTextColor(Color.GREEN);
				tvstatus.setText(R.string.successfuly);
				tvDatetime.setText(fetchDateTime);
				resultView.setVisibility(View.VISIBLE);
				updatingView.setVisibility(View.GONE);
				break;
			case FAILED:
				tvstatus.setTextColor(Color.RED);
				tvstatus.setText(R.string.failed);
				tvDatetime.setText(fetchDateTime);
				resultView.setVisibility(View.VISIBLE);
				updatingView.setVisibility(View.GONE);
				break;
			case LOADING:
				resultView.setVisibility(View.GONE);
				updatingView.setVisibility(View.VISIBLE);
				break;
			default:
				throw new IllegalStateException("Not expected state");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);

	}

	@Override
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, iff);
		updateViews();

	}
}