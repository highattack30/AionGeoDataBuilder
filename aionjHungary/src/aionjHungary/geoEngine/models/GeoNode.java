// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 17.09.2011 21:59:07
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   GeoNode.java

package aionjHungary.geoEngine.models;

import java.util.*;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.math.Matrix3f;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

public class GeoNode extends Spatial
{

    public GeoNode(String name)
    {
        super(name);
        childrens = new ArrayList<Spatial>();
    }

    public void addGeoNode(Spatial geoNode)
    {
        childrens.add(geoNode);
    }

    public List<Spatial> getChildrens()
    {
        return childrens;
    }

    public int collideWith(Collidable other, CollisionResults results)
        throws UnsupportedCollisionException
    {
        if(!worldBound.intersects((Ray)other))
            return 0;
        int count = 0;
        for (Spatial children : childrens)
        {
            count += children.collideWith(other, results);
        }

        return count;
    }

    public void updateModelBound()
    {
        if(childrens != null)
        {
            int i = 0;
            for(int max = childrens.size(); i < max; i++)
                ((Spatial)childrens.get(i)).updateModelBound();

        }
        updateWorldBound();
    }

    protected void updateWorldBound()
    {
//        super.updateWorldBound();
        BoundingVolume resultBound = null;
        int i = 0;
        for(int cSize = childrens.size(); i < cSize; i++)
        {
            Spatial child = (Spatial)childrens.get(i);
            if(resultBound != null)
            {
                resultBound.mergeLocal(child.getWorldBound());
                continue;
            }
            if(child.getWorldBound() != null)
                resultBound = child.getWorldBound().clone(worldBound);
        }

        worldBound = resultBound;
    }

    public void setModelBound(BoundingVolume modelBound)
    {
        worldBound = modelBound;
    }

	@Override
	public int getVertexCount() {
		int res = 0;
		for (Spatial spatial : childrens) {
			res += spatial.getVertexCount();
		}
		return res;
	}

	@Override
	public int getTriangleCount() {
		int res = 0;
		for (Spatial spatial : childrens) {
			res += spatial.getTriangleCount();
		}
		return res;
	}

	public void setTransform(Matrix3f paramMatrix3f, Vector3f paramVector3f, float paramFloat) {
		for (Spatial spatial : childrens) {
			spatial.setLocalRotation(paramMatrix3f);
			spatial.setLocalTranslation(paramVector3f);
			spatial.setLocalScale(paramFloat);
		}
	}

    private List<Spatial> childrens;

	@Override
	protected void breadthFirstTraversal(SceneGraphVisitor arg0, Queue<Spatial> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Spatial deepClone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void depthFirstTraversal(SceneGraphVisitor arg0) {
		// TODO Auto-generated method stub
		
	}
}