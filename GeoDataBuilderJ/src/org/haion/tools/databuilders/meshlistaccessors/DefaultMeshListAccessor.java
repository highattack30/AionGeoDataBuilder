/**
 *
 */
package org.haion.tools.databuilders.meshlistaccessors;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.haion.tools.databuilders.interfaces.MeshInfo;
import org.haion.tools.databuilders.interfaces.client.IAionMeshListAccessor;
import org.haion.tools.databuilders.utils.DataInputStream;

/**
 * @author Haron
 *
 */
public class DefaultMeshListAccessor implements IAionMeshListAccessor
{

	private FileInputStream meshListFileStream = null;

	private final List<String> meshFiles = new ArrayList<String>();
	private final List<MeshInfo> meshEntries = new ArrayList<MeshInfo>();

	@Override
	public void setMeshListFile(final String path) throws IOException
	{
		close();

		meshListFileStream = new FileInputStream(path);
		final DataInputStream meshInputStream = new DataInputStream(meshListFileStream);

		final byte[] signature = new byte[3];
		meshInputStream.readFully(signature);
		if (signature[0] != 0x43 || signature[1] != 0x52 || signature[2] != 0x59) // CRY
			throw new IOException("Wrong signature");

		final int dw1 = meshInputStream.readInt();
		final int meshDataBlockSz = meshInputStream.readInt();
		final int titlesCount = meshInputStream.readInt();
		for (int i = 0; i < titlesCount; i++)
		{
			final int nameLen = meshInputStream.readInt();
			final byte[] nameBytes = new byte[nameLen - 4];
			meshInputStream.readFully(nameBytes);
			// TODO Use these names somehow
		}

		// meshes info
		final int meshInfoCount = meshInputStream.readInt();
		final byte[] fileNameBytes = new byte[128];
//		float[] fl = new float[6];
		for (int i = 0; i < meshInfoCount; i++)
		{
			meshInputStream.skip(4);
//			int dw1 = meshInputStream.readInt();

			meshInputStream.readFully(fileNameBytes);
			final String fileName = new String(fileNameBytes);
			meshFiles.add(fileName.toLowerCase().replace('\\', '/').trim());

			meshInputStream.skip(4 * 7);

//			int dw2 = meshInputStream.readInt();
//			fl[0] = meshInputStream.readFloat();
//			fl[1] = meshInputStream.readFloat();
//			fl[2] = meshInputStream.readFloat();
//			fl[3] = meshInputStream.readFloat();
//			fl[4] = meshInputStream.readFloat();
//			fl[5] = meshInputStream.readFloat();
		}

		// meshes data
		final int meshDataCount = meshInputStream.readInt();
		for (int i = 0; i < meshDataCount; i++)
		{
			meshInputStream.skip(4 * 2);
			final int meshIdx = meshInputStream.readInt();
			meshInputStream.skip(4 * 3);
			final float[] mesh_matrix = new float[3 * 4];
			for (int j = 0; j < mesh_matrix.length; j++)
			{
				mesh_matrix[j] = meshInputStream.readFloat();
			}

			meshInputStream.skip(4 * (meshDataBlockSz - 10));

			final MeshInfo meshData = new MeshInfo();
			meshData.meshIdx = meshIdx;
			meshData.matrix = mesh_matrix;
			meshEntries.add(meshData);
		}
	}

	@Override
	public void close()
	{
		if (meshListFileStream != null)
		{
			try
			{
				meshListFileStream.close();
			}
			catch (final IOException e)
			{
				System.err.println(e.toString());
				e.printStackTrace();
			}
		}
		meshFiles.clear();
		meshEntries.clear();
	}

	@Override
	public List<String> getFilesName()
	{
		return (List<String>) ((ArrayList) meshFiles).clone();
	}

	@Override
	public List<MeshInfo> getMeshEntries()
	{
		return (List<MeshInfo>) ((ArrayList) meshEntries).clone();
	}
}
