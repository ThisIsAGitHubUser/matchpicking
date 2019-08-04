# matchpicking

Hi!

How to get started:

* checkout
* mvn clean install
* java -jar matchpicking-0.0.1-SNAPSHOT.jar
* start a new game: send POST request with empty JSON to *http://localhost:8080/games*
* draw matches: send PATCH request with JSON like { "playerDraw": [1-3]} to *http://localhost:8080/games/{id}*
