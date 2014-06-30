package snapshot;



/**
 * Interface for all ImageMagick Commands
 * @author kyleb2
 */
public interface IMCommand {
    
    public boolean execute(String imageInPath, String imageOutPath);
    
}
