package denys.salikhov.exam01.profileloader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import denys.salikhov.exam01.profileloader.model.ProfilesKeeper;
import denys.salikhov.exam01.profileloader.R;
import denys.salikhov.exam01.profileloader.model.UserModel;


public class UserListActivity extends ActionBarActivity implements UserListFragment.IProfileClickHandler {
	private boolean isLandscape;
	private UserModel lastClickedUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			UserModel user = new UserModel();
			user.setLogin(savedInstanceState.getString(LOGIN_EXTRA));
			user.setAvatarBaseLink(savedInstanceState.getString(AVATAR_EXTRA));
			user.setProfileLink(savedInstanceState.getString(PROFILE_EXTRA));
			lastClickedUser = user;
		}
		setContentView(R.layout.activity_user_list);
		Log.d("UserListActivity", "onCreate");
		isLandscape = getResources().getBoolean(R.bool.orientation_landscape);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_user_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			ProfilesKeeper.INSTANCE.startLoading(this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (lastClickedUser != null) {
			outState.putString(LOGIN_EXTRA, lastClickedUser.getLogin());
			outState.putString(AVATAR_EXTRA, lastClickedUser.getAvatarBaseLink());
			outState.putString(PROFILE_EXTRA, lastClickedUser.getProfileLink());
		}
	}

	@Override
	public void onOpenAvatar(UserModel user) {
		lastClickedUser = user;
		if (isLandscape) {
			// we're in two-pane layout...
			BigAvatarFragment bigAvatarFragment = (BigAvatarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_big_avatar);
			if (bigAvatarFragment != null) {
				bigAvatarFragment.updateAvatar(user.getAvatarBaseLink());
			}
		} else {
			// single-pane
			Intent startintent = new Intent(this, AvatarActivity.class);
			startintent.putExtra(AVATAR_EXTRA, user.getAvatarBaseLink());
			startActivity(startintent);
		}

	}

	@Override
	public void onOpenProfile(UserModel user) {
	// reserved for future enhancements like handling profile opening
	}

	@Override
	public UserModel getLastUserClicked() {
		return lastClickedUser;
	}

}
