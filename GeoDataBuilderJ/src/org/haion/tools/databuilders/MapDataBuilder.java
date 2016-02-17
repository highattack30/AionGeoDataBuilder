package org.haion.tools.databuilders;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.haion.tools.databuilders.interfaces.client.IAionTerrainConverter;

/**
 *
 */

/**
 * @author Haron
 *
 */
public class MapDataBuilder
{
	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		if (args.length < 2)
		{
			System.out.println("Usage: MapDataBuilder <h32-file> <map-file> [plugin-name]");
			return;
		}

		FileInputStream inputStream;
		try
		{
			inputStream = new FileInputStream(args[0]);
		}
		catch (final FileNotFoundException e)
		{
			System.out.println("ERROR: Cannot open input file: " + args[0]);
			return;
		}

		FileOutputStream outputStream;
		try
		{
			outputStream = new FileOutputStream(args[1]);
		}
		catch (final FileNotFoundException e)
		{
			System.out.println("ERROR: Cannot open output file: " + args[1]);
			return;
		}

		String pluginName = null;
		if (args.length >= 3)
		{
			pluginName = args[2];
		}
		else
		{
			System.out.println("Using default terrain converter.");
		}

		final IAionTerrainConverter terrainConverter = Factory.getTerrainConverter(pluginName);
		if (terrainConverter == null)
		{
			System.out.println("ERROR: Unknown terrain converter: " + pluginName != null ? pluginName : "<default>");
			return;
		}

		try
		{
			System.out.println("Start converting file " + args[0] + " ...");

			terrainConverter.convert(inputStream, outputStream, true);

			System.out.println("Done.\nPak file successfully converted to the Zip file: " + args[1]);
		}
		catch (final Exception e)
		{
			System.out.println("ERROR: Cannot convert terrain.\nException: " + e.toString() + "\n");
			e.printStackTrace();
			return;
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (final IOException e)
			{
				System.out.println("ERROR: Cannot close input file: " + args[0]);
				e.printStackTrace();
				return;
			}
			try
			{
				outputStream.close();
			}
			catch (final IOException e)
			{
				System.out.println("ERROR: Cannot close output file: " + args[1]);
				e.printStackTrace();
				return;
			}
		}
	}

}
