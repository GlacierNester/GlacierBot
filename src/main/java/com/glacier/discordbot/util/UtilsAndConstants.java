package com.glacier.discordbot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import com.glacier.discordbot.commands.CommandHandler;
import com.glacier.discordbot.lavaplayer.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class UtilsAndConstants {
	public static String BOT_PREFIX = "~";
	private static final String PROPERTIES_FILENAME = "discordbot.properties";	
	public static final String BEGINNING_PIECE_OF_URL = "http://www.youtube.com/watch?v=";
	public static Properties properties = setupProperties();
	//public static Logger logger = LoggerFactory.getLogger(App.class);
	//I'm going to persist in using system.err for my logging because I don't need anything special
	//additionally adding this logger didn't solve the "Defaulting to slf4j" didn't fix anything
	//so I'm not going to include it, just leave this commented in line as a reminder to sort out how to fix this
	
	public static void setupLogFiles()
	{
		setOutToLogFile();
		setErrorToLogFile();
	}
	
	public static Properties setupProperties()
	{
		Properties properties = new Properties();
        try {
        	ClassLoader classLoader = UtilsAndConstants.class.getClassLoader();
        	File propertiesFile = new File(classLoader.getResource(PROPERTIES_FILENAME).getFile());
            InputStream in = new FileInputStream(propertiesFile);
            properties.load(in);
            return properties;
        } catch (IOException e) {
            System.err.println("There was an error reading " + UtilsAndConstants.PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage() + " at " + getCurrentTimestamp());
            return null;
        }
	}
	
	
	private static void setOutToLogFile() {
		// TODO Auto-generated method stub
		File logFolder = new File("C:\\Glacier Nester\\logs");
    	File file = null;
    	if(!logFolder.exists())
    	{
    		logFolder.setWritable(true);
    		if(logFolder.mkdirs())
    		{
    			file = new File("C:\\Glacier Nester\\logs\\GlacierBot.log");
    		}
    	}
    	else
    	{
    		file = new File("C:\\Glacier Nester\\logs\\GlacierBot.log");
    	}
    	try {
	    	FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setOut(ps);
			System.out.println("Started GlacierBot at " + getCurrentTimestamp());
    	}
    	catch(IOException ex)
    	{
    		System.err.println("Oh dear, making the log failed. That's an issue!");
    	}
	}


	private static void setErrorToLogFile() {
		
		File logFolder = new File("C:\\Glacier Nester\\logs");
    	File file = null;
    	if(!logFolder.exists())
    	{
    		logFolder.setWritable(true);
    		if(logFolder.mkdirs())
    		{
    			file = new File("C:\\Glacier Nester\\logs\\GlacierBotErrors.log");
    		}
    	}
    	else
    	{
    		file = new File("C:\\Glacier Nester\\logs\\GlacierBotErrors.log");
    	}
    	try {
	    	FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setErr(ps);
			System.err.println("Started GlacierBot at " + getCurrentTimestamp());
    	}
    	catch(IOException ex)
    	{
    		System.out.println("Oh dear, making the log failed. That's an issue!");
    	}
	}
	
	/**
	 * Creates a discord Client (what's needed to send messages/interact) for the token it's passed
	 * @param token
	 * @return A DiscordClient for the bot to work with
	 */
    public static IDiscordClient getBuiltDiscordClient(String token){

        return new ClientBuilder()//build a client
                .withToken(token)//with the token it's been passed
                .withRecommendedShardCount()//and the reccomended shard count
                //look at the docs for what shards are in detail
                //but the short version is that they manage how much threads your bot can use
                //based on how many discord servers it's in
                .build();

    }
    /**
     * a function that, given a channel and a message, sends that message to the channel
     * I'm considering including a command to set a default channel which is stored as a constant above
     * In addition to that, a function to change the prefix, but I need to make this work simply first
     * @param channel The channel to send the message to
     * @param message the message to send to the channel
     */
    public static void sendMessage(IChannel channel, String message){

    	//arrow functions don't always make much sense to me
    	//but I think what's going down here is, we pass RequestBuffer's request method a function to run
    	//which, in this case, sends a message or passes information to the log file
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (DiscordException e){
                System.err.println("Message could not be sent, timestamp " + getCurrentTimestamp() + " and following trace");
                e.printStackTrace();
            }
            catch(NullPointerException ex)
            {
            	System.err.println("NullPointerException, timestamp " + getCurrentTimestamp() + " and following trace");
                ex.printStackTrace();
            }
        });

    }

	public static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
        long guildId = guild.getLongID();
        GuildMusicManager musicManager = CommandHandler.musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(new DefaultAudioPlayerManager());
            CommandHandler.musicManagers.put(guildId, musicManager);
        }
        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }

	public static String getCurrentTimestamp() {
		return DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss").format(LocalDateTime.now());
	}
}
