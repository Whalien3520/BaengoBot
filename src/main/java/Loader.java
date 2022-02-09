import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Loader {
    Properties properties;
    InputStream inputStream;
    String token, font, rushia;

    public Loader(String prop) throws IOException {
        try {
            properties = new Properties();

            inputStream = this.getClass().getClassLoader().getResourceAsStream(prop);
            if (inputStream != null) {properties.load(inputStream);}
            else {throw new FileNotFoundException("Property file 'config.properties' not found. Please add file and change the path name accordingly in Main.java.");}

            token=properties.getProperty("token");
            font=properties.getProperty("font");
            rushia=properties.getProperty("board");
        }
        catch (Exception e) {System.out.println("Exception: " + e);}
        finally {inputStream.close();}

    }

    public String getToken() {return token;}
    public String getFont() {return font;}
    public String getRushia() {return rushia;}
}
