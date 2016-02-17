package org.haion.tools.databuilders.interfaces.server;

public interface IAionServerFactory
{
	IAionGeoMeshReader getGeoMeshReader();
	IAionGeoMeshWriter getGeoMeshWriter();
	IAionGeoReader     getGeoReader();
	IAionGeoWriter     getGeoWriter();
}
