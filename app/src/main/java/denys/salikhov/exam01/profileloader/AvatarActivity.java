package denys.salikhov.exam01.profileloader;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class AvatarActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_avatar);
		String avatarURL = getIntent().getStringExtra(UserListFragment.IProfileClickHandler.AVATAR_EXTRA);
		if (avatarURL != null) {
			BigAvatarFragment bigAvatarFragment = (BigAvatarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_big_avatar);
			bigAvatarFragment.updateAvatar(avatarURL);
		}
	}


}
