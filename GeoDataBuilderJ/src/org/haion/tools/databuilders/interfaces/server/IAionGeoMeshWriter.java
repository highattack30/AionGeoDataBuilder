package org.haion.tools.databuilders.interfaces.server;

import java.io.FileNotFoundException;

public interface IAionGeoMeshWriter
{
	void setFile(String path) throws FileNotFoundException;
	void write(IAionGeoMeshDataBlock dataBlock);
}
