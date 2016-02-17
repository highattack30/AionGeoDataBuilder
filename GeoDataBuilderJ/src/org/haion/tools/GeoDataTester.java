package org.haion.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import aionjHungary.geoEngine.GeoWorldLoader;
import aionjHungary.geoEngine.models.GeoMap;
import aionjHungary.geoEngine.scene.Spatial;

public class GeoDataTester {
	
	private static Map<String, Integer> getServerMapIdData(File serverWorldMapsFile) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();

		// read server maps
		Map<String, Integer> serverMapIdData = new HashMap<String, Integer>();
		Document document = (Document) builder.build(serverWorldMapsFile);
		Element rootNode = document.getRootElement();
		List list = rootNode.getChildren("map");

		for (int i = 0; i < list.size(); i++) {
			Element node = (Element) list.get(i);

			serverMapIdData.put(node.getAttributeValue("id"), Integer.parseInt(node.getAttributeValue("world_size")));
		}
		return serverMapIdData;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 0) {
			System.err.println("Path to geo data is not specified.");
			return;
		}
		
		File serverFolder = new File(args[0]);
		if (!serverFolder.exists()) {
			System.err.println("Path " + args[0] + " doesn't exist");
			return;
		}
		
		File worldMapsFile = new File(serverFolder, "/gameserver/data/static_data/world_maps.xml");
		if (!worldMapsFile.exists()) {
			System.err.println("Path " + worldMapsFile.getPath() + " doesn't exist");
			return;
		}
		
		Map<String, Integer> serverMapIdData = null;
		try {
			serverMapIdData = getServerMapIdData(worldMapsFile);
		} catch (Exception e) {
			System.err.println(e.toString());
			e.printStackTrace();
		}
		
		File geoDataFolder = new File(serverFolder, "/gameserver/data/geo/");
		if (!geoDataFolder.exists()) {
			System.err.println("Path " + geoDataFolder.getPath() + " doesn't exist");
			return;
		}
		
//		File[] levelGeoFiles = geoDataFolder.listFiles(new FilenameFilter() {
//			@Override
//			public boolean accept(File dir, String name) {
//				return !name.equalsIgnoreCase("meshs.geo");
//			}
//		});

		GeoWorldLoader.setGeoDir(geoDataFolder.getPath() + "/");
		
		Map<Integer, GeoMap> geoMaps = new HashMap<Integer, GeoMap>();
		
		System.out.println("Loading meshs.geo ...");
		Map models = GeoWorldLoader.loadMeshs();
		System.out.println("Done.");

		System.out.println("Loading levels ...");
		for (Entry<String, Integer> mapData : serverMapIdData.entrySet())
		{
			System.out.print("  Level " + mapData.getKey() + " ...");
			GeoMap geoMap = new GeoMap(mapData.getKey(), mapData.getValue());
			if (GeoWorldLoader.loadWorld(Integer.parseInt(mapData.getKey()), models, geoMap))
			{
				geoMaps.put(Integer.parseInt(mapData.getKey()), geoMap);
			}
			System.out.println("Done.");
		}
		System.out.println("Done.");
		
		models.clear();
		
		System.out.println("Geodata engine: " + geoMaps.size() + " geoMaps loaded!");
	}

}
