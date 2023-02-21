package simulated_cars;


import core_car_sim.*;
import core_car_sim.AbstractCell.CellType;
import core_car_sim.AbstractInformationCell.InformationCell;
import core_car_sim.RoadCell.RoadMarking;
import core_car_sim.TrafficLightCell.TrafficLightCellInformation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

// reactive car: the car will follow all the recommendations from RoTRA
public class RudeCar extends AbstractROTRCar implements CarEvents{
    
    private boolean isFinished = false;
    private boolean wallAhead;
    private boolean atWhiteLine;
    private boolean exitIsClear;
  

    ArrayDeque<Direction> directions = new ArrayDeque<>();
    private HashMap<CarAction,CarPriority> actionsToDo = new HashMap<CarAction,CarPriority>();
   
    public RudeCar(Point startPos, Point endPos, int startingSpeed){
        super(startPos,endPos, startingSpeed, System.getProperty("user.dir") + "/RoTRExperiments/resources/bluecar.png", CarType.car_AI);
        addCarEventListener(this);
    }
    
    // check if there are same points between two ArrayList
    public Boolean checkCommonPoint(ArrayList<Point> l1, ArrayList<Point> l2) {
        l1.retainAll(l2);
        if(l1.isEmpty()) {
            return false;
        }
        else {
            return true;
        }        
    }
    
    // reset the action list
    public void resetActions() { 
        actionsToDo.clear();
    }

    // car decision making system, make use of the received observations to decide what
    // should be the current moving direction 
    @Override
    protected ArrayDeque<Direction> getSimulationRoute(){
        setSpeed(1);
        directions.push(cmd);
        updateOutcomes();
        if(getSpeed() == 0) {
            directions.clear();
        }

        
//        System.out.println("----------------------------------------------------------------------");
//        // print out the car action list
//        System.out.println("action list:");
//        for(Entry<CarAction, CarPriority> ca1 : actionsToDo.entrySet()){
//            System.out.print(ca1.getKey().toString() + " ");
//            System.out.println(ca1.getValue());
//        }
//        System.out.println("----------------------------------------------------------------------");
//        System.out.println("intention list: "); 
//        for(Entry<CarIntention, Boolean> i : intentions.entrySet()){
//            if(i.getValue()) {
//               System.out.println(i.getKey().toString());
//            }        
//        }
        System.out.println("----------------------------------------------------------------------");
        //print out the car belief list
        System.out.println("belief list");
        for(Entry<CarBelief,Boolean> k : beliefs.entrySet()){
            if(k.getValue()){
                System.out.println(k.getKey().toString());
               
            }
        }
        System.out.println("----------------------------------------------------------------------");

        //clear beliefs,intentions,actions after we get the moving directions
        clearBeliefs();
        clearIntentions();
        resetActions();
        return directions;
    }

    @Override
    protected boolean isFinished(Point arg0)
    {
        if(currentPosition.equals(endPosition)) {
            isFinished = true;
        }
        else {
            isFinished = false;
        }
        return isFinished;
    }

    @Override
    public void worldUpdate(WorldSim visibleWorld, Point location) 
    {
        updateBeliefs(visibleWorld, location); 
        updateIntentions(cmd,pmd);
    }

    @Override
    public void actionUpdate(CarAction action, CarPriority priority) 
    {
        if(priority == CarPriority.CP_MUST) {
            switch(action)
            {
            //TODO
            case CA_adjust_speed:
                actionsToDo.put(action, priority);
                break;
            case CA_allow_cyclists_moto_pass:   //not simulated 
                break;
            case CA_allow_emergency_vehicle_to_pass: //not simulated
                break;
            case CA_allow_extra_space: //not simulated
                break;
            case CA_allow_extra_space_for_works_vehicles: //not simulated
                break;
            case CA_allow_traffic_to_pass: //not simulated
                break;
            case CA_allow_undertaking: //not simulated
                break;
            case CA_allowed_to_proceed: //not simulated
                break;
            case CA_approach_left_hand_lane: //not simulated
                break;
            case CA_approach_with_caution: //not simulated
                break;
            // TODO
            case CA_avoidLaneChanges:
                if((pmd == Direction.north && cmd == Direction.east) || (pmd == Direction.north && cmd == Direction.west)) {
                    
                }
                else if((pmd == Direction.south && cmd == Direction.east) || (pmd == Direction.north && cmd == Direction.west)) {
                    
                }
                else if((pmd == Direction.east && cmd == Direction.north) || (pmd == Direction.east && cmd == Direction.south)) {
                    
                }
                else if((pmd == Direction.west && cmd == Direction.north) || (pmd == Direction.west && cmd == Direction.south)) {
                    
                }
                actionsToDo.put(action, priority);
                break;
            case CA_avoidRightHandLane:
                break;
            case CA_avoid_blocking_sideroads://not simulated
                break;
            case CA_avoid_bus_lane: //not simulated
                break;
            case CA_avoid_closed_lane://not simulated
                break;
            case CA_avoid_coasting://not simulated
                break;
            case CA_avoid_coned_off_area://not simulated
                break;
            case CA_avoid_crossing_central_reservation://not simulated
                break;
            case CA_avoid_crossing_crossing://not simulated
                break;
            case CA_avoid_crossing_level_crossing://not simulated
                break;
            case CA_avoid_cutting_corner://not simulated
                break;
            case CA_avoid_drive_against_traffic_flow://not simulated
                break;
            case CA_avoid_driving_on_rails://not simulated
                break;
            case CA_avoid_emergency_area://not simulated
                break;
            //TODO
            case CA_avoid_hard_shoulder:
                actionsToDo.put(action, priority);
                break;
            case CA_avoid_harsh_braking://not simulated
                break;
            case CA_avoid_horn://not simulated
                break;
            case CA_avoid_hov_lane://not simulated
                break;
            case CA_avoid_lane_lane://not simulated
                break;
            //TODO
            case CA_avoid_lane_switching:
                actionsToDo.put(action, priority);
                break;
            case CA_avoid_level_crossing://not simulated
                break;
            case CA_avoid_loading_unloading://not simulated
                break;
            case CA_avoid_motorway://not simulated
                break;
            case CA_avoid_non://not simulated
                break;
            //TODO
            case CA_avoid_overtaking:
                actionsToDo.put(action, priority);
                break;
            //TODO
            case CA_avoid_overtaking_on_left:
                actionsToDo.put(action, priority);
                break;
            case CA_avoid_parking: // not simulated
                break;
            case CA_avoid_parking_against_flow: //not simulated
                break;
            case CA_avoid_pick_up_set_down: // not simulated
                break;
            //TODO
            case CA_avoid_reversing:
                actionsToDo.put(action, priority);
                break;
            case CA_avoid_revs:// not simulated
                break;
            //TODO
            case CA_avoid_stopping:
                actionsToDo.put(action, priority);
                break;
            case CA_avoid_tram_reserved_road: // not simulated
                break;
            //TODO
            case CA_avoid_undertaking:
                actionsToDo.put(action, priority);
                break;
            case CA_avoid_uturn: //not simulated
                break;
            case CA_avoid_waiting://not simulated
                break;
            case CA_avoid_weaving://not simulated
                break;
            case CA_brake_early_lightly://not simulated
                break;
            case CA_brake_hard://not simulated
                break;
            case CA_buildup_speed_on_motorway://not simulated
                break;
            //TODO
            case CA_cancel_overtaking:
                actionsToDo.put(action, priority);
                break;
            //TODO
            case CA_cancel_reverse:
                actionsToDo.put(action, priority);
                break;
            case CA_cancel_signals://not simulated
                break;
            //TODO
            case CA_cancel_undertaking:
                actionsToDo.put(action, priority);
                break;
            case CA_clear_ice_snow_all_windows://not simulated
                break;
            case CA_close_to_kerb://not simulated
                break;
            case CA_consideration_others://not simulated
                break;
            case CA_doNotEnterWhiteDiagonalStripeWhiteBrokenBorder://not simulated
                break;
            case CA_doNotEnterWhiteDiagonalStripeWhiteSolidBorder://not simulated
                break;
            case CA_do_not_drive://not simulated
                break;
            case CA_do_not_hestitate://not simulated
                break;
            //TODO
            case CA_do_not_overtake:
                actionsToDo.put(action, priority);
                break;
            case CA_do_not_park_in_passing_place: //not simulated
                break;
            //TODO
            case CA_do_not_reverse:
                actionsToDo.put(action, priority);
                break;
            //TODO
            case CA_do_not_stop:
                actionsToDo.put(action, priority);
                break;
            //TODO
            case CA_dontExceedTempSpeedLimit:
                actionsToDo.put(action, priority);
                break;
            //TODO
            case CA_dont_cross_solid_white: 
                break;
            case CA_dont_use_central_reservation://not simulated
                break;
            case CA_drive_care_attention://not simulated
                break;
            //TODO
            case CA_drive_slowly:
                actionsToDo.put(action, priority);
                break;
            //TODO
            case CA_drive_very_slowly:
                actionsToDo.put(action, priority);
                break;
            case CA_drive_very_slowly_on_bends: //not simulated
                break;
            case CA_drop_back://not simulated
                break;
            case CA_dry_brakes://not simulated
                break;
            case CA_ease_off://not simulated
                break;
            case CA_engage_child_locks://not simulated
                break;
            case CA_engage_parking_break://not simulated
                break;
            case CA_engine_off:
                setSpeed(0);
                actionsToDo.put(action, priority);
                break;
            case CA_find_other_route://not simulated
                break;
            case CA_find_quiet_side_road://not simulated
                break;
            case CA_find_safe_place_to_stop://not simulated
                break;
            case CA_fit_booster_seat://not simulated
                break;
            case CA_flash_amber_beacon://not simulated
                break;
            case CA_fog_lights_off://not simulated
                break;
            case CA_fog_lights_on://not simulated
                break;
            case CA_followLaneSigns://not simulated
                break;
            case CA_follow_dvsa_until_stopped://not simulated
                break;
            case CA_follow_police_direction://not simulated
                break;
            case CA_follow_sign://not simulated
                break;
            case CA_follow_signs://not simulated
                break;
            case CA_get_in_lane://not simulated
                break;
            case CA_get_into_lane://not simulated
                break;
            case CA_get_off_road://not simulated
                break;
            case CA_give_extensive_extra_seperation_distance://not simulated
                break;
            case CA_give_extra_seperation_distance://not simulated
                break;
            case CA_give_priority_to_public_transport://not simulated
                break;
            case CA_give_priority_to_right://not simulated
                break;
            case CA_give_room_when_passing://not simulated
                break;
            case CA_give_signal://not simulated
                break;
            case CA_give_up_control://not simulated
                break;
            case CA_give_way_at_dotted_white_line://not simulated
                break;
            case CA_give_way_other_roads://not simulated
                break;
            case CA_give_way_to_other://not simulated
                break;
            case CA_give_way_to_pedestrians: //TODO
                setSpeed(0);
                break;
            case CA_give_way_to_tram://not simulated
                break;
            case CA_goBetweenLaneDividers://not simulated
                break;
            case CA_go_to_left_hand_land://not simulated
                break;
            case CA_going_left_use_left://not simulated
                break;
            case CA_going_right_use_left://not simulated
                break;
            case CA_handbrake_on://not simulated
                break;
            case CA_headlights_on://not simulated
                break;
            //TODO
            case CA_increase_distance_to_car_infront:
                actionsToDo.put(action, priority);
                break;
            case CA_indicatorOn://not simulated
                break;
            case CA_indicator_on://not simulated
                break;
            case CA_keep_crossing_clear://not simulated
                break;
            //TODO
            case CA_keep_left:
                
                actionsToDo.put(action, priority);
                break;
            //TODO
            case CA_keep_left_lane:
                actionsToDo.put(action, priority);
                break;
            //TODO
            case CA_keep_safe_distance:
                actionsToDo.put(action, priority);
                break;
            case CA_keep_sidelights_on://not simulated
                break;
            //TODO
            case CA_keep_under_speed_limit:
                actionsToDo.put(action, priority);
                break;
            case CA_keep_well_back://not simulated
                break;
            case CA_lane_clear://not simulated
                break;
            case CA_leave_space_for_manover://not simulated
                break;
            case CA_leave_space_to_stop://not simulated
                break;
            case CA_light_and_number_plates_clean://not simulated
                break;
            case CA_lock://not simulated
                break;
            case CA_maintained_reduced_speed://not simulated
                break;
            case CA_match_speed_to_motorway://not simulated
                break;
            case CA_mergeInTurn://not simulated
                break;
            case CA_merge_in_turn://not simulated
                break;
            case CA_mini://not simulated
                break;
            case CA_minimise_reversing://not simulated
                break;
            case CA_mirrors_clear://not simulated
                break;
            case CA_move_adjacent_lane://not simulated
                break;
            case CA_move_left: //TODO
                actionsToDo.put(action, priority);
                break;
            case CA_move_quickly_past: // TODO
                actionsToDo.put(action, priority);
                break;
            case CA_move_to_left_hand_lane: //TODO
                actionsToDo.put(action, priority);
                break;
            case CA_must_stop_pedestrian_crossing://TODO
                actionsToDo.put(action, priority);
                break;
            case CA_nextLaneClear://not simulated
                break;
            case CA_next_safe_stop://not simulated
                break;
            case CA_not_drive_dangerously://not simulated
                break;
            case CA_not_overtaken: //TODO
                actionsToDo.put(action, priority);
                break;
            case CA_obey_signal://not simulated
                break;
            case CA_obey_work_vehicle_sign://not simulated
                break;
            case CA_overtake_on_right: //TODO
                actionsToDo.put(action, priority);
                break;
            case CA_park_as_close_to_side://not simulated
                break;
            case CA_parking_lights_on://not simulated
                break;
            case CA_pass_around://not simulated
                break;
            case CA_position_right_turn://not simulated
                break;
            case CA_prepare_drop_back://not simulated
                break;
            case CA_prepare_load://not simulated
                break;
            case CA_prepare_route://not simulated
                break;
            case CA_prepare_to_change_lane://not simulated
                break;
            case CA_prepare_to_stop://not simulated
                break;
            case CA_priority_to_motoway_traffic://not simulated
                break;
            //TODO
            case CA_pull_into_hard_shoulder:
                actionsToDo.put(action, priority);
                break;
            case CA_pull_into_passing_place://not simulated
                break;
            case CA_pull_over_safe_place://not simulated
                break;
            case CA_pull_up_in_visible_distance://not simulated
                break;
            case CA_put_on_seatbelts://not simulated
                break;
            case CA_reduce_distance_between_front_vehicle: //TODO
                actionsToDo.put(action, priority);
                break;
            case CA_reduce_lighting://not simulated
                break;
            case CA_reduce_overall_speed:
                //check current speed
                int speed1 = getSpeed();
                if(speed1 > 1) {
                    setSpeed(speed1 - 1);
                }
                actionsToDo.put(action, priority);
                break;
            case CA_reduce_speed:
                int speed2 = getSpeed();
                if(speed2 > 1) {
                    setSpeed(speed2 - 1);
                }
                actionsToDo.put(action, priority);
                break;
            case CA_reduce_speed_if_pedestrians://not simulated
                break;
            case CA_reduce_speed_on_slip_road://not simulated
                break;
            case CA_release_brake://not simulated
                break;
            case CA_remove_all_snow://not simulated
                break;
            case CA_remove_flash_intention://not simulated 
                break;
            case CA_remove_horn_intention://not simulated
                break;
            case CA_reverse_into_drive://not simulated
                break;
            case CA_reverse_to_passing_place://not simulated
                break;
            case CA_road_clear_to_manover://not simulated
                break;
            //TODO
            case CA_safe_distance:
                actionsToDo.put(action, priority);
                break;
            case CA_safe_pull_over_and_stop://not simulated
                break;
            case CA_select_lane://not simulated
                break;
            case CA_set_hazards_off://not simulated (hazards is hazard warning light)
                break;
            case CA_set_headlights_to_dipped://not simulated
                break;
            case CA_signal: //not simulated
                break;
            case CA_signal_left://not simulated
                break;
            case CA_signal_left_on_exit://not simulated
                break;
            case CA_signal_right://not simulated
                break;
            case CA_slow_down:
                int speed3 = getSpeed();
                if(speed3 > 1) {
                    setSpeed(speed3 - 1);
                }
                actionsToDo.put(action, priority);
                break;
            case CA_slow_down_and_stop:
                setSpeed(0);
                actionsToDo.put(action, priority);
                break;
            case CA_space_for_vehicle://not simulated
                break;
            case CA_stay_in_lane://not simulated
                break;
            case CA_stay_on_running_lane://not simulated
                break;
            case CA_steady_speed://not simulated
                break;
            case CA_stop:
                setSpeed(0);
                actionsToDo.put(action, priority);
                break;
            case CA_stopCrossDoubleWhiteClosestSolid://not simulated
                break;
            case CA_stopCrossingHazardWarningLine://not simulated
                break;
            case CA_stop_and_turn_engine_off:
                setSpeed(0);
                actionsToDo.put(action, priority);
                break;
            case CA_stop_at_crossing: //not simulated
                break;
            case CA_stop_at_crossing_patrol://not simulated
                break;
            case CA_stop_at_sign://not simulated
                break;
            case CA_stop_at_white_line: //TODO
                setSpeed(0);
                actionsToDo.put(action, priority);
                break;
            case CA_switch_off_engine:
                setSpeed(0);
                actionsToDo.put(action, priority);
                break;
            case CA_travel_sign_direction://not simulated
                break;
            case CA_treat_as_roundabout://not simulated
                break;
            case CA_treat_as_traffic_light://not simulated
                break;
            case CA_turn_foglights_off://not simulated
                break;
            case CA_turn_into_skid://not simulated
                break;
            case CA_turn_sidelights_on://not simulated
                break;
            case CA_use_central_reservation://not simulated
                break;
            case CA_use_crawler_lane://not simulated
                break;
            case CA_use_demisters://not simulated
                break;
            case CA_use_hazard_lights://not simulated
                break;
            case CA_use_left_indicator://not simulated
                break;
            case CA_use_right_indicator://not simulated
                break;
            case CA_use_road://not simulated
                break;
            case CA_use_signals://not simulated
                break;
            case CA_use_tram_passing_lane://not simulated
                break;
            case CA_use_windscreen_wipers://not simulated
                break;
            case CA_wait_at_advanced_stop://not simulated
                break;
            case CA_wait_at_white_line:
                setSpeed(0);
                actionsToDo.put(action, priority);
                break;
            case CA_wait_at_first_white_line://not simulated
                break;
            case CA_wait_for_gap_before_moving_off: //TODO
                actionsToDo.put(action, priority);
                break;
            case CA_wait_until_clear://not simulated
                break;
            case CA_wait_until_route_clear://not simulated
                break;
            case CA_wait_until_safe_gap: //TODO
                actionsToDo.put(action, priority);
                break;
            case CA_wheel_away_from_kerb://not simulated
                break;
            case CA_wheel_toward_from_kerb://not simulated
                break;
            }
        }
        
    }
    
    public void updateIntentions(Direction cmd, Direction pmd) {
        for (CarIntention ci : CarIntention.values()) {
            switch(ci) {
                case CI_approachingTrafficLight:
                    break;
                case CI_areaWithSolidWhiteBorder: 
                    break;
                case CI_beInCycleLane:
                    break;
                case CI_brake:
                    break;
                case CI_changeCourseOrDirection:
                    break;
                case CI_changeLane:
                    break;
                case CI_crossControlledCrossing:
                    break;
                case CI_crossDoubleWhiteClosestSolid: 
                    break; 
                case CI_crossDualCarriageWay:
                    break;
                case CI_crossHazardWarningLine: 
                    break;
                case CI_crossLevelCrossing: 
                    break;
                case CI_diagnosingFaults:
                    break;
                case CI_dropOffPassengers:
                    break;
                case CI_enterBoxJunction:
                    break;
                case CI_enterMotorway:
                    break;
                case CI_enterTramReservedRoad:
                    break;
                case CI_enterWhiteDiagonalStripeWhiteBrokenBorder:
                    break;
                case CI_enterWhiteDiagonalStripeWhiteSolidBorder:
                    break;
                case CI_firstExitRoundabout:
                    break;
                case CI_flashHeadlight:
                    break;
                case CI_goodsLoadingUn:
                    break;
                case CI_joinMotorway:
                    break;
                case CI_leaveMotorway:
                    break;
                case CI_loadUnloading:
                    break;
                case CI_otherRoundabout:
                    break;
                case CI_overtake:
                    break;
                case CI_overtakeSnowplow:
                    break;
                case CI_park:
                    break;
                case CI_passParkedVehicles:
                    break;
                case CI_passVehicles:
                    break;
                case CI_pullIntoDriveway:
                    break;
                case CI_reversing:
                    break;
                case CI_rightExitRoundabout:
                    break;
                case CI_selectLane:
                    break;
                case CI_setHazardsOn:
                    break;
                case CI_setOff:
                    for(Entry<CarBelief, Boolean> bf : beliefs.entrySet()) {
                        if(bf.getKey() == CarBelief.CB_atTrafficLight) {
                            if (bf.getValue()) {
                                intentions.put(ci, true);
                            }
                        }
                    }
                    break;
                case CI_settingOff:
                    break;
                case CI_soundHorn:
                    break;
                case CI_stop:
                    break;
                case CI_towing:
                    break;
                case CI_turnOff: 
                    break;
                case CI_turnRight:
                    //check the previous moving direction and current moving direction to
                    //identify the turn right intentions
                    if(pmd == Direction.north && cmd == Direction.east) {
                        intentions.put(ci, true);
                    }
                    else if(pmd == Direction.south && cmd == Direction.west) {
                        intentions.put(ci, true);
                    }
                    else if(pmd == Direction.east && cmd == Direction.south){
                        intentions.put(ci, true);
                    }
                    else if(pmd == Direction.west && cmd == Direction.north) {
                        intentions.put(ci, true);
                    }
                    break;
                case CI_undertaking:
                    break;
                case CI_uturn: 
                    break;
                default:
                    break;
            }
        }
    }
    
    
    // visible world: the visible world that the car can see
    // location: current location of the car in its visible world
    public void updateBeliefs(WorldSim visibleWorld, Point location)
    {
        for (CarBelief cb : CarBelief.values())
        {
            switch(cb)
            {
            case CB_allChildrenUsingChildSeatAsRequired: // not simulated
                break;
            case CB_allPassengersWearingSeatBeltsAsRequired: // not simulated
                break;
            case CB_approachingCorner:
                switch (cmd)
                {
                case east:
                    wallAhead = visibleWorld.getCell(location.getX() + 1, location.getY()).getCellType() != CellType.ct_road;
                    break;
                case north:
                    wallAhead = visibleWorld.getCell(location.getX(), location.getY()-1).getCellType() != CellType.ct_road;
                    break;
                case south:
                    wallAhead = visibleWorld.getCell(location.getX(), location.getY()+1).getCellType() != CellType.ct_road;
                    break;
                case west:
                    wallAhead = visibleWorld.getCell(location.getX() - 1, location.getY()).getCellType() != CellType.ct_road;
                    break;
                }
                beliefs.put(cb, wallAhead);
                break;
            case CB_atTrafficLight://Only traffic lights simulated
                atWhiteLine = false;
                beliefs.put(cb, false);
                //at traffic light white line
                for (int i = 0; i < visibleWorld.getWidth(); i++) {
                    for(int j = 0; j < visibleWorld.getWidth();j++) {
                        //check if the cell type is information cell
                        if(visibleWorld.getCell(i, j).getCellType() == CellType.ct_information ) {
                            //check if the information cell is traffic light
                            AbstractInformationCell aic = (AbstractInformationCell)visibleWorld.getCell(i, j);
                            if(aic.getInformationType() == InformationCell.ic_trafficLight) {
                                
                                TrafficLightCell tlc = (TrafficLightCell)aic;
                                //the list of this traffic light cell faces 
                                ArrayList<Direction> faces = tlc.getFaces();
                                if(faces.size() != 0) {
                                    if(faces.get(0) == Direction.north) {
                                       //the point of the white line
                                       Point visibleWorldStopPoint = new Point(i-1,j);
                                       //the traffic light affects cell in the car's visible world
                                       ArrayList<Point> affectedCells = new ArrayList<>();
                                       for(int k = j; k >= 0; k--) {
                                           affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                       }
                                       if(cmd == Direction.south && affectedCells.contains(location)) {
                                           atWhiteLine = visibleWorldStopPoint.equals(location);
                                           beliefs.put(cb, atWhiteLine);
                                           if(atWhiteLine) {
                                               intentions.put(CarIntention.CI_approachingTrafficLight, true);
                                           }
                                       }
                                       
                                    }
                                    else if(faces.get(0) == Direction.south) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i+1,j);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = j ; k < visibleWorld.getHeight();k++) {
                                            affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                        }
                                        if(cmd == Direction.north && affectedCells.contains(location)) {
                                            atWhiteLine = visibleWorldStopPoint.equals(location);
                                            beliefs.put(cb, atWhiteLine);
                                            if(atWhiteLine) {
                                                intentions.put(CarIntention.CI_approachingTrafficLight, true);
                                            }
                                        }     
                                    }
                                    else if(faces.get(0) == Direction.east) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j-1);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i;k < visibleWorld.getWidth();k++) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.west && affectedCells.contains(location)) {
                                            atWhiteLine = visibleWorldStopPoint.equals(location);
                                            beliefs.put(cb, atWhiteLine);
                                            if(atWhiteLine) {
                                                intentions.put(CarIntention.CI_approachingTrafficLight, true);
                                            }
                                        }
                                    }
                                    else if(faces.get(0) == Direction.west) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j+1);
                                        //the traffic light affects cell in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i; k >= 0;k--) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.east && affectedCells.contains(location)) {
                                            atWhiteLine = visibleWorldStopPoint.equals(location);
                                            beliefs.put(cb, atWhiteLine);
                                            if(atWhiteLine) {
                                                intentions.put(CarIntention.CI_approachingTrafficLight, true);
                                            }
                                        }
                                        
                                    }
                                }
                            }
                        }
                    }
                }
                 break;
            case CB_behindWantToOvertake: //Only for north
//                beliefs.put(cb, visibleWorld.containsCar(location.getX(), location.getY() - 1));//Not car in front yet
//                intentions.put(CarIntention.CI_overtake, beliefs.get(cb));
                break;
            case CB_bendInRoad://Not simulated
                break;
            case CB_brokendown://Car cannot break down.
                beliefs.put(cb, false);
                break;
            case CB_canReadNumberPlate://not simulated
                break;
            
            case CB_canStopBeforeCarInFrontStops:
                if(cmd == Direction.north) {
                    if(visibleWorld.containsCar(location.getX(), location.getY() -1)) {
                       AbstractCar ab = visibleWorld.getCarAtPosition(location.getX(), location.getY() -1);
                       if(ab.getSpeed() == 0) {
                           beliefs.put(cb, true);  
                       }
                    }
                }
                else if(cmd == Direction.south) {
                    if(visibleWorld.containsCar(location.getX(), location.getY() + 1)) {
                        AbstractCar ab = visibleWorld.getCarAtPosition(location.getX(), location.getY() +1);
                        if(ab.getSpeed() == 0) {
                            beliefs.put(cb, true);
                        }
                    }
                }
                else if(cmd == Direction.east) {
                    if(visibleWorld.containsCar(location.getX() + 1 , location.getY())) {
                        AbstractCar ab = visibleWorld.getCarAtPosition(location.getX() + 1,location.getY());
                        if(ab.getSpeed() == 0) {
                            beliefs.put(cb, true);
                        }
                    }
                }
                else if(cmd == Direction.west) {
                    if(visibleWorld.containsCar(location.getX() - 1 , location.getY())) {
                        AbstractCar ab = visibleWorld.getCarAtPosition(location.getX() -1 , location.getY());
                        if(ab.getSpeed() == 0) {
                            beliefs.put(cb, true);
                        }
                    }
                }
                break;
            
            // check that whether there are cars towards you.
            case CB_carTowardsYou:
                //check the car's current moving Direction:
                if(cmd == Direction.north) {
                    if(visibleWorld.containsCar(location.getX(), location.getY() -1)) {
                        beliefs.put(cb,true);
                    }
                }
                else if(cmd == Direction.south) {
                    if(visibleWorld.containsCar(location.getX(), location.getY() + 1)) {
                        beliefs.put(cb,true);
                    }
                }
                else if(cmd == Direction.east) {
                    if(visibleWorld.containsCar(location.getX() + 1 , location.getY())) {
                        beliefs.put(cb,true);
                    }
                }
                else if(cmd == Direction.west) {
                    if(visibleWorld.containsCar(location.getX() - 1 , location.getY())) {
                        beliefs.put(cb,true);
                    }
                }
                break;
            case CB_carriageway: //Not simulated
                break;
            case CB_centerLine: //Not simulated
                break;
            case CB_clearRoadAhead: // Not simulated
                break;
            case CB_clearToManover: //Not simulated
                break;
            case CB_clearToTurnOff: //Not simulated
                break;
            case CB_clearway:       // Not simulated
                break;
            case CB_completeOvertakeBeforeSolidWhiteLine://No solid white lines simulated
                break;
            case CB_damagedOrInjury:
                beliefs.put(cb, isCrashed());
                break;
            case CB_directionSigns: //Not simulated
                break;
            case CB_dottedWhiteLineAcrossRoad: //Not simulated
                break;
            case CB_doubleWhiteLines: //Not simulated
                break;
            case CB_driving: //Not simulated
                break;
            case CB_dualCarriageWay: //Not simulated
                break;
            case CB_enterWhiteDiagonalStripeWhiteBrokenBorderNecessary: //Not simulated
                break;
            case CB_essentialTravel: //Not simulated
                break;
            case CB_exceedingSpeedLimit: 
                beliefs.put(cb, visibleWorld.speedLimit(location.getX(), location.getY()) < getSpeed());
                break;
            
            // check whether there are cars will block current car's way 
            case CB_exitClear:
                 exitIsClear = true;
                
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
                   
                 //TODO
//                 for(Point p: pointPassing) {
//                     System.out.println("the car's passing position is: " + p.getX() + " " + p.getY());
//                 }
                   
                 for(int i = 0; i < visibleWorld.getWidth(); i++) {
                       for(int j = location.getY() - 1; j >= location.getY() - espeed;j--) {
                           if(visibleWorld.containsCar(i,j)) {           
                               AbstractCar car1 = visibleWorld.getCarAtPosition(i, j);
                               //get the car's speed
                               int speed1 = car1.getSpeed();
                               Direction d1 = car1.getCMD();
                               
//                               System.out.println("other car's speed is: " + speed1);
//                               System.out.println("other car's current moving direction is: " + d1.toString());
                               if(d1 == Direction.east) {
                                   //the predicted location the car will be at
                                   Point predicted_point1 = new Point(i + speed1, j);
                                   //the point list that the car will pass from its current location to its predicted point(without the current position)
                                   ArrayList<Point> pointPassing1 = new ArrayList<>();
                                   
                                   for(int currentX = i + 1; currentX <= predicted_point1.getX(); currentX++) {
                                       Point tmpPoint1 = new Point(currentX, predicted_point1.getY());
                                       pointPassing1.add(tmpPoint1);
                                   }   
                                   //TODO
//                                   for(Point p: pointPassing1) {
//                                       System.out.println("the other car's passing position is: " + p.getX() + " " + p.getY());
//                                   }
//     
                                   if(checkCommonPoint(pointPassing, pointPassing1)) {
                                       exitIsClear = false;
                                   }
                               }
                               else if(d1 == Direction.west) {
                                   Point predicted_point1 = new Point(i - speed1,j);
                                   //the point list that the car will pass from its current location to its predicted point(without the current position)
                                   ArrayList<Point> pointPassing1 = new ArrayList<>();
                                   
                                   for(int currentX = i - 1; currentX >= predicted_point1.getX(); currentX--) {
                                       Point tmpPoint1 = new Point(currentX, predicted_point1.getY());
                                       pointPassing1.add(tmpPoint1);
                                   }
                                   //TODO
//                                   for(Point p: pointPassing1) {
//                                       System.out.println("the other car's passing position is: " + p.getX() + " " + p.getY());
//                                   }
                                   if(checkCommonPoint(pointPassing, pointPassing1)) {
                                       exitIsClear = false;
                                   }
                               }
                           }
                       }
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
                    
                    for(int i = 0; i < visibleWorld.getWidth(); i++) {
                        for(int j = location.getY() + 1; j <= location.getY() + espeed; j++) {
                            if(visibleWorld.containsCar(i, j)) {
                                //get the car at this point
                                AbstractCar car1 = visibleWorld.getCarAtPosition(i, j);
                                //get the car's speed
                                int speed1 = car1.getSpeed();
                                Direction d1 = car1.getCMD();
                                if(d1 == Direction.east) {
                                    //the predicted location the car will be at
                                    Point predicted_point1 = new Point(i + speed1, j);
                                    //the point list that the car will pass from its current location to its predicted point(without the current position)
                                    ArrayList<Point> pointPassing1 = new ArrayList<>();
                                    
                                    for(int currentX = i + 1; currentX <= predicted_point1.getX(); currentX++) {
                                        Point tmpPoint1 = new Point(currentX, predicted_point1.getY());
                                        pointPassing1.add(tmpPoint1);
                                    }
                                    if(checkCommonPoint(pointPassing, pointPassing1)) {
                                        exitIsClear = false;
                                    }
                                }
                                else if(d1 == Direction.west) {
                                    Point predicted_point1 = new Point(i - speed1,j);
                                    //the point list that the car will pass from its current location to its predicted point(without the current position)
                                    ArrayList<Point> pointPassing1 = new ArrayList<>();
                                    
                                    for(int currentX = i - 1; currentX >= predicted_point1.getX(); currentX --) {
                                        Point tmpPoint1 = new Point(currentX, predicted_point1.getY());
                                        pointPassing1.add(tmpPoint1);
                                    }
                                    if(checkCommonPoint(pointPassing, pointPassing1)) {
                                        exitIsClear = false;
                                    }
                                }
                            }
                        }
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
                    
                    for(int i = location.getX() + 1; i <= location.getX() + espeed; i++) {
                        for(int j = 0; j < visibleWorld.getHeight(); j++) {
                            if(visibleWorld.containsCar(i, j)) {
                                AbstractCar car1 = visibleWorld.getCarAtPosition(i, j);
                                int speed1 = car1.getSpeed();
                                Direction d1 = car1.getCMD();
                                if(d1 == Direction.south) {
                                    //  get the predicted point of the car in the next move
                                    Point predicted_point1 = new Point(i,j+speed1);
                                    //  the points list the car passing by in the next move(without current point)
                                    ArrayList<Point> pointPassing1 = new ArrayList<>();    
                                    for(int currentY = j + 1; currentY <= predicted_point1.getY(); currentY++) {
                                        Point tmpPoint1 = new Point(i, currentY);
                                        pointPassing1.add(tmpPoint1);
                                    }
                                    
                                    // check if two ArrayLists conflict or not 
                                    if(checkCommonPoint(pointPassing, pointPassing1)) {
                                        exitIsClear = false;
                                    }
                                }
                                else if(d1 == Direction.north) {
                                    //  get the predicted point of the car in the next move
                                    Point predicted_point1 = new Point(i,j-speed1);
                                    //  the points list the car passing by in the next move(without current point)
                                    ArrayList<Point> pointPassing1 = new ArrayList<>();
                                    for(int currentY = j - 1; currentY >= predicted_point1.getY(); currentY--) {
                                        Point tmpPoint1 = new Point(i, currentY);
                                        pointPassing1.add(tmpPoint1);
                                        
                                    }
                                    
                                    //  check if two ArrayLists conflict or not
                                    if(checkCommonPoint(pointPassing, pointPassing1)) {
                                        exitIsClear = false;
                                    }
                                }
                            }
                        }
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
                    
                    for(int i = location.getX() - 1; i >= location.getX() - espeed; i--) {
                        for(int j = 0; j < visibleWorld.getHeight(); j++) {
                            if(visibleWorld.containsCar(i, j)) {
                                AbstractCar car1 = visibleWorld.getCarAtPosition(i, j);
                                int speed1 = car1.getSpeed();
                                Direction d1 = car1.getCMD();
                                if(d1 == Direction.south) {
                                    //  get the predicted point of the car in the next move
                                    Point predicted_point1 = new Point(i,j+speed1);
                                    //  the points list the car passing by in the next move(without current point)
                                    ArrayList<Point> pointPassing1 = new ArrayList<>();    
                                    for(int currentY = j + 1; currentY <= predicted_point1.getY(); currentY++) {
                                        Point tmpPoint1 = new Point(i, currentY);
                                        pointPassing1.add(tmpPoint1);
                                    }
                                    
                                    // check if two ArrayLists conflict or not 
                                    if(checkCommonPoint(pointPassing, pointPassing1)) {
                                        exitIsClear = false;
                                    }
                                }
                                else if(d1 == Direction.north) {
                                    //  get the predicted point of the car in the next move
                                    Point predicted_point1 = new Point(i,j-speed1);
                                    //  the points list the car passing by in the next move(without current point)
                                    ArrayList<Point> pointPassing1 = new ArrayList<>();
                                    for(int currentY = j - 1; currentY >= predicted_point1.getY(); currentY--) {
                                        Point tmpPoint1 = new Point(i, currentY);
                                        pointPassing1.add(tmpPoint1);
                                    }
                                    
                                    //  check if two ArrayLists conflict or not
                                    if(checkCommonPoint(pointPassing, pointPassing1)) {
                                        exitIsClear = false;
                                    }
                                }
                            }
                        }
                    } 
                }
                beliefs.put(cb, exitIsClear);
                break;
            case CB_finishedManoeuvre: //Not simulated
                break;
            case CB_flashingAmber: //Not simulated
                break;
            case CB_flashingAmberBeacon: //Not simulated
                break;
            case CB_flashingRed: //Not simulated
                break;
            case CB_fuel: //Not simulated
                break;
            case CB_greenLight: //Not simulated
                break;
            case CB_hazardAhead: //Not simulated
                break;
            case CB_headlightsOff: //Not simulated
                break;
            case CB_indicatorOn: //Not simulated
                break;
            case CB_informOtherRoadUser: //Not simulated
                break;
            case CB_laneAvailiable: //Not simulated
                break;
            case CB_laneCleared: //Not simulated
                break;
            case CB_laneDividers: //Not simulated
                break;
            case CB_laneRestricted: //Not simulated
                break;
            case CB_lanes2: //Not simulated
                break;
            case CB_largeVehicle: //Not simulated
                break;
            case CB_largeVehicleInFront: //Not simulated
                break;
            case CB_leftMostLane: //Not simulated
                break;
            // simulated yellow light
            case CB_lightAmber:
                beliefs.put(cb, false);
                boolean yellowLightOn = false;
                //at traffic light white line
                for (int i = 0; i < visibleWorld.getWidth(); i++) {
                    for(int j = 0; j < visibleWorld.getWidth();j++) {
                        //check if the cell type is information cell
                        if(visibleWorld.getCell(i, j).getCellType() == CellType.ct_information ) {
                            //check if the information cell is traffic light
                            AbstractInformationCell aic = (AbstractInformationCell)visibleWorld.getCell(i, j);
                            if(aic.getInformationType() == InformationCell.ic_trafficLight) {
                                TrafficLightCell tlc = (TrafficLightCell)aic;
                                TrafficLightCellInformation tlci = tlc.getInformation();
                                //the list of this traffic light cell faces 
                                ArrayList<Direction> faces = tlc.getFaces();
                                if(faces.size() != 0) {
                                    if(faces.get(0) == Direction.north) {
                                       //the point of the white line
                                       Point visibleWorldStopPoint = new Point(i-1,j);
                                       //the traffic light affects cell in the car's visible world
                                       ArrayList<Point> affectedCells = new ArrayList<>();
                                       for(int k = j; k >= 0; k--) {
                                           affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                       }
                                       if(cmd == Direction.south && affectedCells.contains(location)) {
                                          yellowLightOn = tlci.yellowOn;
                                          beliefs.put(cb, yellowLightOn);
                                       }
                                      
                                    }
                                    else if(faces.get(0) == Direction.south) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i+1,j);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = j ; k < visibleWorld.getHeight();k++) {
                                            affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                        }
                                        if(cmd == Direction.north && affectedCells.contains(location)) {
                                           yellowLightOn = tlci.yellowOn;
                                           beliefs.put(cb, yellowLightOn); 
                                        }
                                           
                                    }
                                    else if(faces.get(0) == Direction.east) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j-1);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i;k < visibleWorld.getWidth();k++) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.west && affectedCells.contains(location)) {
                                            yellowLightOn = tlci.yellowOn;
                                            beliefs.put(cb, yellowLightOn);
                                        }
                                      
                                    }
                                    else if(faces.get(0) == Direction.west) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j+1);
                                        //the traffic light affects cell in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i; k >= 0;k--) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.east && affectedCells.contains(location)) {
                                            yellowLightOn = tlci.yellowOn;
                                            beliefs.put(cb, yellowLightOn);
                                        }
                                       
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case CB_lightFlashingAmber: // not simulated
                break;
                
            // simulated green light
            case CB_lightGreen:
                beliefs.put(cb, false);
                boolean greenLightOn = false;
                //at traffic light white line
                for (int i = 0; i < visibleWorld.getWidth(); i++) {
                    for(int j = 0; j < visibleWorld.getWidth();j++) {
                        //check if the cell type is information cell
                        if(visibleWorld.getCell(i, j).getCellType() == CellType.ct_information ) {
                            //check if the information cell is traffic light
                            AbstractInformationCell aic = (AbstractInformationCell)visibleWorld.getCell(i, j);
                            if(aic.getInformationType() == InformationCell.ic_trafficLight) {
                                TrafficLightCell tlc = (TrafficLightCell)aic;
                                TrafficLightCellInformation tlci = tlc.getInformation();
                                //the list of this traffic light cell faces 
                                ArrayList<Direction> faces = tlc.getFaces();
                                if(faces.size() != 0) {
                                    if(faces.get(0) == Direction.north) {
                                       //the point of the white line
                                       Point visibleWorldStopPoint = new Point(i-1,j);
                                       //the traffic light affects cell in the car's visible world
                                       ArrayList<Point> affectedCells = new ArrayList<>();
                                       for(int k = j; k>= 0; k--) {
                                           affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                       }
                                       if(cmd == Direction.south && affectedCells.contains(location)) {
                                          greenLightOn = tlci.greenOn;
                                          beliefs.put(cb, greenLightOn);
                                       }
                                      
                                    }
                                    else if(faces.get(0) == Direction.south) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i+1,j);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = j ; k < visibleWorld.getHeight();k++) {
                                            affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                        }
                                        if(cmd == Direction.north && affectedCells.contains(location)) {
                                           greenLightOn = tlci.greenOn;
                                           beliefs.put(cb, greenLightOn); 
                                        }
                                           
                                    }
                                    else if(faces.get(0) == Direction.east) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j-1);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i;k < visibleWorld.getWidth();k++) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.west && affectedCells.contains(location)) {
                                           greenLightOn = tlci.greenOn;
                                           beliefs.put(cb, greenLightOn);
                                        }
                                       
                                    }
                                    else if(faces.get(0) == Direction.west) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j+1);
                                        //the traffic light affects cell in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i; k >= 0;k--) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.east && affectedCells.contains(location)) {
                                           greenLightOn = tlci.greenOn;
                                           beliefs.put(cb,greenLightOn);
                                        }
                                       
                                    }
                                }
                            }
                        }
                    }
                }
                 break;
            //simulate red light
            case CB_lightRed:
                beliefs.put(cb, false);
                boolean redLightOn = false;
                //at traffic light white line
                for (int i = 0; i < visibleWorld.getWidth(); i++) {
                    for(int j = 0; j < visibleWorld.getWidth();j++) {
                        //check if the cell type is information cell
                        if(visibleWorld.getCell(i, j).getCellType() == CellType.ct_information ) {
                            //check if the information cell is traffic light
                            AbstractInformationCell aic = (AbstractInformationCell)visibleWorld.getCell(i, j);
                            if(aic.getInformationType() == InformationCell.ic_trafficLight) {
                                TrafficLightCell tlc = (TrafficLightCell)aic;
                                TrafficLightCellInformation tlci = tlc.getInformation();
                                //the list of this traffic light cell faces 
                                ArrayList<Direction> faces = tlc.getFaces();
                                if(faces.size() != 0) {
                                    if(faces.get(0) == Direction.north) {
                                       //the point of the white line
                                       Point visibleWorldStopPoint = new Point(i-1,j);
                                       //the traffic light affects cell in the car's visible world
                                       ArrayList<Point> affectedCells = new ArrayList<>();
                                       for(int k = j; k >= 0; k--) {
                                           affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                       }
                                       if(cmd == Direction.south && affectedCells.contains(location)) {
                                          redLightOn = tlci.redOn;
                                          beliefs.put(cb, redLightOn);
                                       }
                                       
                                    }
                                    else if(faces.get(0) == Direction.south) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i+1,j);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = j ; k < visibleWorld.getHeight();k++) {
                                            affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                        }
                                        if(cmd == Direction.north && affectedCells.contains(location)) {
                                           redLightOn = tlci.redOn;
                                           beliefs.put(cb, redLightOn);
                                        }
                                            
                                    }
                                    else if(faces.get(0) == Direction.east) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j-1);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i;k < visibleWorld.getWidth();k++) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.west && affectedCells.contains(location)) {
                                            redLightOn = tlci.redOn;
                                            beliefs.put(cb, redLightOn);
                                        }
                                     
                                    }
                                    else if(faces.get(0) == Direction.west) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j+1);
                                        //the traffic light affects cell in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i; k >= 0;k--) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.east && affectedCells.contains(location)) {
                                            redLightOn = tlci.redOn;
                                            beliefs.put(cb, redLightOn);
                                        }
                                   
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case CB_mainRoadNextRoad: // not simulated
                break;
            case CB_maxPossibleSpeed25orLess: // not simulated 
                break;
            case CB_nextLaneClear: // not simulated
                break;
            case CB_night: // not simulated
                break;
            case CB_noCrossingLights: // not simulated
                break;
            case CB_noLights: // not simulated
                break;
            case CB_nodanger: // not simulated
                break;
            case CB_openCrossing: // not simulated
                break;
            case CB_oppositeParkedVehicle: // not simulated 
                break;
            case CB_oppositeTrafficIsland: // not simulated 
                break;
            case CB_overtaking: // not simulated 
                break;
            case CB_passedFirstWhiteLine: // not simulated 
                break;
            case CB_rightHandLane: //not simulated 
                break;
            case CB_roadAheadClear: //not simulated 
                break;
            case CB_roadClear: //not simulated 
                break;
            case CB_routeClear: //not simulated
                break;
            case CB_routePlanned: //not simulated 
                break;
            case CB_safeToCross: //not simulated 
                break;
            case CB_safeToEnter: //not simulated 
                break; 
            case CB_sharingRoadWithOthers: //not simulated 
                break;
            case CB_sidelightsOff: //not simulated 
                break;
            case CB_singleCarriageWay: //not simulated 
                break;
            case CB_singleTrackRoad: //not simulated 
                break;
            case CB_stationary: //not simulated 
                break;
            case CB_stationaryTraffic: //not simulated 
                break;
            case CB_stayingInLane: //not simulated 
                break;
            case CB_toJunction10meters: //not simulated 
                break;
            case CB_toflashingAmber: //not simulated 
                break;
            case CB_tpDirectingLeft: //not simulated 
                break;
            case CB_turning: //not simulated 
                break;
            //TODO
            case CB_unableToStopByWhiteLine: // car is always about to stop by the white line
                beliefs.put(cb, false);
                break;
            case CB_vehicleSafe: //not simulated 
                break;
            case CB_whiteLineAcrossRoad: //not simulated 
                break;
            case CB_accessProperty: //not simulated 
                break;
            case CB_activefrontalairbaginfrontpassengerseat: //not simulated 
                break;
            case CB_adverseWeather: //not simulated 
                break;
            case CB_againstFlowOfTraffic: //not simulated 
                break;
            case CB_amSlowMovingVehicle: //not simulated 
                break;
            case CB_amber: //not simulated 
                break;
            case CB_animalInRoad: //not simulated 
                break;
            case CB_approachingBrow: //not simulated 
                break;
            case CB_approachingFog: //not simulated 
                break;
            case CB_approachingHumpBridge: //not simulated 
                break;
            case CB_approachingJunction: //not simulated 
                break;
            case CB_approachingRoundabout: //not simulated 
                break;
            case CB_approachingSchoolCrossing: //not simulated 
                break;
            case CB_atCrossing: //not simulated 
                break;
            case CB_authorisedParkingPlace: //not simulated 
                break;
            case CB_barriersOpen: //not simulated 
                break;
            case CB_betweenLanes: //not simulated 
                break;
            case CB_betweenSunriseSunset: //not simulated 
                break;
            case CB_boosterSeatsRequired: //not simulated 
                break;
            case CB_boosterSeatsfittedCorrectly: //not simulated 
                break;
            case CB_bridleway: //not simulated 
                break;
            case CB_builtuparea: //not simulated 
                break;
            case CB_busLane: //not simulated 
                break;
            case CB_busLaneInOperation: //not simulated 
                break;
            case CB_canPassAnimal: //not simulated 
                break;
            case CB_carInFrontTurningRight: //not simulated 
                break;
            case CB_childPassengers: //not simulated 
                break;
            case CB_congestedTraffic: //not simulated 
                break;
            case CB_contraflow: //not simulated 
                break;
            case CB_controlledCrossing: //not simulated 
                break;
            case CB_crawlerLaneExists: //not simulated 
                break;
            case CB_crossinglightsOff: //not simulated 
                break;
            case CB_crowdedShoppingStreet: //not simulated 
                break;
            case CB_cycleLaneUnavoidable: //not simulated 
                break;
            case CB_cyclelane: //not simulated 
                break;
            case CB_dangerousToStop: //not simulated 
                break;
            case CB_dazzled: //not simulated 
                break;
            case CB_dedicatedParkingArea: //not simulated 
                break;
            case CB_directedByPoliceOfficer: //not simulated 
                break;
            case CB_doubleYellowLine: //not simulated 
                break;
            case CB_downhill: //not simulated 
                break;
            case CB_drivenThroughDeepPuddle: //not simulated 
                break;
            case CB_driverWantsControl: //not simulated 
                break;
            case CB_dullWeather: //not simulated 
                break;
            case CB_dvsaflashingAmber: //not simulated 
                break;
            case CB_dvsafollowRequest: //not simulated 
                break;
            case CB_dvsapullOverSignal: //not simulated 
                break;
            case CB_emergencyArea: //not simulated 
                break;
            case CB_emergencyVehicle: //not simulated 
                break;
            case CB_emergencyVehicleFlashingLightsAndStopped: //not simulated 
                break;
            case CB_enterRestrictedLane: //not simulated 
                break;
            case CB_equestrianCrossing: //not simulated 
                break;
            case CB_flashingSirens: //not simulated 
                break;
            case CB_fog://not simulated 
                break;
            case CB_fogLightsOn: //not simulated 
                break;
            case CB_footpath: //not simulated 
                break;
            case CB_forceTrafficToTramlane: //not simulated 
                break;
            case CB_gearNeutral: //not simulated 
                break;
            case CB_giveWaySign://not simulated 
                break;
            case CB_goingDownhill://not simulated 
                break;
            case CB_greenFilterLightForExit://not simulated 
                break;
            // TODO
            case CB_hardshoulder: 
                boolean atHardShoulder = false;
               
                if(visibleWorld.getCell(location.getX(),location.getY()).getCellType() == CellType.ct_road) {
                    RoadCell rc = (RoadCell)visibleWorld.getCell(location.getX(),location.getY());
                    for(RoadMarking rm : rc.getRoadMarkings()) {
                        if(rm == RoadMarking.rm_hard_shoulder) {
                            atHardShoulder = true;
                        }
                    }
                }
                beliefs.put(cb, atHardShoulder);  
                break;
            case CB_hasAdvancedStop: //not simulated 
                break;
            case CB_headlightsDipped: //not simulated 
                break; 
            case CB_hill: //not simulated  
                break;
            case CB_homezone: //not simulated  
                break;
            case CB_hovLane: //not simulated  
                break;
            case CB_icyWeather: //not simulated  
                break;
            case CB_inIncident: //not simulated 
                break;
            case CB_kerbLoweredForWheelchair: //not simulated 
                break;
            case CB_lanes3: //not simulated 
                break;
            case CB_lanes4plus: //not simulated 
                break;
            case CB_levelCrossing: //not simulated 
                break;
            case CB_levelCrossingApproach: //not simulated 
                break;
            case CB_lightsCausingDiscomfortToOthers: //not simulated 
                break;
            case CB_litStreetLightingRoad: //not simulated 
                break;
            case CB_loadBalanced: //not simulated 
                break;
            case CB_loadNotStickingOut: //not simulated 
                break;
            case CB_loadSecure: //not simulated 
                break;
            case CB_london: //not simulated 
                break;
            case CB_longQueueBehind: //not simulated 
                break;
            case CB_markedBayForLoading: //not simulated 
                break;
            case CB_meetHeightRequirement: //not simulated 
                break;
            case CB_meetParkingRestrictions: //not simulated 
                break;
            case CB_middleLane: //not simulated 
                break;
            case CB_misleadingSignal: //not simulated 
                break;
            case CB_motorcyclistAhead: //not simulated 
                break; 
            case CB_motorcyclistInFront: //not simulated 
                break;
            case CB_motorway: //TODO
                break;
            case CB_nearBrowOfHill: //not simulated 
                break;
            case CB_nearHumpbridge: //not simulated 
                break;
            case CB_nearLevelCrossing: //not simulated 
                break;
            case CB_nearPedistrianCrossing: //not simulated
                break;
            case CB_nearSchool: //not simulated 
                break;
            case CB_nearTaxiRank: //not simulated 
                break;
            case CB_nearTramStop: //not simulated 
                break;
            case CB_nearbusStop: //not simulated 
                break;
            case CB_noOvertakingSign: //not simulated 
                break;
            case CB_noPassingPlaceInFront: //not simulated 
                break;
            case CB_nonMotorTraffic: //not simulated 
                break;
            case CB_numLanesReducing: //not simulated 
                break;
            case CB_obstructCycleFacilities: //not simulated 
                break;
            case CB_onMotorway: //not simulated 
                break;
            case CB_onPavement: //not simulated 
                break;
            case CB_overtaken: //not simulated 
                break;
            case CB_overtakingHighSidedVehicle: //not simulated 
                break;
            case CB_overtakingSchoolBus: //not simulated 
                break;
            case CB_parked: //not simulated 
                break; 
            case CB_parkedInRoad: //not simulated 
                break;
            case CB_parkingAllowedBySigns://not simulated 
                break; 
            case CB_parkingRestrictions: //not simulated 
                break;
            case CB_pavement: //not simulated 
                break;
            case CB_pedestrianCrossing: //TODO
                boolean pedestraincrossing = false;
                
               
//                espeed = 1;
//                //check car current moving direction
//                if(cmd == Direction.north) {         
//                 for(int i = 0; i < visibleWorld.getWidth(); i++) {
//                       for(int j = location.getY() - 1; j >= location.getY() - espeed;j--) {
//                           if(visibleWorld.containsPedestrain(i,j)) { 
//                               
//                               Pedestrian p1 = visibleWorld.getPedestrainAtPosition(i, j);
//                               //get pedestrian moving direction
//                               Direction d1 = p1.getMovingDirection(); 
//                               
//                               pedestraincrossing = true;
//                               beliefs.put(cb,  pedestraincrossing);
//                           }
//                       }
//                   }
//                }
//                else if(cmd == Direction.south) { 
//                    for(int i = 0; i < visibleWorld.getWidth(); i++) {
//                        for(int j = location.getY() + 1; j <= location.getY() + espeed; j++) {
//                            if(visibleWorld.containsPedestrain(i,j)) {
//                                pedestraincrossing = true;
//                                beliefs.put(cb,  pedestraincrossing);
//                            }
//                         }
//                    }
//                               
//                }
//                else if(cmd == Direction.east) {
//                    for(int i = location.getX() + 1; i <= location.getX() + espeed; i++) {
//                        for(int j = 0; j < visibleWorld.getHeight(); j++) {
//                            if(visibleWorld.containsPedestrain(i,j)) {
//                                pedestraincrossing = true;
//                                beliefs.put(cb,  pedestraincrossing);
//                            }
//                           }
//                        }
//                    } 
//                
//                
//                else if(cmd == Direction.west) {
//                    for(int i = location.getX() - 1; i >= location.getX() - espeed; i--) {
//                        for(int j = 0; j < visibleWorld.getHeight(); j++) {
//                            if(visibleWorld.containsPedestrain(i,j)) {
//                                pedestraincrossing = true;
//                                beliefs.put(cb,  pedestraincrossing);
//                            }
//                        }
//                    } 
//                }
                break;
            case CB_pedestriansInRoad:
                boolean pedestrainInRoad = false;
                
                espeed = 1;
                //check car current moving direction
                if(cmd == Direction.north) {         
                 for(int i = 0; i < visibleWorld.getWidth(); i++) {
                       for(int j = location.getY() - 1; j >= location.getY() - espeed;j--) {
                           if(visibleWorld.containsPedestrian(i,j)) {
                               if(j == location.getY() - 1) {
                                   Pedestrian p1 = visibleWorld.getPedestrianAtPosition(i, j);
                                   pedestrainInRoad = true;
                                   beliefs.put(cb,  pedestrainInRoad );
                               }
                              
                           }
                       }
                   }
                }
                else if(cmd == Direction.south) { 
                    for(int i = 0; i < visibleWorld.getWidth(); i++) {
                        for(int j = location.getY() + 1; j <= location.getY() + espeed; j++) {
                            if(visibleWorld.containsPedestrian(i,j)) {
                                if(j == location.getY() + 1) {
                                    pedestrainInRoad = true;
                                    beliefs.put(cb, pedestrainInRoad );
                                }
                               
                            }
                         }
                    }
                               
                }
                else if(cmd == Direction.east) {
                    for(int i = location.getX() + 1; i <= location.getX() + espeed; i++) {
                        for(int j = 0; j < visibleWorld.getHeight(); j++) {
                            if(visibleWorld.containsPedestrian(i,j)) {
                                if(i == location.getX() + 1) {
                                    pedestrainInRoad = true;
                                    beliefs.put(cb, pedestrainInRoad );
                                }
                               
                            }
                           }
                        }
                    } 
                
                
                else if(cmd == Direction.west) {
                    for(int i = location.getX() - 1; i >= location.getX() - espeed; i--) {
                        for(int j = 0; j < visibleWorld.getHeight(); j++) {
                            if(visibleWorld.containsPedestrian(i,j)) {
                               if(i == location.getX() - 1) {
                                   pedestrainInRoad = true;
                                   beliefs.put(cb, pedestrainInRoad );
                               }
                            }
                        }
                    } 
                }
                break;
            case CB_pelicanCrossing: //not simulated 
                break;
            case CB_policeDirectingLeft: //not simulated 
                break;
            case CB_policeflashingBlue: //not simulated 
                break;
            case CB_policeflashingHeadlight: //not simulated 
                break;
            case CB_policehornSounding: //not simulated 
                break;
            case CB_preventingAccessForEmergencyServices: //not simulated 
                break;
            case CB_prohibitedToStopPark: //not simulated 
                break;
            case CB_propertyEntrance: //not simulated          
                break;
            case CB_publicTransport: //not simulated 
               break;
            case CB_puffinCrossing: //not simulated  
                break;
            case CB_pulledOver: //not simulated         
                break;
            case CB_quietlane: //not simulated      
                break;
            case CB_reachedRoundabout: //not simulated 
                break;
            case CB_rearfacingbabyseatinfrontpassengerseat: //not simulated 
                break;
            case CB_redRoute: //not simulated 
                break;
            case CB_redlines: //not simulated   
                break;
            case CB_reversing: //not simulated   
                break;
            case CB_roadMarkingKeepLeftOverride: //not simulated
                break;
            case CB_roadNarrows: //not simulated         
                break;
            case CB_roadPresentsHazards: //not simulated    
                break;
            case CB_roadSignKeepLeftOverride: //not simulated     
                break;
            case CB_roadWorks: //not simulated              
                break;
            case CB_roadWorksAhead: //not simulated 
                break;
            case CB_ruralRoad://not simulated
                break;
            case CB_schoolEntrance://not simulated
                break;
            case CB_schoolEntranceMarkings://not simulated
                break;
            case CB_seenSign://not simulated
                break;
            case CB_seenSignalByAuthorisedPerson://not simulated
                break;
            case CB_sideroad: //not simulated 
                break;
            case CB_signConfictsWithAuthorisedPersonDirection://not simulated
                break;
            case CB_signFlashingAmber://not simulated
                break;
            case CB_signFlashingRedX://not simulated
                break;
            case CB_emergencyStopSign://not simulated
                break;
            case CB_signalledRoundabout://not simulated
                break;
            case CB_signsAdviseRestrictions://not simulated
                break;
            case CB_skidding://not simulated
                break;
            case CB_sliproad://not simulated
                break;
            case CB_slowMovingTraffic://not simulated
                break;
            case CB_slowMovingVehicle://not simulated
                break;
            case CB_slowMovingVehicleInfront://not simulated
                break;
            case CB_speedlimitForHardShoulder://not simulated
                break;
            case CB_stationaryVehicleInFront: // not simulated
                break;
            case CB_stopForChildrenSign://not simulated
                break;
            case CB_stopSign://not simulated
                break;
            case CB_stopSignCrossing://not simulated
                break;
            case CB_taxibay://not simulated
                break;
            case CB_tempObstructingTraffic://not simulated
                break;
            case CB_toucanCrossing://not simulated
                break;
            case CB_trafficCalming://not simulated
                break;
            case CB_trafficCongested: // not simulated 
                break;
            case CB_trafficQueuing: // not simulated 
                break;
            case CB_trafficSlow: // not simulated
                break;
            case CB_tram://not simulated
                break;
            case CB_tramPassingLane://not simulated
                break;
            case CB_tramStop://not simulated
                break;
            case CB_tramlines://not simulated
                break;
            case CB_tramlinesCrossingApproach://not simulated
                break;
            case CB_unncessaryObstruction://not simulated
                break;
            case CB_uphill://not simulated
                break;
            case CB_urbanClearway://not simulated
                break;
            case CB_vehicleDoesntFitsInCentralReservation://not simulated
                break;
            case CB_vehicleFitsInCentralReservation://not simulated
                break;
            case CB_vehiclesWishToOvertake://TODO
                break;
            case CB_visibilityReduced://not simulated
                break;
            case CB_wetWeather://not simulated
                break;
            case CB_whiteDiagonalStripeWhiteBrokenBorder://not simulated
                break;
            case CB_whiteDiagonalStripeWhiteSolidBorder://not simulated
                break;
            case CB_windy://not simulated
                break;
            case CB_withinCyclelaneOpteration://not simulated
                break;
            case CB_withinTimePlateTimes://not simulated
                break;
            case CB_withinUrbanClearwayHours://not simulated
                break;
            case CB_workVehicleSign://not simulated
                break;
            case CB_yellowLine://not simulated
                break;
            case CB_yellowMarkingsOnKerb://not simulated
                break;
            case CB_zebraCrossing:
                boolean approaching_zebra_crossing = false;
                beliefs.put(cb, false);
                for(int x = 0 ; x < visibleWorld.getWidth(); x++) {
                    for(int y = 0; y < visibleWorld.getHeight(); y++) {
                        if(visibleWorld.getCell(x, y).getCellType() == CellType.ct_road) {     
                            RoadCell rc = (RoadCell)visibleWorld.getCell(x, y);
                            for(RoadMarking rm : rc.getRoadMarkings()) {
                                if(rm == RoadMarking.rm_Zebra_Horizontal) {
                                    if(x == location.getX()){
                                       if(cmd == Direction.north && y == location.getY() - 1) {
                                           approaching_zebra_crossing = true;
                                           beliefs.put(cb, approaching_zebra_crossing);
                                       }
                                       else if(cmd == Direction.south && y == location.getY() + 1) {
                                            approaching_zebra_crossing = true;
                                            beliefs.put(cb, approaching_zebra_crossing);
                                        }
                                    }
                                }
                                else if(rm == RoadMarking.rm_Zebra_Vertical) {  
                                    //check the car's current position
                                    if(y == location.getY()) {
                                        if(cmd == Direction.east && x == location.getX() + 1) {
                                            approaching_zebra_crossing = true;
                                            beliefs.put(cb, approaching_zebra_crossing);
                                        }
                                        else if(cmd == Direction.south && x == location.getX() - 1) {
                                            approaching_zebra_crossing = true;
                                            beliefs.put(cb, approaching_zebra_crossing);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            //added
            case CB_ableToStopByWhiteLine: //car is always able to stop at white line
                beliefs.put(cb, true);
                break;
            case CB_exitNotClear:
                if(!exitIsClear) {
                    beliefs.put(cb,true);
                }
                else {
                    beliefs.put(cb, false);
                }
                break;
            case CB_lightNotGreen:
                beliefs.put(cb, false);
                boolean greenLightNotOn = false;
                //at traffic light white line
                for (int i = 0; i < visibleWorld.getWidth(); i++) {
                    for(int j = 0; j < visibleWorld.getWidth();j++) {
                        //check if the cell type is information cell
                        if(visibleWorld.getCell(i, j).getCellType() == CellType.ct_information ) {
                            //check if the information cell is traffic light
                            AbstractInformationCell aic = (AbstractInformationCell)visibleWorld.getCell(i, j);
                            if(aic.getInformationType() == InformationCell.ic_trafficLight) {
                                TrafficLightCell tlc = (TrafficLightCell)aic;
                                TrafficLightCellInformation tlci = tlc.getInformation();
                                //the list of this traffic light cell faces 
                                ArrayList<Direction> faces = tlc.getFaces();
                                if(faces.size() != 0) {
                                    if(faces.get(0) == Direction.north) {
                                       //the point of the white line
                                       Point visibleWorldStopPoint = new Point(i-1,j);
                                       //the traffic light affects cell in the car's visible world
                                       ArrayList<Point> affectedCells = new ArrayList<>();
                                       for(int k = j; k>= 0; k--) {
                                           affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                       }
                                       if(cmd == Direction.south && affectedCells.contains(location)) {
                                          greenLightNotOn = !tlci.greenOn;
                                          beliefs.put(cb, greenLightNotOn);
                                       }                     
                                    }
                                    else if(faces.get(0) == Direction.south) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i+1,j);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = j ; k < visibleWorld.getHeight();k++) {
                                            affectedCells.add(new Point(visibleWorldStopPoint.getX(),k));
                                        }
                                        if(cmd == Direction.north && affectedCells.contains(location)) {
                                           greenLightNotOn = !tlci.greenOn;
                                           beliefs.put(cb, greenLightNotOn);
                                        }               
                                    }
                                    else if(faces.get(0) == Direction.east) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j-1);
                                        //the traffic light affects cells in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i;k < visibleWorld.getWidth();k++) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.west && affectedCells.contains(location)) {
                                            greenLightNotOn = !tlci.greenOn;
                                            beliefs.put(cb, greenLightNotOn);
                                        }         
                                    }
                                    else if(faces.get(0) == Direction.west) {
                                        //the point of the white line
                                        Point visibleWorldStopPoint = new Point(i,j+1);
                                        //the traffic light affects cell in the car's visible world
                                        ArrayList<Point> affectedCells = new ArrayList<>();
                                        for(int k = i; k >= 0;k--) {
                                            affectedCells.add(new Point(k,visibleWorldStopPoint.getY()));
                                        }
                                        if(cmd == Direction.east && affectedCells.contains(location)) {
                                            greenLightNotOn = !tlci.greenOn;
                                            beliefs.put(cb, greenLightNotOn);
                                        }      
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            default:
                break;
            }
        }
    }
}
