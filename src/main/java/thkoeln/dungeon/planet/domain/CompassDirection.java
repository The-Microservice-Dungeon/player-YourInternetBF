package thkoeln.dungeon.planet.domain;

import java.util.Random;

public enum CompassDirection {
    north, east, south, west;

    public CompassDirection getOppositeDirection() {
        switch( this ) {
            case north: return south;
            case east: return west;
            case south: return north;
            case west: return east;
        }
        return null;
    }

    public static CompassDirection getRandomDirection() {
        Random random = new Random();
        int i = 0;
        int randInt = random.nextInt(3);
        for (CompassDirection value : CompassDirection.values()) {
            if (randInt == i) {
                return value;
            }

            i++;
        }
        // this should never be the case, but to avoid an exception we just return north
        return CompassDirection.north;
    }
}
