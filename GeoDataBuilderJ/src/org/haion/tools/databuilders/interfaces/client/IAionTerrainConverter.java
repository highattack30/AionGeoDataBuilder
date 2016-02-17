/**
 * 
 */
package org.haion.tools.databuilders.interfaces.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Haron
 *
 */
public interface IAionTerrainConverter {
	void convert(InputStream inputStream, OutputStream outputStream, boolean swapBytes) throws IOException; 
}
