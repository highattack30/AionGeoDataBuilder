/**
 *
 */
package org.haion.tools.databuilders;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.haion.tools.databuilders.utils.BinaryXmlDecoder;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * @author Haron
 *
 */
public final class XmlConverter
{
	@Option(name="-i", usage="Path to binary XML file", metaVar="PATH", required=true)
	protected String binaryXmlFilePath;

	@Option(name="-o", usage="Path to converted XML file", metaVar="PATH", required=true)
	protected String convertedXmlFilePath;

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		final XmlConverter xmlConverter = new XmlConverter();
		final CmdLineParser cmdLineParser = new CmdLineParser(xmlConverter);
		try
		{
			cmdLineParser.parseArgument(args);
		}
		catch (final CmdLineException e)
		{
			System.err.println(e.getMessage());
			System.err.println("Usage: XmlConverter <options>");
			cmdLineParser.printUsage(System.err);
			return;
		}

		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;

		try
		{
			System.out.println("Start converting file " + xmlConverter.binaryXmlFilePath + " ...");

			inputStream = new FileInputStream(xmlConverter.binaryXmlFilePath);
			outputStream = new FileOutputStream(xmlConverter.convertedXmlFilePath);

			BinaryXmlDecoder.Decode(inputStream, outputStream);

			System.out.println("Done.");
		}
		catch (final Exception e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (inputStream != null)
				{
					inputStream.close();
				}
				if (outputStream != null)
				{
					outputStream.close();
				}
			}
			catch (final IOException e)
			{
				System.err.println(e.toString());
				e.printStackTrace();
			}
		}
	}

}
