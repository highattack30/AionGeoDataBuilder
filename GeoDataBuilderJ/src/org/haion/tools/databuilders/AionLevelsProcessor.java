package org.haion.tools.databuilders;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.haion.tools.databuilders.interfaces.AionException;
import org.haion.tools.databuilders.interfaces.MeshData;
import org.haion.tools.databuilders.interfaces.MeshFace;
import org.haion.tools.databuilders.interfaces.MeshInfo;
import org.haion.tools.databuilders.interfaces.Vector3;
import org.haion.tools.databuilders.interfaces.client.IAionMeshAccessor;
import org.haion.tools.databuilders.interfaces.client.IAionMeshListAccessor;
import org.haion.tools.databuilders.interfaces.client.IAionPakAccessor;
import org.haion.tools.databuilders.interfaces.client.IAionTerrainConverter;
import org.haion.tools.databuilders.utils.BinaryXmlDecoder;
import org.haion.tools.databuilders.utils.CommonUtils;
import org.haion.tools.databuilders.utils.DataOutputStream;
import org.haion.tools.databuilders.utils.IStringComparer;
import org.haion.tools.databuilders.utils.ZipUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class AionLevelsProcessor
{
	@Option(name = "-o", usage = "Path to the output folder. If not set ./out folder will be used", metaVar = "PATH")
	protected String outputPath = "./out";

	@Option(name = "-v", usage = "Version Aion client installation (1 or 2). Default is 2", metaVar = "VER")
	protected int aionClientVersion = 2;

	@Option(name = "-lvl", usage = "Set exect level Id e.g. 110010000", metaVar = "LVLID")
	protected String levelId = null;

	@Option(name = "-no_h32", usage = "Do not include *.h32 data into *.geo file")
	protected boolean noH32 = false;

	@Option(name = "-no_mesh", usage = "Do not generate mesh.geo file")
	protected boolean noMesh = false;

	@Option(name = "-no_cleanup", usage = "Do not delete unpacked, converted, etc. files")
	protected boolean noCleanup = false;

	@Option(name = "-w", usage = "Path to WorldId.xml file. If not set then client WorldId.xml file will be used", metaVar = "PATH")
	protected String worldIdPath = "./WorldId.xml";

	@Option(name = "-t", usage = "Path to the temporary folder. If not set ./tmp folder will be used", metaVar = "PATH")
	protected String tmpPath = "./tmp";

	@Option(name = "-keep_tmp", usage = "Do not clean temporary folder")
	protected boolean keepTmp = false;

    // receives other command line parameters than options
    @Argument(usage="Path to Aion client installation. Required", metaVar="PATH", required=true)
    protected String aionClientPath;

    // -----------------------------------------------------------------------------------------------------------
	private class StringComparer implements IStringComparer
	{
		@Override
		public boolean compare(final String s1, final String s2)
		{
			return s1.equalsIgnoreCase(s2);
		}
	}

	private class EndsWithStringComparer implements IStringComparer
	{
		@Override
		public boolean compare(final String s1, final String s2)
		{
			return s1.endsWith(s2);
		}
	}

	// -----------------------------------------------------------------------------------------------------------
	private File[] collectMeshesPath(final File[] rootFolders)
	{
		File[] res = new File[0];
		for (final File file : rootFolders)
		{
			// collect meshes
			final File[] meshFiles = file.listFiles(new FilenameFilter()
			{

				@Override
				public boolean accept(final File dir, final String name)
				{
					return name.matches("Mesh_Meshes_\\d\\d\\d\\.pak") || name.endsWith("_Meshes.pak");
				}
			});
			if (meshFiles.length > 0)
			{
				res = CommonUtils.concat(res, meshFiles);
			}

			// process subfolders recursively
			final File[] subFolders = file.listFiles(new FileFilter()
			{

				@Override
				public boolean accept(final File pathname)
				{
					return pathname.isDirectory();
				}
			});
			if (subFolders.length > 0)
			{
				res = CommonUtils.concat(res, collectMeshesPath(subFolders));
			}
		}
		return res;
	}

	private void retreiveFromPak(final String zipFileName, final Map<String, OutputStream> filterStreamMap,
			final IStringComparer comparer) throws AionException, IOException
	{
		// convert to zip
		final File zipFile = new File(tmpPath, zipFileName);
		final FileOutputStream zipStream = new FileOutputStream(zipFile);
		pakAccessor.convertToZip(zipStream);
		zipStream.close();

		// extract files by filter
		try
		{
			ZipUtils.unzipEntry(zipFile, filterStreamMap, comparer);
		}
		finally
		{
			if (!keepTmp && !zipFile.delete())
			{
				System.err.println("Cannot delete file: " + zipFile.getPath());
			}
		}
	}

	private File getClientWorldIdFile(final String aionClientPath) throws FileNotFoundException, IOException,
			AionException
	{

		final File worldIdPakFile = new File(aionClientPath, "Data/World/World.pak");

		final File worldIdXmlFile = new File(tmpPath, "WorldId_orig.xml");
		final FileOutputStream origWorldIdXmlStream = new FileOutputStream(worldIdXmlFile);

		final Map<String, OutputStream> filterStreamMap = new HashMap<String, OutputStream>();
		filterStreamMap.put("WorldId.xml", origWorldIdXmlStream);

		try
		{
			pakAccessor.setPakFile(worldIdPakFile.getPath(), aionClientVersion);
			retreiveFromPak(worldIdPakFile.getName() + ".zip", filterStreamMap, new EndsWithStringComparer());
			pakAccessor.close();
		}
		catch (final Exception e)
		{
			System.err.println("ERROR: Cannot process Pak file: " + worldIdPakFile.getPath());
			System.err.println(e.toString());

			if (!keepTmp && worldIdXmlFile.exists())
			{
				worldIdXmlFile.delete();
			}
		}
		finally
		{
			try
			{
				origWorldIdXmlStream.close();
			}
			catch (final IOException e)
			{
			}
		}

		// decode XML
		final FileInputStream inputStream = new FileInputStream(worldIdXmlFile);
		final File convertedWorldIdXmlFile = new File(tmpPath, "WorldId.xml");
		try
		{
			final FileOutputStream outputStream = new FileOutputStream(convertedWorldIdXmlFile);

			BinaryXmlDecoder.Decode(inputStream, outputStream);

			inputStream.close();
			outputStream.close();
		}
		finally
		{
			if (!keepTmp && !worldIdXmlFile.delete())
			{
				System.err.println("Cannot delete file: " + worldIdXmlFile.getPath());
			}
		}

		return convertedWorldIdXmlFile;
	}

	private class BrushLstMeshData
	{
		List<String> meshFiles = null;
		int[] meshUsage = null;
	}

	private final Map<String, BrushLstMeshData> levelMeshData = new HashMap<String, BrushLstMeshData>();

	private Map<String, String> mapIdClientFolderMap = null;

	private Map<String, String> generateWorldMapsList(final File clientWorldMapsFile)
			throws JDOMException, IOException
	{
		final SAXBuilder builder = new SAXBuilder();

		// read client maps
		final Document document = builder.build(clientWorldMapsFile);
		final Element rootNode = document.getRootElement();
		final List list = rootNode.getChildren("data");

		final Map<String, String> res = new HashMap<String, String>();
		for (int i = 0; i < list.size(); i++)
		{
			final Element node = (Element) list.get(i);

			res.put(node.getAttributeValue("id"), node.getText());
		}

		return res;
	}

	private File[] clientMeshFiles = null;

	private void collectMeshFilePaths() throws JDOMException, IOException
	{
		System.out.println("  Collecting mesh file paths ...");
		clientMeshFiles = collectMeshesPath(new File[] { levelsCommonDir, objectsDir });
		System.out.println("    " + clientMeshFiles.length + " files collected");
		System.out.println("  Done.");

		if (clientMeshFiles.length == 0)
		{
			System.out.println("There are no mesh files in the Aion client. No geomaps generated.");
			return;
		}

		// Read world_maps.xml and WorldId.xml and find Levels to process
		System.out.println("  Generating avaiable levels list...");
		try
		{
			mapIdClientFolderMap = generateWorldMapsList(worldIdFile);
		}
		finally
		{
//			if (!keepTmp && !worldIdFile.delete())
//				System.err.println("Cannot delete file: " + worldIdFile.getPath());
		}
		System.out.println("  Done.");
	}

	private String[] clientLevelFolders = null;

	private void collectLevelFoldersName()
	{
		final File clientLevelRootFolder = new File(aionClientPath, "/Levels/");
		final FilenameFilter filter = new FilenameFilter()
		{
		    @Override
			public boolean accept(final File dir, final String name)
		    {
		        final File file = new File(dir, name);
		        return file.isDirectory() && name.toLowerCase() != "common";
		    }
		};
		clientLevelFolders = clientLevelRootFolder.list(filter);
	}

	private String correctLevelFolderName(final String folder)
	{
		final String n = folder.toLowerCase();
		for (final String dir : clientLevelFolders)
		{
			if (dir.toLowerCase().equals(n))
				return dir;
		}
		return null;
	}

	private String getLevelFileName(final String folder)
	{
		final File clientLevelFolder = new File(aionClientPath, "/Levels/" + folder);
		final FilenameFilter filter = new FilenameFilter()
		{
		    @Override
			public boolean accept(final File dir, final String name)
		    {
		        final File file = new File(aionClientPath, dir + "/" + name);
		        return /*file.isFile() &&*/ name.toLowerCase().equals("level.pak");
		    }
		};
		final String[] list = clientLevelFolder.list(filter);
		return list.length != 0 ? list[0] : null;
	}

	private File levelsTmpFolder = null;
	private File meshesTmpFolder = null;
	private File levelsCommonDir = null;
	private File objectsDir = null;
	private File worldIdFile = null;

	private void initResources() throws FileNotFoundException, IOException
	{
		// TODO: cleanup temporary and output folders

		levelsTmpFolder = new File(tmpPath, "Levels");
		if (!levelsTmpFolder.exists())
		{
			levelsTmpFolder.mkdirs();
		}

		meshesTmpFolder = new File(tmpPath, "Meshes");
		if (!meshesTmpFolder.exists())
		{
			meshesTmpFolder.mkdirs();
		}

		worldIdFile = null;
		if (worldIdPath != null)
		{
			worldIdFile = new File(worldIdPath);
			if (!worldIdFile.exists() || !worldIdFile.isFile())
			{
				System.out.println("Path to WorldId.xml file [" + worldIdPath
						+ "] doesn't exist or not a file path. Trying to use client WorldId.xml");
				worldIdFile = null;
			}
		}
		if (worldIdFile == null)
		{
			try
			{
				worldIdFile = getClientWorldIdFile(aionClientPath);
				worldIdPath = worldIdFile.getPath();
			}
			catch (final AionException e)
			{
				System.out.println("Cannot find client WorldId.xml. Trying to use local one");
			}
		}
		if (worldIdFile == null)
		{
			worldIdFile = new File("./WorldId.xml");
			if (!worldIdFile.exists() || !worldIdFile.isFile())
				throw new FileNotFoundException("Path to WorldId.xml file [" + worldIdPath
						+ "] doesn't exist or not a file path");
			worldIdPath = "./WorldId.xml";
		}

		// Path to the levels' meshes
		levelsCommonDir = new File(aionClientPath, "/Levels/common");
		if (!levelsCommonDir.exists() || !levelsCommonDir.isDirectory())
			throw new FileNotFoundException("Path to clients Levels/common folder [" + levelsCommonDir.getPath()
					+ "] doesn't exist or not a folder path");

		// Path to the objects' meshes
		objectsDir = new File(aionClientPath, "/Objects");
		if (!objectsDir.exists() || !objectsDir.isDirectory())
			throw new FileNotFoundException("Path to clients Objects folder [" + objectsDir.getPath()
					+ "] doesn't exist or not a folder path");
	}

	private boolean parseLevelPak(final String levelFolderName, final File clientLevelPakFile, final File brushLstFile,
			final File landMapH32File) throws FileNotFoundException
	{
		System.out.print("      Extracting data from Level.pak ... ");
		final Map<String, OutputStream> filterStreamMap = new HashMap<String, OutputStream>();

		FileOutputStream landMapH32OutputStream = null;
		if (!noH32)
		{
			landMapH32OutputStream = new FileOutputStream(landMapH32File);

			filterStreamMap.put("land_map.h32", landMapH32OutputStream);
		}

		final FileOutputStream brushLstStream = new FileOutputStream(brushLstFile);
		filterStreamMap.put("brush.lst", brushLstStream);

		try
		{
			pakAccessor.setPakFile(clientLevelPakFile.getPath(), aionClientVersion);
			retreiveFromPak(clientLevelPakFile.getName() + ".zip", filterStreamMap, new EndsWithStringComparer());
			pakAccessor.close();
		}
		catch (final Exception e)
		{
			System.out.println("\n***   ERROR: Cannot process Pak file: " + clientLevelPakFile.getPath());
			System.out.println(e.toString());

			if (!keepTmp && !noH32 && landMapH32File.exists() && !landMapH32File.delete())
			{
				System.out.println("\n*     WARNING: Cannot delete file: " + landMapH32File.getPath());
			}
			if (!keepTmp && brushLstFile.exists() && !brushLstFile.delete())
			{
				System.out.println("\n*     WARNING: Cannot delete file: " + brushLstFile.getPath());
			}
			return false;
		}
		finally
		{
			try
			{
				if (!noH32)
				{
					landMapH32OutputStream.close();
				}
				brushLstStream.close();
			}
			catch (final IOException e)
			{
			}
		}
		System.out.println("Done.");
		return true;
	}

	private List<MeshInfo> parseBrushLst(final String levelFolderName, final File brushLstFile,
			final BrushLstMeshData meshData)
	{
		System.out.print("      Parsing brush.lst ... ");
		List<MeshInfo> meshEntries = null;
//		File levelMeshesListFile = new File(tmpPath, levelFolderName + "_Meshes.txt");
//		PrintStream levelMeshesListStream = new PrintStream(levelMeshesListFile);
		try
		{
			meshListAccessor.setMeshListFile(brushLstFile.getPath());

			meshData.meshFiles = meshListAccessor.getFilesName();
			meshData.meshUsage = new int[meshData.meshFiles.size()];
			if (!noMesh)
			{
				levelMeshData.put(levelFolderName, meshData);

//				System.out.println("      Mesh files:");
//				for (String fileName : meshData.meshFiles)
//				{
//					levelMeshesListStream.println("        " + fileName);
//				}
			}

			meshEntries = meshListAccessor.getMeshEntries();

			meshListAccessor.close();
		}
		catch (final Exception e)
		{
			System.out.println("\n***   ERROR: Cannot access mesh file: " + brushLstFile.getPath());
			System.out.println(e.toString());
			// e.printStackTrace();
		}
		finally
		{
			// levelMeshesListStream.close();
			if (!keepTmp && brushLstFile.exists() && !brushLstFile.delete())
			{
				System.out.println("\n*     WARNING: Cannot delete file: " + brushLstFile.getPath());
			}
		}
		System.out.println("             Done.");
		return meshEntries;
	}

	private void createGeoFile(final String clientLevelId, final File landMapH32File, final List<MeshInfo> meshEntries, final BrushLstMeshData meshData) throws IOException
	{
		System.out.print("      Creating " + clientLevelId + ".geo file ... ");

		final File geoFile = new File(outputPath, clientLevelId + ".geo");
		final FileOutputStream geoFileStream = new FileOutputStream(geoFile);
		final DataOutputStream geoDataStream = new DataOutputStream(geoFileStream);

		boolean h32DataCopied = false;
		if (!noH32 && landMapH32File.exists() && landMapH32File.length() > 0)
		{
			// TODO Shrink flat meshes

			// mesh exists
			geoDataStream.writeByte(1);
			// count of terrain data elements
			geoDataStream.writeInt((int) landMapH32File.length() / 3);

			final FileInputStream landMapH32InputStream = new FileInputStream(landMapH32File);

			// Convert terrain
			try
			{
				terrainConverter.convert(landMapH32InputStream, geoFileStream, false);
				h32DataCopied = true;
			}
//			catch (Exception e)
//			{
//				System.out.println("ERROR: Cannot convert terrain.\nException: " + e.toString() + "\n");
//				e.printStackTrace();
//				return;
//			}
			finally
			{
				landMapH32InputStream.close();
				if (!keepTmp && landMapH32File.exists() && !landMapH32File.delete())
				{
					System.out.println("\n*     WARNING: Cannot delete file: " + landMapH32File.getPath());
				}
			}
		}
		if (!h32DataCopied)
		{
			geoDataStream.writeByte(0); // mesh exists
			geoDataStream.writeShort(0); // stub
		}

		// save meshes info
		if (meshEntries != null && meshEntries.size() > 0)
		{
			for (final MeshInfo simpleEntry : meshEntries)
			{
				final String meshFileName = meshData.meshFiles.get(simpleEntry.meshIdx);
				final byte[] meshFileNameBytes = meshFileName.getBytes(Charset.forName("ASCII"));

				// mesh file name size
				geoDataStream.writeShort(meshFileNameBytes.length);
				// file name
				geoDataStream.write(meshFileNameBytes);

				final float[] matrix = simpleEntry.matrix;
				// position vector
				for (int i = 0; i < 3; i++)
				{
					geoDataStream.writeFloat((float) (matrix[i * 4 + 3] / 100.0));
				}
				// orientation matrix
				for (int i = 0; i < 3; i++)
				{
					geoDataStream.writeFloat((float) (matrix[i * 4 + 0] / 100.0));
					geoDataStream.writeFloat((float) (matrix[i * 4 + 1] / 100.0));
					geoDataStream.writeFloat((float) (matrix[i * 4 + 2] / 100.0));
				}
				// scale
				geoDataStream.writeFloat((float) 1.0);
			}
		}

		geoFileStream.close();
		System.out.println("   Done.");
	}

	private void saveMeshData(final String cfgFileName, final List<MeshData> meshesPack, final DataOutputStream meshesGeoDataStream) throws IOException
	{
		final byte[] meshFileNameBytes = cfgFileName.getBytes(Charset.forName("ASCII"));

		// mesh file name size
		meshesGeoDataStream.writeShort(meshFileNameBytes.length);
		// file name
		meshesGeoDataStream.write(meshFileNameBytes);
		// size
		meshesGeoDataStream.writeShort(meshesPack.size());

		// save meshes
		for (final MeshData meshData : meshesPack)
		{
			meshesGeoDataStream.writeShort(meshData.vertices.length);
			for (final Vector3 vector : meshData.vertices)
			{
				meshesGeoDataStream.writeFloat((float) (vector.x / 100.0));
				meshesGeoDataStream.writeFloat((float) (vector.y / 100.0));
				meshesGeoDataStream.writeFloat((float) (vector.z / 100.0));
			}

			meshesGeoDataStream.writeShort(meshData.indices.length * 3);
			for (final MeshFace meshFace : meshData.indices)
			{
				meshesGeoDataStream.writeShort(meshFace.v0);
				meshesGeoDataStream.writeShort(meshFace.v1);
				meshesGeoDataStream.writeShort(meshFace.v2);
			}

			System.out.println("          Vertices: " + meshData.vertices.length
					+ " Indexes: " + (meshData.indices.length * 3) + " = "
					+ meshData.indices.length + " * 3");
		}
	}

	private void findCgfFiles(final Map<String, File> cgfFiles, final Map<String, OutputStream> filterStreamMap) throws FileNotFoundException
	{
		final Set<String> cgfFileNames = pakAccessor.getFilesName();
		// System.out.println("      Level that contains meshes from Pak [" + file.getPath() + "]:");
		for (final Entry<String, BrushLstMeshData> levelMeshes : levelMeshData.entrySet())
		{
			final BrushLstMeshData brushLstMeshData = levelMeshes.getValue();
			for (final String cgfName : cgfFileNames)
			{
				// for (String meshPath : levelMeshes.getValue().meshFiles) {
				for (int i = 0; i < brushLstMeshData.meshFiles.size(); i++)
				{
					final String meshPath = brushLstMeshData.meshFiles.get(i);
					// if (meshPath.equalsIgnoreCase("levels/common/" + cgfName)) {
					if (meshPath.endsWith(cgfName))
					{
						// System.out.println("        " + levelMeshes.getKey());

						final File cgfOutputFile = new File(meshesTmpFolder, cgfName
								.replaceAll("\\\\", "_").replaceAll("/", "_"));
						cgfFiles.put(meshPath, cgfOutputFile);

						final FileOutputStream cgfOutputStream = new FileOutputStream(cgfOutputFile);
						filterStreamMap.put(cgfName, cgfOutputStream);

						brushLstMeshData.meshUsage[i]++;

						break;
					}
				}
			}
		}
		System.out.println("      Using " + filterStreamMap.size() + " of " + cgfFileNames.size()
				+ " meshes");
	}

	private int createMeshesGeo(final Map<String, File> cgfFiles, final DataOutputStream meshesGeoDataStream)
	{
		int meshesSaved = 0;
		int cgfFileIdx = 1;
		final int cgfFileCount = cgfFiles.size();
		for (final Entry<String, File> cgfFileInfo : cgfFiles.entrySet())
		{
			try
			{
				System.out.println("      [" + cgfFileIdx + "/" + cgfFileCount + "] Parsing " + cgfFileInfo.getKey() + " ...");

				meshAccessor.setMeshFile(cgfFileInfo.getValue().getPath());
				final int meshesCount = meshAccessor.getMeshesCount();

				// collect not empty meshes
				final List<MeshData> meshesPack = new ArrayList<MeshData>();
				for (int i = 0; i < meshesCount; i++)
				{
					final MeshData meshData = meshAccessor.getMeshData(i);
					if (meshData.vertices.length <= 0 || meshData.indices.length <= 0)
					{
						System.out.println("        Skipping: " + meshData.vertices.length + "/"
								+ meshData.indices.length);
					}
					else if ((meshData.vertices.length & 0xffff0000) != 0
							|| ((meshData.indices.length * 3) & 0xffff0000) != 0)
					{
						System.out.println("***     ERROR: Count of elements is bigger than MAX_SHORT");
						System.out.println("        Skipping: " + meshData.vertices.length + "/"
								+ meshData.indices.length * 3 + " [" + meshData.indices.length
								+ " * 3]");
					}
					else
					{
						meshesPack.add(meshData);
					}
				}

				if (meshesPack.size() > 0)
				{
					saveMeshData(cgfFileInfo.getKey(), meshesPack, meshesGeoDataStream);
					meshesSaved += meshesPack.size();
				}
				System.out.println("      Done. " + meshesPack.size() + " of " + meshesCount + " meshes added.");
			}
			catch (final Exception e)
			{
				System.out.println("***     ERROR: Cannot process mesh");
				System.err.println(e.toString());
				// e.printStackTrace();
			}
			finally
			{
				meshAccessor.close();

//				if (fileInfo.getValue().exists())
				if (!keepTmp)
				{
					cgfFileInfo.getValue().delete();
				}
			}
			cgfFileIdx++;
		}
		return meshesSaved;
	}

	private IAionPakAccessor pakAccessor = null;
	private IAionMeshListAccessor meshListAccessor = null;
	private IAionTerrainConverter terrainConverter = null;
	private IAionMeshAccessor meshAccessor = null;

	private boolean initFactoryObjects()
	{
		pakAccessor = Factory.getPakAccessor(null);
		if (pakAccessor == null)
		{
			System.err.println("ERROR: Cannot load default Pak accessor");
			return false;
		}
		meshListAccessor = Factory.getMeshListAccessor(null);
		if (meshListAccessor == null)
		{
			System.out.println("\n***   ERROR: Cannot load default mesh list accessor");
			return false;
		}
		terrainConverter = Factory.getTerrainConverter(null);
		if (terrainConverter == null)
		{
			System.out.println("\n***   ERROR: Cannot load default terrain converter");
			return false;
		}
		meshAccessor = Factory.getMeshAccessor(null);
		if (meshAccessor == null)
		{
			System.out.println("\n***   ERROR: Cannot load default mesh accessor");
			return false;
		}
		return true;
	}

	protected void Process() throws JDOMException, IOException
	{
		long timer = -System.currentTimeMillis();

		if (!initFactoryObjects())
			return;

		final File tmpDir = new File(tmpPath);
		if (!tmpDir.exists() || !tmpDir.isDirectory())
			throw new FileNotFoundException("Temporary folder path [" + tmpPath
					+ "] doesn't exist or not a folder path");

		final File aionClientDir = new File(aionClientPath);
		if (!aionClientDir.exists() || !aionClientDir.isDirectory())
			throw new FileNotFoundException("Aion client installation path [" + aionClientPath
					+ "] doesn't exist or not a folder path");

		initResources();

		collectMeshFilePaths();

		collectLevelFoldersName();

		System.out.println("  Processing levels...");
		boolean containsValidLevel = false;
		final Iterator<Entry<String, String>> it = mapIdClientFolderMap.entrySet().iterator();
		while (it.hasNext())
		{
			final Map.Entry<String, String> pairs = it.next();
			final String clientLevelId = pairs.getKey();
			final String levelFolder = pairs.getValue();
			final String clientLevelFolderName = correctLevelFolderName(levelFolder); // Correction needed to make it works under Linux/Unix

			if (clientLevelFolderName == null)
			{
				System.out.println("*     WARNING: Level folder doesn't exist (" + levelFolder + ").");
				continue;
			}

			System.out.println("    [" + clientLevelId + "] " + clientLevelFolderName + " ...");

			final String clientLevelFileName = getLevelFileName(clientLevelFolderName);

			if (clientLevelFileName == null)
			{
				System.out.println("*     WARNING: Level file doesn't exist (" + levelFolder + "/Level.pak).");
				continue;
			}

			final File clientLevelPakFile = new File(aionClientPath, "/Levels/" + clientLevelFolderName + "/" + clientLevelFileName);

			final File brushLstFile = new File(levelsTmpFolder, clientLevelFolderName + "_brush.lst");
			final File landMapH32File = noH32 ? null : new File(levelsTmpFolder, clientLevelFolderName + "_land_map.h32");

			if (!parseLevelPak(clientLevelFolderName, clientLevelPakFile, brushLstFile, landMapH32File))
			{
				continue;
			}

			final BrushLstMeshData meshData = new BrushLstMeshData();
			final List<MeshInfo> meshEntries = parseBrushLst(clientLevelFolderName, brushLstFile, meshData);

			createGeoFile(clientLevelId, landMapH32File, meshEntries, meshData);

			containsValidLevel = true;

			System.out.println("    Done.");
		}
		System.out.println("  Done.");

		// --------------------------------------------------------------------------------
		if (!noMesh && containsValidLevel)
		{
			System.out.println("  Generating meshs.geo ...");

			int meshesSaved = 0;
			final File meshesGeoFile = new File(outputPath, "meshs.geo");
			final FileOutputStream meshesGeoFileStream = new FileOutputStream(meshesGeoFile);
			final DataOutputStream meshesGeoDataStream = new DataOutputStream(meshesGeoFileStream);

			// File meshesListFile = new File(tmpPath, "Meshes.txt");
			// PrintStream meshesListStream = new PrintStream(meshesListFile);
			int meshPakFileIdx = 1;
			final int meshPakFileCount = clientMeshFiles.length;
			for (final File meshPakFile : clientMeshFiles)
			{
				// System.out.println("  " + file.getPath());
				try
				{
					System.out.println("    [" + meshPakFileIdx + "/" + meshPakFileCount + "] Accessing "
							+ meshPakFile.getPath() + " ...");

					pakAccessor.setPakFile(meshPakFile.getPath(), aionClientVersion);

					// find all cgf's used in levels
					final Map<String, File> cgfFiles = new HashMap<String, File>();
					final Map<String, OutputStream> filterStreamMap = new HashMap<String, OutputStream>();

					findCgfFiles(cgfFiles, filterStreamMap);

					if (filterStreamMap.size() > 0)
					{
						// Unpack
						try
						{
							retreiveFromPak(meshPakFile.getName() + ".zip", filterStreamMap, new StringComparer());
						}
						catch (final Exception e)
						{
							System.err.println(e.toString());
							// e.printStackTrace();
						}

						// Save meshes to meshs.geo
						meshesSaved += createMeshesGeo(cgfFiles, meshesGeoDataStream);
					}
					System.out.println("    Done.");

//					meshesListStream.println("Accessing file " + file.getPath() + " ...");
//					for (String cgf : cgfFiles)
//					{
//						meshesListStream.println("  " + cgf);
//					}
//					meshesListStream.println("Done.");
				}
				catch (final Exception e)
				{
					System.err.println("\n*** ERROR:" + e.toString());
					// e.printStackTrace();
				}
				finally
				{
					pakAccessor.close();
				}
				meshPakFileIdx++;
			}
			meshesGeoFileStream.close();
			// meshesListStream.close();
			System.out.println("  Done. " + meshesSaved + " meshes saved.");

			System.out.println("  Check meshes that were not found ...");
			for (final Entry<String, BrushLstMeshData> levelMeshes : levelMeshData.entrySet())
			{
				System.out.println("    " + levelMeshes.getKey());
				final BrushLstMeshData brushLstMeshData = levelMeshes.getValue();
				for (int i = 0; i < brushLstMeshData.meshFiles.size(); i++)
				{
					if (brushLstMeshData.meshUsage[i] == 0)
					{
						System.out.println("      " + brushLstMeshData.meshFiles.get(i));
					}
				}
			}
			System.out.println("  Done.");
		}
		timer += System.currentTimeMillis();
		final int seconds = (int) (timer / 1000) % 60;
		final int minutes = (int) ((timer / (1000 * 60)) % 60);
		final int hours = (int) ((timer / (1000 * 60 * 60)) % 24);
		System.out.println("  Processing time: " + hours + " h " + minutes + " m " + seconds + "s");
	}
}
