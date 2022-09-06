package bran.parser.composition;

enum OrderZone {
	START,        // Start or of expression - or end of one, to start a new one
	MIDDLE,        // After right bracket or variable
	ANY,        //
	NOWHERE;    //

	public boolean inZoneOf(OrderZone other) {
		return this == other || other == ANY || this == ANY;
	}
}
