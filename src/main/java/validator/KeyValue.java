package validator;

/**
 * Represents Key Value data
 * 
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */
public class KeyValue 
{
	private String key, value;
	private int keySize, valSize;

	public KeyValue() {
		this.key 	= null;
		this.value 	= null;
		this.keySize = 0;
		this.valSize = 0;
	}

	public KeyValue(String key, String value)
	{
		this.key 	= key;
		this.value 	= value;
		this.keySize = key.length();
		this.valSize = value.length();
	}

	public String getKey()
	{
		return this.key;
	}

	public String getValue()
	{
		return this.value;
	}

	/**
	 * @return the total size of the Key Value in bytes 
	 * (includes key, value, key_size and value_size)
	 */
	public int size()
	{
		// 4 + 4 is used to represent the size of the integers for the 
		// size of key and the size of the value
		return (key.length() + value.length() + 4 + 4);
	}

	public String toString() 
	{
		return (keySize + ":" + key + "," + valSize + ":" + value);
	}
}
