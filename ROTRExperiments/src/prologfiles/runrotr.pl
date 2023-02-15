:- include('findactions').
:- initialization(main).

main :- getRecommendedActions(standard,[exitClear,ableToStopByWhiteLine],[],Actions), write(Actions), halt(0).