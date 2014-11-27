package cs4720.asparagus.assasinmobile;

public class Player {
	public int id;
	public int game_id;
	public int user_id;
	public String alias;
	public boolean is_alive;
	public int target;
	public int killer;
	public int killed_by;

	
	/**public String toString(){
		return "Player [id = "+id+"," +
				" game_id = "+game_id+"," +
						" user_id = "+user_id+", alias = "+alias+", is_alive = "+
				is_alive+", killed_by = "+killed_by+"]";
	}**/
	public String toString(){
		String deathStatus;
		if(is_alive){
			deathStatus = "Alive";
		}
		else{
			deathStatus = "Dead";
		}
		return "Player Name: "+alias+" , Death Status: "+deathStatus;
	}
	
	public int getPlayerId(){
		return id;
	}
	
	public int getGameId(){
		return game_id;
	}
	
	public int getUserId(){
		return user_id;
	}
	
	public String getAlias(){
		return alias;
	}
	
	public boolean getis_alive(){
		return is_alive;
	}
	
	public int getTarget() {
		return target;
	}
	
	public int getKiller() {
		return killer;
	}
	
	public int getkilled_by(){
		return killed_by;
	}
	
	public void setPlayerId(int id){
		this.id = id;
	}
	
	public void setGameId(int game_id){
		this.game_id = game_id;
	}
	
	public void setUserId(int user_id){
		this.user_id = user_id;
	}
	
	public void setAlias(String alias){
		this.alias = alias;
	}
	
	public void setis_alive(boolean is_alive){
		this.is_alive = is_alive;
	}
	
	public void setTarget(int target) {
		this.target = target;
	}
	
	public void setKiller(int killer) {
		this.killer = killer;
	}
	
	public void setkilled_by(int killed_by){
		this.killed_by = killed_by;
	}


}
