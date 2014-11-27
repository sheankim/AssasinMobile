package cs4720.asparagus.assasinmobile;

import com.google.gson.annotations.SerializedName;

public class Game {
	@SerializedName("id")
	public int id;
	@SerializedName("title")
	public String title;
	@SerializedName("organization")
	public String organization;
	public int admin_id;
	public boolean game_started;

	public String toString(){
		return "Game Name: " + title +", Game Organizer: " + organization;
	}
	
	/**@Override
	public String toString() {
		return "Game [id=" + id
				+ ", title=" + title + ", organization=" + organization
				+"]";
	}**/


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getOrganization() {
		return organization;
	}


	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public int getAdmin_id() {
		return admin_id;
	}

	public void setAdmin_id(int admin_id) {
		this.admin_id = admin_id;
	}

	public boolean isGame_started() {
		return game_started;
	}

	public void setGame_started(boolean game_started) {
		this.game_started = game_started;
	}
	
}
