package antonis.Redis;

/**
 * A custom exection that is raised when a standalone redis client discovers 
 * that a key-slot has been moved to another node
 * 
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */
public class MovedSlotException extends RuntimeException 
{
	public MovedSlotException(String errorMessage, Throwable err) 
	{
        super(errorMessage, err);
    }
}
