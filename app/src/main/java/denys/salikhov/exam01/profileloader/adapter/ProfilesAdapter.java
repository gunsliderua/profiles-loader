package denys.salikhov.exam01.profileloader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import denys.salikhov.exam01.profileloader.utils.ImageLoader;
import denys.salikhov.exam01.profileloader.R;
import denys.salikhov.exam01.profileloader.model.UserModel;
import denys.salikhov.exam01.profileloader.ui.UserListFragment;

public class ProfilesAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<UserModel> data;
	private String imageSizeUrlTail;
	private UserListFragment.IProfileClickHandler profileClickHandler;

	public ProfilesAdapter(Context context, List<UserModel> data, float imageSize, UserListFragment.IProfileClickHandler profileClickHandler) {
		this.data = data;
		this.imageSizeUrlTail = context.getString(R.string.image_size_url_tail, (int) imageSize);
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.profileClickHandler = profileClickHandler;
	}

	public void substituteData(List<UserModel> newdata) {
		data = newdata;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.profile_row, null);
			holder.tvLogin = (TextView) convertView.findViewById(R.id.tv_profile_login);
			holder.tvProfileLink = (TextView) convertView.findViewById(R.id.tv_profile_link);
			holder.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_profile_avatar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final UserModel user = data.get(position);
		holder.tvLogin.setText(user.getLogin());
		holder.tvProfileLink.setText(user.getProfileLink());
		ImageLoader.INSTANCE.loadBitmap(user.getAvatarBaseLink() + imageSizeUrlTail, holder.ivAvatar, true);
		convertView.setClickable(true);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				profileClickHandler.onOpenAvatar(user);
			}
		});
		return convertView;
	}

	public static class ViewHolder {
		public TextView tvLogin;
		public TextView tvProfileLink;
		public ImageView ivAvatar;
	}

}
