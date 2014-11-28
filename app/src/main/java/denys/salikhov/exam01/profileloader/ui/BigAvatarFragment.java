package denys.salikhov.exam01.profileloader.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import denys.salikhov.exam01.profileloader.utils.ImageLoader;
import denys.salikhov.exam01.profileloader.R;
import denys.salikhov.exam01.profileloader.model.UserModel;

public class BigAvatarFragment extends Fragment {
	public static final int BIG_AVATER_DP_SIZE = 600;
	String imageSizeUrlTail;

	// We use that avatar reference to refresh view when it will be ready
	// We need it because there could be a situation when updateAvatar(...) is called, but ImageView was not yet created.
	String lastAvatarBaseLinkRequested;
	ImageView ivAvatar;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			UserListFragment.IProfileClickHandler profileClickHandler = (UserListFragment.IProfileClickHandler) activity;
			UserModel user = profileClickHandler.getLastUserClicked();
			if (user != null) {
				updateAvatar(user.getAvatarBaseLink());
			}
		} catch (ClassCastException e) {
			// if we failed to cast - thats OK, it means that fragment was used in single-pane mode
			// so we just do not expect parent activity to provide us "lastClickedUser" and
			// updateAvatar expected to be called by other party (ie by Activity)
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_big_avatar, container, false);
		ivAvatar = (ImageView) rootView.findViewById(R.id.iv_big_avatar);
		int requestImageSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BIG_AVATER_DP_SIZE, getResources().getDisplayMetrics());
		this.imageSizeUrlTail = getString(R.string.image_size_url_tail, requestImageSize);
		if (lastAvatarBaseLinkRequested != null) {
			updateAvatar(lastAvatarBaseLinkRequested);
		}
		return rootView;
	}

	public void updateAvatar(String avatarBaseLink) {
		if (ivAvatar != null) {
			ImageLoader.INSTANCE.loadBitmap(avatarBaseLink + imageSizeUrlTail, ivAvatar, false);
		} else {
			lastAvatarBaseLinkRequested = avatarBaseLink;
		}
	}
}
