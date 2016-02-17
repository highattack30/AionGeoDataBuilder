package org.haion.tools.databuilders.interfaces.server;

import java.io.FileNotFoundException;


public interface IAionGeoMeshReader {
	void setFile(String path) throws FileNotFoundException;
	
	String[] getMeshNames();
	IAionGeoMeshDataBlock getMesh(int i);
}
