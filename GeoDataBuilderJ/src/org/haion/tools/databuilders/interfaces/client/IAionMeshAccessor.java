/**
 *
 */
package org.haion.tools.databuilders.interfaces.client;

import java.io.IOException;

import org.haion.tools.databuilders.interfaces.MeshData;

/**
 * @author Haron
 *
 */
public interface IAionMeshAccessor {
	void setMeshFile(String path) throws IOException;
	void close();
	int getMeshesCount();
	MeshData getMeshData(int idx) throws IOException;
}
