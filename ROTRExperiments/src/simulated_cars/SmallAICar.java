package simulated_cars;

import core_car_sim.AbstractCar;
import core_car_sim.Direction;
import core_car_sim.Point;
import core_car_sim.WorldSim;

import java.util.ArrayDeque;

public class SmallAICar extends AbstractCar {

	Direction toDrive;
	ArrayDeque<Direction> movement = new ArrayDeque<>();
	// Basic AI car has 4 attributes
	// 1. Start Position
	// 2. end Position
	// 3. starting speed
	// 4. its driving direction
	public SmallAICar(Point startPos, Point endPos, Point referencePos,int startingSpeed, Direction directionToDrive)
	{
		super(startPos, endPos, referencePos,startingSpeed,System.getProperty("user.dir") + "/RoTRExperiments/resources/redcar.png", CarType.car_small);
		toDrive = directionToDrive;
	}

	@Override
	protected ArrayDeque<Direction> getSimulationRoute() 
	{
	    movement.add(cmd);
		return movement;
	}

	@Override
	protected boolean isFinished(Point arg0) 
	{
	    boolean isFinished;
		isFinished = getCurrentPosition().equals(getEndPosition());
        return isFinished;
	}

	@Override
	protected void visibleWorldUpdate(WorldSim arg0, Point arg1) 
	{

	}

}
