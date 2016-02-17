// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17.09.2011 21:59:07
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   GeoMap.java

package aionjHungary.geoEngine.models;

import java.util.*;

import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class GeoMap extends Node
{

    public GeoMap(String name, int worldSize)
    {
        tmpBox = new ArrayList<BoundingBox>();
        for(int x = 0; x < worldSize; x += 256)
        {
            for(int y = 0; y < worldSize; y += 256)
            {
                Node geoNode = new Node("");
                tmpBox.add(new BoundingBox(new Vector3f(x, y, 0.0F), new Vector3f(x + 256, y + 256, 4000F)));
                super.attachChild(geoNode);
            }

        }

    }

    public int attachChild(Spatial child)
    {
        int i = 0;
        for(Iterator<Spatial> i$ = getChildren().iterator(); i$.hasNext();)
        {
            Spatial spatial = i$.next();
            if(tmpBox.get(i).intersects(child.getWorldBound()))
                ((Node)spatial).attachChild(child);
            i++;
        }

        return 0;
    }

    public void setTerrainData(short terrainData[])
    {
        this.terrainData = terrainData;
    }

    public float getZ(float x, float y, float z)
    {
        CollisionResults results = new CollisionResults();
        float newZ = 0.0F;
        if(terrainData.length == 1)
            newZ = (float)terrainData[0] / 32F;
        else
            newZ = getZ(x, y);
        if(newZ < z + 2.0F)
        {
            CollisionResult result = new CollisionResult();
            Vector3f contactPoint = new Vector3f(x, y, newZ);
            result.setContactPoint(contactPoint);
            result.setDistance(z - newZ);
            results.addCollision(result);
        }
        Vector3f pos = new Vector3f(x, y, z + 2.0F);
        Vector3f dir = new Vector3f(x, y, 0.0F);
        Float limit = Float.valueOf(pos.distance(dir));
        dir.subtractLocal(pos).normalizeLocal();
        Ray r = new Ray(pos, dir);
        r.setLimit(limit.floatValue());
        collideWith(r, results);
        if(results.size() == 0)
            return newZ;
        else
            return results.getClosestCollision().getContactPoint().z;
    }

    private float getZ(float x, float y)
    {
        if(terrainData.length == 1)
        {
            return (float)terrainData[0] / 32F;
        } else
        {
            y /= 2.0F;
            x /= 2.0F;
            int size = (int)Math.sqrt(terrainData.length);
            int xInt = (int)x;
            int yInt = (int)y;
            float p1 = terrainData[yInt + xInt * size];
            float p2 = terrainData[yInt + 1 + xInt * size];
            float p3 = terrainData[yInt + (xInt + 1) * size];
            float p4 = terrainData[yInt + 1 + (xInt + 1) * size];
            float p13 = p1 + (p1 - p3) * (x % 1.0F);
            float p24 = p2 + (p4 - p2) * (x % 1.0F);
            float p1234 = p13 + (p24 - p13) * (y % 1.0F);
            return p1234 / 32F;
        }
    }

    public boolean canSee(float x, float y, float z, float targetX, float targetY, float targetZ)
    {
        targetZ++;
        z++;
        float x2 = x - targetX;
        float y2 = y - targetY;
        float z2 = z - targetZ;
        float distance = (float)Math.sqrt(x2 * x2 + y2 * y2);
        if(distance > 80F)
            return false;
        int intD = (int)Math.abs(distance);
        boolean terrain = getZ(x, y) < z;
        for(float s = 2.0F; s < (float)intD; s += 2.0F)
        {
            float tempX = targetX + (x2 * s) / distance;
            float tempY = targetY + (y2 * s) / distance;
            float tempZ = targetZ + (z2 * s) / distance;
            if(terrain)
            {
                if(getZ(tempX, tempY) > tempZ)
                    return false;
                continue;
            }
            if(getZ(tempX, tempY) < tempZ)
                return false;
        }

        Vector3f pos = new Vector3f(x, y, z);
        Vector3f dir = new Vector3f(targetX, targetY, targetZ);
        Float limit = Float.valueOf(pos.distance(dir));
        dir.subtractLocal(pos).normalizeLocal();
        Ray r = new Ray(pos, dir);
        r.setLimit(limit.floatValue());
        CollisionResults results = new CollisionResults();
        collideWith(r, results);
        return results.size() == 0;
    }

    public void updateModelBound()
    {
        if(getChildren() != null)
        {
            Iterator<Spatial> i = getChildren().iterator();
            do
            {
                if(!i.hasNext())
                    break;
                Spatial s = i.next();
                if((s instanceof Node) && ((Node)s).getChildren().isEmpty())
                    i.remove();
            } while(true);
            tmpBox = null;
        }
        super.updateModelBound();
    }

    private short terrainData[];
    private List<BoundingBox> tmpBox;
}