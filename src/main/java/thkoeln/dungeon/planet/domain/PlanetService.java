package thkoeln.dungeon.planet.domain;

import org.springframework.stereotype.Service;
import thkoeln.dungeon.robot.domain.RobotRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanetService {
    private final PlanetRepository planetRepository;

    PlanetService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    public List<Planet> getPlanetsWithSpacestation(){
        List<Planet> planets = new ArrayList<>();
        for(Planet planet : this.planetRepository.findAll()) {
            if (planet.isSpaceStation()) planets.add(planet);
        }

        return planets;
    }
}
