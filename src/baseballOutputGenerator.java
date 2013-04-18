import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class baseballOutputGenerator {
	private static final int numberOfAtBats = 4000;
	private static final String destinationPath = "/Users/Justin/gitProjects/440-HW3/src/baseballData.txt";
	
	public static void main(String[] args) {
		baseballOutputGenerator bog= new baseballOutputGenerator();
	}
	
	private String[] players = {"Andrew", "Alan", "James", "Martin", "William", "Maurice", "Gerry", "Gary", 
			"Earnest", "Jake", "Kevin", "Stuart", "Lawrence", "Paul", "Bill", "Gregorio", "Jared", "Zimmerman",
			"John", "Langston", "Henry", "Puckett", "Larry", "Schwartz", "Gregory", "Young", "Arthur", "Towles", "Shaffer", 
			"David", "Johnson", "Andres", "Carmine", "Sean", "Ellis", "Hayden", "Vincent", "Prince", "Lloyd", "Fletcher",
			"Bradley", "Werner", "Rupert", "Rolando"};
	
	private String[] outcomes = new String[100];
	
	public baseballOutputGenerator() {
		for (int i = 0; i < 72; i++) {
			outcomes[i] = "out";
		}
		for (int i = 0; i < 15; i++) {
			outcomes[i + 72] = "single";
		}
		for (int i = 0; i < 7; i++) {
			outcomes[i + 87] = "double";
		}
		outcomes[94] = "triple";
		for (int i = 0; i < 5; i++) {
			outcomes[94 + i] = "homer";
		}
		
		OutputStream os = null;
		try {
			os = Files.newOutputStream(Paths.get(destinationPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < numberOfAtBats; i++) {
			String batter = players[(int)(Math.random()*players.length)];
			String outcome = outcomes[(int)(Math.random()*outcomes.length)];
			String onFile;
			if (i % 10 == 0 && i != 0) {
				onFile = batter + "-" + outcome + " " + '\n';
			}
			else {
				onFile = batter + "-" + outcome + " ";
			}
			byte[] bytes = new byte[onFile.length()];
			for (int j = 0; j < bytes.length; j++) {
				bytes[j] = (byte) onFile.charAt(j);
			}
			
			try {
				os.write(bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
