/**
 * 
 */
package org.haion.tools.databuilders.interfaces.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.haion.tools.databuilders.interfaces.MeshInfo;
import org.haion.tools.databuilders.utils.KeyValuePair;

/**
 * @author Haron
 *
 */
public interface IAionMeshListAccessor {
	void setMeshListFile(String path) throws FileNotFoundException, IOException;
	void close();
	List<String> getFilesName();
	List<MeshInfo> getMeshEntries();
}
