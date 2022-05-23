package antonis.Redis;

/**
 * Holds the status and a message description of the validation resykt
 * 
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */
public class ValidationResult 
{
	public enum Status {
		OK,
		SLOT_MOVED,
		VALUE_MISSMATCH,
		SLOT_UNKNOWN
	}

	private String message;
	private Status status;

	public ValidationResult(Status status, String message) 
	{
		this.status = status;
		this.message = message;
	}

	public Status getStatus()
	{
		return status;
	}

	public String getMessage()
	{
		return message;
	}
}

