import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application. 
 * "World of Zuul" is a very simple, text based adventure game.  
 *
 * A "Room" represents one location in the scenery of the game.  It is 
 * connected to other rooms via exits.  For each existing exit, the room 
 * stores a reference to the neighboring room.
 * 
 * @author  Michael Kölling and David J. Barnes
 * @version 2011.08.08
 */

public class Room 
{
    private String description;
    private HashMap<String, Room> exits;        // stores exits of this room.
    private boolean locked;
    private ArrayList<Item> items;

    /**
     * Create a room described "description". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * @param description The room's description.
     */
    public Room(String description) 
    {
        this.description = description;
        exits = new HashMap<String, Room>();
        locked = false;
        items = new ArrayList<Item>();
    }

    public void addItem(Item item)
    {
        for (Item i : items)
            if (i.getName().equals(item.getName()))
                //throw new Exception("Item already exists, specify a unique name!");
                return;
        items.add(item);
    }

    public void removeItem(String name)
    {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).getName().equals(name)) {
                items.remove(i);
                break;
            }
    }
    
    public Item getItem(String name)
    {
        for (Item item : items)
            if (item.getName().toLowerCase().equals(name.toLowerCase()))
                return item;
        return null;
    }
    
    /**
     * Define an exit from this room.
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) 
    {
        exits.put(direction, neighbor);
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    /**
     * @return The short description of the room
     * (the one that was defined in the constructor).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Return a description of the room in the form:
     *     You are in the kitchen.
     *     Exits: north west
     * @return A long description of this room
     */
    public String getLongDescription()
    {
        return "You are " + description + ".\n" + getExitString();
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * @return Details of the room's exits.
     */
    private String getExitString()
    {
        if (exits.size() == 0)
            return "";
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    public boolean getLocked()
    {
        return locked;
    }

    public void examine()
    {
        int arraySize = items.size();
        if (arraySize == 0) {
            System.out.println("There is nothing here!");
            return;
        }

        String text = "You can see ";
        if (arraySize == 1)
            text += items.get(0).getNameWithPrefix().toLowerCase();
        else if (arraySize > 1) {
            for (int i = 0; i < arraySize - 1; i++) {
                text += items.get(i).getNameWithPrefix().toLowerCase();
                if (i + 1 < arraySize - 1)
                    text += ", ";
                else
                    text += " and ";
            }
            text += items.get(arraySize - 1).getNameWithPrefix().toLowerCase();
        }
        System.out.println(text + ".");
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) 
    {
        return exits.get(direction);
    }
}

