:- include('findactions').
:- initialization(main).

main :- getRecommendedActions(standard,[vehicleDoesntFitsInCentralReservation,canReadNumberPlate,exitClear,bendInRoad,approachingCorner],[],Actions), write(Actions), halt(0).