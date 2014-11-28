package denys.salikhov.exam01.profileloader.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import denys.salikhov.exam01.profileloader.R;

//enum singleton
public enum ImageLoader {
	INSTANCE;
	private final static String TAG = "ImageLoader";
	final int cacheSize = 100;
	private final LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize);

	//I will use this map in oder to keep last requested URL for particular ImageView
	// After image is downloaded successfully and before we put it inside original intended ImageView we need to check if
	// there were another subsequent requests for that ImageView (because ListView reuses row views)
	private final Map<View, String> lastContentForView = new HashMap<View, String>(cacheSize);

	//There is possible situation when loadBitmap is called multiple times for same URL while image is still not in the cache and previous AsyncTask is running.
	//We want to avoid extra traffic consumption so we will use this Set to keep track of current image downloading tasks
	// and will spawn new tasks only if such URL is not currently downloading
	private final Set<String> currentlyDownloading = new HashSet<String>();

	public void loadBitmap(String imageKey, ImageView imageView, boolean isSmallPlaceholder) {
		final Bitmap bitmap = mMemoryCache.get(imageKey);
		lastContentForView.put(imageView, imageKey);
		if (bitmap != null) {
			Log.d(TAG, "bitmap found in cache");
			imageView.setImageBitmap(bitmap);
		} else {
			Log.d(TAG, "Not found, will download");
			if (isSmallPlaceholder) {
				imageView.setImageResource(R.drawable.profile_placeholder_small);
			} else {
				imageView.setImageResource(R.drawable.profile_placeholder_big);
			}
			if (!currentlyDownloading.contains(imageKey)) {
				currentlyDownloading.add(imageKey);
				new ImageLoaderAsyncTask(imageView).execute(imageKey);
			}
		}
	}

	private static class ImageLoaderAsyncTask extends AsyncTask<String, Void, Bitmap> {
		private final ImageView container;

		public ImageLoaderAsyncTask(ImageView imageView) {
			container = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String key = params[0];
			Bitmap bmp = null;
			try {
				bmp = BitmapFactory.decodeStream(new URL(key).openConnection().getInputStream());
				if (bmp != null) {
					ImageLoader.INSTANCE.mMemoryCache.put(key, bmp);
				}
			} catch (MalformedURLException e) {
				Log.e(TAG, "Malformed URL: " + e.getMessage(), e);
			} catch (IOException e) {
				Log.e(TAG, "ImageLoaderAsyncTask IO exception: " + e.getMessage(), e);
			}
			ImageLoader.INSTANCE.currentlyDownloading.remove(key);
			if (ImageLoader.INSTANCE.lastContentForView.get(container) == key) {
				return bmp;
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				container.setImageBitmap(bitmap);
			}
		}
	}
}
