// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17.09.2011 21:59:04
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Meshs.java

package aionjHungary.geoEngine;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import org.apache.log4j.Logger;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

public class Meshs
{

    public Meshs()
    {
    }

    public static Map<String, List<Mesh>> load()
    {
        HashMap<String, List<Mesh>> nodeList = new HashMap<String, List<Mesh>>();
        File geoFile = new File("data/geo/meshs.geo");
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
            log.warn("geo/meshs.geo file missing!!");
            return null;
        }
        catch(IOException e)
        {
            log.warn("geo/meshs.geo file IO error!!");
            return null;
        }
        geo.order(ByteOrder.LITTLE_ENDIAN);
        String name;
        List<Mesh> meshs;
        for(; geo.hasRemaining(); nodeList.put(name.toLowerCase(), meshs))
        {
            short namelenght = geo.getShort();
            byte nameByte[] = new byte[namelenght];
            geo.get(nameByte);
            name = new String(nameByte);
            int modelCount = geo.getShort();
            meshs = new ArrayList<Mesh>();
            for(int c = 0; c < modelCount; c++)
            {
                Mesh m = new Mesh();
                short vectorCount = geo.getShort();
                Vector3f vertices[] = new Vector3f[vectorCount];
                for(short x = 0; x < vectorCount; x++)
                    vertices[x] = new Vector3f(geo.getFloat(), geo.getFloat(), geo.getFloat());

                short tringle = geo.getShort();
                short indexes[] = new short[tringle];
                for(short x = 0; x < tringle; x++)
                    indexes[x] = geo.getShort();

                m.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
                m.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createShortBuffer(indexes));
                m.setBound(new BoundingBox());
                m.updateBound();
                meshs.add(m);
            }

        }

        return nodeList;
    }

    private static Logger log = Logger.getLogger(Meshs.class.getName());

}