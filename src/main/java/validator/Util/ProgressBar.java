package validator.Util;

import java.util.Arrays;

/**
* A progress bar that prints the current status (progress percent) 
* in the terminal using ASCII characters
* @author  Antonis Papaioannou  (antonis.papaioannou@outlook.com)
*/
public class ProgressBar {

	private int maxBarSize;    
	private double iconPercent;
	
	public ProgressBar() 
	{ 
		maxBarSize = 30;    
		iconPercent = 100.0 / maxBarSize;
	}

	/**
	* Prints the current progress percent using ASCII code characters
	* @param currStatus	the current state
	* @param total 		the total size/length
	*/
	public void printProgress(int currStatus, int total, String extraMsg) 
    {
        assert(currStatus <= total);

        int currProgressPercent = (currStatus * 100) / total;
        int filledBar = (int) (currProgressPercent / iconPercent);
        char emptyBarIcon = ' ';
        char filledBarIcon = '*';
        
        char bar[] = new char[maxBarSize];
        Arrays.fill(bar, emptyBarIcon);
        for (int i = 0; i < filledBar; ++i) {
            bar[i] = filledBarIcon;
        }

        System.out.print("\r[" + new String(bar) + "] " + currProgressPercent  + "%  " + extraMsg);

        if (currStatus == total) {
            System.out.print("\n");
        }
    }
}
