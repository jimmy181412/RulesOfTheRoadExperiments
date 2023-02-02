package simulated_cars;


import java.util.ArrayDeque;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import core_car_sim.AbstractCar;
import core_car_sim.AbstractCell.CellType;
import core_car_sim.AbstractInformationCell.InformationCell;
import core_car_sim.RoadCell.RoadMarking;
import core_car_sim.TrafficLightCell.TrafficLightCellInformation;
import core_car_sim.AbstractInformationCell;
import core_car_sim.Direction;
import core_car_sim.Point;
import core_car_sim.RoadCell;
import core_car_sim.TrafficLightCell;
import core_car_sim.WorldSim;

// reactive car: the car will follow all the recommendations from RoTRA
public class ReactiveCar extends AbstractROTRCar implements CarEvents{
    
    private boolean isFinished = false;
    private boolean overtakenOther = false;
    private boolean getIntoLeftLane = false;
    private boolean safegap = false;
    private boolean atRightTurn = false;
    private boolean wallAhead = false;
    
    private boolean trafficLightRed;
    private boolean atWhiteLine = false;
    private boolean finished = false;
    private boolean atHardShoulder;
    private boolean no_right_turn = false;
    private boolean no_left_turn = false;
    private boolean no_down_turn = false;
    private boolean no_up_turn;
    private boolean approaching_vertical_zebra = false;
    private boolean approaching_horizontal_zebra = false;
    private boolean no_overtake= false;
    private boolean no_go_north_because_other_car = false;
    private boolean no_go_south_because_other_car =false;
    private boolean no_go_east_because_other_car = false;
    private boolean no_go_west_because_other_car = false;
        
    ArrayDeque<Direction> directions = new ArrayDeque<Direction>();
    private HashMap<CarAction,CarPriority> actionsToDo = new HashMap<CarAction,CarPriority>();
    
    // constructor
    public ReactiveCar(Point startPos, Point endPos, int startingSpeed){
        super(startPos,endPos, startingSpeed, System.getProperty("user.dir") + "/resources/bluecar.png");
        addCarEventListener(this);
    }
    
    // check if there are same points between two arraylists
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
    
    //reset the observations
    public void reMakeDecisions() {
        this.trafficLightRed = false;
        this.atWhiteLine = false;
        this.wallAhead = false;
        this.atHardShoulder = false;
        this.no_right_turn = false;
        this.no_left_turn = false;
        this.no_down_turn = false;
        this.no_up_turn = false;
        this.approaching_vertical_zebra = false;
        this.approaching_horizontal_zebra = false;
        this.no_overtake = false;
        this.no_go_north_because_other_car = false;
        this.no_go_south_because_other_car = false;
        this.no_go_east_because_other_car = false;
        this.no_go_west_because_other_car = false;
     }

    // car decision making system, make use of the received observations to decide what
    // should be the current moving direction 
    @Override
    protected ArrayDeque<Direction> getSimulationRoute(){ 
        updateOutcomes();
        
        
        System.out.println(currentPosition.getX() + " " + currentPosition.getY());
        int j = 0;
        int m = 0;
        
        System.out.println("----------------------------------------------------------------------");
        // print out the car action list
        System.out.println("action list:");
        for(Entry<CarAction, CarPriority> ca1 : actionsToDo.entrySet()){
            System.out.print(ca1.getKey().toString() + " ");
            System.out.println(ca1.getValue());
        }
        System.out.println("----------------------------------------------------------------------");
        // print out the car intention list
        System.out.println("intention list: "); 
        for(Entry<CarIntention, Boolean> i : intentions.entrySet()){
            if(i.getValue()) {
               System.out.println(i.getKey().toString());
               j++;
            }        
        }
        System.out.println("----------------------------------------------------------------------");
        //print out the car belief list
        System.out.println("belief list");
        for(Entry<CarBelief,Boolean> k : beliefs.entrySet()){
            if(k.getValue()){
                System.out.println(k.getKey().toString());
                m++;
            }
        }
        System.out.println("----------------------------------------------------------------------");


        if ((trafficLightRed && atWhiteLine) || finished)
        {
            setSpeed(0);
        }
        else if(atHardShoulder && no_down_turn && cmd == Direction.south) {
                directions.push(cmd);
        }
        
        else if(atHardShoulder && no_up_turn && cmd == Direction.north) {
                    directions.push(cmd);
        }
        
        else if(atHardShoulder && no_right_turn && cmd == Direction.east) {     
                directions.push(cmd);
        }
        else if(atHardShoulder && no_left_turn && cmd == Direction.west) {
                directions.push(cmd);
        }
        
        else if(no_down_turn && cmd == Direction.south) {
            if(atHardShoulder) {
                directions.push(cmd);
            }
            else{
                directions.push(Direction.east);    
            }   
        }
        else if(no_up_turn && cmd == Direction.north) {
            if(atHardShoulder) {
                directions.push(cmd);
            }
            else {
                directions.push(Direction.west);
            }
                
        }
        else if(no_right_turn && cmd == Direction.east) {
            if(atHardShoulder) {
                directions.push(cmd);
            }
            else {
                directions.push(Direction.north);
            }
        }
        else if(no_left_turn && cmd == Direction.west) {
            if(atHardShoulder) {
                directions.push(cmd);
              }
            else {
                directions.push(Direction.south);
            }
                    
        }
        else {
            directions.push(cmd);   
        }
        
        //clear beliefs,intentions,actions after we get the moving directions
        reMakeDecisions();
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
        switch(action)
        {
        case CA_adjust_speed:
            actionsToDo.put(action, priority);
            break;
        case CA_allow_cyclists_moto_pass:
            actionsToDo.put(action, priority);
            break;
        case CA_allow_emergency_vehicle_to_pass:
            actionsToDo.put(action, priority);
            break;
        case CA_allow_extra_space:
            actionsToDo.put(action, priority);
            break;
        case CA_allow_extra_space_for_works_vehicles:
            actionsToDo.put(action, priority);
            break;
        case CA_allow_traffic_to_pass:
            actionsToDo.put(action, priority);
            break;
        case CA_allow_undertaking:
            actionsToDo.put(action, priority);
            break;
        case CA_allowed_to_proceed:
            actionsToDo.put(action, priority);
            break;
        case CA_approach_left_hand_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_approach_with_caution:
            actionsToDo.put(action, priority);
            break;
        case CA_avoidLaneChanges:
            actionsToDo.put(action, priority);
            break;
        case CA_avoidRightHandLane:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_blocking_sideroads:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_bus_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_closed_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_coasting:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_coned_off_area:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_crossing_central_reservation:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_crossing_crossing:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_crossing_level_crossing:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_cutting_corner:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_drive_against_traffic_flow:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_driving_on_rails:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_emergency_area:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_hard_shoulder:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_harsh_braking:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_horn:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_hov_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_lane_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_lane_switching:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_level_crossing:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_loading_unloading:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_motorway:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_non:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_overtaking:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_overtaking_on_left:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_parking:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_parking_against_flow:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_pick_up_set_down:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_reversing:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_revs:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_stopping:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_tram_reserved_road:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_undertaking:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_uturn:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_waiting:
            actionsToDo.put(action, priority);
            break;
        case CA_avoid_weaving:
            actionsToDo.put(action, priority);
            break;
        case CA_brake_early_lightly:
            actionsToDo.put(action, priority);
            break;
        case CA_brake_hard:
            actionsToDo.put(action, priority);
            break;
        case CA_buildup_speed_on_motorway:
            actionsToDo.put(action, priority);
            break;
        case CA_cancel_overtaking:
            actionsToDo.put(action, priority);
            break;
        case CA_cancel_reverse:
            actionsToDo.put(action, priority);
            break;
        case CA_cancel_signals:
            actionsToDo.put(action, priority);
            break;
        case CA_cancel_undertaking:
            actionsToDo.put(action, priority);
            break;
        case CA_clear_ice_snow_all_windows:
            actionsToDo.put(action, priority);
            break;
        case CA_close_to_kerb:
            actionsToDo.put(action, priority);
            break;
        case CA_consideration_others:
            actionsToDo.put(action, priority);
            break;
        case CA_doNotEnterWhiteDiagonalStripeWhiteBrokenBorder:
            actionsToDo.put(action, priority);
            break;
        case CA_doNotEnterWhiteDiagonalStripeWhiteSolidBorder:
            actionsToDo.put(action, priority);
            break;
        case CA_do_not_drive:
            actionsToDo.put(action, priority);
            break;
        case CA_do_not_hestitate:
            actionsToDo.put(action, priority);
            break;
        case CA_do_not_overtake:
            actionsToDo.put(action, priority);
            break;
        case CA_do_not_park_in_passing_place:
            actionsToDo.put(action, priority);
            break;
        case CA_do_not_reverse:
            actionsToDo.put(action, priority);
            break;
        case CA_do_not_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_dontExceedTempSpeedLimit:
            actionsToDo.put(action, priority);
            break;
        case CA_dont_cross_solid_white:
            actionsToDo.put(action, priority);
            break;
        case CA_dont_use_central_reservation:
            actionsToDo.put(action, priority);
            break;
        case CA_drive_care_attention:
            actionsToDo.put(action, priority);
            break;
        case CA_drive_slowly:
            actionsToDo.put(action, priority);
            break;
        case CA_drive_very_slowly:
            actionsToDo.put(action, priority);
            break;
        case CA_drive_very_slowly_on_bends:
            actionsToDo.put(action, priority);
            break;
        case CA_drop_back:
            actionsToDo.put(action, priority);
            break;
        case CA_dry_brakes:
            actionsToDo.put(action, priority);
            break;
        case CA_ease_off:
            actionsToDo.put(action, priority);
            break;
        case CA_engage_child_locks:
            actionsToDo.put(action, priority);
            break;
        case CA_engage_parking_break:
            actionsToDo.put(action, priority);
            break;
        case CA_engine_off:
            actionsToDo.put(action, priority);
            break;
        case CA_find_other_route:
            actionsToDo.put(action, priority);
            break;
        case CA_find_quiet_side_road:
            actionsToDo.put(action, priority);
            break;
        case CA_find_safe_place_to_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_fit_booster_seat:
            actionsToDo.put(action, priority);
            break;
        case CA_flash_amber_beacon:
            actionsToDo.put(action, priority);
            break;
        case CA_fog_lights_off:
            actionsToDo.put(action, priority);
            break;
        case CA_fog_lights_on:
            actionsToDo.put(action, priority);
            break;
        case CA_followLaneSigns:
            actionsToDo.put(action, priority);
            break;
        case CA_follow_dvsa_until_stopped:
            actionsToDo.put(action, priority);
            break;
        case CA_follow_police_direction:
            actionsToDo.put(action, priority);
            break;
        case CA_follow_sign:
            actionsToDo.put(action, priority);
            break;
        case CA_follow_signs:
            actionsToDo.put(action, priority);
            break;
        case CA_get_in_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_get_into_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_get_off_road:
            actionsToDo.put(action, priority);
            break;
        case CA_give_extensive_extra_seperation_distance:
            actionsToDo.put(action, priority);
            break;
        case CA_give_extra_seperation_distance:
            actionsToDo.put(action, priority);
            break;
        case CA_give_priority_to_public_transport:
            actionsToDo.put(action, priority);
            break;
        case CA_give_priority_to_right:
            actionsToDo.put(action, priority);
            break;
        case CA_give_room_when_passing:
            actionsToDo.put(action, priority);
            break;
        case CA_give_signal:
            actionsToDo.put(action, priority);
            break;
        case CA_give_up_control:
            actionsToDo.put(action, priority);
            break;
        case CA_give_way_at_dotted_white_line:
            actionsToDo.put(action, priority);
            break;
        case CA_give_way_other_roads:
            actionsToDo.put(action, priority);
            break;
        case CA_give_way_to_other:
            actionsToDo.put(action, priority);
            break;
        case CA_give_way_to_pedestrians:
            actionsToDo.put(action, priority);
            break;
        case CA_give_way_to_tram:
            actionsToDo.put(action, priority);
            break;
        case CA_goBetweenLaneDividers:
            actionsToDo.put(action, priority);
            break;
        case CA_go_to_left_hand_land:
            actionsToDo.put(action, priority);
            break;
        case CA_going_left_use_left:
            actionsToDo.put(action, priority);
            break;
        case CA_going_right_use_left:
            actionsToDo.put(action, priority);
            break;
        case CA_handbrake_on:
            actionsToDo.put(action, priority);
            break;
        case CA_headlights_on:
            actionsToDo.put(action, priority);
            break;
        case CA_increase_distance_to_car_infront:
            actionsToDo.put(action, priority);
            break;
        case CA_indicatorOn:
            actionsToDo.put(action, priority);
            break;
        case CA_indicator_on:
            actionsToDo.put(action, priority);
            break;
        case CA_keep_crossing_clear:
            actionsToDo.put(action, priority);
            break;
        case CA_keep_left:
            actionsToDo.put(action, priority);
            break;
        case CA_keep_left_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_keep_safe_distance:
            actionsToDo.put(action, priority);
            break;
        case CA_keep_sidelights_on:
            actionsToDo.put(action, priority);
            break;
        case CA_keep_under_speed_limit:
            actionsToDo.put(action, priority);
            break;
        case CA_keep_well_back:
            actionsToDo.put(action, priority);
            break;
        case CA_lane_clear:
            actionsToDo.put(action, priority);
            break;
        case CA_leave_space_for_manover:
            actionsToDo.put(action, priority);
            break;
        case CA_leave_space_to_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_light_and_number_plates_clean:
            actionsToDo.put(action, priority);
            break;
        case CA_lock:
            actionsToDo.put(action, priority);
            break;
        case CA_maintained_reduced_speed:
            actionsToDo.put(action, priority);
            break;
        case CA_match_speed_to_motorway:
            actionsToDo.put(action, priority);
            break;
        case CA_mergeInTurn:
            actionsToDo.put(action, priority);
            break;
        case CA_merge_in_turn:
            actionsToDo.put(action, priority);
            break;
        case CA_mini:
            actionsToDo.put(action, priority);
            break;
        case CA_minimise_reversing:
            actionsToDo.put(action, priority);
            break;
        case CA_mirrors_clear:
            actionsToDo.put(action, priority);
            break;
        case CA_move_adjacent_lane:
            actionsToDo.put(action, priority);
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
        case CA_nextLaneClear:
            actionsToDo.put(action, priority);
            break;
        case CA_next_safe_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_not_drive_dangerously:
            actionsToDo.put(action, priority);
            break;
        case CA_not_overtaken: //TODO
            actionsToDo.put(action, priority);
            break;
        case CA_obey_signal: // TODO
            actionsToDo.put(action, priority);
            break;
        case CA_obey_work_vehicle_sign:
            actionsToDo.put(action, priority);
            break;
        case CA_overtake_on_right: //TODO
            actionsToDo.put(action, priority);
            break;
        case CA_park_as_close_to_side:
            actionsToDo.put(action, priority);
            break;
        case CA_parking_lights_on:
            actionsToDo.put(action, priority);
            break;
        case CA_pass_around:
            actionsToDo.put(action, priority);
            break;
        case CA_position_right_turn:
            actionsToDo.put(action, priority);
            break;
        case CA_prepare_drop_back:
            actionsToDo.put(action, priority);
            break;
        case CA_prepare_load:
            actionsToDo.put(action, priority);
            break;
        case CA_prepare_route:
            actionsToDo.put(action, priority);
            break;
        case CA_prepare_to_change_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_prepare_to_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_priority_to_motoway_traffic:
            actionsToDo.put(action, priority);
            break;
        case CA_pull_into_hard_shoulder:
            actionsToDo.put(action, priority);
            break;
        case CA_pull_into_passing_place:
            actionsToDo.put(action, priority);
            break;
        case CA_pull_over_safe_place:
            actionsToDo.put(action, priority);
            break;
        case CA_pull_up_in_visible_distance:
            actionsToDo.put(action, priority);
            break;
        case CA_put_on_seatbelts:
            actionsToDo.put(action, priority);
            break;
        case CA_reduce_distance_between_front_vehicle: //TODO
            actionsToDo.put(action, priority);
            break;
        case CA_reduce_lighting:
            actionsToDo.put(action, priority);
            break;
        case CA_reduce_overall_speed:
            actionsToDo.put(action, priority);
            break;
        case CA_reduce_speed:
            actionsToDo.put(action, priority);
            break;
        case CA_reduce_speed_if_pedestrians:
            actionsToDo.put(action, priority);
            break;
        case CA_reduce_speed_on_slip_road:
            actionsToDo.put(action, priority);
            break;
        case CA_release_brake:
            actionsToDo.put(action, priority);
            break;
        case CA_remove_all_snow:
            actionsToDo.put(action, priority);
            break;
        case CA_remove_flash_intention:
            actionsToDo.put(action, priority);
            break;
        case CA_remove_horn_intention:
            actionsToDo.put(action, priority);
            break;
        case CA_reverse_into_drive:
            actionsToDo.put(action, priority);
            break;
        case CA_reverse_to_passing_place:
            actionsToDo.put(action, priority);
            break;
        case CA_road_clear_to_manover:
            actionsToDo.put(action, priority);
            break;
        case CA_safe_distance:
            actionsToDo.put(action, priority);
            break;
        case CA_safe_pull_over_and_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_select_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_set_hazards_off:
            actionsToDo.put(action, priority);
            break;
        case CA_set_headlights_to_dipped:
            actionsToDo.put(action, priority);
            break;
        case CA_signal:
            actionsToDo.put(action, priority);
            break;
        case CA_signal_left:
            actionsToDo.put(action, priority);
            break;
        case CA_signal_left_on_exit:
            actionsToDo.put(action, priority);
            break;
        case CA_signal_right:
            actionsToDo.put(action, priority);
            break;
        case CA_slow_down:
            actionsToDo.put(action, priority);
            break;
        case CA_slow_down_and_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_space_for_vehicle:
            actionsToDo.put(action, priority);
            break;
        case CA_stay_in_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_stay_on_running_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_steady_speed:
            actionsToDo.put(action, priority);
            break;
        case CA_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_stopCrossDoubleWhiteClosestSolid:
            actionsToDo.put(action, priority);
            break;
        case CA_stopCrossingHazardWarningLine:
            actionsToDo.put(action, priority);
            break;
        case CA_stop_and_turn_engine_off:
            actionsToDo.put(action, priority);
            break;
        case CA_stop_at_crossing:
            actionsToDo.put(action, priority);
            break;
        case CA_stop_at_crossing_patrol:
            actionsToDo.put(action, priority);
            break;
        case CA_stop_at_sign:
            actionsToDo.put(action, priority);
            break;
        case CA_stop_at_white_line: //TODO
            actionsToDo.put(action, priority);
            break;
        case CA_switch_off_engine:
            actionsToDo.put(action, priority);
            break;
        case CA_travel_sign_direction:
            actionsToDo.put(action, priority);
            break;
        case CA_treat_as_roundabout:
            actionsToDo.put(action, priority);
            break;
        case CA_treat_as_traffic_light:
            actionsToDo.put(action, priority);
            break;
        case CA_turn_foglights_off:
            actionsToDo.put(action, priority);
            break;
        case CA_turn_into_skid:
            actionsToDo.put(action, priority);
            break;
        case CA_turn_sidelights_on:
            actionsToDo.put(action, priority);
            break;
        case CA_use_central_reservation:
            actionsToDo.put(action, priority);
            break;
        case CA_use_crawler_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_use_demisters:
            actionsToDo.put(action, priority);
            break;
        case CA_use_hazard_lights:
            actionsToDo.put(action, priority);
            break;
        case CA_use_left_indicator:
            actionsToDo.put(action, priority);
            break;
        case CA_use_right_indicator:
            actionsToDo.put(action, priority);
            break;
        case CA_use_road:
            actionsToDo.put(action, priority);
            break;
        case CA_use_signals:
            actionsToDo.put(action, priority);
            break;
        case CA_use_tram_passing_lane:
            actionsToDo.put(action, priority);
            break;
        case CA_use_windscreen_wipers:
            actionsToDo.put(action, priority);
            break;
        case CA_wait_at_advanced_stop:
            actionsToDo.put(action, priority);
            break;
        case CA_wait_at_first_white_line:
            actionsToDo.put(action, priority);
            break;
        case CA_wait_for_gap_before_moving_off: //TODO
            actionsToDo.put(action, priority);
            break;
        case CA_wait_until_clear:
            actionsToDo.put(action, priority);
            break;
        case CA_wait_until_route_clear:
            actionsToDo.put(action, priority);
            break;
        case CA_wait_until_safe_gap: //TODO
            actionsToDo.put(action, priority);
            break;
        case CA_wheel_away_from_kerb:
            actionsToDo.put(action, priority);
            break;
        case CA_wheel_toward_from_kerb:
            actionsToDo.put(action, priority);
            break;
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
                    beliefs.put(cb, true);
                    break;
                case north:
                    wallAhead = visibleWorld.getCell(location.getX(), location.getY()-1).getCellType() != CellType.ct_road;
                    beliefs.put(cb, true);
                    break;
                case south:
                    wallAhead = visibleWorld.getCell(location.getX(), location.getY()+1).getCellType() != CellType.ct_road;
                    beliefs.put(cb, true);
                    break;
                case west:
                    wallAhead = visibleWorld.getCell(location.getX() - 1, location.getY()).getCellType() != CellType.ct_road;
                    beliefs.put(cb, true);
                    break;
                }
            case CB_atTrafficLight://Only traffic lights simulated
                 for (int y = 0; y < visibleWorld.getHeight(); y++){
                        for (int x = 0; x < visibleWorld.getWidth(); x++){
                            if (visibleWorld.getCell(x, y).getCellType() == CellType.ct_information){
                                if (((AbstractInformationCell)visibleWorld.getCell(x, y)).getInformationType() == InformationCell.ic_trafficLight){
                                    TrafficLightCell tlc = (TrafficLightCell)visibleWorld.getCell(x, y);
                                    TrafficLightCellInformation tlci = ((TrafficLightCell)visibleWorld.getCell(x, y)).getInformation(); 
                                    //faces list
                                    ArrayList<Direction> faces = tlc.getFaces();
                                    if(faces.size() != 0) {
                                        // check for car going west
                                        if(faces.get(0) == Direction.east) {
                                            if (cmd == Direction.west) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x , y - 1);
                                                //Car belief updates
                                                if(visibleWorldStopPoint.equals(location)) {
                                                    beliefs.put(cb, true);
                                                    intentions.put(CarIntention.CI_approachingTrafficLight, true);
                                                }
                                                // TODO
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }       
                                        }
                                        // check for car going east
                                        else if(faces.get(0) == Direction.west) {
                                            if(cmd == Direction.east) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x , y + 1);
                                                // Car belief updates
                                                if(visibleWorldStopPoint.equals(location)) {
                                                    beliefs.put(cb, true);
                                                    intentions.put(CarIntention.CI_approachingTrafficLight, true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }
                                        }
                                        // check for car going north
                                        else if(faces.get(0) == Direction.south) {
                                            if(cmd == Direction.north) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x + 1, y);
                                                // Car belief updates
                                                if(visibleWorldStopPoint.equals(location)) {
                                                    beliefs.put(cb, true);
                                                    intentions.put(CarIntention.CI_approachingTrafficLight, true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }
                                        }
                                        // check for car going south
                                        else if(faces.get(0) == Direction.north) {
                                            if(cmd == Direction.south) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x - 1, y );
                                                // Car belief updates
                                                if(visibleWorldStopPoint.equals(location)) {
                                                    beliefs.put(cb, true);
                                                    intentions.put(CarIntention.CI_approachingTrafficLight, true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }
                                        }
                                        else {
                                            //car belief update
                                            beliefs.put(cb, false);
                                        }
                                    }
                                }
                            }
                        }
                 }
                 break;
            case CB_behindWantToOvertake: //Only for north
                beliefs.put(cb, visibleWorld.containsCar(location.getX(), location.getY() - 1));//Not car in front yet
                intentions.put(CarIntention.CI_overtake, beliefs.get(cb));
                break;
            case CB_bendInRoad://Not simulated
                beliefs.put(cb, true);
                break;
            case CB_brokendown://Car cannot break down.
                beliefs.put(cb, false);
                break;
            case CB_canReadNumberPlate:
                beliefs.put(cb, true);
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
                boolean exitIsClear = true;
      
                //check car current moving direction
                if(cmd == Direction.north) {
                   //the position that the car will be at in the next move is
                   Point predicted_point = new Point(location.getX(), location.getY() - speed);
                   //the point list that the car will pass from its current location to its predicted point(without the current position) 
                   ArrayList<Point> pointPassing = new ArrayList<>();
                   
                   for(int currentY = location.getY() - 1; currentY >= predicted_point.getY(); currentY--){
                       Point tmpPoint = new Point(location.getX(), currentY);
                       pointPassing.add(tmpPoint);
                   }
                    
                   for(int i = 0; i < visibleWorld.getWidth(); i++) {
                       for(int j = location.getY() - 1; j >= location.getY() - speed;j--) {
                           if(visibleWorld.containsCar(i,j)) {
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
                                   
                                   for(int currentX = i - 1; currentX >= predicted_point1.getX(); currentX--) {
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
                else if(cmd == Direction.south) {
                    // the position that the car will be at in the next move is
                    Point predicted_point = new Point(location.getX(), location.getY() + speed);
                    
                    //the point list that the car will pass from its current location to its predicted point(without the current position) 
                    ArrayList<Point> pointPassing = new ArrayList<>();
                    
                    for(int currentY = location.getY() + 1; currentY <= predicted_point.getY(); currentY++){
                        Point tmpPoint = new Point(location.getX(), currentY);
                        pointPassing.add(tmpPoint);
                    }
                    
                    for(int i = 0; i < visibleWorld.getWidth(); i++) {
                        for(int j = location.getY() + 1; j <= location.getY() + speed; j++) {
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
                    Point predicted_point = new Point(location.getX() + speed,location.getY());
                    //  the points list the car passing by in the next move(without current point)
                    ArrayList<Point> pointPassing = new ArrayList<>();
                    for(int currentX = location.getX() + 1; currentX <= predicted_point.getX(); currentX++) {
                        Point tmpPoint = new Point(currentX, location.getY());
                        pointPassing.add(tmpPoint);
                    }
                    
                    for(int i = location.getX() + 1; i <= location.getX() + speed; i++) {
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
                    Point predicted_point = new Point(location.getX() - speed,location.getY());
                    //  the points list the car passing by in the next move(without current point)
                    ArrayList<Point> pointPassing = new ArrayList<>();
                    for(int currentX = location.getX() - 1; currentX >= predicted_point.getX(); currentX--) {
                        Point tmpPoint = new Point(currentX, location.getY());
                        pointPassing.add(tmpPoint);
                    }
                    
                    for(int i = location.getX() - 1; i >= location.getX() - speed; i--) {
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
                beliefs.put(cb,false);
                for (int y = 0; y < visibleWorld.getHeight(); y++){
                    for (int x = 0; x < visibleWorld.getWidth(); x++){
                        if (visibleWorld.getCell(x, y).getCellType() == CellType.ct_information){
                            if (((AbstractInformationCell)visibleWorld.getCell(x, y)).getInformationType() == InformationCell.ic_trafficLight){
                                TrafficLightCell tlc = (TrafficLightCell)visibleWorld.getCell(x, y);
                                TrafficLightCellInformation tlci = ((TrafficLightCell)visibleWorld.getCell(x, y)).getInformation(); 
                                //faces list
                                ArrayList<Direction> faces = tlc.getFaces();
                                if(faces.size() != 0) {
                                    if(faces.get(0) == Direction.east) {
                                        if (cmd == Direction.west) {
                                            trafficLightRed = tlci.redOn;
                                            Point visibleWorldStopPoint = new Point(x , y - 1);
                                            //Car belief update
                                            if(tlci.yellowOn) {
                                                beliefs.put(cb, true); 
                                            }
                                            atWhiteLine = visibleWorldStopPoint.equals(location);
                                        }       
                                    }
                                    else if(faces.get(0) == Direction.west) {
                                        if(cmd == Direction.east) {
                                            trafficLightRed = tlci.redOn;
                                            Point visibleWorldStopPoint = new Point(x , y + 1);
                                            //Car belief update
                                            if(tlci.yellowOn) {
                                                beliefs.put(cb, true);
                                            }
                                            atWhiteLine = visibleWorldStopPoint.equals(location);
                                        }
                                    }
                                    else if(faces.get(0) == Direction.south) {
                                        if(cmd == Direction.north) {
                                            trafficLightRed = tlci.redOn;
                                            Point visibleWorldStopPoint = new Point(x + 1, y);
                                            // Car belief update
                                            if(tlci.yellowOn) {
                                                beliefs.put(cb, true);
                                            }
                                            atWhiteLine = visibleWorldStopPoint.equals(location);
                                        }
                                    }
                                    else if(faces.get(0) == Direction.north) {
                                        if(cmd == Direction.south) {
                                            trafficLightRed = tlci.redOn;
                                            Point visibleWorldStopPoint = new Point(x - 1, y );
                                            // Car belief update
                                            if(tlci.yellowOn) {
                                                beliefs.put(cb,true);
                                            }
                                            atWhiteLine = visibleWorldStopPoint.equals(location);
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
                 for (int y = 0; y < visibleWorld.getHeight(); y++){
                        for (int x = 0; x < visibleWorld.getWidth(); x++){
                            if (visibleWorld.getCell(x, y).getCellType() == CellType.ct_information){
                                if (((AbstractInformationCell)visibleWorld.getCell(x, y)).getInformationType() == InformationCell.ic_trafficLight){
                                    TrafficLightCell tlc = (TrafficLightCell)visibleWorld.getCell(x, y);
                                    TrafficLightCellInformation tlci = ((TrafficLightCell)visibleWorld.getCell(x, y)).getInformation(); 
                                    //faces list
                                    ArrayList<Direction> faces = tlc.getFaces();
                                    if(faces.size() != 0) {
                                        if(faces.get(0) == Direction.east) {
                                            if (cmd == Direction.west) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x , y - 1);
                                                //Car belief update
                                                if(tlci.greenOn) {
                                                    beliefs.put(cb, true); 
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }       
                                        }
                                        else if(faces.get(0) == Direction.west) {
                                            if(cmd == Direction.east) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x , y + 1);
                                                //Car belief update
                                                if(tlci.greenOn) {
                                                    beliefs.put(cb, true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }
                                        }
                                        else if(faces.get(0) == Direction.south) {
                                            if(cmd == Direction.north) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x + 1, y);
                                                // Car belief update
                                                if(tlci.greenOn) {
                                                    beliefs.put(cb, true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }
                                        }
                                        else if(faces.get(0) == Direction.north) {
                                            if(cmd == Direction.south) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x - 1, y );
                                                // Car belief update
                                                if(tlci.greenOn) {
                                                    beliefs.put(cb,true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
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
                 for (int y = 0; y < visibleWorld.getHeight(); y++){
                        for (int x = 0; x < visibleWorld.getWidth(); x++){
                            if (visibleWorld.getCell(x, y).getCellType() == CellType.ct_information){
                                if (((AbstractInformationCell)visibleWorld.getCell(x, y)).getInformationType() == InformationCell.ic_trafficLight){
                                    TrafficLightCell tlc = (TrafficLightCell)visibleWorld.getCell(x, y);
                                    TrafficLightCellInformation tlci = ((TrafficLightCell)visibleWorld.getCell(x, y)).getInformation(); 
                                    //faces list
                                    ArrayList<Direction> faces = tlc.getFaces();
                                    if(faces.size() != 0) {
                                        if(faces.get(0) == Direction.east) {
                                            if (cmd == Direction.west) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x , y - 1);
                                                //Car belief update
                                                if(tlci.redOn) {
                                                    beliefs.put(cb, true); 
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }       
                                        }
                                        else if(faces.get(0) == Direction.west) {
                                            if(cmd == Direction.east) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x , y + 1);
                                                //Car belief update
                                                if(tlci.redOn) {
                                                    beliefs.put(cb, true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }
                                        }
                                        else if(faces.get(0) == Direction.south) {
                                            if(cmd == Direction.north) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x + 1, y);
                                                // Car belief update
                                                if(tlci.redOn) {
                                                    beliefs.put(cb, true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
                                            }
                                        }
                                        else if(faces.get(0) == Direction.north) {
                                            if(cmd == Direction.south) {
                                                trafficLightRed = tlci.redOn;
                                                Point visibleWorldStopPoint = new Point(x - 1, y );
                                                // Car belief update
                                                if(tlci.redOn) {
                                                    beliefs.put(cb,true);
                                                }
                                                atWhiteLine = visibleWorldStopPoint.equals(location);
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
            case CB_unableToStopByWhiteLine: //not simulated 
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
            case CB_pedestrianCrossing: //not simulated 
                break;
            case CB_pedestriansInRoad: //not simulated 
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
            case CB_ruralRoad:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_schoolEntrance:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_schoolEntranceMarkings:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_seenSign:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_seenSignalByAuthorisedPerson:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_sideroad: //not simulated 
                break;
            case CB_signConfictsWithAuthorisedPersonDirection:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_signFlashingAmber:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_signFlashingRedX:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_emergencyStopSign:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_signalledRoundabout:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_signsAdviseRestrictions:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_skidding:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_sliproad:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_slowMovingTraffic:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_slowMovingVehicle:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_slowMovingVehicleInfront:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_speedlimitForHardShoulder:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_stationaryVehicleInFront: // not simulated
                break;
            case CB_stopForChildrenSign:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_stopSign:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_stopSignCrossing:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_taxibay:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_tempObstructingTraffic:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_toucanCrossing:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_trafficCalming:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_trafficCongested: // not simulated 
                break;
            case CB_trafficQueuing: // not simulated 
                break;
            case CB_trafficSlow: // not simulated
                break;
            case CB_tram:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_tramPassingLane:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_tramStop:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_tramlines:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_tramlinesCrossingApproach:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_unncessaryObstruction:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_uphill:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_urbanClearway:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_vehicleDoesntFitsInCentralReservation:
                beliefs.put(cb, true);//Not simulated
                break;
            case CB_vehicleFitsInCentralReservation:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_vehiclesWishToOvertake:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_visibilityReduced:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_wetWeather:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_whiteDiagonalStripeWhiteBrokenBorder:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_whiteDiagonalStripeWhiteSolidBorder:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_windy:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_withinCyclelaneOpteration:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_withinTimePlateTimes:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_withinUrbanClearwayHours:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_workVehicleSign:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_yellowLine:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_yellowMarkingsOnKerb:
                beliefs.put(cb, false);//Not simulated
                break;
            case CB_zebraCrossing:
                boolean approaching_zebra_crossing = false;
                
               
                for(int x = 0 ; x < visibleWorld.getWidth(); x++) {
                    for(int y = 0; y < visibleWorld.getHeight(); y++) {
                        if(visibleWorld.getCell(x, y).getCellType() == CellType.ct_road) {     
                            RoadCell rc = (RoadCell)visibleWorld.getCell(x, y);
                            for(RoadMarking rm : rc.getRoadMarkings()) {
                                if(rm == RoadMarking.rm_Zebra_Horizontal) {
                                    if(y == location.getY()){
                                        if(cmd == Direction.east) {
                                            if(x == location.getX() + 1) {
                                                approaching_horizontal_zebra = true;
                                                beliefs.put(cb,true);
                                            }
                                        }
                                        else if(cmd == Direction.west) {
                                            if(x == location.getX() - 1) {
                                                approaching_horizontal_zebra = true;
                                                beliefs.put(cb,true);
                                            }
                                        }
                                    }
                                }
                                else if(rm == RoadMarking.rm_Zebra_Vertical) {  
                                    //check the car's current position
                                    if(x == location.getX()) {
                                        if(cmd == Direction.north) {
                                            if(y == location.getY() + 1){
                                                approaching_vertical_zebra = true;
                                                beliefs.put(cb,true);
                                            }
                                        }
                                        else if(cmd == Direction.south) {
                                            if(y == location.getY() - 1) {
                                                approaching_vertical_zebra = true;
                                                beliefs.put(cb ,true);
                                            }
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
