/**
 * 
 */
package org.haion.tools.databuilders;

import org.haion.tools.databuilders.interfaces.client.IAionMeshAccessor;
import org.haion.tools.databuilders.interfaces.client.IAionMeshListAccessor;
import org.haion.tools.databuilders.interfaces.client.IAionPakAccessor;
import org.haion.tools.databuilders.interfaces.client.IAionTerrainConverter;
import org.haion.tools.databuilders.terrainconverters.DefaultTerrainConverter;
import org.haion.tools.databuilders.meshaccessors.DefaultMeshAccessor;
import org.haion.tools.databuilders.meshlistaccessors.DefaultMeshListAccessor;
import org.haion.tools.databuilders.pakaccessors.DefaultPakAccessor;

/**
 * @author Haron
 *
 */
public class Factory {
	public static final IAionTerrainConverter getTerrainConverter(String name) {
		if (name == null || name.isEmpty())
			return new DefaultTerrainConverter();
		return null;
	}
	public static final IAionMeshListAccessor getMeshListAccessor(String name) {
		if (name == null || name.isEmpty())
			return new DefaultMeshListAccessor();
		return null;
	}
	public static final IAionMeshAccessor getMeshAccessor(String name) {
		if (name == null || name.isEmpty())
			return new DefaultMeshAccessor();
		return null;
	}
	public static final IAionPakAccessor getPakAccessor(String name) {
		if (name == null || name.isEmpty())
			return new DefaultPakAccessor();
		return null;
	}
}
