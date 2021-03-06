package technobotts.soccer.util;

import java.awt.Rectangle;
import java.util.List;

import technobotts.util.RectGroup;

import lejos.nxt.addon.NXTCam;
import technobotts.robotics.CamRect;
import lejos.robotics.DirectionFinder;

public class GoalFinder implements DirectionFinder
{
	public final static int BLUE   = 0;
	public final static int YELLOW = 1;

	private NXTCam   camera;
	private int      goalColorId;

	public GoalFinder(NXTCam camera, int goalColorId)
	{
		this.camera = camera;
		camera.enableTracking(true);
		this.goalColorId = goalColorId;
	}

	private float getAngle(float xCoord)
	{
		final float offset = 86;
		final float scale = 0.01f;
		return (float) (Math.toDegrees(Math.atan(scale * (xCoord - offset))));
	}
	private float getAngle(Rectangle rect)
	{
		if(rect == null)
			return Float.NaN;
		return getAngle(rect.x + rect.width / 2);
	}
	
	private Rectangle calcGoalRectangle()
	{
		RectGroup blocks = new RectGroup();
		CamRect[] rects = CamRect.fromNXTCam(camera);

		for(CamRect r : rects)
		{
			if(r.getArea() >= 50)
			{
				if(r.colorId == goalColorId)
					blocks.add(r);
			}
		}
		
		List<RectGroup> groupedBlocks = blocks.getDistinctGroups();
		RectGroup mergedBlocks = new RectGroup(groupedBlocks.size());

		for(RectGroup group : groupedBlocks)
		{
			mergedBlocks.add(group.getBoundingBox());
		}

		return mergedBlocks.getLargest(250);
	}
	
	public Rectangle getGoalRectangle()
	{
		return calcGoalRectangle();
	}
	
	public float getDegreesCartesian()
	{
		return getAngle(getGoalRectangle());
	}


	@Override
    public void resetCartesianZero()
    {  
    }

	@Override
    public void startCalibration()
    {
    }

	@Override
    public void stopCalibration()
    {
    }
}
