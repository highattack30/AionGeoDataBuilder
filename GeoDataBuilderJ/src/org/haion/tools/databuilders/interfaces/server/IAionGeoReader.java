package org.haion.tools.databuilders.interfaces.server;

import java.io.FileNotFoundException;

public interface IAionGeoReader
{
	void setFile(String path) throws FileNotFoundException;
	IAionGeoDataBlock read();
}
