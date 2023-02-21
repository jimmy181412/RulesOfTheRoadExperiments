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
public class ReactiveCar extends AbstractROTRCar implements CarEvents{
    
    private boolean isFinished = false;
    private boolean wallAhead;
    private boolean atWhiteLine;
    private boolean exitIsClear;
    private boolean startOvertaking;
    private boolean overtaking;
    private boolean finished_overtaking;

    ArrayDeque<Direction> directions = new ArrayDeque<>();
    private HashMap<CarAction,CarPriority> actionsRecommended = new HashMap<>();
    private HashMap<CarAction,CarPriority> actionsToDo = new HashMap<>();


    public ReactiveCar(Point startPos, Point endPos, int startingSpeed){
        super(startPos,endPos, startingSpeed, System.getProperty("user.dir") + "/RoTRExperiments/resources/bluecar.png",CarType.car_AI);
        addCarEventListener(this);
    }
   
    // car decision-making system, make use of the received observations to decide what
    // should be the current moving direction 
    @Override
    protected ArrayDeque<Direction> getSimulationRoute(){

        setSpeed(1);
        directions = getCurrentMovingDirectionList();
        updateOutcomes();

        if(overtaking){
            actionsToDo.put(CarAction.CA_move_quickly_past, CarPriority.CP_SHOULD);
        }
        if(finished_overtaking){
            actionsToDo.put(CarAction.CA_move_left, CarPriority.CP_SHOULD);
        }
        //after update the outcome, we get the actionsToDo list which indicates what
        // the car should do in the next move
        for(Entry<CarAction, CarPriority> entry : actionsToDo.entrySet()){
            CarAction ca = entry.getKey();
            CarPriority pr = entry.getValue();
            switch(ca){
                case CA_adjust_speed:
                    setSpeed(1);
                    break;
                case CA_avoid_overtaking:
                    intentions.put(CarIntention.CI_overtake,false);
                    break;
                case CA_cancel_overtaking:
                    intentions.put(CarIntention.CI_overtake, false);
                    break;
                case CA_avoid_hard_shoulder: //not simulated //TODO
                    break;
                case CA_avoid_motorway://not simulated
                    break;
                case CA_consideration_others://not simulated actually //TODO
                    break;
                case CA_do_not_overtake: //TODO
                    break;
                case CA_dontExceedTempSpeedLimit: //TODO
                    // adjust speed according to the world speed limit
                    break;
                case CA_dont_cross_solid_white: //TODO
                    break;
                case CA_drive_care_attention://TODO
                    break;
                case CA_drive_slowly: //TODO
                    break;
                case CA_drive_very_slowly: //TODO
                    break;
                case CA_drop_back://TODO
                    break;
                case CA_give_way_to_pedestrians: //TODO
                    break;
                case CA_increase_distance_to_car_infront:
                    //should increase the distance to infront car
                    // r126
                    break;
                case CA_keep_safe_distance: //TODO
                    //Keep a safe distance from the vehicle in front
                    break;
                case CA_keep_under_speed_limit: //TODO
                    //r261
                    break;
                case CA_move_left: //TODO
                    //after finishing overtaking, the AI car should go to its original line
                    if(finished_overtaking){
                        //for cmd == north
                        if (cmd == Direction.north) {
                            directions.push(Direction.west);
                        }
                        else if(cmd  == Direction.south){
                            directions.push(Direction.east);
                        }
                        else if(cmd == Direction.east){
                            directions.push(Direction.north);
                        }
                        else if(cmd == Direction.west){
                            directions.push(Direction.south);
                        }
                        finished_overtaking = false;
                    }
                    break;
                case CA_move_quickly_past: //TODO
                    if(overtaking){
                        setSpeed(2);
                    }
                    break;
                case CA_must_stop_pedestrian_crossing: //TODO
                    break;
                case CA_not_drive_dangerously: //TODO
                    //we do nothing about this
                    break;
                case CA_not_overtaken: //TODO
                    break;
                case CA_overtake_on_right: //TODO
                    break;
                case CA_prepare_drop_back://TODO
                    break;
                case CA_reduce_distance_between_front_vehicle: //TODO
                    break;
                case CA_reduce_overall_speed, CA_reduce_speed: //TODO
                    if(getSpeed() > 1){
                        setSpeed(getSpeed() - 1);
                    }
                    break;
                case CA_reduce_speed_if_pedestrians://TODO
                    break;
                case CA_safe_distance: //TODO
                    break;
                case CA_space_for_vehicle:
                    //overtaking,should not get too close to the vehicle you intend to overtake
                    startOvertaking = true;
                    break;
                case CA_stop_at_white_line, CA_wait_at_white_line, CA_switch_off_engine, CA_stop, CA_stop_and_turn_engine_off: //TODO
                    setSpeed(0);
                    break;
                case CA_wait_for_gap_before_moving_off: //TODO
                    //r171
                    break;
                case CA_wait_until_safe_gap: //TODO
                    //r180
                    break;
            }
        }

        if(startOvertaking){
            if(cmd == Direction.north){
                directions.push(Direction.east);
                startOvertaking = false;
                overtaking = true;
            }
            else if(cmd == Direction.south){
                directions.push(Direction.west);
                startOvertaking = false;
                overtaking = true;
            }
            else if(cmd == Direction.west){
                directions.push(Direction.north);
                startOvertaking = false;
                overtaking = true;
            }
            else if(cmd == Direction.east){
                directions.push(Direction.south);
                startOvertaking = false;
                overtaking = true;
            }
        }

        System.out.println(startOvertaking);
        System.out.println(overtaking);
        System.out.println(finished_overtaking);

        //According to the speed, get the direction list
        ArrayDeque<Direction> currentDirection = new ArrayDeque<>();
        for(int i = 0; i < getSpeed(); i++){
            if(!directions.isEmpty()){
                currentDirection.add(directions.pop());
            }

        }

        System.out.println("current speed is: " + getSpeed());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("intention list: ");
        for(Entry<CarIntention, Boolean> i : intentions.entrySet()){
            if(i.getValue()) {
               System.out.println(i.getKey().toString());
            }
        }
        System.out.println("----------------------------------------------------------------------");
        //print out the car belief list
        System.out.println("belief list");
        for(Entry<CarBelief,Boolean> k : beliefs.entrySet()){
            if(k.getValue()){
                System.out.println(k.getKey().toString());

            }
        }
        System.out.println("----------------------------------------------------------------------");
        return currentDirection;
    }

    @Override
    protected boolean isFinished(Point arg0)
    {
        isFinished = currentPosition.equals(endPosition);
        return isFinished;
    }

    @Override
    public void worldUpdate(WorldSim visibleWorld, Point location) 
    {
        updateIntentions(visibleWorld, location, cmd,pmd);
        updateBeliefs(visibleWorld, location);

        //check whether the car finished overtaking or not...
        if(overtaking){
            if(cmd == Direction.north){
                    finished_overtaking = visibleWorld.containsCar(location.getX() - 1, location.getY() + 2 )
                            ||visibleWorld.containsCar(location.getX() - 1, location.getY() + 3  );
                }
                else if(cmd == Direction.south){
                    finished_overtaking = visibleWorld.containsCar(location.getX() + 1, location.getY() - 2)
                            ||visibleWorld.containsCar(location.getX() + 1 , location.getY() - 3);
                }
                else if(cmd == Direction.east){
                    finished_overtaking = visibleWorld.containsCar( location.getX() - 2, location.getY() - 1)
                            ||visibleWorld.containsCar(location.getX() - 3, location.getY() - 1);
                }
                else if(cmd == Direction.west){
                    finished_overtaking = visibleWorld.containsCar( location.getX() + 2 , location.getY() - 1)
                            ||visibleWorld.containsCar(location.getX() - 3, location.getY() - 1);
                }
            if(finished_overtaking){
                overtaking = false;
            }

        }
    }


    @Override
    public void actionUpdate(CarAction action, CarPriority priority) 
    {
        actionsRecommended.put(action, priority);
        switch (action) {
            //TODO
            case CA_adjust_speed -> actionsToDo.put(CarAction.CA_adjust_speed, priority);
            case CA_allow_cyclists_moto_pass -> //not simulated
                    actionsToDo.put(CarAction.CA_allow_cyclists_moto_pass, priority);
            case CA_allow_emergency_vehicle_to_pass -> //not simulated
                    actionsToDo.put(CarAction.CA_allow_emergency_vehicle_to_pass, priority);
            case CA_allow_extra_space -> //not simulated
                    actionsToDo.put(CarAction.CA_allow_extra_space, priority);
            case CA_allow_extra_space_for_works_vehicles -> //not simulated
                    actionsToDo.put(CarAction.CA_allow_extra_space_for_works_vehicles, priority);
            case CA_allow_traffic_to_pass -> //not simulated
                    actionsToDo.put(CarAction.CA_allow_traffic_to_pass, priority);
            case CA_allow_undertaking -> //not simulated
                    actionsToDo.put(CarAction.CA_allow_undertaking, priority);
            case CA_allowed_to_proceed -> //not simulated
                    actionsToDo.put(CarAction.CA_allowed_to_proceed, priority);
            case CA_approach_left_hand_lane -> //not simulated
                    actionsToDo.put(CarAction.CA_approach_left_hand_lane, priority);
            case CA_approach_with_caution -> //not simulated
                    actionsToDo.put(CarAction.CA_approach_with_caution, priority);
            case CA_avoidLaneChanges -> //not simulated
                    actionsToDo.put(CarAction.CA_avoidLaneChanges, priority);
            case CA_avoidRightHandLane -> //not simulated
                    actionsToDo.put(CarAction.CA_avoidRightHandLane, priority);
            case CA_avoid_blocking_sideroads ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_blocking_sideroads, priority);
            case CA_avoid_bus_lane -> //not simulated
                    actionsToDo.put(CarAction.CA_avoid_bus_lane, priority);
            case CA_avoid_closed_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_closed_lane, priority);
            case CA_avoid_coasting ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_coasting, priority);
            case CA_avoid_coned_off_area ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_coned_off_area, priority);
            case CA_avoid_crossing_central_reservation ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_crossing_central_reservation, priority);
            case CA_avoid_crossing_crossing ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_crossing_crossing, priority);
            case CA_avoid_crossing_level_crossing ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_crossing_level_crossing, priority);
            case CA_avoid_cutting_corner ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_cutting_corner, priority);
            case CA_avoid_drive_against_traffic_flow ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_drive_against_traffic_flow, priority);
            case CA_avoid_driving_on_rails ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_driving_on_rails, priority);
            case CA_avoid_emergency_area ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_emergency_area, priority);
            case CA_avoid_hard_shoulder -> //not simulated
                    actionsToDo.put(CarAction.CA_avoid_hard_shoulder, priority);
            case CA_avoid_harsh_braking ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_harsh_braking, priority);
            case CA_avoid_horn ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_horn, priority);
            case CA_avoid_hov_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_hov_lane, priority);
            case CA_avoid_lane_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_lane_lane, priority);
            case CA_avoid_lane_switching -> // not simulated
                    actionsToDo.put(CarAction.CA_avoid_lane_switching, priority);
            case CA_avoid_level_crossing ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_level_crossing, priority);
            case CA_avoid_loading_unloading ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_loading_unloading, priority);
            case CA_avoid_motorway ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_motorway, priority);
            case CA_avoid_non ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_non, priority);
            case CA_avoid_overtaking -> actionsToDo.put(CarAction.CA_avoid_overtaking, priority);
            case CA_avoid_overtaking_on_left -> //not simulated
                    actionsToDo.put(CarAction.CA_avoid_overtaking_on_left, priority);
            case CA_avoid_parking -> //not simulated
                    actionsToDo.put(CarAction.CA_avoid_parking, priority);
            case CA_avoid_parking_against_flow -> //not simulated
                    actionsToDo.put(CarAction.CA_avoid_parking_against_flow, priority);
            case CA_avoid_pick_up_set_down -> // not simulated
                    actionsToDo.put(CarAction.CA_avoid_pick_up_set_down, priority);
            case CA_avoid_reversing -> //not simulated=
                    actionsToDo.put(CarAction.CA_avoid_reversing, priority);
            case CA_avoid_revs ->// not simulated
                    actionsToDo.put(CarAction.CA_avoid_revs, priority);
            case CA_avoid_stopping ->// not simulated
                    actionsToDo.put(CarAction.CA_avoid_stopping, priority);
            case CA_avoid_tram_reserved_road -> // not simulated
                    actionsToDo.put(CarAction.CA_avoid_tram_reserved_road, priority);
            case CA_avoid_undertaking -> //not simulated
                    actionsToDo.put(CarAction.CA_avoid_undertaking, priority);
            case CA_avoid_uturn -> //not simulated
                    actionsToDo.put(CarAction.CA_avoid_uturn, priority);
            case CA_avoid_waiting ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_waiting, priority);
            case CA_avoid_weaving ->//not simulated
                    actionsToDo.put(CarAction.CA_avoid_weaving, priority);
            case CA_brake_early_lightly ->//not simulated
                    actionsToDo.put(CarAction.CA_brake_early_lightly, priority);
            case CA_brake_hard ->//not simulated
                    actionsToDo.put(CarAction.CA_brake_hard, priority);
            case CA_buildup_speed_on_motorway ->//not simulated
                    actionsToDo.put(CarAction.CA_buildup_speed_on_motorway, priority);
            case CA_cancel_overtaking -> actionsToDo.put(CarAction.CA_cancel_overtaking, priority);
            case CA_cancel_reverse -> //not simulated
                    actionsToDo.put(CarAction.CA_cancel_reverse, priority);
            case CA_cancel_signals ->//not simulated
                    actionsToDo.put(CarAction.CA_cancel_signals, priority);
            case CA_cancel_undertaking -> //not simulated
                    actionsToDo.put(CarAction.CA_cancel_undertaking, priority);
            case CA_clear_ice_snow_all_windows ->//not simulated
                    actionsToDo.put(CarAction.CA_clear_ice_snow_all_windows, priority);
            case CA_close_to_kerb ->//not simulated
                    actionsToDo.put(CarAction.CA_close_to_kerb, priority);
            case CA_consideration_others ->//not simulated actually
                    actionsToDo.put(CarAction.CA_consideration_others, priority);
            case CA_doNotEnterWhiteDiagonalStripeWhiteBrokenBorder ->//not simulated
                    actionsToDo.put(CarAction.CA_doNotEnterWhiteDiagonalStripeWhiteBrokenBorder, priority);
            case CA_doNotEnterWhiteDiagonalStripeWhiteSolidBorder ->//not simulated
                    actionsToDo.put(CarAction.CA_doNotEnterWhiteDiagonalStripeWhiteSolidBorder, priority);
            case CA_do_not_drive ->//not simulated
                    actionsToDo.put(CarAction.CA_do_not_drive, priority);
            case CA_do_not_hestitate ->//not simulated
                    actionsToDo.put(CarAction.CA_do_not_hestitate, priority);
            case CA_do_not_overtake -> actionsToDo.put(CarAction.CA_do_not_overtake, priority);
            case CA_do_not_park_in_passing_place -> //not simulated
                    actionsToDo.put(CarAction.CA_do_not_park_in_passing_place, priority);
            case CA_do_not_reverse -> //not simulated
                    actionsToDo.put(CarAction.CA_do_not_reverse, priority);
            case CA_do_not_stop -> //not simulated
                    actionsToDo.put(CarAction.CA_do_not_stop, priority);
            case CA_dontExceedTempSpeedLimit ->
                // adjust speed according to the world speed limit
                    actionsToDo.put(CarAction.CA_dontExceedTempSpeedLimit, priority);
            case CA_dont_cross_solid_white -> actionsToDo.put(CarAction.CA_dont_cross_solid_white, priority);
            case CA_dont_use_central_reservation ->//not simulated
                    actionsToDo.put(CarAction.CA_dont_use_central_reservation, priority);
            case CA_drive_care_attention -> actionsToDo.put(CarAction.CA_drive_care_attention, priority);
            case CA_drive_slowly -> actionsToDo.put(CarAction.CA_drive_slowly, priority);
            case CA_drive_very_slowly -> actionsToDo.put(CarAction.CA_drive_very_slowly, priority);
            case CA_drive_very_slowly_on_bends -> //not simulated
                    actionsToDo.put(CarAction.CA_drive_very_slowly_on_bends, priority);
            case CA_drop_back -> actionsToDo.put(CarAction.CA_drop_back, priority);
            case CA_dry_brakes ->//not simulated
                    actionsToDo.put(CarAction.CA_dry_brakes, priority);
            case CA_ease_off ->//not simulated
                    actionsToDo.put(CarAction.CA_ease_off, priority);
            case CA_engage_child_locks ->//not simulated
                    actionsToDo.put(CarAction.CA_engage_child_locks, priority);
            case CA_engage_parking_break ->//not simulated
                    actionsToDo.put(CarAction.CA_engage_parking_break, priority);
            case CA_engine_off -> actionsToDo.put(CarAction.CA_engine_off, priority);
            case CA_find_other_route ->//not simulated
                    actionsToDo.put(CarAction.CA_find_other_route, priority);
            case CA_find_quiet_side_road ->//not simulated
                    actionsToDo.put(CarAction.CA_find_quiet_side_road, priority);
            case CA_find_safe_place_to_stop ->//not simulated
                    actionsToDo.put(CarAction.CA_find_safe_place_to_stop, priority);
            case CA_fit_booster_seat ->//not simulated
                    actionsToDo.put(CarAction.CA_fit_booster_seat, priority);
            case CA_flash_amber_beacon ->//not simulated
                    actionsToDo.put(CarAction.CA_flash_amber_beacon, priority);
            case CA_fog_lights_off ->//not simulated
                    actionsToDo.put(CarAction.CA_fog_lights_off, priority);
            case CA_fog_lights_on ->//not simulated
                    actionsToDo.put(CarAction.CA_fog_lights_on, priority);
            case CA_followLaneSigns ->//not simulated
                    actionsToDo.put(CarAction.CA_followLaneSigns, priority);
            case CA_follow_dvsa_until_stopped ->//not simulated
                    actionsToDo.put(CarAction.CA_follow_dvsa_until_stopped, priority);
            case CA_follow_police_direction ->//not simulated
                    actionsToDo.put(CarAction.CA_follow_police_direction, priority);
            case CA_follow_sign ->//not simulated
                    actionsToDo.put(CarAction.CA_follow_sign, priority);
            case CA_follow_signs ->//not simulated
                    actionsToDo.put(CarAction.CA_follow_signs, priority);
            case CA_get_in_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_get_in_lane, priority);
            case CA_get_into_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_get_into_lane, priority);
            case CA_get_off_road ->//not simulated
                    actionsToDo.put(CarAction.CA_get_off_road, priority);
            case CA_give_extensive_extra_seperation_distance ->//not simulated
                    actionsToDo.put(CarAction.CA_give_extensive_extra_seperation_distance, priority);
            case CA_give_extra_seperation_distance ->//not simulated
                    actionsToDo.put(CarAction.CA_give_extra_seperation_distance, priority);
            case CA_give_priority_to_public_transport ->//not simulated
                    actionsToDo.put(CarAction.CA_give_priority_to_public_transport, priority);
            case CA_give_priority_to_right ->//not simulated
                    actionsToDo.put(CarAction.CA_give_priority_to_right, priority);
            case CA_give_room_when_passing ->//not simulated
                    actionsToDo.put(CarAction.CA_give_room_when_passing, priority);
            case CA_give_signal ->//not simulated
                    actionsToDo.put(CarAction.CA_give_signal, priority);
            case CA_give_up_control ->//not simulated
                    actionsToDo.put(CarAction.CA_give_up_control, priority);
            case CA_give_way_at_dotted_white_line ->//not simulated
                    actionsToDo.put(CarAction.CA_give_way_at_dotted_white_line, priority);
            case CA_give_way_other_roads ->//not simulated
                    actionsToDo.put(CarAction.CA_give_way_other_roads, priority);
            case CA_give_way_to_other ->//not simulated
                    actionsToDo.put(CarAction.CA_give_way_to_other, priority);
            case CA_give_way_to_pedestrians -> actionsToDo.put(CarAction.CA_give_way_to_pedestrians, priority);
            case CA_give_way_to_tram ->//not simulated
                    actionsToDo.put(CarAction.CA_give_way_to_tram, priority);
            case CA_goBetweenLaneDividers ->//not simulated
                    actionsToDo.put(CarAction.CA_goBetweenLaneDividers, priority);
            case CA_go_to_left_hand_land ->//not simulated
                    actionsToDo.put(CarAction.CA_go_to_left_hand_land, priority);
            case CA_going_left_use_left ->//not simulated
                    actionsToDo.put(CarAction.CA_going_left_use_left, priority);
            case CA_going_right_use_left ->//not simulated
                    actionsToDo.put(CarAction.CA_going_right_use_left, priority);
            case CA_handbrake_on ->//not simulated
                    actionsToDo.put(CarAction.CA_handbrake_on, priority);
            case CA_headlights_on ->//not simulated
                    actionsToDo.put(CarAction.CA_headlights_on, priority);
            case CA_increase_distance_to_car_infront ->
                    actionsToDo.put(CarAction.CA_increase_distance_to_car_infront, priority);
            case CA_indicatorOn ->//not simulated
                    actionsToDo.put(CarAction.CA_indicatorOn, priority);
            case CA_indicator_on ->//not simulated
                    actionsToDo.put(CarAction.CA_indicator_on, priority);
            case CA_keep_crossing_clear ->//not simulated\
                    actionsToDo.put(CarAction.CA_keep_crossing_clear, priority);
            case CA_keep_left -> //not simulated
                    actionsToDo.put(CarAction.CA_keep_left, priority);
            case CA_keep_left_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_keep_left_lane, priority);
            case CA_keep_safe_distance -> actionsToDo.put(CarAction.CA_keep_safe_distance, priority);
            case CA_keep_sidelights_on ->//not simulated
                    actionsToDo.put(CarAction.CA_keep_sidelights_on, priority);
            case CA_keep_under_speed_limit -> actionsToDo.put(CarAction.CA_keep_under_speed_limit, priority);
            case CA_keep_well_back ->//not simulated
                    actionsToDo.put(CarAction.CA_keep_well_back, priority);
            case CA_lane_clear ->//not simulated
                    actionsToDo.put(CarAction.CA_lane_clear, priority);
            case CA_leave_space_for_manover ->//not simulated
                    actionsToDo.put(CarAction.CA_leave_space_for_manover, priority);
            case CA_leave_space_to_stop ->//not simulated
                    actionsToDo.put(CarAction.CA_leave_space_to_stop, priority);
            case CA_light_and_number_plates_clean ->//not simulated
                    actionsToDo.put(CarAction.CA_light_and_number_plates_clean, priority);
            case CA_lock ->//not simulated
                    actionsToDo.put(CarAction.CA_lock, priority);
            case CA_maintained_reduced_speed ->//not simulated
                    actionsToDo.put(CarAction.CA_maintained_reduced_speed, priority);
            case CA_match_speed_to_motorway ->//not simulated
                    actionsToDo.put(CarAction.CA_match_speed_to_motorway, priority);
            case CA_mergeInTurn ->//not simulated
                    actionsToDo.put(CarAction.CA_mergeInTurn, priority);
            case CA_merge_in_turn ->//not simulated
                    actionsToDo.put(CarAction.CA_merge_in_turn, priority);
            case CA_mini ->//not simulated
                    actionsToDo.put(CarAction.CA_mini, priority);
            case CA_minimise_reversing ->//not simulated
                    actionsToDo.put(CarAction.CA_minimise_reversing, priority);
            case CA_mirrors_clear ->//not simulated
                    actionsToDo.put(CarAction.CA_mirrors_clear, priority);
            case CA_move_adjacent_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_move_adjacent_lane, priority);
            case CA_move_left ->
                //after finishing overtaking, the AI car should go to its original line
                    actionsToDo.put(CarAction.CA_move_left, priority);
            case CA_move_quickly_past -> actionsToDo.put(CarAction.CA_move_quickly_past, priority);
            case CA_move_to_left_hand_lane -> //not simulated
                    actionsToDo.put(CarAction.CA_move_to_left_hand_lane, priority);
            case CA_must_stop_pedestrian_crossing ->
                    actionsToDo.put(CarAction.CA_must_stop_pedestrian_crossing, priority);
            case CA_nextLaneClear ->//not simulated
                    actionsToDo.put(CarAction.CA_nextLaneClear, priority);
            case CA_next_safe_stop ->//not simulated
                    actionsToDo.put(CarAction.CA_next_safe_stop, priority);
            case CA_not_drive_dangerously -> actionsToDo.put(CarAction.CA_not_drive_dangerously, priority);
            case CA_not_overtaken -> actionsToDo.put(CarAction.CA_not_overtaken, priority);
            case CA_obey_signal ->//not simulated
                    actionsToDo.put(CarAction.CA_obey_signal, priority);
            case CA_obey_work_vehicle_sign ->//not simulated
                    actionsToDo.put(CarAction.CA_obey_work_vehicle_sign, priority);
            case CA_overtake_on_right -> actionsToDo.put(CarAction.CA_overtake_on_right, priority);
            case CA_park_as_close_to_side ->//not simulated
                    actionsToDo.put(CarAction.CA_park_as_close_to_side, priority);
            case CA_parking_lights_on ->//not simulated
                    actionsToDo.put(CarAction.CA_parking_lights_on, priority);
            case CA_pass_around ->//not simulated
                    actionsToDo.put(CarAction.CA_pass_around, priority);
            case CA_position_right_turn ->//not simulated
                    actionsToDo.put(CarAction.CA_position_right_turn, priority);
            case CA_prepare_drop_back -> actionsToDo.put(CarAction.CA_prepare_drop_back, priority);
            case CA_prepare_load ->//not simulated
                    actionsToDo.put(CarAction.CA_prepare_load, priority);
            case CA_prepare_route ->//not simulated
                    actionsToDo.put(CarAction.CA_prepare_route, priority);
            case CA_prepare_to_change_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_prepare_to_change_lane, priority);
            case CA_prepare_to_stop ->//not simulated
                    actionsToDo.put(CarAction.CA_prepare_to_stop, priority);
            case CA_priority_to_motoway_traffic ->//not simulated
                    actionsToDo.put(CarAction.CA_priority_to_motoway_traffic, priority);
            case CA_pull_into_hard_shoulder ->//not simulated
                    actionsToDo.put(CarAction.CA_pull_into_hard_shoulder, priority);
            case CA_pull_into_passing_place ->//not simulated
                    actionsToDo.put(CarAction.CA_pull_into_passing_place, priority);
            case CA_pull_over_safe_place ->//not simulated
                    actionsToDo.put(CarAction.CA_pull_over_safe_place, priority);
            case CA_pull_up_in_visible_distance ->//not simulated
                    actionsToDo.put(CarAction.CA_pull_up_in_visible_distance, priority);
            case CA_put_on_seatbelts ->//not simulated
                    actionsToDo.put(CarAction.CA_put_on_seatbelts, priority);
            case CA_reduce_distance_between_front_vehicle ->
                    actionsToDo.put(CarAction.CA_reduce_distance_between_front_vehicle, priority);
            case CA_reduce_lighting ->//not simulated
                    actionsToDo.put(CarAction.CA_reduce_lighting, priority);
            case CA_reduce_overall_speed -> actionsToDo.put(CarAction.CA_reduce_overall_speed, priority);
            case CA_reduce_speed -> actionsToDo.put(CarAction.CA_reduce_speed, priority);
            case CA_reduce_speed_if_pedestrians -> actionsToDo.put(CarAction.CA_reduce_speed_if_pedestrians, priority);
            case CA_reduce_speed_on_slip_road ->//not simulated
                    actionsToDo.put(CarAction.CA_reduce_speed_on_slip_road, priority);
            case CA_release_brake ->//not simulated
                    actionsToDo.put(CarAction.CA_release_brake, priority);
            case CA_remove_all_snow ->//not simulated
                    actionsToDo.put(CarAction.CA_remove_all_snow, priority);
            case CA_remove_flash_intention ->//not simulated
                    actionsToDo.put(CarAction.CA_remove_flash_intention, priority);
            case CA_remove_horn_intention ->//not simulated
                    actionsToDo.put(CarAction.CA_remove_horn_intention, priority);
            case CA_reverse_into_drive ->//not simulated
                    actionsToDo.put(CarAction.CA_reverse_into_drive, priority);
            case CA_reverse_to_passing_place ->//not simulated
                    actionsToDo.put(CarAction.CA_reverse_to_passing_place, priority);
            case CA_road_clear_to_manover ->//not simulated
                    actionsToDo.put(CarAction.CA_road_clear_to_manover, priority);
            case CA_safe_distance -> actionsToDo.put(CarAction.CA_safe_distance, priority);
            case CA_safe_pull_over_and_stop ->//not simulated
                    actionsToDo.put(CarAction.CA_safe_pull_over_and_stop, priority);
            case CA_select_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_select_lane, priority);
            case CA_set_hazards_off ->//not simulated (hazards is hazard warning light)
                    actionsToDo.put(CarAction.CA_set_hazards_off, priority);
            case CA_set_headlights_to_dipped ->//not simulated
                    actionsToDo.put(CarAction.CA_set_headlights_to_dipped, priority);
            case CA_signal -> //not simulated
                    actionsToDo.put(CarAction.CA_signal, priority);
            case CA_signal_left ->//not simulated
                    actionsToDo.put(CarAction.CA_signal_left, priority);
            case CA_signal_left_on_exit ->//not simulated
                    actionsToDo.put(CarAction.CA_signal_left_on_exit, priority);
            case CA_signal_right ->//not simulated
                    actionsToDo.put(CarAction.CA_signal_right, priority);
            case CA_slow_down -> //not simulated
                //car should slow down if it is in fog, this is not simulated
                    actionsToDo.put(CarAction.CA_slow_down, priority);
            case CA_slow_down_and_stop -> //not simulated
                //car should slow down if the sight is dazzled, this is not simulated
                    actionsToDo.put(CarAction.CA_slow_down_and_stop, priority);
            case CA_space_for_vehicle ->
                //overtaking,should not get too close to the vehicle you intend to overtake
                    actionsToDo.put(CarAction.CA_space_for_vehicle, priority);
            case CA_stay_in_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_stay_in_lane, priority);
            case CA_stay_on_running_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_stay_on_running_lane, priority);
            case CA_steady_speed ->//not simulated
                    actionsToDo.put(CarAction.CA_steady_speed, priority);
            case CA_stop -> actionsToDo.put(CarAction.CA_stop, priority);
            case CA_stopCrossDoubleWhiteClosestSolid ->//not simulated
                    actionsToDo.put(CarAction.CA_stopCrossDoubleWhiteClosestSolid, priority);
            case CA_stopCrossingHazardWarningLine ->//not simulated
                    actionsToDo.put(CarAction.CA_stopCrossingHazardWarningLine, priority);
            case CA_stop_and_turn_engine_off -> actionsToDo.put(CarAction.CA_stop_and_turn_engine_off, priority);
            case CA_stop_at_crossing -> //not simulated
                    actionsToDo.put(CarAction.CA_stop_at_crossing, priority);
            case CA_stop_at_crossing_patrol ->//not simulated
                    actionsToDo.put(CarAction.CA_stop_at_crossing_patrol, priority);
            case CA_stop_at_sign ->//not simulated
                    actionsToDo.put(CarAction.CA_stop_at_sign, priority);
            case CA_stop_at_white_line -> actionsToDo.put(CarAction.CA_stop_at_white_line, priority);
            case CA_switch_off_engine -> actionsToDo.put(CarAction.CA_switch_off_engine, priority);
            case CA_travel_sign_direction ->//not simulated
                    actionsToDo.put(CarAction.CA_travel_sign_direction, priority);
            case CA_treat_as_roundabout ->//not simulated
                    actionsToDo.put(CarAction.CA_treat_as_roundabout, priority);
            case CA_treat_as_traffic_light ->//not simulated
                    actionsToDo.put(CarAction.CA_treat_as_traffic_light, priority);
            case CA_turn_foglights_off ->//not simulated
                    actionsToDo.put(CarAction.CA_turn_foglights_off, priority);
            case CA_turn_into_skid ->//not simulated
                    actionsToDo.put(CarAction.CA_turn_into_skid, priority);
            case CA_turn_sidelights_on ->//not simulated
                    actionsToDo.put(CarAction.CA_turn_sidelights_on, priority);
            case CA_use_central_reservation ->//not simulated
                    actionsToDo.put(CarAction.CA_use_central_reservation, priority);
            case CA_use_crawler_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_use_crawler_lane, priority);
            case CA_use_demisters ->//not simulated
                    actionsToDo.put(CarAction.CA_use_demisters, priority);
            case CA_use_hazard_lights ->//not simulated
                    actionsToDo.put(CarAction.CA_use_hazard_lights, priority);
            case CA_use_left_indicator ->//not simulated
                    actionsToDo.put(CarAction.CA_use_left_indicator, priority);
            case CA_use_right_indicator ->//not simulated
                    actionsToDo.put(CarAction.CA_use_right_indicator, priority);
            case CA_use_road ->//not simulated
                    actionsToDo.put(CarAction.CA_use_road, priority);
            case CA_use_signals ->//not simulated
                    actionsToDo.put(CarAction.CA_use_signals, priority);
            case CA_use_tram_passing_lane ->//not simulated
                    actionsToDo.put(CarAction.CA_use_tram_passing_lane, priority);
            case CA_use_windscreen_wipers ->//not simulated
                    actionsToDo.put(CarAction.CA_use_windscreen_wipers, priority);
            case CA_wait_at_advanced_stop ->//not simulated
                    actionsToDo.put(CarAction.CA_wait_at_advanced_stop, priority);
            case CA_wait_at_white_line -> actionsToDo.put(CarAction.CA_wait_at_white_line, priority);
            case CA_wait_at_first_white_line ->//not simulated
                    actionsToDo.put(CarAction.CA_wait_at_first_white_line, priority);
            case CA_wait_for_gap_before_moving_off ->
                    actionsToDo.put(CarAction.CA_wait_for_gap_before_moving_off, priority);
            case CA_wait_until_clear ->//not simulated
                    actionsToDo.put(CarAction.CA_wait_until_clear, priority);
            case CA_wait_until_route_clear ->//not simulated
                    actionsToDo.put(CarAction.CA_wait_until_route_clear, priority);
            case CA_wait_until_safe_gap -> actionsToDo.put(CarAction.CA_wait_until_safe_gap, priority);
            case CA_wheel_away_from_kerb ->//not simulated
                    actionsToDo.put(CarAction.CA_wheel_away_from_kerb, priority);
            case CA_wheel_toward_from_kerb ->//not simulated
                    actionsToDo.put(CarAction.CA_wheel_toward_from_kerb, priority);
            default -> {
            }
        }
    }
    
    public void updateIntentions(WorldSim visibleWorld, Point location,Direction cmd, Direction pmd) {
        for (CarIntention ci : CarIntention.values()) {
            switch(ci) {
                case CI_approachingTrafficLight:
                    break;
                case CI_areaWithSolidWhiteBorder://not simulated
                    intentions.put(CarIntention.CI_areaWithSolidWhiteBorder,false);
                    break;
                case CI_beInCycleLane: //not simulated
                    intentions.put(CarIntention.CI_beInCycleLane,false);
                    break;
                case CI_brake://not simulated
                    intentions.put(CarIntention.CI_brake,false);
                    break;
                case CI_changeCourseOrDirection://not simulated
                    intentions.put(CarIntention.CI_changeCourseOrDirection,false);
                    break;
                case CI_changeLane://not simulated
                    intentions.put(CarIntention.CI_changeLane,false);
                    break;
                case CI_crossControlledCrossing://not simulated
                    intentions.put(CarIntention.CI_crossControlledCrossing,false);
                    break;
                case CI_crossDoubleWhiteClosestSolid://not simulated
                    intentions.put(CarIntention.CI_crossDoubleWhiteClosestSolid,false);
                    break; 
                case CI_crossDualCarriageWay://not simulated
                    intentions.put(CarIntention.CI_crossDualCarriageWay,false);
                    break;
                case CI_crossHazardWarningLine://not simulated
                    intentions.put(CarIntention. CI_crossHazardWarningLine,false);
                    break;
                case CI_crossLevelCrossing://not simulated
                    intentions.put(CarIntention.CI_crossLevelCrossing,false);
                    break;
                case CI_diagnosingFaults://not simulated
                    intentions.put(CarIntention.CI_diagnosingFaults,false);
                    break;
                case CI_dropOffPassengers://not simulated
                    intentions.put(CarIntention.CI_dropOffPassengers,false);
                    break;
                case CI_enterBoxJunction://not simulated
                    intentions.put(CarIntention.CI_enterBoxJunction,false);
                    break;
                case CI_enterMotorway://not simulated
                    intentions.put(CarIntention.CI_enterMotorway,false);
                    break;
                case CI_enterTramReservedRoad://not simulated
                    intentions.put(CarIntention. CI_enterTramReservedRoad,false);
                    break;
                case CI_enterWhiteDiagonalStripeWhiteBrokenBorder://not simulated
                    intentions.put(CarIntention.CI_enterWhiteDiagonalStripeWhiteBrokenBorder,false);
                    break;
                case CI_enterWhiteDiagonalStripeWhiteSolidBorder://not simulated
                    intentions.put(CarIntention.CI_enterWhiteDiagonalStripeWhiteSolidBorder,false);
                    break;
                case CI_firstExitRoundabout://not simulated
                    intentions.put(CarIntention.CI_firstExitRoundabout,false);
                    break;
                case CI_flashHeadlight://not simulated
                    intentions.put(CarIntention.CI_flashHeadlight,false);
                    break;
                case CI_goodsLoadingUn://not simulated
                    intentions.put(CarIntention.CI_goodsLoadingUn,false);
                    break;
                case CI_joinMotorway://not simulated
                    intentions.put(CarIntention.CI_joinMotorway,false);
                    break;
                case CI_leaveMotorway://not simulated
                    intentions.put(CarIntention.CI_leaveMotorway,false);
                    break;
                case CI_loadUnloading://not simulated
                    intentions.put(CarIntention.CI_loadUnloading,false);
                    break;
                case CI_otherRoundabout: //not simulated
                    intentions.put(CarIntention.CI_otherRoundabout,false);
                    break;
                // car intends to overtake other car if there are cars towards it, and the speed of other car is slow
                case CI_overtake:
                    //driving direction is north
                    if(cmd == Direction.north){
                        for(int i = location.getY() - 1; i >= 0; i--){
                            if(visibleWorld.containsCar(location.getX(), i)){
                                AbstractCar car1 = visibleWorld.getCarAtPosition(location.getX(), i);
                                System.out.println(car1.getSpeed());
                                if(car1.getSpeed() == 1 && car1.getCMD() == cmd){
                                    intentions.put(ci,true);
                                }
                            }
                        }
                    }
                    //driving direction is south
                    else if(cmd == Direction.south){
                        for(int i = location.getY() + 1; i < visibleWorld.getHeight(); i++){
                            if(visibleWorld.containsCar(location.getX(), i)){
                                AbstractCar car1 = visibleWorld.getCarAtPosition(location.getX(), i);
                                if(car1.getSpeed() == 1 && car1.getCMD() == cmd){
                                    intentions.put(ci,true);
                                }
                            }
                        }
                    }
                    //driving direction is east
                    else if(cmd == Direction.east){
                        for(int i = location.getX() + 1; i < visibleWorld.getWidth(); i++){
                            if(visibleWorld.containsCar(i, location.getY())){
                                AbstractCar car1 = visibleWorld.getCarAtPosition(i, location.getY());
                                if(car1.getSpeed() == 1 && car1.getCMD() == cmd){
                                    intentions.put(ci,true);
                                }
                            }
                        }
                    }
                    //driving direction is west
                    else if(cmd == Direction.west){
                        for(int i = location.getX() - 1; i >= 0; i--){
                            if(visibleWorld.containsCar(i, location.getY())){
                                AbstractCar car1 = visibleWorld.getCarAtPosition(i, location.getY());
                                if(car1.getSpeed() == 1 && car1.getCMD() == cmd){
                                    intentions.put(ci,true);
                                }
                            }
                        }
                    }
                    break;
                case CI_overtakeSnowplow: //not simulated
                    intentions.put(CarIntention.CI_overtakeSnowplow,false);
                    break;
                case CI_park://not simulated
                    intentions.put(CarIntention.CI_park,false);
                    break;
                case CI_passParkedVehicles://not simulated
                    intentions.put(CarIntention.CI_passParkedVehicles,false);
                    break;
                case CI_passVehicles://not simulated
                    intentions.put(CarIntention.CI_passVehicles,false);
                    break;
                case CI_pullIntoDriveway://not simulated
                    intentions.put(CarIntention.CI_pullIntoDriveway,false);
                    break;
                case CI_reversing://not simulated
                    intentions.put(CarIntention.CI_reversing,false);
                    break;
                case CI_rightExitRoundabout://not simulated
                    intentions.put(CarIntention.CI_rightExitRoundabout,false);
                    break;
                case CI_selectLane://not simulated
                    intentions.put(CarIntention.CI_selectLane,false);
                    break;
                case CI_setHazardsOn://not simulated
                    intentions.put(CarIntention.CI_setHazardsOn,false);
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
                case CI_settingOff://not simulated
                    intentions.put(CarIntention.CI_settingOff,false);
                    break;
                case CI_soundHorn://not simulated
                    intentions.put(CarIntention.CI_soundHorn,false);
                    break;
                case CI_stop://not simulated
                    intentions.put(CarIntention.CI_stop,false);
                    break;
                case CI_towing://not simulated
                    intentions.put(CarIntention. CI_towing,false);
                    break;
                case CI_turnOff: //not simulated
                    intentions.put(CarIntention.CI_turnOff,false);
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
                case CI_undertaking://not simulated
                    intentions.put(CarIntention.CI_undertaking,false);
                    break;
                case CI_uturn: //not simulated
                    intentions.put(CarIntention.CI_uturn,false);
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
                beliefs.put(CarBelief.CB_allChildrenUsingChildSeatAsRequired,false);
                break;
            case CB_allPassengersWearingSeatBeltsAsRequired: // not simulated
                beliefs.put(CarBelief. CB_allPassengersWearingSeatBeltsAsRequired,false);
                break;
            case CB_approachingCorner:
                switch (cmd) {
                    case east ->
                            wallAhead = visibleWorld.getCell(location.getX() + 1, location.getY()).getCellType() != CellType.ct_road;
                    case north ->
                            wallAhead = visibleWorld.getCell(location.getX(), location.getY() - 1).getCellType() != CellType.ct_road;
                    case south ->
                            wallAhead = visibleWorld.getCell(location.getX(), location.getY() + 1).getCellType() != CellType.ct_road;
                    case west ->
                            wallAhead = visibleWorld.getCell(location.getX() - 1, location.getY()).getCellType() != CellType.ct_road;
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
            case CB_behindWantToOvertake: // not simulated
                beliefs.put(CarBelief.CB_behindWantToOvertake,false);
                break;
            case CB_bendInRoad://Not simulated
                beliefs.put(CarBelief.CB_bendInRoad,false);
                break;
            case CB_brokendown://Car cannot break down.
                beliefs.put(CarBelief.CB_brokendown,false);
                break;
            case CB_canReadNumberPlate://not simulated
                beliefs.put(CarBelief.CB_canReadNumberPlate,false);
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
                beliefs.put(CarBelief.CB_carriageway,false);
                break;
            case CB_centerLine: //Not simulated
                beliefs.put(CarBelief.CB_centerLine,false);
                break;
            case CB_clearRoadAhead: // Not simulated
                beliefs.put(CarBelief.CB_clearRoadAhead,false);
                break;
            case CB_clearToManover: //Not simulated
                beliefs.put(CarBelief.CB_clearToManover,false);
                break;
            case CB_clearToTurnOff: //Not simulated
                beliefs.put(CarBelief.CB_clearToTurnOff,false);
                break;
            case CB_clearway:       // Not simulated
                beliefs.put(CarBelief.CB_clearway,false);
                break;
            case CB_completeOvertakeBeforeSolidWhiteLine://No solid white lines simulated
                break;
            case CB_damagedOrInjury:
                beliefs.put(cb, isCrashed());
                break;
            case CB_directionSigns: //Not simulated
                beliefs.put(CarBelief.CB_directionSigns,false);
                break;
            case CB_dottedWhiteLineAcrossRoad: //Not simulated
                beliefs.put(CarBelief.CB_dottedWhiteLineAcrossRoad,false);
                break;
            case CB_doubleWhiteLines: //Not simulated
                beliefs.put(CarBelief.CB_doubleWhiteLines,false);
                break;
            case CB_driving: //Not simulated
                beliefs.put(CarBelief.CB_driving,false);
                break;
            case CB_dualCarriageWay: //Not simulated
                beliefs.put(CarBelief.CB_dualCarriageWay,false);
                break;
            case CB_enterWhiteDiagonalStripeWhiteBrokenBorderNecessary: //Not simulated
                beliefs.put(CarBelief.CB_enterWhiteDiagonalStripeWhiteBrokenBorderNecessary,false);
                break;
            case CB_essentialTravel: //Not simulated
                beliefs.put(CarBelief.CB_essentialTravel,false);
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
                beliefs.put(CarBelief.CB_finishedManoeuvre,false);
                break;
            case CB_flashingAmber: //Not simulated
                beliefs.put(CarBelief.CB_flashingAmber,false);
                break;
            case CB_flashingAmberBeacon: //Not simulated
                beliefs.put(CarBelief.CB_flashingAmberBeacon,false);
                break;
            case CB_flashingRed: //Not simulated
                beliefs.put(CarBelief.CB_flashingRed,false);
                break;
            case CB_fuel: //Not simulated
                beliefs.put(CarBelief.CB_fuel,false);
                break;
            case CB_greenLight: //Not simulated
                beliefs.put(CarBelief.CB_greenLight,false);
                break;
            case CB_hazardAhead: //Not simulated
                beliefs.put(CarBelief.CB_hazardAhead,false);
                break;
            case CB_headlightsOff: //Not simulated
                beliefs.put(CarBelief.CB_headlightsOff,false);
                break;
            case CB_indicatorOn: //Not simulated
                beliefs.put(CarBelief.CB_indicatorOn,false);
                break;
            case CB_informOtherRoadUser: //Not simulated
                beliefs.put(CarBelief.CB_informOtherRoadUser,false);
                break;
            case CB_laneAvailiable: //Not simulated
                beliefs.put(CarBelief.CB_laneAvailiable,false);
                break;
            case CB_laneCleared: //Not simulated
                beliefs.put(CarBelief.CB_laneCleared,false);
                break;
            case CB_laneDividers: //Not simulated
                beliefs.put(CarBelief.CB_laneDividers,false);
                break;
            case CB_laneRestricted: //Not simulated
                beliefs.put(CarBelief. CB_laneRestricted,false);
                break;
            case CB_lanes2: //Not simulated
                beliefs.put(CarBelief.CB_lanes2,false);
                break;
            case CB_largeVehicle: //Not simulated
                //driving direction is north
                if(cmd == Direction.north){
                    for(int i = location.getY() - 1; i >= 0; i--){
                        if(visibleWorld.containsCar(location.getX(), i)){
                            AbstractCar car1 = visibleWorld.getCarAtPosition(location.getX(), i);
                            if(car1.getCarType() == CarType.car_large && car1.getCMD() == cmd){
                                beliefs.put(cb, true);
                            }
                        }
                    }
                }
                //driving direction is south
                else if(cmd == Direction.south){
                    for(int i = location.getY() + 1; i < visibleWorld.getHeight(); i++){
                        if(visibleWorld.containsCar(location.getX(), i)){
                            AbstractCar car1 = visibleWorld.getCarAtPosition(location.getX(), i);
                            if(car1.getCarType() == CarType.car_large && car1.getCMD() == cmd){
                                beliefs.put(cb, true);
                            }
                        }
                    }
                }
                //driving direction is east
                else if(cmd == Direction.east){
                    for(int i = location.getX() + 1; i < visibleWorld.getWidth(); i++){
                        if(visibleWorld.containsCar(i, location.getY())){
                            AbstractCar car1 = visibleWorld.getCarAtPosition(i, location.getY());
                            if(car1.getCarType() == CarType.car_large && car1.getCMD() == cmd){
                                beliefs.put(cb, true);
                            }
                        }
                    }
                }
                //driving direction is west
                else if(cmd == Direction.west){
                    for(int i = location.getX() - 1; i >= 0; i--){
                        if(visibleWorld.containsCar(i, location.getY())){
                            AbstractCar car1 = visibleWorld.getCarAtPosition(i, location.getY());
                            if(car1.getCarType() == CarType.car_large && car1.getCMD() == cmd){
                                beliefs.put(cb, true);
                            }
                        }
                    }
                }
                break;
            case CB_largeVehicleInFront: //Not simulated
                //driving direction is north
                if(cmd == Direction.north){
                    for(int i = location.getY() - 1; i >= 0; i--){
                        if(visibleWorld.containsCar(location.getX(), i)){
                            AbstractCar car1 = visibleWorld.getCarAtPosition(location.getX(), i);
                            if(car1.getCarType() == CarType.car_large){
                                beliefs.put(cb, true);
                            }
                        }
                    }
                }
                //driving direction is south
                else if(cmd == Direction.south){
                    for(int i = location.getY() + 1; i < visibleWorld.getHeight(); i++){
                        if(visibleWorld.containsCar(location.getX(), i)){
                            AbstractCar car1 = visibleWorld.getCarAtPosition(location.getX(), i);
                            if(car1.getCarType() == CarType.car_large){
                                beliefs.put(cb, true);
                            }
                        }
                    }
                }
                //driving direction is east
                else if(cmd == Direction.east){
                    for(int i = location.getX() + 1; i < visibleWorld.getWidth(); i++){
                        if(visibleWorld.containsCar(i, location.getY())){
                            AbstractCar car1 = visibleWorld.getCarAtPosition(i, location.getY());
                            if(car1.getCarType() == CarType.car_large){
                                beliefs.put(cb, true);
                            }
                        }
                    }
                }
                //driving direction is west
                else if(cmd == Direction.west){
                    for(int i = location.getX() - 1; i >= 0; i--){
                        if(visibleWorld.containsCar(i, location.getY())){
                            AbstractCar car1 = visibleWorld.getCarAtPosition(i, location.getY());
                            if(car1.getCarType() == CarType.car_large){
                                beliefs.put(cb, true);
                            }
                        }
                    }
                }
                break;
            case CB_leftMostLane: //Not simulated
                beliefs.put(CarBelief.CB_leftMostLane,false);
                break;
            // simulated yellow light
            case CB_lightAmber:
                beliefs.put(cb, false);
                boolean yellowLightOn;
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
                beliefs.put(CarBelief.CB_lightFlashingAmber,false);
                break;
            // simulated green light
            case CB_lightGreen:
                beliefs.put(cb, false);
                boolean greenLightOn;
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
                boolean redLightOn;
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
                beliefs.put(CarBelief.CB_mainRoadNextRoad,false);
                break;
            case CB_maxPossibleSpeed25orLess: // not simulated
                beliefs.put(CarBelief.CB_maxPossibleSpeed25orLess,false);
                break;
            case CB_nextLaneClear: // not simulated
                beliefs.put(CarBelief.CB_nextLaneClear,false);
                break;
            case CB_night: // not simulated
                beliefs.put(CarBelief. CB_night,false);
                break;
            case CB_noCrossingLights: // not simulated
                beliefs.put(CarBelief.CB_noCrossingLights,false);
                break;
            case CB_noLights: // not simulated
                beliefs.put(CarBelief.CB_noLights,false);
                break;
            case CB_nodanger: // not simulated
                beliefs.put(CarBelief.CB_nodanger,false);
                break;
            case CB_openCrossing: // not simulated
                beliefs.put(CarBelief.CB_openCrossing,false);
                break;
            case CB_oppositeParkedVehicle: // not simulated
                beliefs.put(CarBelief.CB_oppositeParkedVehicle,false);
                break;
            case CB_oppositeTrafficIsland: // not simulated
                beliefs.put(CarBelief.CB_oppositeTrafficIsland,false);
                break;
            case CB_overtaking: // the car is currently overtaking other car //TODO
                break;
            case CB_passedFirstWhiteLine: // not simulated
                beliefs.put(CarBelief.CB_passedFirstWhiteLine,false);
                break;
            case CB_rightHandLane: //not simulated
                beliefs.put(CarBelief.CB_rightHandLane,false);
                break;
            case CB_roadAheadClear: //not simulated
                beliefs.put(CarBelief.CB_roadAheadClear,false);
                break;
            case CB_roadClear: //not simulated
                beliefs.put(CarBelief.CB_roadClear,false);
                break;
            case CB_routeClear: //not simulated
                beliefs.put(CarBelief. CB_routeClear,false);
                break;
            case CB_routePlanned: //not simulated
                beliefs.put(CarBelief.CB_routePlanned,false);
                break;
            case CB_safeToCross: //not simulated
                beliefs.put(CarBelief.CB_safeToCross,false);
                break;
            case CB_safeToEnter: //not simulated
                beliefs.put(CarBelief.CB_safeToEnter,false);
                break; 
            case CB_sharingRoadWithOthers: //not simulated
                beliefs.put(CarBelief.CB_sharingRoadWithOthers,false);
                break;
            case CB_sidelightsOff: //not simulated
                beliefs.put(CarBelief.CB_sidelightsOff,false);
                break;
            case CB_singleCarriageWay: //not simulated
                beliefs.put(CarBelief.CB_singleCarriageWay,false);
                break;
            case CB_singleTrackRoad: //not simulated
                beliefs.put(CarBelief.CB_singleTrackRoad,false);
                break;
            case CB_stationary: //not simulated
                beliefs.put(CarBelief.CB_stationary,false);
                break;
            case CB_stationaryTraffic: //not simulated
                beliefs.put(CarBelief.CB_stationaryTraffic,false);
                break;
            case CB_stayingInLane: //not simulated
                beliefs.put(CarBelief.CB_stayingInLane,false);
                break;
            case CB_toJunction10meters: //not simulated
                beliefs.put(CarBelief.CB_toJunction10meters,false);
                break;
            case CB_toflashingAmber: //not simulated
                beliefs.put(CarBelief.CB_toflashingAmber,false);
                break;
            case CB_tpDirectingLeft: //not simulated
                beliefs.put(CarBelief.CB_tpDirectingLeft,false);
                break;
            case CB_turning: //not simulated
                beliefs.put(CarBelief.CB_turning,false);
                break;
            //TODO
            case CB_unableToStopByWhiteLine: // car is always about to stop by the white line
                beliefs.put(cb, false);
                break;
            case CB_vehicleSafe: //not simulated
                beliefs.put(CarBelief.CB_vehicleSafe,false);
                break;
            case CB_whiteLineAcrossRoad: //not simulated
                beliefs.put(CarBelief.CB_whiteLineAcrossRoad,false);
                break;
            case CB_accessProperty: //not simulated
                beliefs.put(CarBelief.CB_accessProperty,false);
                break;
            case CB_activefrontalairbaginfrontpassengerseat: //not simulated
                beliefs.put(CarBelief.CB_activefrontalairbaginfrontpassengerseat,false);
                break;
            case CB_adverseWeather: //not simulated
                beliefs.put(CarBelief.CB_adverseWeather,false);
                break;
            case CB_againstFlowOfTraffic: //not simulated
                beliefs.put(CarBelief.CB_againstFlowOfTraffic,false);
                break;
            case CB_amSlowMovingVehicle: //not simulated
                beliefs.put(CarBelief.CB_amSlowMovingVehicle,false);
                break;
            case CB_amber: //not simulated
                beliefs.put(CarBelief.CB_amber,false);
                break;
            case CB_animalInRoad: //not simulated
                beliefs.put(CarBelief.CB_animalInRoad,false);
                break;
            case CB_approachingBrow: //not simulated
                beliefs.put(CarBelief.CB_approachingBrow,false);
                break;
            case CB_approachingFog: //not simulated
                beliefs.put(CarBelief.CB_approachingFog,false);
                break;
            case CB_approachingHumpBridge: //not simulated
                beliefs.put(CarBelief.CB_approachingHumpBridge,false);
                break;
            case CB_approachingJunction: //not simulated
                beliefs.put(CarBelief.CB_approachingJunction,false);
                break;
            case CB_approachingRoundabout: //not simulated
                beliefs.put(CarBelief.CB_approachingRoundabout,false);
                break;
            case CB_approachingSchoolCrossing: //not simulated
                beliefs.put(CarBelief.CB_approachingSchoolCrossing,false);
                break;
            case CB_atCrossing: //not simulated
                beliefs.put(CarBelief.CB_atCrossing,false);
                break;
            case CB_authorisedParkingPlace: //not simulated
                beliefs.put(CarBelief.CB_authorisedParkingPlace,false);
                break;
            case CB_barriersOpen: //not simulated
                beliefs.put(CarBelief. CB_barriersOpen,false);
                break;
            case CB_betweenLanes: //not simulated
                beliefs.put(CarBelief.CB_betweenLanes,false);
                break;
            case CB_betweenSunriseSunset: //not simulated
                beliefs.put(CarBelief.CB_betweenSunriseSunset,false);
                break;
            case CB_boosterSeatsRequired: //not simulated
                beliefs.put(CarBelief.CB_boosterSeatsRequired,false);
                break;
            case CB_boosterSeatsfittedCorrectly: //not simulated
                beliefs.put(CarBelief.CB_boosterSeatsfittedCorrectly,false);
                break;
            case CB_bridleway: //not simulated
                beliefs.put(CarBelief.CB_bridleway,false);
                break;
            case CB_builtuparea: //not simulated
                beliefs.put(CarBelief.CB_builtuparea,false);
                break;
            case CB_busLane: //not simulated
                beliefs.put(CarBelief.CB_busLane,false);
                break;
            case CB_busLaneInOperation: //not simulated
                beliefs.put(CarBelief.CB_busLaneInOperation,false);
                break;
            case CB_canPassAnimal: //not simulated
                beliefs.put(CarBelief.CB_canPassAnimal,false);
                break;
            case CB_carInFrontTurningRight: //not simulated
                beliefs.put(CarBelief.CB_carInFrontTurningRight,false);
                break;
            case CB_childPassengers: //not simulated
                beliefs.put(CarBelief.CB_childPassengers,false);
                break;
            case CB_congestedTraffic: //not simulated
                beliefs.put(CarBelief.CB_congestedTraffic,false);
                break;
            case CB_contraflow: //not simulated
                beliefs.put(CarBelief.CB_contraflow,false);
                break;
            case CB_controlledCrossing: //not simulated
                beliefs.put(CarBelief.CB_controlledCrossing,false);
                break;
            case CB_crawlerLaneExists: //not simulated
                beliefs.put(CarBelief.CB_crawlerLaneExists,false);
                break;
            case CB_crossinglightsOff: //not simulated
                beliefs.put(CarBelief.CB_crossinglightsOff,false);
                break;
            case CB_crowdedShoppingStreet: //not simulated
                beliefs.put(CarBelief.CB_crowdedShoppingStreet,false);
                break;
            case CB_cycleLaneUnavoidable: //not simulated
                beliefs.put(CarBelief.CB_cycleLaneUnavoidable,false);
                break;
            case CB_cyclelane: //not simulated
                beliefs.put(CarBelief.CB_cyclelane,false);
                break;
            case CB_dangerousToStop: //not simulated
                beliefs.put(CarBelief.CB_dangerousToStop,false);
                break;
            case CB_dazzled: //not simulated
                beliefs.put(CarBelief. CB_dazzled,false);
                break;
            case CB_dedicatedParkingArea: //not simulated
                beliefs.put(CarBelief.CB_dedicatedParkingArea,false);
                break;
            case CB_directedByPoliceOfficer: //not simulated
                beliefs.put(CarBelief.CB_directedByPoliceOfficer,false);
                break;
            case CB_doubleYellowLine: //not simulated
                beliefs.put(CarBelief.CB_doubleYellowLine,false);
                break;
            case CB_downhill: //not simulated
                beliefs.put(CarBelief.CB_downhill,false);
                break;
            case CB_drivenThroughDeepPuddle: //not simulated
                beliefs.put(CarBelief.CB_drivenThroughDeepPuddle,false);
                break;
            case CB_driverWantsControl: //not simulated
                beliefs.put(CarBelief.CB_driverWantsControl,false);
                break;
            case CB_dullWeather: //not simulated
                beliefs.put(CarBelief.CB_dullWeather,false);
                break;
            case CB_dvsaflashingAmber: //not simulated
                beliefs.put(CarBelief.CB_dvsaflashingAmber,false);
                break;
            case CB_dvsafollowRequest: //not simulated
                beliefs.put(CarBelief.CB_dvsafollowRequest,false);
                break;
            case CB_dvsapullOverSignal: //not simulated
                beliefs.put(CarBelief. CB_dvsapullOverSignal,false);
                break;
            case CB_emergencyArea: //not simulated
                beliefs.put(CarBelief.CB_emergencyArea,false);
                break;
            case CB_emergencyVehicle: //not simulated
                beliefs.put(CarBelief.CB_emergencyVehicle,false);
                break;
            case CB_emergencyVehicleFlashingLightsAndStopped: //not simulated
                beliefs.put(CarBelief.CB_emergencyVehicleFlashingLightsAndStopped,false);
                break;
            case CB_enterRestrictedLane: //not simulated
                beliefs.put(CarBelief.CB_enterRestrictedLane,false);
                break;
            case CB_equestrianCrossing: //not simulated
                beliefs.put(CarBelief.CB_equestrianCrossing,false);
                break;
            case CB_flashingSirens: //not simulated
                beliefs.put(CarBelief.CB_flashingSirens,false);
                break;
            case CB_fog://not simulated
                beliefs.put(CarBelief.CB_fog,false);
                break;
            case CB_fogLightsOn: //not simulated
                beliefs.put(CarBelief.CB_fogLightsOn,false);
                break;
            case CB_footpath: //not simulated
                beliefs.put(CarBelief.CB_footpath,false);
                break;
            case CB_forceTrafficToTramlane: //not simulated
                beliefs.put(CarBelief.CB_forceTrafficToTramlane,false);
                break;
            case CB_gearNeutral: //not simulated
                beliefs.put(CarBelief.CB_gearNeutral,false);
                break;
            case CB_giveWaySign://not simulated
                beliefs.put(CarBelief.CB_giveWaySign,false);
                break;
            case CB_goingDownhill://not simulated
                beliefs.put(CarBelief.CB_goingDownhill,false);
                break;
            case CB_greenFilterLightForExit://not simulated
                beliefs.put(CarBelief.CB_greenFilterLightForExit,false);
                break;
            case CB_hardshoulder: //TODO
                boolean atHardShoulder = false;
               
                if(visibleWorld.getCell(location.getX(),location.getY()).getCellType() == CellType.ct_road) {
                    RoadCell rc = (RoadCell)visibleWorld.getCell(location.getX(),location.getY());
                    for(RoadMarking rm : rc.getRoadMarkings()) {
                        if (rm == RoadMarking.rm_hard_shoulder) {
                            atHardShoulder = true;
                            break;
                        }
                    }
                }
                beliefs.put(cb, atHardShoulder);  
                break;
            case CB_hasAdvancedStop: //not simulated
                beliefs.put(CarBelief.CB_hasAdvancedStop,false);
                break;
            case CB_headlightsDipped: //not simulated
                beliefs.put(CarBelief.CB_headlightsDipped,false);
                break; 
            case CB_hill: //not simulated
                beliefs.put(CarBelief.CB_hill,false);
                break;
            case CB_homezone: //not simulated
                beliefs.put(CarBelief.CB_homezone,false);
                break;
            case CB_hovLane: //not simulated
                beliefs.put(CarBelief.CB_hovLane,false);
                break;
            case CB_icyWeather: //not simulated
                beliefs.put(CarBelief. CB_icyWeather,false);
                break;
            case CB_inIncident: //not simulated
                beliefs.put(CarBelief.CB_inIncident,false);
                break;
            case CB_kerbLoweredForWheelchair: //not simulated
                beliefs.put(CarBelief.CB_kerbLoweredForWheelchair,false);
                break;
            case CB_lanes3: //not simulated
                beliefs.put(CarBelief.CB_lanes3,false);
                break;
            case CB_lanes4plus: //not simulated
                beliefs.put(CarBelief.CB_lanes4plus,false);
                break;
            case CB_levelCrossing: //not simulated
                beliefs.put(CarBelief. CB_levelCrossing,false);
                break;
            case CB_levelCrossingApproach: //not simulated
                beliefs.put(CarBelief.CB_levelCrossingApproach,false);
                break;
            case CB_lightsCausingDiscomfortToOthers: //not simulated
                beliefs.put(CarBelief.CB_lightsCausingDiscomfortToOthers,false);
                break;
            case CB_litStreetLightingRoad: //not simulated
                beliefs.put(CarBelief. CB_litStreetLightingRoad,false);
                break;
            case CB_loadBalanced: //not simulated
                beliefs.put(CarBelief.CB_loadBalanced,false);
                break;
            case CB_loadNotStickingOut: //not simulated
                beliefs.put(CarBelief.CB_loadNotStickingOut,false);
                break;
            case CB_loadSecure: //not simulated
                beliefs.put(CarBelief.CB_loadSecure,false);
                break;
            case CB_london: //not simulated
                beliefs.put(CarBelief.CB_london,false);
                break;
            case CB_longQueueBehind: //not simulated
                beliefs.put(CarBelief.CB_longQueueBehind,false);
                break;
            case CB_markedBayForLoading: //not simulated
                beliefs.put(CarBelief.CB_markedBayForLoading,false);
                break;
            case CB_meetHeightRequirement: //not simulated
                beliefs.put(CarBelief.CB_meetHeightRequirement,false);
                break;
            case CB_meetParkingRestrictions: //not simulated
                beliefs.put(CarBelief.CB_meetParkingRestrictions,false);
                break;
            case CB_middleLane: //not simulated
                beliefs.put(CarBelief.CB_middleLane,false);
                break;
            case CB_misleadingSignal: //not simulated
                beliefs.put(CarBelief.CB_misleadingSignal,false);
                break;
            case CB_motorcyclistAhead: //not simulated
                beliefs.put(CarBelief.CB_motorcyclistAhead,false);
                break;
            case CB_motorcyclistInFront:
                beliefs.put(CarBelief.CB_motorcyclistInFront,false);
                break;
            case CB_motorway: //TODO
                beliefs.put(CarBelief.CB_motorway,false);
                break;
            case CB_nearBrowOfHill: //not simulated
                beliefs.put(CarBelief.CB_nearBrowOfHill,false);
                break;
            case CB_nearHumpbridge: //not simulated
                beliefs.put(CarBelief.CB_nearHumpbridge,false);
                break;
            case CB_nearLevelCrossing: //not simulated
                beliefs.put(CarBelief.CB_nearLevelCrossing,false);
                break;
            case CB_nearPedistrianCrossing: //not simulated
                beliefs.put(CarBelief.CB_nearPedistrianCrossing,false);
                break;
            case CB_nearSchool: //not simulated
                beliefs.put(CarBelief.CB_nearSchool,false);
                break;
            case CB_nearTaxiRank: //not simulated
                beliefs.put(CarBelief.CB_nearTaxiRank,false);
                break;
            case CB_nearTramStop: //not simulated
                beliefs.put(CarBelief.CB_nearTramStop,false);
                break;
            case CB_nearbusStop: //not simulated
                beliefs.put(CarBelief.CB_nearbusStop,false);
                break;
            case CB_noOvertakingSign: //not simulated
                beliefs.put(CarBelief.CB_noOvertakingSign,false);
                break;
            case CB_noPassingPlaceInFront: //not simulated
                beliefs.put(CarBelief.CB_noPassingPlaceInFront,false);
                break;
            case CB_nonMotorTraffic: //not simulated
                beliefs.put(CarBelief.CB_nonMotorTraffic,false);
                break;
            case CB_numLanesReducing: //not simulated
                beliefs.put(CarBelief.CB_numLanesReducing,false);
                break;
            case CB_obstructCycleFacilities: //not simulated
                beliefs.put(CarBelief.CB_obstructCycleFacilities,false);
                break;
            case CB_onMotorway: //not simulated
                beliefs.put(CarBelief. CB_onMotorway,false);
                break;
            case CB_onPavement: //not simulated
                beliefs.put(CarBelief. CB_onPavement,false);
                break;
            case CB_overtaken: //not simulated //TODO
                beliefs.put(CarBelief.CB_overtaken,false);
                break;
            case CB_overtakingHighSidedVehicle: //not simulated
                beliefs.put(CarBelief.CB_overtakingHighSidedVehicle,false);
                break;
            case CB_overtakingSchoolBus: //not simulated
                beliefs.put(CarBelief.CB_overtakingSchoolBus,false);
                break;
            case CB_parked: //not simulated
                beliefs.put(CarBelief.CB_parked,false);
                break; 
            case CB_parkedInRoad: //not simulated
                beliefs.put(CarBelief.CB_parkedInRoad,false);
                break;
            case CB_parkingAllowedBySigns://not simulated
                beliefs.put(CarBelief.CB_parkingAllowedBySigns,false);
                break; 
            case CB_parkingRestrictions: //not simulated
                beliefs.put(CarBelief.CB_parkingRestrictions,false);
                break;
            case CB_pavement: //not simulated
                beliefs.put(CarBelief.CB_pavement,false);
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
                               if(j == location.getY() - 1 && i == location.getX()) {
                                   System.out.println(i + " " +j);
                                   Pedestrian p1 = visibleWorld.getPedestrianAtPosition(i,j);
                                   pedestrainInRoad = true;
                                   beliefs.put(cb, true);
                               }
                              
                           }
                       }
                   }
                }
                else if(cmd == Direction.south) { 
                    for(int i = 0; i < visibleWorld.getWidth(); i++) {
                        for(int j = location.getY() + 1; j <= location.getY() + espeed; j++) {
                            if(visibleWorld.containsPedestrian(i,j)) {
                                if(j == location.getY() + 1 && i == location.getX()) {
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
                                if(i == location.getX() + 1 && j == location.getY()) {
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
                               if(i == location.getX() - 1 && j == location.getY()) {
                                   pedestrainInRoad = true;
                                   beliefs.put(cb, pedestrainInRoad );
                               }
                            }
                        }
                    } 
                }
                break;
            case CB_pelicanCrossing: //not simulated
                beliefs.put(CarBelief.CB_pelicanCrossing,false);
                break;
            case CB_policeDirectingLeft: //not simulated
                beliefs.put(CarBelief.CB_policeDirectingLeft,false);
                break;
            case CB_policeflashingBlue: //not simulated
                beliefs.put(CarBelief.CB_policeflashingBlue,false);
                break;
            case CB_policeflashingHeadlight: //not simulated
                beliefs.put(CarBelief.CB_policeflashingHeadlight,false);
                break;
            case CB_policehornSounding: //not simulated
                beliefs.put(CarBelief.CB_policehornSounding,false);
                break;
            case CB_preventingAccessForEmergencyServices: //not simulated
                beliefs.put(CarBelief.CB_preventingAccessForEmergencyServices,false);
                break;
            case CB_prohibitedToStopPark: //not simulated
                beliefs.put(CarBelief.CB_prohibitedToStopPark,false);
                break;
            case CB_propertyEntrance: //not simulated
                beliefs.put(CarBelief.CB_propertyEntrance,false);
                break;
            case CB_publicTransport: //not simulated
                beliefs.put(CarBelief.CB_publicTransport,false);
               break;
            case CB_puffinCrossing: //not simulated
                beliefs.put(CarBelief.CB_puffinCrossing,false);
                break;
            case CB_pulledOver: //not simulated
                beliefs.put(CarBelief.CB_pulledOver,false);
                break;
            case CB_quietlane: //not simulated
                beliefs.put(CarBelief.CB_quietlane,false);
                break;
            case CB_reachedRoundabout: //not simulated
                beliefs.put(CarBelief.CB_reachedRoundabout,false);
                break;
            case CB_rearfacingbabyseatinfrontpassengerseat: //not simulated
                beliefs.put(CarBelief.CB_rearfacingbabyseatinfrontpassengerseat,false);
                break;
            case CB_redRoute: //not simulated
                beliefs.put(CarBelief.CB_redRoute,false);
                break;
            case CB_redlines: //not simulated
                beliefs.put(CarBelief.CB_redlines,false);
                break;
            case CB_reversing: //not simulated
                beliefs.put(CarBelief.CB_reversing,false);
                break;
            case CB_roadMarkingKeepLeftOverride: //not simulated
                beliefs.put(CarBelief.CB_roadMarkingKeepLeftOverride,false);
                break;
            case CB_roadNarrows: //not simulated
                beliefs.put(CarBelief.CB_roadNarrows,false);
                break;
            case CB_roadPresentsHazards: //not simulated
                beliefs.put(CarBelief.CB_roadPresentsHazards,false);
                break;
            case CB_roadSignKeepLeftOverride: //not simulated
                beliefs.put(CarBelief.CB_roadSignKeepLeftOverride,false);
                break;
            case CB_roadWorks: //not simulated
                beliefs.put(CarBelief.CB_roadWorks,false);
                break;
            case CB_roadWorksAhead: //not simulated
                beliefs.put(CarBelief.CB_roadWorksAhead,false);
                break;
            case CB_ruralRoad://not simulated
                beliefs.put(CarBelief.CB_ruralRoad,false);
                break;
            case CB_schoolEntrance://not simulated
                beliefs.put(CarBelief.CB_schoolEntrance,false);
                break;
            case CB_schoolEntranceMarkings://not simulated
                beliefs.put(CarBelief.CB_schoolEntranceMarkings,false);
                break;
            case CB_seenSign://not simulated
                beliefs.put(CarBelief.CB_seenSign,false);
                break;
            case CB_seenSignalByAuthorisedPerson://not simulated
                beliefs.put(CarBelief.CB_seenSignalByAuthorisedPerson,false);
                break;
            case CB_sideroad: //not simulated
                beliefs.put(CarBelief.CB_sideroad,false);
                break;
            case CB_signConfictsWithAuthorisedPersonDirection://not simulated
                beliefs.put(CarBelief.CB_signConfictsWithAuthorisedPersonDirection,false);
                break;
            case CB_signFlashingAmber://not simulated
                beliefs.put(CarBelief.CB_signFlashingAmber,false);
                break;
            case CB_signFlashingRedX://not simulated
                beliefs.put(CarBelief.CB_signFlashingRedX,false);
                break;
            case CB_emergencyStopSign://not simulated
                beliefs.put(CarBelief.CB_emergencyStopSign,false);
                break;
            case CB_signalledRoundabout://not simulated
                beliefs.put(CarBelief.CB_signalledRoundabout,false);
                break;
            case CB_signsAdviseRestrictions://not simulated
                beliefs.put(CarBelief.CB_signsAdviseRestrictions,false);
                break;
            case CB_skidding://not simulated
                beliefs.put(CarBelief.CB_skidding,false);
                break;
            case CB_sliproad://not simulated
                beliefs.put(CarBelief.CB_sliproad,false);
                break;
            case CB_slowMovingTraffic://not simulated
                beliefs.put(CarBelief.CB_slowMovingTraffic,false);
                break;
            case CB_slowMovingVehicle://not simulated
                beliefs.put(CarBelief.CB_slowMovingVehicle,false);
                break;
            case CB_slowMovingVehicleInfront://not simulated
                beliefs.put(CarBelief.CB_slowMovingVehicleInfront,false);
                break;
            case CB_speedlimitForHardShoulder://not simulated
                beliefs.put(CarBelief.CB_speedlimitForHardShoulder,false);
                break;
            case CB_stationaryVehicleInFront: // not simulated
                beliefs.put(CarBelief.CB_stationaryVehicleInFront,false);
                break;
            case CB_stopForChildrenSign://not simulated
                beliefs.put(CarBelief.CB_stopForChildrenSign,false);
                break;
            case CB_stopSign://not simulated
                beliefs.put(CarBelief.CB_stopSign,false);
                break;
            case CB_stopSignCrossing://not simulated
                beliefs.put(CarBelief.CB_stopSignCrossing,false);
                break;
            case CB_taxibay://not simulated
                beliefs.put(CarBelief.CB_taxibay,false);
                break;
            case CB_tempObstructingTraffic://not simulated
                beliefs.put(CarBelief.CB_tempObstructingTraffic,false);
                break;
            case CB_toucanCrossing://not simulated
                beliefs.put(CarBelief.CB_toucanCrossing,false);
                break;
            case CB_trafficCalming://not simulated
                beliefs.put(CarBelief.CB_trafficCalming,false);
                break;
            case CB_trafficCongested: // not simulated
                beliefs.put(CarBelief.CB_trafficCongested,false);
                break;
            case CB_trafficQueuing: // not simulated
                beliefs.put(CarBelief.CB_trafficQueuing,false);
                break;
            case CB_trafficSlow: // not simulated
                beliefs.put(CarBelief.CB_trafficSlow,false);
                break;
            case CB_tram://not simulated
                beliefs.put(CarBelief.CB_tram,false);
                break;
            case CB_tramPassingLane://not simulated
                beliefs.put(CarBelief.CB_tramPassingLane,false);
                break;
            case CB_tramStop://not simulated
                beliefs.put(CarBelief. CB_tramStop,false);
                break;
            case CB_tramlines://not simulated
                beliefs.put(CarBelief.CB_tramlines,false);
                break;
            case CB_tramlinesCrossingApproach://not simulated
                beliefs.put(CarBelief.CB_tramlinesCrossingApproach,false);
                break;
            case CB_unncessaryObstruction://not simulated
                beliefs.put(CarBelief.CB_unncessaryObstruction,false);
                break;
            case CB_uphill://not simulated
                beliefs.put(CarBelief.CB_uphill,false);
                break;
            case CB_urbanClearway://not simulated
                beliefs.put(CarBelief.CB_urbanClearway,false);
                break;
            case CB_vehicleDoesntFitsInCentralReservation://not simulated
                beliefs.put(CarBelief.CB_vehicleDoesntFitsInCentralReservation,false);
                break;
            case CB_vehicleFitsInCentralReservation://not simulated
                beliefs.put(CarBelief.CB_vehicleFitsInCentralReservation,false);
                break;
            case CB_vehiclesWishToOvertake://TODO
                beliefs.put(CarBelief.CB_vehiclesWishToOvertake,false);
                break;
            case CB_visibilityReduced://not simulated
                beliefs.put(CarBelief.CB_visibilityReduced,false);
                break;
            case CB_wetWeather://not simulated
                beliefs.put(CarBelief.CB_wetWeather,false);
                break;
            case CB_whiteDiagonalStripeWhiteBrokenBorder://not simulated
                beliefs.put(CarBelief.CB_whiteDiagonalStripeWhiteBrokenBorder,false);
                break;
            case CB_whiteDiagonalStripeWhiteSolidBorder://not simulated
                beliefs.put(CarBelief.CB_whiteDiagonalStripeWhiteSolidBorder,false);
                break;
            case CB_windy://not simulated
                beliefs.put(CarBelief.CB_windy,false);
                break;
            case CB_withinCyclelaneOpteration://not simulated
                beliefs.put(CarBelief.CB_withinCyclelaneOpteration,false);
                break;
            case CB_withinTimePlateTimes://not simulated
                beliefs.put(CarBelief.CB_withinTimePlateTimes,false);
                break;
            case CB_withinUrbanClearwayHours://not simulated
                beliefs.put(CarBelief.CB_withinUrbanClearwayHours,false);
                break;
            case CB_workVehicleSign://not simulated
                beliefs.put(CarBelief.CB_workVehicleSign,false);
                break;
            case CB_yellowLine://not simulated
                beliefs.put(CarBelief.CB_yellowLine,false);
                break;
            case CB_yellowMarkingsOnKerb://not simulated
                beliefs.put(CarBelief.CB_yellowMarkingsOnKerb,false);
                break;
            case CB_zebraCrossing:
                boolean approaching_zebra_crossing;
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
                boolean greenLightNotOn;
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
    // check if there are same points between two ArrayList
    public Boolean checkCommonPoint(ArrayList<Point> l1, ArrayList<Point> l2) {
        l1.retainAll(l2);
        return !l1.isEmpty();
    }
    
    public HashMap<CarAction, CarPriority> getActionsPerformed(){
       return this.actionsToDo;
    }
    
    public HashMap<CarAction,CarPriority> getRecommendedActions() {
        return this.actionsRecommended;
    }
    
    public HashMap<CarBelief, Boolean> getBeliefsList(){
        return this.beliefs;
    }
    
    public HashMap<CarIntention, Boolean> getIntentionsList(){
        return this.intentions;
    }
    // reset the action list
    public void resetActions() { 
        this.actionsToDo.clear();
    }
    
    // reset the recommendation list
    public void resetRecommendations() {
        this.actionsRecommended.clear();
    }
}
