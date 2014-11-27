package cs4720.asparagus.assasinmobile;

public class User {
	public int id;
	public String name;
	public String password;

	
	public String toString(){
		return "User [id = "+id+"," +
				" name = "+name+"," +
						" password = "+password+"]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


}
