package com.glacier.discordbot.commands;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.glacier.discordbot.lavaplayer.GuildMusicManager;
import com.glacier.discordbot.model.Command;
import com.glacier.discordbot.util.UtilsAndConstants;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;

public class CommandHandler {
	
	// A static map of commands mapping from command string to the functional impl
    private static Map<String, Command> commandMap = new HashMap<>();
    //the music managers map is put here so that all the commands can get to it
    public static final Map<Long, GuildMusicManager> musicManagers  = new HashMap<>();
	
    //TODO: set up this command map to contain the commands
    
    static
    {
    	//I suppose since commandMap has to be static, 
    	//the placement of said commands have to be static
    	commandMap.put("say", new TalkBack());
    	commandMap.put("join", new JoinUser());
    	commandMap.put("yt", new OrdinaryYoutubePlayer());
    	//note that the yt command takes youtube URLS
    	commandMap.put("skip", new SkipTrack());
    	/*
    	 * the current plan for the actual play my videos command is pulling in the youtube api
    	 * since you can limit your searches to specific users with that
    	 * in order to get the urls that we then pass on to lavaplayer
    	 * alternatively we can do it the hard way, i.e. have a json file with each title as a key and url as a value
    	 * but that's a lot of manual work and would require almost literally daily updates which is rough
    	 * so let's hope we can use the youtube api
    	 */
    }
    
	@EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
		//TODO: add slf4j to point all the logging at the intended file, since this doesn't like me using system.err
        //If there's an error, we'll be outputting about it to the log file
		//At least initially
		//Eventually, I'll look into messaging the user which tried to make the command 
		//and send them their error/a help dialog
		
        //split the message into the command and the arguments
		String[] argArray = event.getMessage().getContent().split(" ");

		//if there's not anything in the message, stop the method
		//It's a bit odd that the example bot uses returns to stop the method despite it being a void
		//I had no idea that's a technique until now, and I like it, a lot!
		//might start using this in my own code, actually
        if(argArray.length == 0)
            return;

        //if the "command" doesn't start with the right prefix, don't do anything with it
        if(!argArray[0].startsWith(UtilsAndConstants.BOT_PREFIX))
            return;

        //remove the prefix from the command
        String commandStr = argArray[0].substring(UtilsAndConstants.BOT_PREFIX.length());

        //rather than trying to handle the array with the command and args in it
        //we're tossing the rest of them into an arraylist to make it easier to handle
        List<String> argsList = new ArrayList<>(Arrays.asList(argArray));
        argsList.remove(0); // Remove the command

        //this is INCREDIBLY clever
        //essentially, we get the command's code from the map of all commands
        //and then call its runCommand method right here
        //I personally would have like, done some really inefficient stuff with a bunch of if/elses or a switch
        //but this is much better
        if(commandMap.containsKey(commandStr))
            commandMap.get(commandStr).runCommand(event, argsList);
        else
        {
        	System.err.println("Failed command " + commandStr + " at " + DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss").format(LocalDateTime.now()));
        }
    }
}
