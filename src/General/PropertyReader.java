package General;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
	public static String getProperty(String property) {
		Properties prop = new Properties();
		FileInputStream input = null;
		try {
		input = new FileInputStream("config.properties");
		prop.load(input);
		return prop.getProperty(property);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
