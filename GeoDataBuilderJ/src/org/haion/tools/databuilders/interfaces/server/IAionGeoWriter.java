package org.haion.tools.databuilders.interfaces.server;

import java.io.FileNotFoundException;

public interface IAionGeoWriter
{
	void setFile(String path) throws FileNotFoundException;
	void write(IAionGeoDataBlock dataBlock);
}
