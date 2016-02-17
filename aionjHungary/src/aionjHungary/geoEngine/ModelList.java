// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17.09.2011 21:59:04
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ModelList.java

package aionjHungary.geoEngine;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

public class ModelList
{

    public static Node load(int worldId, Map<String, List<Mesh>> meshs)
    {
        File Geo = new File("data/geo/" + worldId + ".geo");

        FileChannel roChannel = null;
        Node worldNode = new Node(String.valueOf(worldId));
        try
        {
          roChannel = new RandomAccessFile(Geo, "r").getChannel();
          int size = (int)roChannel.size();
          MappedByteBuffer geo = roChannel.map(FileChannel.MapMode.READ_ONLY, 0L, size).load();
          geo.order(ByteOrder.LITTLE_ENDIAN);
          int modelCount = geo.getInt();
          for (int cc = 0; cc < modelCount; cc++)
          {
            int nameLenght = geo.getShort();
            byte[] nameByte = new byte[nameLenght];
            geo.get(nameByte);
            String modelName = new String(nameByte);
            Vector3f loc = new Vector3f(geo.getFloat(), geo.getFloat(), geo.getFloat());

            float[][] tmp = new float[3][3];
            for (int r = 0; r < 3; r++) {
              for (int c = 0; c < 3; c++)
                tmp[c][r] = geo.getFloat();
            }
            Matrix3f matrix = new Matrix3f();
            matrix.set(tmp);
            List<Mesh> meshss = (List<Mesh>)meshs.get(modelName.toLowerCase());
            Node node = new Node(modelName);
            for (Mesh m : meshss)
            {
              Geometry geom = new Geometry(modelName, (Mesh) m.clone());
              geom.setModelBound(new BoundingBox());
              geom.setLocalTranslation(loc);
              geom.setLocalRotation(matrix);
              node.attachChild(geom);
            }
            node.setModelBound(new BoundingBox());
            worldNode.attachChild(node);
          }
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.out.println("Failed to Load GeoFile data/geo/" + worldId + ".geo");
        }
        finally
        {
          try
          {
            if (roChannel != null)
              roChannel.close();
          }
          catch (Exception e)
          {
          }
        }
        worldNode.updateGeometricState();
        return worldNode;
      }
}