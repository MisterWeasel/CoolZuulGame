/**
 * Write a description of class Item here.
 * 
 * @author Simon Jäger
 */
public class Item
{
    private String name;
    private String prefix;
    
    public String getName()
    {
        return name;
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public String getNameWithPrefix()
    {
        return prefix + " " + name;
    }
    
    public Item(String name, String prefix)
    {
        this.name = name;
        this.prefix = prefix;
    }
}
