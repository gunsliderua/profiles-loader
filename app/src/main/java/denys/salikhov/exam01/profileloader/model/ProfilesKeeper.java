package denys.salikhov.exam01.profileloader.model;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.protocol.HTTP;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import denys.salikhov.exam01.profileloader.R;

//  Enum singleton by Joshua Bloch explained this approach in his Effective Java Reloaded talk at Google I/O 2008
//  I will use it as a long-live object to keep downloaded data and reflect the current state of data readiness
public enum ProfilesKeeper {
	INSTANCE;
	private final String TAG = "ProfilesKeeper";
	private List<UserModel> profiles = new ArrayList<UserModel>();
	private Context applicationContext;
	private State state = State.READY;
	private String fetchDateTime;

	public List<UserModel> getProfiles() {
		return profiles;
	}

	public String getFetchDateTime() {
		return fetchDateTime;
	}

	private void setFetchDateTime(String fetchDateTime) {
		this.fetchDateTime = fetchDateTime;
	}

	private void setApplicationContext(Context context) {
		this.applicationContext = context.getApplicationContext();
	}

	private void provideRefreshedData(List<UserModel> data) {
		if (data != null) {
			profiles = data;
		}
	}

	private void updateObservers(State state) {
		Log.d(TAG, "updateObservers " + state);
		Intent intent = new Intent(state.toString());
		LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
	}

	public void startLoading(Context context) {
		setApplicationContext(context);
		if (state == State.LOADING) return;
		setState(State.LOADING);
		new LoadingProfilesAsyncTask().execute(applicationContext.getString(R.string.github_users));
	}

	public State getState() {
		return state;
	}

	private void setState(State state) {
		this.state = state;
		updateObservers(state);
	}

	public static enum State {
		READY, LOADING, FAILED
	}

	private static class LoadingProfilesAsyncTask extends AsyncTask<String, Void, Boolean> {
		private final String TAG = "LoadingProfilesAsyncTask";

		@Override
		protected Boolean doInBackground(String... urls) {
			URL url;
			HttpURLConnection urlConnection = null;
			try {
				url = new URL(urls[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				ProfilesKeeper.INSTANCE.setFetchDateTime(urlConnection.getHeaderField(HTTP.DATE_HEADER));
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				ArrayList<UserModel> result = new Gson().fromJson(new InputStreamReader(in), new TypeToken<ArrayList<UserModel>>() {
				}.getType());
				ProfilesKeeper.INSTANCE.provideRefreshedData(result);
			} catch (Exception e) {
				Log.e(TAG, "Error during IO operation: " + e.getMessage());
				e.printStackTrace();
				return false;

			} finally {
				if (urlConnection != null) urlConnection.disconnect();
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				ProfilesKeeper.INSTANCE.setState(State.READY);
			} else {
				ProfilesKeeper.INSTANCE.setState(State.FAILED);
			}
		}
	}
}
