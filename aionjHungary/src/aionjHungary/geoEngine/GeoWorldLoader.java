// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17.09.2011 21:59:04
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   GeoWorldLoader.java

package aionjHungary.geoEngine;

import aionjHungary.geoEngine.models.GeoMap;
import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.util.*;

import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;

public class GeoWorldLoader
{

    public GeoWorldLoader()
    {
    }

    public static void setGeoDir(String dir)
    {
        GEO_DIR = dir;
    }

    public static void setDebugMod(boolean debug)
    {
        DEBUG = debug;
    }

    public static Map<String, Spatial> loadMeshs()
    {
        HashMap<String, Spatial> geoms = new HashMap<String, Spatial>();
        File geoFile = new File((new StringBuilder()).append(GEO_DIR).append("meshs.geo").toString());
        FileChannel roChannel = null;
        MappedByteBuffer geo = null;
        try
        {
            roChannel = (new RandomAccessFile(geoFile, "r")).getChannel();
            int size = (int)roChannel.size();
            geo = roChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0L, size).load();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return null;
        }
        geo.order(ByteOrder.LITTLE_ENDIAN);
        int meshsCount = 0;
        do
        {
            if(!geo.hasRemaining())
                break;
            int namelenght = geo.getShort() & 0xffff;
            byte nameByte[] = new byte[namelenght];
            geo.get(nameByte);
            String name = new String(nameByte);
            int modelCount = geo.getShort() & 0xffff;
            Node node = new Node(DEBUG ? name : null);
            for(int c = 0; c < modelCount; c++)
            {
                Mesh m = new Mesh();
                int vectorCount = (geo.getShort() & 0xffff) * 3;
                FloatBuffer vertices = FloatBuffer.allocate(vectorCount);
                for(int x = 0; x < vectorCount; x++)
                    vertices.put(geo.getFloat());

                int tringle = geo.getShort() & 0xffff;
                ShortBuffer indexes = ShortBuffer.allocate(tringle);
                for(int x = 0; x < tringle; x++)
                    indexes.put((short) (geo.getShort() & 0xffff));

                m.setBuffer(VertexBuffer.Type.Position, 3, vertices);
                m.setBuffer(VertexBuffer.Type.Index, 3, indexes);
                m.createCollisionData();
                Geometry geom = new Geometry(null, m);
                if(modelCount == 1)
                    geoms.put(name, geom);
                node.attachChild(geom);
            }

            if(!node.getChildren().isEmpty())
                geoms.put(name, node);
            meshsCount++;
        } while(true);
        return geoms;
    }

    public static boolean loadWorld(int worldId, Map<String, Spatial> models, GeoMap map)
    {
        File geoFile = new File((new StringBuilder()).append(GEO_DIR).append(worldId).append(".geo").toString());
        FileChannel roChannel = null;
        MappedByteBuffer geo = null;
        try
        {
            roChannel = (new RandomAccessFile(geoFile, "r")).getChannel();
            int size = (int)roChannel.size();
            geo = roChannel.map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0L, size).load();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }
        geo.order(ByteOrder.LITTLE_ENDIAN);
        if(geo.get() == 0)
        {
            map.setTerrainData(new short[] {
                geo.getShort()
            });
        } else
        {
            int size = geo.getInt();
            short terrainData[] = new short[size];
            for(int i = 0; i < size; i++)
                terrainData[i] = geo.getShort();

            map.setTerrainData(terrainData);
        }
        do
        {
            if(!geo.hasRemaining())
                break;
            int nameLenght = geo.getShort() & 0xffff;
            byte nameByte[] = new byte[nameLenght];
            geo.get(nameByte);
            String name = new String(nameByte);
            Vector3f loc = new Vector3f(geo.getFloat(), geo.getFloat(), geo.getFloat());
            float matrix[] = new float[9];
            for(int i = 0; i < 9; i++)
                matrix[i] = geo.getFloat();

            float scale = geo.getFloat();
            Matrix3f matrix3f = new Matrix3f();
            matrix3f.set(matrix);
            Spatial node = (Spatial)models.get(name.toLowerCase());
            if(node != null)
            {
                Spatial nodeClone = node.clone();

                nodeClone.setLocalRotation(matrix3f);
                nodeClone.setLocalTranslation(loc);
                nodeClone.setLocalScale(scale);

                nodeClone.updateModelBound();

                map.attachChild(nodeClone);
            }
        } while(true);
        map.updateModelBound();
        return true;
    }

    private static String GEO_DIR = "data/geo/";
    private static boolean DEBUG = false;

}