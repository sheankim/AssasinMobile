package cs4720.asparagus.assasinmobile;

public class Target {
	public int playerId;
	public int gameId;
	public int targetId;
	
	public String toString(){
		return "Player ID:"+playerId+" Game ID:"+gameId+" Target ID:"+targetId;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public int getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	
	
}
