/**
 *
 */
package org.haion.tools.databuilders.meshaccessors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.haion.tools.databuilders.interfaces.MeshData;
import org.haion.tools.databuilders.interfaces.MeshFace;
import org.haion.tools.databuilders.interfaces.Vector3;
import org.haion.tools.databuilders.interfaces.client.IAionMeshAccessor;
import org.haion.tools.databuilders.utils.DataInputStream;

/**
 * @author Haron
 *
 */
public class DefaultMeshAccessor implements IAionMeshAccessor
{
	private FileInputStream meshFileStream = null;

	private final List<Long> meshChunksPos = new ArrayList<Long>();

	@Override
	public void setMeshFile(final String path) throws IOException
	{
		close();

		meshFileStream = new FileInputStream(path);
		final DataInputStream meshInputStream = new DataInputStream(meshFileStream);

		final byte[] signature = new byte[8];
		meshInputStream.readFully(signature);
		if (signature[0] != 0x4E || signature[1] != 0x43 || signature[2] != 0x41 || signature[3] != 0x69
				|| signature[4] != 0x6F || signature[5] != 0x6E || signature[6] != 0x00 || signature[7] != 0x00) // NCAion
			throw new IOException("Wrong signature");

		final int fileType = meshInputStream.readInt();
		if (fileType == 0xFFFF0001)
			// System.out.println("Animation data");
			return;
		if (fileType != 0xFFFF0000)
			throw new IOException("Wrong filetype");

		meshInputStream.skip(4); // unknown data

		final int tableOffset = meshInputStream.readInt();

		// Move to the chunks table
		meshFileStream.getChannel().position(tableOffset);

		final int chunksCount = meshInputStream.readInt();
		for (int i = 0; i < chunksCount; i++)
		{
			final int chunkType = meshInputStream.readInt();
			if (chunkType == 0xCCCC0000)
			{ // Mesh
				final int chunkVersion = meshInputStream.readInt();
				final int chunkOffset = meshInputStream.readInt();
				final int chunkId = meshInputStream.readInt();

				meshChunksPos.add((long) chunkOffset);
			}
			else
			{
				meshInputStream.skip(4 * 3); // unknown data
			}
		}
		// meshInputStream.close();
	}

	@Override
	public void close()
	{
		if (meshFileStream != null)
		{
			try
			{
				meshFileStream.close();
			}
			catch (final IOException e)
			{
				System.err.println(e.toString());
				e.printStackTrace();
			}
		}
		meshChunksPos.clear();
	}

	@Override
	public int getMeshesCount()
	{
		return meshChunksPos.size();
	}

	@Override
	public MeshData getMeshData(final int idx) throws IOException
	{
		final DataInputStream meshInputStream = new DataInputStream(meshFileStream);

		final long chunkOffset = meshChunksPos.get(idx);

		// Move to the chunks table
		meshFileStream.getChannel().position(chunkOffset);

		// Skip duplicate chunk header and byte[hasVertexWeights, hasVertexColors, reserved1, reserved2]
		meshInputStream.skip(4 * 5);

		final int verticesCount = meshInputStream.readInt();

		// Skip uvsCount
		meshInputStream.skip(4);

		final int indicesCount = meshInputStream.readInt();

		// Skip vertAnim reference
		meshInputStream.skip(4);

		final MeshData res = new MeshData();
		res.vertices = new Vector3[verticesCount];
		res.indices = new MeshFace[indicesCount];

		// read vertices
		for (int i = 0; i < verticesCount; i++)
		{
			res.vertices[i] = new Vector3();
			res.vertices[i].x = meshInputStream.readFloat();
			res.vertices[i].y = meshInputStream.readFloat();
			res.vertices[i].z = meshInputStream.readFloat();

			// Skip normal
			meshInputStream.skip(4 * 3);
		}

		// read indices
		for (int i = 0; i < indicesCount; i++)
		{
			res.indices[i] = new MeshFace();
			res.indices[i].v0 = meshInputStream.readInt();
			res.indices[i].v1 = meshInputStream.readInt();
			res.indices[i].v2 = meshInputStream.readInt();

			// Skip Material and SmGroup
			meshInputStream.skip(4 * 2);
		}

		return res;
	}

}
