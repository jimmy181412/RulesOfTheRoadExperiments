:- include('findactions').
:- initialization(main).

main :- getRecommendedActions(standard,[ableToStopByWhiteLine,exitClear],[turnRight],Actions), write(Actions), halt(0).