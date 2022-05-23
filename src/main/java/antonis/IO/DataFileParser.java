package antonis.IO;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import antonis.KeyValue;
import antonis.Util.Util;

/**
 * Reads Key Value pairs from binary files
 * Data are in binary format.
 * 
 * @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
 */

public class DataFileParser {
	private DataInputStream inDataStream = null;
	private String dataFilename;
	private int filesizeBytes = -1;

	public DataFileParser(String inputFileName)
	{
		try {
			this.dataFilename = inputFileName;
			FileInputStream fis = new FileInputStream(dataFilename);
			filesizeBytes = fis.available();
			inDataStream = new DataInputStream(new BufferedInputStream(fis));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Read a Key Value pair. Each KV has the following format
	 * <key_size><value_size><key><value>
	 * key_size and value_size are integers and denote the size of following key and value.
	 * The, the parser reads the following <key_size> bytes for the key and the 
	 * <value_size> bytes for the value.
	 * 
	 * @return a KeyValue object (@see antonis.KeyValue) containing the KV data read
	 */
	public KeyValue readKV()
	{
			try {
				if (inDataStream.available() <= 0) {
					return null;
				}
				int key_size = inDataStream.readInt();
				int val_size = inDataStream.readInt();
				byte[] keyBytes = new byte[key_size];
				byte[] valBytes = new byte[val_size];
				inDataStream.readFully(keyBytes);
				inDataStream.readFully(valBytes);
				String key = new String(keyBytes);
				String value = new String(valBytes);

				// sanity checks
				if (key.length() != key_size) { 
					System.err.println(Util.pointInCode() + " Error with key size: " + key);
				}
				if (value.length() != val_size) { 
					System.err.println("Error with value size: " + value); 
				}

				return new KeyValue(key, value);
			} catch (IOException e) {
				e.printStackTrace();
				close();
				System.exit(0);
			} 
			return null;
	}

	/**
	 * @return the file size in bytes
	 */
	public int getFileSizeBytes() 
	{
		return this.filesizeBytes;
	}

	public String getDataFilename() 
	{
		return dataFilename;
	}

	/**
	 * Close the DataInputStream from the data file
	 */
	public void close() {
		if (inDataStream != null) {
			try {
				inDataStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}