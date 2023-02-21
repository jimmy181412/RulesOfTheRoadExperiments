:- include('findactions').
:- initialization(main).

main :- getRecommendedActions(standard,[ableToStopByWhiteLine,exitClear],[],Actions), write(Actions), halt(0).