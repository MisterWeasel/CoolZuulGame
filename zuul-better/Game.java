/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2011.08.08
 */

import java.util.ArrayList;

public class Game 
{
    private final int MAX_DAYS = 1;
    private final int MAX_HOURS = 6;

    private Parser parser;
    private Room currentRoom;
    private Room beamRoom;
    private boolean finished;

    private int day;
    private int hour;

    private ArrayList<Item> inventory;
    private Room outside, theater, pub, lab, office;

    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
        finished = false;
        day = 1;
        hour = 1;
        inventory = new ArrayList<Item>();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        // create the rooms
        outside = new Room("outside the main entrance of the university");
        theater = new Room("in a lecture theater");
        pub = new Room("in the campus pub, it looks like there\n is no exit out of here... " + 
            "\nI need to find another way out");
        lab = new Room("in a computing lab");
        office = new Room("in the computing admin office");

        // initialise room exits
        outside.setExit("east", theater);
        outside.setExit("south", lab);
        outside.setExit("west", pub);

        theater.setExit("west", outside);

        //pub.setExit("east", outside);
        pub.setLocked(true);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);

        theater.addItem(new Item("Key", "A"));
        lab.addItem(new Item("Sword", "A"));
        lab.addItem(new Item("Orb", "An"));
        pub.addItem(new Item("Beamer", "A"));
        beamRoom = outside;

        currentRoom = outside;  // start game outside
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.

        finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly boring adventure game.");
        System.out.println("You have " + MAX_DAYS + (MAX_DAYS > 1 ? " days " : " day ") + " and " +
            MAX_HOURS + (MAX_HOURS > 1 ? " hours " : " hour ") + "before you die...");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        printTime();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            goRoom(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        else if (commandWord.equals("examine")) {
            currentRoom.examine();
        }
        else if (commandWord.equals("pickup") && command.getSecondWord() != null) {
            Item item = currentRoom.getItem(command.getSecondWord());
            if (item == null)
                System.out.println("There is no such item here!");
            else {
                if (item.getName().toLowerCase().equals("Orb".toLowerCase())) {
                    System.out.println("The magical orb teleports you to the outside!");
                    goRoom(outside);
                }
                else {
                    currentRoom.removeItem(item.getName());
                    System.out.println("You picked up " + 
                                       item.getNameWithPrefix().toLowerCase() + ".");
                    inventory.add(item);
                }
            }
        }
        else if (commandWord.equals("inventory")) {
            int arraySize = inventory.size();
            if (arraySize == 0) {
                System.out.println("There is nothing in your inventory!");
                return wantToQuit;
            }

            String text = "You have ";
            if (arraySize == 1)
                text += inventory.get(0).getNameWithPrefix().toLowerCase();
            else if (arraySize > 1) {
                for (int i = 0; i < arraySize - 1; i++) {
                    text += inventory.get(i).getNameWithPrefix().toLowerCase();
                    if (i + 1 < arraySize - 1)
                        text += ", ";
                    else
                        text += " and ";
                }
                text += inventory.get(arraySize - 1).getNameWithPrefix().toLowerCase();
            }
            System.out.println(text + ".");
        }
        else if (commandWord.equals("beam")) {
            Item beamer = null;
            for (Item item : inventory)
                if (item.getName().toLowerCase().equals("beamer".toLowerCase()))
                    beamer = item;
            if (beamer == null)
                System.out.println("You need to find the beamer before you can beam yourself!");
            else {
                if (beamRoom == currentRoom)
                    System.out.println("You are already in the beamers' charged room!");
                else {
                    System.out.println("Beamed!");
                    goRoom(beamRoom);
                }
            }
        }     
        else if (commandWord.equals("charge")) {
            Item beamer = null;
            for (Item item : inventory)
                if (item.getName().toLowerCase().equals("beamer".toLowerCase()))
                    beamer = item;
            if (beamer == null)
                System.out.println("You need to find the beamer before you can charge your beamer!");
            else {
                if (beamRoom == currentRoom)
                    System.out.println("Your beamer is already charged with the current room!");
                else {
                    System.out.println("Beamer charged with the current room!");
                    beamRoom = currentRoom;
                }
            }
        }
        else if (commandWord.equals("time")) {
            printTime();
        }
        // else command not recognised.
        return wantToQuit;
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        }
        else {
            if (nextRoom.getLocked()) {
                Item key = null;
                for (Item item : inventory)
                    if (item.getName().toLowerCase().equals("key".toLowerCase()))
                        key = item;
                if (key != null) {
                    System.out.println("The key was used to unlock the door...");
                    inventory.remove(key);
                    nextRoom.setLocked(false);
                }
                else {
                    System.out.println("The door is locked, find a key...");
                    return;
                }
            }

            hour++;
            if (hour > MAX_HOURS && day >= MAX_DAYS) {
                endGame();
                return;
            }
            else if (hour > MAX_HOURS) {
                hour %= MAX_HOURS;
                day++;
            }
            goRoom(nextRoom);
        }
    }

    private void goRoom(Room room)
    {
        printTime();
        currentRoom = room;
        System.out.println(currentRoom.getLongDescription());
    }

    private void printTime()
    {
        System.out.println("Day: " + day + " Hour: " + hour);
    }

    private void endGame()
    {
        System.out.println("Whatever, let's go home and make a real game!");
        finished = true;
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else {
            return true;  // signal that we want to quit
        }
    }
}
