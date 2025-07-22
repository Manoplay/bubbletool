# Bubbletool

Simple tool to build a grid of bubbles in LittleBigPlanet (produces a GOTY version revisioned level).

## Arguments
* w/width - the max width of the grid
* s/source - the source file (txt only for now) from where to read guids
* o/output - the output file of the level
* C/gameroot - the game root (USRDIR) from where to resolve paths from a text file made up of paths (RLST).

## Input
An input file is for now only a txt file with a list of GUIDs (txt) or Paths (RLST). They may or may not start with a "g" letter. Newline is the delimiter.

## Output
Produces a level which you can add to the bigfart and load in LittleBigPlanet.

## Note
Only loads items references by GUID and not by Hash, for now.

## Dependencies and credits
This is both a credit and a dependency note: this project DEPENDS on toolkit by ennuo (https://github.com/ennuo/toolkit). Before building, make sure to clone toolkit, and run
> mvn clean install

This way maven can find toolkit and their classes, which are used in this project for level (de)serialization!
