package validator.IO;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ResultLogger 
{
	BufferedWriter bw = null;
	String outFilename;
	
	public ResultLogger() 
	{
		outFilename = "validationResults.out";
		try {
			bw = new BufferedWriter(new FileWriter(outFilename, false));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public ResultLogger(String outFilename) 
	{
		this.outFilename = outFilename;
		try {
			bw = new BufferedWriter(new FileWriter(outFilename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void log(String msg)
	{
		try {
			bw.write(msg + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close()
	{
		if (bw == null) {
			return;
		}

		try {
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
