package org.haion.tools.databuilders.serveraccessors;

import org.haion.tools.databuilders.interfaces.server.IAionGeoMeshReader;
import org.haion.tools.databuilders.interfaces.server.IAionGeoMeshWriter;
import org.haion.tools.databuilders.interfaces.server.IAionGeoReader;
import org.haion.tools.databuilders.interfaces.server.IAionGeoWriter;
import org.haion.tools.databuilders.interfaces.server.IAionServerFactory;

public class DefaultServerFactory implements IAionServerFactory {

	@Override
	public IAionGeoMeshReader getGeoMeshReader() {
		return new DefaultGeoMeshReader();
	}

	@Override
	public IAionGeoMeshWriter getGeoMeshWriter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAionGeoReader getGeoReader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAionGeoWriter getGeoWriter() {
		// TODO Auto-generated method stub
		return null;
	}

}
