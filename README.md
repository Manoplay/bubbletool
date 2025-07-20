# Bubbletool

Simple tool to build a grid of bubbles in LittleBigPlanet (produces a GOTY version revisioned level).

## Arguments
* w/width - the max width of the grid
* s/source - the source file (txt only for now) from where to read guids
* o/output - the output file of the level

## Input
An input file is for now only a txt file with a list of GUIDs. They may or may not start with a "g" letter. Newline is the delimiter.

## Output
Produces a level which you can add to the bigfart and load in LittleBigPlanet.

## Note
Only loads items references by GUID and not by Hash, for now.
