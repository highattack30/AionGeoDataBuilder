/**
 * Based on pak2zip.py
 */
package org.haion.tools.databuilders.interfaces.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.haion.tools.databuilders.interfaces.AionException;
import org.haion.tools.databuilders.interfaces.PakFileFormatException;

/**
 * @author Haron
 *
 */
public interface IAionPakAccessor {
	void setPakFile(String path, int version) throws FileNotFoundException, IOException,
			PakFileFormatException;

	void close();

	void convertToZip(OutputStream outputStream) throws AionException, IOException;

	Set<String> getFilesName();

	int getUnpakedFileSize(String fileName) throws AionException;

	//void unpackFile(String fileName, OutputStream outputStream);
}
