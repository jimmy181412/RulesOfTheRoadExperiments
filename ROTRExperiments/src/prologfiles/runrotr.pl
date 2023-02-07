:- include('findactions').
:- initialization(main).

main :- getRecommendedActions(standard,[exitClear,vehicleDoesntFitsInCentralReservation,approachingCorner,atTrafficLight,bendInRoad,canReadNumberPlate,lightRed],[approachingTrafficLight,setOff],Actions), write(Actions), halt(0).