package org.haion.tools.databuilders.serveraccessors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.haion.tools.databuilders.interfaces.server.IAionGeoMeshDataBlock;
import org.haion.tools.databuilders.interfaces.server.IAionGeoMeshReader;
import org.haion.tools.databuilders.utils.DataInputStream;

class DefaultGeoMeshReader implements IAionGeoMeshReader {

	private FileInputStream meshFileStream = null;
	private DataInputStream meshInputStream = null;
	
	private List<String> meshName = new ArrayList<String>();
	private List<Long> meshPos = new ArrayList<Long>();
	
	@Override
	public void setFile(String path) throws FileNotFoundException {
		// TODO Auto-generated method stub

		meshFileStream = new FileInputStream(path);
		meshInputStream = new DataInputStream(meshFileStream);
		
		// fill meshName and meshPos lists
	}

	@Override
	public String[] getMeshNames() {
		return (String[]) meshName.toArray();
	}

	@Override
	public IAionGeoMeshDataBlock getMesh(int i) {
		// TODO Auto-generated method stub
		return null;
	}

}
