/**
 * 
 */
package org.haion.tools.databuilders;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import org.haion.tools.databuilders.interfaces.client.IAionPakAccessor;

/**
 * @author Haron
 *
 */
public class Pak2ZipConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			//System.out.println("Usage: Pak2ZipConverter <aion-version> <pak-file> <zip-file> [plugin-name]\n  Supported Aion vrsions 1 or 2");
			System.out.println("Usage: Pak2ZipConverter <pak-file> [<zip-file>]");
			return;
		}
		
		int aionClientVersion = 2;
//		try {
//			aionClientVersion = Integer.parseInt(args[0]);
//		} catch (NumberFormatException e) {
//			System.out.println("ERROR: Cannot Cannot parse Aion version: " + args[0]);
//			return;
//		}
//		if (aionClientVersion != 1 && aionClientVersion != 2) {
//			System.out.println("ERROR: Unsupported Aion version: " + args[0] + ". Should be 1 or 2.");
//			return;
//		}
		String zipFilePath = null;
		if (args.length > 1)
			zipFilePath = args[1];
		else
			zipFilePath = args[0] + ".zip";

		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(zipFilePath);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot open output file: " + zipFilePath);
			return;
		}
		
		String pluginName = null;
//		if (args.length >= 4)
//			pluginName = args[3];
//		else
//			System.out.println("Using default Pak accessor.");
		
		IAionPakAccessor pakAccessor = Factory.getPakAccessor(pluginName);
		if (pakAccessor == null) {
			System.out.println("ERROR: Unknown Pak accessor: " + pluginName != null ? pluginName : "<default>");
			return;
		}
		
		try {
			System.out.println("Start converting file " + args[0] + " ...");
			pakAccessor.setPakFile(args[0], aionClientVersion);
			pakAccessor.convertToZip(outputStream);
			Set<String> pakFilesNamesSet = pakAccessor.getFilesName();
			System.out.println("Pak files:");
			for (String fileName : pakFilesNamesSet)
				System.out.println(fileName);
			
			System.out.println("Done.\nPak file successfully converted to the Zip file: " + zipFilePath);
		} catch (Exception e) {
			System.out.println("ERROR: Cannot convert Pak file to Zip.\nException: " + e.toString() + "\n");
			e.printStackTrace();
			return;
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				System.out.println("ERROR: Cannot close output file: " + zipFilePath);
				e.printStackTrace();
				return;
			}
		}
	}

}
