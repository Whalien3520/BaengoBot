import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageBuilder;

import java.io.File;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {

        Loader l = new Loader("config.properties");
        DiscordApi api = new DiscordApiBuilder().setToken(l.getToken()).login().join();

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().equalsIgnoreCase("!rushia")) {
                new MessageBuilder()
                        .addAttachment(new File(l.getRushia()))
                        .send(event.getChannel());
            }
        });

        api.addMessageCreateListener(event -> {
            if (event.getMessageContent().contains("!baengo")) {
                String[] array = event.getMessageContent().split("\r?\n");
                Bingo b=new Bingo(array);
                if(array.length<26)
                    event.getChannel().sendMessage("Not enough lines!");
                else if (!b.getRushia(l.getRushia()))
                    event.getChannel().sendMessage("Failed to get board.");
                else if (!b.getFont(l.getFont()))
                    event.getChannel().sendMessage("Failed to get font. Please download a font of your choice, ");
                else if (!b.generateBoard())
                    event.getChannel().sendMessage("Cell " + b.failCell() + " failed to generate.");
                else if (!b.writeImage())
                    event.getChannel().sendMessage("Failed to output file.");
                else {
                    b.writeImage();
                    new MessageBuilder()
                            .addAttachment(new File("output.png"))
                            .send(event.getChannel());
                }
            }
        });
    }
}