package scraper;

public class Post {

	private String username;
	private String post;
	
	public Post(String username, String post) {
		this.username = username;
		this.post = post;
		
		String reassemble = "";
		
		for(String s : post.split("\n")) {
			if(s.equals("- show quoted text -")) {
				this.post = reassemble;
				break;
			} else {
				reassemble = reassemble + "\n" + s;
			}
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPost() {
		return post;
	}
	
}
