package scraper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SupportPage extends ArrayList<Post> {
	private static final long serialVersionUID = -5858917927528574354L;

	public String title;
	public String saveTitle;

	public SupportPage(String title) {
		this.title     = title;
		this.saveTitle = (title.replaceAll("[\\\\/:*?\"<>|]", "_") + ".txt").replaceAll(" ", "_");
		
		if(this.saveTitle.length() > 200) {
			this.saveTitle = this.saveTitle.substring(0, 199);
		}
	}
	
	/**
	 * Writes this thread to a specified location.
	 * @param path
	 */
	public void writeToFile(File path) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.getAbsolutePath() + "\\" + saveTitle))) {
			String content = this.toString();
			bw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		String toReturn = "##TITLE##" + title + "\n";		

		int i = 1;
		
		for(Post post : this) {
			if(i == 0) {
				toReturn = toReturn + post.getUsername() + "\n\n" + post.getPost() + "\n";
			} else {
				toReturn = toReturn + "#=#=#=#" + "\n" + post.getUsername() + "\n\n" + post.getPost() + "\n";
			}
		}


		return toReturn;
	}

}
