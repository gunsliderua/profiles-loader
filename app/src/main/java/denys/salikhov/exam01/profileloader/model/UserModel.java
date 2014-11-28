package denys.salikhov.exam01.profileloader.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {
	@SerializedName("login")
	private String login;
	@SerializedName("html_url")
	private String profileLink;
	@SerializedName("avatar_url")
	private String avatarBaseLink;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getProfileLink() {
		return profileLink;
	}

	public void setProfileLink(String profileLink) {
		this.profileLink = profileLink;
	}

	public String getAvatarBaseLink() {
		return avatarBaseLink;
	}

	public void setAvatarBaseLink(String avatarBaseLink) {
		this.avatarBaseLink = avatarBaseLink;
	}
}
