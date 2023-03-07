package simulated_cars;

import core_car_sim.Direction;
import core_car_sim.Point;
import core_car_sim.WorldSim;
import prologfiles.RulesOfTheRoad.ROTROutcome;

import java.util.ArrayDeque;
import java.util.HashSet;

public class LargeAICar extends AbstractROTRCar
{
	private boolean haveMoved = false;
	ArrayDeque<Direction> movement = new ArrayDeque<>();

	public LargeAICar(Point startPos, Point endPos, Point referenceLoca, int startingSpeed)
	{
		super(startPos, endPos, referenceLoca, startingSpeed, System.getProperty("user.dir") + "/RoTRExperiments/resources/redcar.png",CarType.car_large);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ArrayDeque<Direction> getSimulationRoute(WorldSim world)
	{
		updateOutcomes();
		HashSet<ROTROutcome> currentIssues = new HashSet<>(rulesOfTheRoad);
		//Update beliefs for future
		updateOutcomes(); //get future rules of the road
		HashSet<ROTROutcome> futureIssues = new HashSet<>(rulesOfTheRoad);
		futureIssues.removeAll(currentIssues);
		
		//Is there any current issue to deal with
		
		//Would that action pose a problem in the future
		
		//if not advance forward.
		return null;
	}

	@Override
	protected boolean isFinished(Point arg0)
	{
		return haveMoved && arg0 == getStartingPosition();
	}

}
