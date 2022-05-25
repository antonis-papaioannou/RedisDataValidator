package validator.IO;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import validator.KeyValue;

/**
 * This is a test data generator
 */
public class SampleDataProducer 
{
	//TEST
	public static void writeSampleDataFile() {
		ArrayList<KeyValue> kvs = new ArrayList<>();
		kvs.add(new KeyValue("name", "Antonis"));
		kvs.add(new KeyValue("database", "Redis"));
		kvs.add(new KeyValue("computer", "scienCe"));
		kvs.add(new KeyValue("repository", "github"));

		try (DataOutputStream out = new DataOutputStream(new FileOutputStream("sampleData.dat"))) {
			for (KeyValue kv : kvs) {
				out.writeInt(kv.getKey().length());
				out.writeInt(kv.getValue().length());
				out.writeBytes(kv.getKey());
				out.writeBytes(kv.getValue());
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
