package denys.salikhov.exam01.profileloader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UserListFragment extends ListFragment {
	public static final int SMALL_AVATAR_DP_SIZE = 100;
	IntentFilter iff = new IntentFilter();
	private IProfileClickHandler profileClickHandler;
	private BroadcastReceiver onNotice = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("UserListFragment LocalBroadcast", "onReceive called " + intent.getAction());
			((ProfilesAdapter) getListAdapter()).substituteData(ProfilesKeeper.INSTANCE.getProfiles());
		}
	};

	public UserListFragment() {
		iff.addAction(ProfilesKeeper.State.READY.toString());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			profileClickHandler = (IProfileClickHandler) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IProfileClickHandler");
		}
		if (ProfilesKeeper.INSTANCE.getFetchDateTime() == null) {
			// data have never been loaded so first attempt should start automatically
			// other time user cam always run "refresh" from menu
			ProfilesKeeper.INSTANCE.startLoading(activity);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		float size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SMALL_AVATAR_DP_SIZE, getResources().getDisplayMetrics());
		setListAdapter(new ProfilesAdapter(getActivity(), ProfilesKeeper.INSTANCE.getProfiles(), size, profileClickHandler));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(onNotice);
		super.onPause();
	}

	@Override
	public void onResume() {
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(onNotice, iff);
		super.onResume();
	}

	public interface IProfileClickHandler {
		String AVATAR_EXTRA = "denys.salikhov.exam01.profileloader.AVATAR_EXTRA";

		void onOpenAvatar(UserModel user);

		void onOpenProfile(UserModel user);

		UserModel getLastUserClicked();
	}
}