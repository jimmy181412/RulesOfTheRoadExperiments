package simulated_cars;

import core_car_sim.*;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class SmallAICar extends AbstractCar {

	Direction toDrive;

	boolean trafficLightRed;
	boolean atWhiteLine;
	boolean finished = false;
	boolean wallAhead = false;//
	boolean approaching_vertical_zebra = false;
	boolean approaching_horizontal_zebra = false;
	ArrayDeque<Direction> directions = new ArrayDeque<>();
	// Basic AI car has 4 attributes
	// 1. Start Position
	// 2. end Position
	// 3. starting speed
	// 4. its driving direction
	public SmallAICar(Point startPos, Point endPos, Point referencePos,Direction initialDirection,int startingSpeed, Direction directionToDrive)
	{
		super(startPos, endPos, referencePos,startingSpeed
				,initialDirection
				,System.getProperty("user.dir") + "/RoTRExperiments/resources/imagesicon/basicCarImage1.png"
				,System.getProperty("user.dir") + "/RoTRExperiments/resources/imagesicon/basicCarImage2.png"
				,System.getProperty("user.dir") + "/RoTRExperiments/resources/imagesicon/basicCarImage3.png"
				,System.getProperty("user.dir") + "/RoTRExperiments/resources/imagesicon/basicCarImage4.png"
				,CarType.car_small);
		toDrive = directionToDrive;
	}

	@Override
	protected ArrayDeque<Direction> getSimulationRoute(WorldSim world)
	{
		if ((trafficLightRed && atWhiteLine) || finished)
		{
			setSpeed(0);
		}
		else {
			directions.push(cmd);
		}
		reMakeDecisions();
		return directions;
	}

	@Override
	protected boolean isFinished(Point arg0) 
	{
	    boolean isFinished;
		isFinished = getCurrentPosition().equals(getEndPosition());
        return isFinished;
	}

	@Override
	protected void visibleWorldUpdate(WorldSim visibleWorld, Point location)
	{
		for (int y = 0; y < visibleWorld.getHeight(); y++){
			for (int x = 0; x < visibleWorld.getWidth(); x++){
				if (visibleWorld.getCell(x, y).getCellType() == AbstractCell.CellType.ct_information){
					if (((AbstractInformationCell)visibleWorld.getCell(x, y)).getInformationType() == AbstractInformationCell.InformationCell.ic_trafficLight){
						TrafficLightCell tlc = (TrafficLightCell)visibleWorld.getCell(x, y);
						TrafficLightCell.TrafficLightCellInformation tlci = ((TrafficLightCell)visibleWorld.getCell(x, y)).getInformation();
						//faces list
						ArrayList<Direction> faces = tlc.getFaces();
						if(faces.size() != 0) {
							if(faces.get(0) == Direction.east) {
								if (cmd == Direction.west) {
									trafficLightRed = tlci.redOn;
									Point visibleWorldStopPoint = new Point(x , y - 1);
									atWhiteLine = visibleWorldStopPoint.equals(location);
								}
							}
							else if(faces.get(0) == Direction.west) {
								if(cmd == Direction.east) {
									trafficLightRed = tlci.redOn;
									Point visibleWorldStopPoint = new Point(x , y + 1);
									atWhiteLine = visibleWorldStopPoint.equals(location);
								}
							}
							else if(faces.get(0) == Direction.south) {
								if(cmd == Direction.north) {
									trafficLightRed = tlci.redOn;
									Point visibleWorldStopPoint = new Point(x + 1, y);
									atWhiteLine = visibleWorldStopPoint.equals(location);
								}
							}
							else if(faces.get(0) == Direction.north) {
								if(cmd == Direction.south) {
									trafficLightRed = tlci.redOn;
									Point visibleWorldStopPoint = new Point(x - 1, y );
									atWhiteLine = visibleWorldStopPoint.equals(location);
								}
							}

						}
					}
				}
				else if(visibleWorld.getCell(x, y).getCellType() == AbstractCell.CellType.ct_road) {
					RoadCell rc = (RoadCell)visibleWorld.getCell(x, y);
					for(RoadCell.RoadMarking rm : rc.getRoadMarkings()) {
						if(rm == RoadCell.RoadMarking.rm_Zebra_Horizontal) {
							if(y == location.getY()){
								if(cmd == Direction.east) {
									if(x == location.getX() + 1) {
										approaching_horizontal_zebra = true;
									}
								}
								else if(cmd == Direction.west) {
									if(x == location.getX() - 1) {
										approaching_horizontal_zebra = true;
									}
								}
							}
						}
						else if(rm == RoadCell.RoadMarking.rm_Zebra_Vertical) {
							//check the car's current position
							if(x == location.getX()) {
								if(cmd == Direction.north) {
									if(y == location.getY() + 1){
										approaching_vertical_zebra = true;
									}
								}
								else if(cmd == Direction.south) {
									if(y == location.getY() - 1) {
										approaching_vertical_zebra = true;
									}
								}
							}
						}

					}
				}
			}
		}
	}
	public void reMakeDecisions() {
		trafficLightRed = false;
		atWhiteLine = false;
		wallAhead = false;
		approaching_vertical_zebra = false;
		approaching_horizontal_zebra = false;
	}

}
