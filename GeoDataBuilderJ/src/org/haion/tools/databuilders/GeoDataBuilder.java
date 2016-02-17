/**
 *
 */
package org.haion.tools.databuilders;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * @author Haron
 *
 */
public class GeoDataBuilder
{
	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final AionLevelsProcessor aionLevelProcessor = new AionLevelsProcessor();
		final CmdLineParser cmdLineParser = new CmdLineParser(aionLevelProcessor);
		try
		{
			cmdLineParser.parseArgument(args);
		}
		catch (final CmdLineException e)
		{
			System.err.println(e.getMessage());
			System.err.println("Usage: GeoDataBuilder <options>");
			cmdLineParser.printUsage(System.err);
			return;
		}

		// TODO Plugin(s) loading

		if (aionLevelProcessor.aionClientVersion != 1 && aionLevelProcessor.aionClientVersion != 2)
		{
			System.err.println("Unsupported Aion version " + aionLevelProcessor.aionClientVersion);
			System.err.println("Usage: GeoDataBuilder [options] <path2aion>");
			cmdLineParser.printUsage(System.err);
			return;
		}

		try
		{
			System.out.println("Start levels processing ...");
			aionLevelProcessor.Process();
			System.out.println("Done.");
		}
		catch (final Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
	}
}
