:- include('findactions').
:- initialization(main).

main :- getRecommendedActions(standard,[dualCarriageWay,exitClear,vehicleSafe,headlightsOff,roadAheadClear,bendInRoad,canReadNumberPlate,allPassengersWearingSeatBeltsAsRequired,atTrafficLight,routePlanned,fuel,completeOvertakeBeforeSolidWhiteLine,sidelightsOff,approachingCorner,vehicleDoesntFitsInCentralReservation,allChildrenUsingChildSeatAsRequired,driving],[],Actions), write(Actions), halt(0).