/**
 *
 */
package org.haion.tools.databuilders.terrainconverters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.haion.tools.databuilders.interfaces.client.IAionTerrainConverter;
import org.haion.tools.databuilders.utils.CommonUtils;

/**
 * @author Haron_2
 *
 */
public class DefaultTerrainConverter implements IAionTerrainConverter
{
	private final static int H32_POINT_SIZE = 3;
	private final static int MAP_POINT_SIZE = 2;

	private final static int H32_BUFFER_MAX_SIZE = (CommonUtils.BUFFER_MAX_SIZE / MAP_POINT_SIZE) * H32_POINT_SIZE;
	private final static int MAP_BUFFER_MAX_SIZE = (CommonUtils.BUFFER_MAX_SIZE / MAP_POINT_SIZE) * MAP_POINT_SIZE;

	@Override
	public void convert(final InputStream inputStream, final OutputStream outputStream, final boolean swapBytes) throws IOException
	{
		final byte[] h32PointBuffer = new byte[H32_BUFFER_MAX_SIZE];
		final byte[] mapPointBuffer = new byte[MAP_BUFFER_MAX_SIZE];

		byte byte1Shift, byte2Shift;
		if (swapBytes)
		{
			byte1Shift = 1;
			byte2Shift = 0;
		}
		else
		{
			byte1Shift = 0;
			byte2Shift = 1;
		}

		do
		{
			final int bytesRead = inputStream.read(h32PointBuffer);
			if (bytesRead == -1 || bytesRead == 0)
			{
				break;
			}
			if (bytesRead % H32_POINT_SIZE != 0)
				throw new IOException("Wrong count of bytes per point in the input stream");

			final int pointsCount = bytesRead / H32_POINT_SIZE;
			for (int i = 0; i < pointsCount; i++)
			{
				mapPointBuffer[i * 2]     = h32PointBuffer[i * 3 + byte1Shift];
				mapPointBuffer[i * 2 + 1] = h32PointBuffer[i * 3 + byte2Shift];
			}

			outputStream.write(mapPointBuffer, 0, pointsCount * MAP_POINT_SIZE);
		}
		while (true);
	}
}
