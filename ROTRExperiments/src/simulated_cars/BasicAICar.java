package simulated_cars;

import java.util.ArrayDeque;

import core_car_sim.AbstractCar;
import core_car_sim.Direction;
import core_car_sim.Point;
import core_car_sim.WorldSim;

public class BasicAICar extends AbstractCar {

	Direction toDrive;
	ArrayDeque<Direction> movement = new ArrayDeque<Direction>();
	// Basic AI car has 4 attributes
	// 1. Start Position
	// 2. end Position
	// 3. starting speed
	// 4. its driving direction
	public BasicAICar(Point startPos, Point endPos,int startingSpeed, Direction directionToDrive) 
	{
		super(startPos, endPos, startingSpeed, System.getProperty("user.dir") + "/resources/redcar.png");
		toDrive = directionToDrive;
	}

	@Override
	protected ArrayDeque<Direction> getSimulationRoute() 
	{
		for (int i = 0; i < getSpeed(); i++)
		{
			movement.add(toDrive);
		}
		return movement;
	}

	@Override
	protected boolean isFinished(Point arg0) 
	{
		return false;
	}

	@Override
	protected void visibleWorldUpdate(WorldSim arg0, Point arg1) 
	{
		
	}

}
