package thkoeln.dungeon.planet.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Getter
public class Planet {
    @Id
    private final UUID id = UUID.randomUUID();

    @Setter
    private String name;

    @Setter
    @Getter
    private Integer numberOfVisits = 0;

    @Setter
    @Getter ( AccessLevel.NONE )
    private Boolean spacestation = Boolean.FALSE;
    public Boolean isSpaceStation() { return spacestation; }

    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet northNeighbour;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet eastNeighbour;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet southNeighbour;
    @OneToOne ( cascade = CascadeType.MERGE)
    @Setter ( AccessLevel.PROTECTED )
    private Planet westNeighbour;

    @Transient
    private Logger logger = LoggerFactory.getLogger( Planet.class );

    /**
     * A neighbour relationship is always set on BOTH sides.
     * @param otherPlanet
     * @param direction
     */
    public void defineNeighbour(Planet otherPlanet, CompassDirection direction ) {
        if ( otherPlanet == null ) throw new PlanetException( "Cannot establish neighbouring relationship with null planet!" ) ;
        try {
            Method otherGetter = neighbouringGetter( direction.getOppositeDirection() );
            Method setter = neighbouringSetter( direction );
            setter.invoke(this, otherPlanet );
            Planet remoteNeighbour = (Planet) otherGetter.invoke( otherPlanet );
            if ( !this.equals( remoteNeighbour ) ) {
                Method otherSetter = neighbouringSetter( direction.getOppositeDirection() );
                otherSetter.invoke( otherPlanet, this );
            }
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException e ) {
            throw new PlanetException( "Something went wrong that should not have happened ..." + e.getStackTrace() );
        }
        logger.info( "Established neighbouring relationship between planet '" + this + "' and '" + otherPlanet + "'." );
    }

    protected Method neighbouringGetter( CompassDirection direction ) throws NoSuchMethodException {
        String name = "get" + WordUtils.capitalize( String.valueOf( direction ) ) + "Neighbour";
        return this.getClass().getDeclaredMethod( name );
    }


    protected Method neighbouringSetter( CompassDirection direction ) throws NoSuchMethodException {
        String name = "set" + WordUtils.capitalize( String.valueOf( direction ) ) + "Neighbour";
        return this.getClass().getDeclaredMethod( name, new Class[]{ this.getClass() } );
    }


    public List<Planet> allNeighbours() {
        List<Planet> allNeighbours = new ArrayList<>();
        if ( getNorthNeighbour() != null ) allNeighbours.add( getNorthNeighbour() );
        if ( getWestNeighbour() != null ) allNeighbours.add( getWestNeighbour() );
        if ( getEastNeighbour() != null ) allNeighbours.add( getEastNeighbour() );
        if ( getSouthNeighbour() != null ) allNeighbours.add( getSouthNeighbour() );
        return allNeighbours;
    }

    public Planet randomNeighbourPlanet (){
        List<Planet> allNeighbours = this.allNeighbours();
        Integer randomPlanet = ThreadLocalRandom.current().nextInt(0, allNeighbours.size() -1 );
        return allNeighbours.get(randomPlanet);
    }

    public Planet randomLeastKnownNeighbourPlanet  (){
        List<Planet> allNeighbours = this.allNeighbours();
        Planet leastKnownPlanet = allNeighbours.get(0);
        for(int x = 0; x<allNeighbours.size(); x++){
            if(allNeighbours.get(x).getNumberOfVisits() < leastKnownPlanet.getNumberOfVisits()){
                leastKnownPlanet = allNeighbours.get(x);
            }
        }
        return leastKnownPlanet;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Planet)) return false;
        Planet planet = (Planet) o;
        return Objects.equals(id, planet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getName() + " (" + getId() + ")";
    }
}
