/*
 *  it is building on work by Joe Collenette.
 */

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
	boolean exitIsClear = true;
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

		if(exitIsClear){
			setSpeed(1);
			if ((trafficLightRed && atWhiteLine) || finished)
			{
				setSpeed(0);
			}
			else {
				directions.push(cmd);
			}


		}
		else{
			setSpeed(0);
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

//		//check the car's current moving Direction:
//		if(cmd == Direction.north) {
//			if(visibleWorld.containsCar(location.getX(), location.getY() -1)) {
//				AbstractCar car1.
//			}
//		}
//		else if(cmd == Direction.south) {
//			if(visibleWorld.containsCar(location.getX(), location.getY() + 1)) {
//				beliefs.put(cb,true);
//			}
//		}
//		else if(cmd == Direction.east) {
//			if(visibleWorld.containsCar(location.getX() + 1 , location.getY())) {
//				beliefs.put(cb,true);
//			}
//		}
//		else if(cmd == Direction.west) {
//			if(visibleWorld.containsCar(location.getX() - 1 , location.getY())) {
//				beliefs.put(cb,true);
//			}
//		}
//		break;


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
									trafficLightRed = tlci.redOn || tlci.redYellowOn;
									Point visibleWorldStopPoint = new Point(x , y - 1);
									atWhiteLine = visibleWorldStopPoint.equals(location);
								}
							}
							else if(faces.get(0) == Direction.west) {
								if(cmd == Direction.east) {
									trafficLightRed = tlci.redOn || tlci.redYellowOn;
									Point visibleWorldStopPoint = new Point(x , y + 1);
									atWhiteLine = visibleWorldStopPoint.equals(location);
								}
							}
							else if(faces.get(0) == Direction.south) {
								if(cmd == Direction.north) {
									trafficLightRed = tlci.redOn || tlci.redYellowOn;
									Point visibleWorldStopPoint = new Point(x + 1, y);
									atWhiteLine = visibleWorldStopPoint.equals(location);
								}
							}
							else if(faces.get(0) == Direction.north) {
								if(cmd == Direction.south) {
									trafficLightRed = tlci.redOn || tlci.redYellowOn;
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

		int espeed = 1;
		//check car current moving direction
		if(cmd == Direction.north) {
			//the position that the car will be at in the next move is
			Point predicted_point = new Point(location.getX(), location.getY() - espeed);

			//the point list that the car will pass from its current location to its predicted point(without the current position)
			ArrayList<Point> pointPassing = new ArrayList<>();
			for(int currentY = location.getY() - 1; currentY >= predicted_point.getY(); currentY--){
				Point tmpPoint = new Point(location.getX(), currentY);
				pointPassing.add(tmpPoint);
			}
			//the point list that the car will pass from its current location to its predicted point(without the current position)
			ArrayList<Point> pointPassing1 = new ArrayList<>();
			for(int i = 0; i < visibleWorld.getWidth(); i++) {
				for(int j = location.getY() - 1; j >= location.getY() - espeed;j--) {
					if(visibleWorld.containsCar(i,j)) {
						AbstractCar car1 = visibleWorld.getCarAtPosition(i, j);
						//get the car's speed
						int speed1 = car1.getSpeed();
						Direction d1 = car1.getCMD();
						if(d1 == Direction.east) {
							//the predicted location the car will be at
							Point predicted_point1 = new Point(i + speed1, j);
							pointPassing1.add(new Point(i,j));
							for(int currentX = i + 1; currentX <= predicted_point1.getX(); currentX++) {
								Point tmpPoint1 = new Point(currentX, predicted_point1.getY());
								pointPassing1.add(tmpPoint1);
							}

						}
						else if(d1 == Direction.west) {
							Point predicted_point1 = new Point(i - speed1,j);
							pointPassing1.add(new Point(i,j));
							for(int currentX = i - 1; currentX >= predicted_point1.getX(); currentX--) {
								Point tmpPoint1 = new Point(currentX, predicted_point1.getY());
								pointPassing1.add(tmpPoint1);
							}

						}
						else if(d1 == Direction.south) {
							//  get the predicted point of the car in the next move
							Point predicted_point1 = new Point(i,j+speed1);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j + 1; currentY <= predicted_point1.getY(); currentY++) {
								Point tmpPoint1 = new Point(i, currentY);
								pointPassing1.add(tmpPoint1);
							}
						}
						else if(d1 == Direction.north) {
							//  get the predicted point of the car in the next move
							Point predicted_point1 = new Point(i,j-speed1);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j - 1; currentY >= predicted_point1.getY(); currentY--) {
								Point tmpPoint1 = new Point(i, currentY);
								pointPassing1.add(tmpPoint1);
							}

						}
					}

					if(visibleWorld.containsPedestrian(i,j)) {
						Pedestrian pedestrian1 = visibleWorld.getPedestrianAtPosition(i,j);
						//get the car's speed
						int speed2 = pedestrian1.getSpeed();
						Direction d2 = pedestrian1.getMovingDirection();
						if(d2 == Direction.east) {
							//the predicted location the car will be at
							Point predicted_point2 = new Point(i + speed2, j);
							pointPassing1.add(new Point(i,j));
							for(int currentX = i + 1; currentX <= predicted_point2.getX(); currentX++) {
								Point tmpPoint2 = new Point(currentX, predicted_point2.getY());
								pointPassing1.add(tmpPoint2);
							}

						}
						else if(d2 == Direction.west) {
							Point predicted_point2 = new Point(i + speed2,j);
							pointPassing1.add(new Point(i,j));
							for(int currentX = i - 1; currentX >= predicted_point2.getX(); currentX--) {
								Point tmpPoint2 = new Point(currentX, predicted_point2.getY());
								pointPassing1.add(tmpPoint2);
							}

						}
						else if(d2 == Direction.south) {
							//  get the predicted point of the car in the next move
							Point predicted_point2 = new Point(i,j+speed2);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j + 1; currentY <= predicted_point2.getY(); currentY++) {
								Point tmpPoint2 = new Point(i, currentY);
								pointPassing1.add(tmpPoint2);
							}
						}
						else if(d2 == Direction.north) {
							//  get the predicted point of the car in the next move
							Point predicted_point2 = new Point(i,j-speed2);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j - 1; currentY >= predicted_point2.getY(); currentY--) {
								Point tmpPoint2 = new Point(i, currentY);
								pointPassing1.add(tmpPoint2);
							}
						}
					}
				}
			}
			if(checkCommonPoint(pointPassing, pointPassing1)) {
				exitIsClear = false;
			}
		}
		else if(cmd == Direction.south) {
			// the position that the car will be at in the next move is
			Point predicted_point = new Point(location.getX(), location.getY() + espeed);

			//the point list that the car will pass from its current location to its predicted point(without the current position)
			ArrayList<Point> pointPassing = new ArrayList<>();

			for(int currentY = location.getY() + 1; currentY <= predicted_point.getY(); currentY++){
				Point tmpPoint = new Point(location.getX(), currentY);
				pointPassing.add(tmpPoint);
			}

			//the point list that the car will pass from its current location to its predicted point(without the current position)
			ArrayList<Point> pointPassing1 = new ArrayList<>();

			for(int i = 0; i < visibleWorld.getWidth(); i++) {
				for(int j = location.getY() + 1; j <= location.getY() + espeed; j++) {
					if(visibleWorld.containsCar(i, j)){
						if(visibleWorld.containsCar(i,j)) {
							AbstractCar car1 = visibleWorld.getCarAtPosition(i,j);
							//get the car's speed
							int speed1 = car1.getSpeed();
							Direction d1 = car1.getCMD();
							if(d1 == Direction.east) {
								//the predicted location the car will be at
								Point predicted_point1 = new Point(i + speed1, j);
								pointPassing1.add(new Point(i,j));
								for(int currentX = i + 1; currentX <= predicted_point1.getX(); currentX++) {
									Point tmpPoint1 = new Point(currentX, predicted_point1.getY());
									pointPassing1.add(tmpPoint1);
								}

							}
							else if(d1 == Direction.west) {
								Point predicted_point1 = new Point(i + speed1,j);
								pointPassing1.add(new Point(i,j));
								for(int currentX = i - 1; currentX >= predicted_point1.getX(); currentX--) {
									Point tmpPoint1 = new Point(currentX, predicted_point1.getY());
									pointPassing1.add(tmpPoint1);
								}

							}
							else if(d1 == Direction.south) {
								//  get the predicted point of the car in the next move
								Point predicted_point1 = new Point(i,j+speed1);
								pointPassing1.add(new Point(i,j));
								for(int currentY = j + 1; currentY <= predicted_point1.getY(); currentY++) {
									Point tmpPoint1 = new Point(i, currentY);
									pointPassing1.add(tmpPoint1);
								}
							}
							else if(d1 == Direction.north) {
								//  get the predicted point of the car in the next move
								Point predicted_point1 = new Point(i,j-speed1);
								pointPassing1.add(new Point(i,j));
								for(int currentY = j - 1; currentY >= predicted_point1.getY(); currentY--) {
									Point tmpPoint1 = new Point(i, currentY);
									pointPassing1.add(tmpPoint1);
								}
							}
						}
					}
					if(visibleWorld.containsPedestrian(i,j)) {
							Pedestrian pedestrian1 = visibleWorld.getPedestrianAtPosition(i,j);
							//get the car's speed
							int speed2 = pedestrian1.getSpeed();
							Direction d2 = pedestrian1.getMovingDirection();
							if(d2 == Direction.east) {
								//the predicted location the car will be at
								Point predicted_point2 = new Point(i + speed2, j);
								pointPassing1.add(new Point(i,j));
								for(int currentX = i + 1; currentX <= predicted_point2.getX(); currentX++) {
									Point tmpPoint2 = new Point(currentX, predicted_point2.getY());
									pointPassing1.add(tmpPoint2);
								}

							}
							else if(d2 == Direction.west) {
								Point predicted_point2 = new Point(i + speed2,j);
								pointPassing1.add(new Point(i,j));
								for(int currentX = i - 1; currentX >= predicted_point2.getX(); currentX--) {
									Point tmpPoint2 = new Point(currentX, predicted_point2.getY());
									pointPassing1.add(tmpPoint2);
								}

							}
							else if(d2 == Direction.south) {
								//  get the predicted point of the car in the next move
								Point predicted_point2 = new Point(i,j+speed2);
								pointPassing1.add(new Point(i,j));
								for(int currentY = j + 1; currentY <= predicted_point2.getY(); currentY++) {
									Point tmpPoint2 = new Point(i, currentY);
									pointPassing1.add(tmpPoint2);
								}
							}
							else if(d2 == Direction.north) {
								//  get the predicted point of the car in the next move
								Point predicted_point2 = new Point(i,j-speed2);
								pointPassing1.add(new Point(i,j));
								for(int currentY = j - 1; currentY >= predicted_point2.getY(); currentY--) {
									Point tmpPoint2 = new Point(i, currentY);
									pointPassing1.add(tmpPoint2);
								}
							}
						}
				}
			}
			if(checkCommonPoint(pointPassing, pointPassing1)) {
				exitIsClear = false;
			}

		}
		else if(cmd == Direction.east) {
			//  the point the car will be at in the next move
			Point predicted_point = new Point(location.getX() + espeed,location.getY());
			//  the points list the car passing by in the next move(without current point)
			ArrayList<Point> pointPassing = new ArrayList<>();
			for(int currentX = location.getX() + 1; currentX <= predicted_point.getX(); currentX++) {
				Point tmpPoint = new Point(currentX, location.getY());
				pointPassing.add(tmpPoint);
			}
//                    System.out.println("the point that will pass is: ");
//                    System.out.println(pointPassing.get(0).getX() + " " + pointPassing.get(0).getY());

			//  the points list the car passing by in the next move(without current point)
			ArrayList<Point> pointPassing1 = new ArrayList<>();
			for(int i = location.getX() + 1; i <= location.getX() + espeed; i++) {
				for(int j = 0; j < visibleWorld.getHeight(); j++) {
					if(visibleWorld.containsCar(i, j)) {
						AbstractCar car1 = visibleWorld.getCarAtPosition(i, j);
						int speed1 = car1.getSpeed();
						Direction d1 = car1.getCMD();
//                                System.out.println("speed is: " + speed1);
//                                System.out.println(d1.toString());
						if(d1 == Direction.south) {
							//  get the predicted point of the car in the next move
							Point predicted_point1 = new Point(i,j+speed1);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j + 1; currentY <= predicted_point1.getY(); currentY++) {
								Point tmpPoint1 = new Point(i, currentY);
								pointPassing1.add(tmpPoint1);
							}
//                                    System.out.println("predicted point is: ");
//                                    System.out.println(predicted_point1.getX() + " " + predicted_point1.getY());
//                                    System.out.println("the point added is: ");
//                                    System.out.println(pointPassing1.get(0).getX() + " " + pointPassing1.get(0).getY());
						}
						else if(d1 == Direction.north) {
							//  get the predicted point of the car in the next move
							Point predicted_point1 = new Point(i,j-speed1);
//                                    //  the points list the car passing by in the next move(without current point)
//                                    ArrayList<Point> pointPassing1 = new ArrayList<>();
							pointPassing1.add(new Point(i,j));
							for(int currentY = j - 1; currentY >= predicted_point1.getY(); currentY--) {
								Point tmpPoint1 = new Point(i, currentY);
								pointPassing1.add(tmpPoint1);

							}
//                                    System.out.println("predicted point is: ");
//                                    System.out.println(predicted_point1.getX() + " " + predicted_point1.getY());
//                                    System.out.println("the point added is: ");
//                                    System.out.println(pointPassing1.get(0).getX() + " " + pointPassing1.get(0).getY());
						}
					}
					if(visibleWorld.containsPedestrian(i,j)) {
						Pedestrian pedestrian1 = visibleWorld.getPedestrianAtPosition(i,j);
						//get the car's speed
						int speed2 = pedestrian1.getSpeed();
						Direction d2 = pedestrian1.getMovingDirection();
						if(d2 == Direction.east) {
							//the predicted location the car will be at
							Point predicted_point2 = new Point(i + speed2, j);
							pointPassing1.add(new Point(i,j));
							for(int currentX = i + 1; currentX <= predicted_point2.getX(); currentX++) {
								Point tmpPoint2 = new Point(currentX, predicted_point2.getY());
								pointPassing1.add(tmpPoint2);
							}

						}
						else if(d2 == Direction.west) {
							Point predicted_point2 = new Point(i + speed2,j);
							pointPassing1.add(new Point(i,j));
							for(int currentX = i - 1; currentX >= predicted_point2.getX(); currentX--) {
								Point tmpPoint2 = new Point(currentX, predicted_point2.getY());
								pointPassing1.add(tmpPoint2);
							}

						}
						else if(d2 == Direction.south) {
							//  get the predicted point of the car in the next move
							Point predicted_point2 = new Point(i,j+speed2);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j + 1; currentY <= predicted_point2.getY(); currentY++) {
								Point tmpPoint2 = new Point(i, currentY);
								pointPassing1.add(tmpPoint2);
							}
						}
						else if(d2 == Direction.north) {
							//  get the predicted point of the car in the next move
							Point predicted_point2 = new Point(i,j-speed2);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j - 1; currentY >= predicted_point2.getY(); currentY--) {
								Point tmpPoint2 = new Point(i, currentY);
								pointPassing1.add(tmpPoint2);
							}
						}
					}
				}
			}

//                    System.out.println("sizr is" +  pointPassing1.size());
			//  check if two ArrayLists conflict or not
			if(checkCommonPoint(pointPassing, pointPassing1)) {

				exitIsClear = false;
			}

		}

		else if(cmd == Direction.west) {
			//  the point the car will be at in the next move
			Point predicted_point = new Point(location.getX() - espeed,location.getY());
			//  the points list the car passing by in the next move(without current point)
			ArrayList<Point> pointPassing = new ArrayList<>();
			for(int currentX = location.getX() - 1; currentX >= predicted_point.getX(); currentX--) {
				Point tmpPoint = new Point(currentX, location.getY());
				pointPassing.add(tmpPoint);
			}

			//  the points list the car passing by in the next move(without current point)
			ArrayList<Point> pointPassing1 = new ArrayList<>();
			for(int i = location.getX() - 1; i >= location.getX() - espeed; i--) {
				for(int j = 0; j < visibleWorld.getHeight(); j++) {
					if(visibleWorld.containsCar(i, j)) {
						AbstractCar car1 = visibleWorld.getCarAtPosition(i, j);
						int speed1 = car1.getSpeed();
						Direction d1 = car1.getCMD();
						if(d1 == Direction.south) {
							//  get the predicted point of the car in the next move
							Point predicted_point1 = new Point(i,j+speed1);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j + 1; currentY <= predicted_point1.getY(); currentY++) {
								Point tmpPoint1 = new Point(i, currentY);
								pointPassing1.add(tmpPoint1);
							}
						}
						else if(d1 == Direction.north) {
							//  get the predicted point of the car in the next move
							Point predicted_point1 = new Point(i,j-speed1);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j - 1; currentY >= predicted_point1.getY(); currentY--) {
								Point tmpPoint1 = new Point(i, currentY);
								pointPassing1.add(tmpPoint1);
							}


						}
					}
					if(visibleWorld.containsPedestrian(i,j)) {
						Pedestrian pedestrian1 = visibleWorld.getPedestrianAtPosition(i,j);
						//get the car's speed
						int speed2 = pedestrian1.getSpeed();
						Direction d2 = pedestrian1.getMovingDirection();
						if(d2 == Direction.east) {
							//the predicted location the car will be at
							Point predicted_point2 = new Point(i + speed2, j);
							pointPassing1.add(new Point(i,j));
							for(int currentX = i + 1; currentX <= predicted_point2.getX(); currentX++) {
								Point tmpPoint2 = new Point(currentX, predicted_point2.getY());
								pointPassing1.add(tmpPoint2);
							}

						}
						else if(d2 == Direction.west) {
							Point predicted_point2 = new Point(i + speed2,j);
							pointPassing1.add(new Point(i,j));
							for(int currentX = i - 1; currentX >= predicted_point2.getX(); currentX--) {
								Point tmpPoint2 = new Point(currentX, predicted_point2.getY());
								pointPassing1.add(tmpPoint2);
							}

						}
						else if(d2 == Direction.south) {
							//  get the predicted point of the car in the next move
							Point predicted_point2 = new Point(i,j+speed2);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j + 1; currentY <= predicted_point2.getY(); currentY++) {
								Point tmpPoint2 = new Point(i, currentY);
								pointPassing1.add(tmpPoint2);
							}
						}
						else if(d2 == Direction.north) {
							//  get the predicted point of the car in the next move
							Point predicted_point2 = new Point(i,j-speed2);
							pointPassing1.add(new Point(i,j));
							for(int currentY = j - 1; currentY >= predicted_point2.getY(); currentY--) {
								Point tmpPoint2 = new Point(i, currentY);
								pointPassing1.add(tmpPoint2);
							}
						}
					}
				}
			}
			// check if two ArrayLists conflict or not
			if(checkCommonPoint(pointPassing, pointPassing1)) {
				exitIsClear = false;
			}
		}
	}
	public void reMakeDecisions() {
		trafficLightRed = false;
		atWhiteLine = false;
		wallAhead = false;
		approaching_vertical_zebra = false;
		approaching_horizontal_zebra = false;
		exitIsClear = true;
	}

	public Boolean checkCommonPoint(ArrayList<Point> l1, ArrayList<Point> l2) {
		l1.retainAll(l2);
		return !l1.isEmpty();
	}

}
