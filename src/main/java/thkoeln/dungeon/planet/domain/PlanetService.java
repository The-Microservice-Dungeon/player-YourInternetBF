package thkoeln.dungeon.planet.domain;

import org.springframework.stereotype.Service;
import thkoeln.dungeon.robot.domain.RobotRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanetService {
    private final PlanetRepository planetRepository;

    public PlanetService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    public Planet findById(UUID uuid) {
        Optional<Planet> planetResult = this.planetRepository.findById(uuid);
        if (planetResult.isEmpty()) {
            throw new PlanetException("Planet not found with this UUID " + uuid);
        }

        return planetResult.get();
    }

    public Planet addOneVisit(Planet p) {
        p.setNumberOfVisits(p.getNumberOfVisits() + 1);
        return this.planetRepository.save(p);
    }

    public List<Planet> getPlanetsWithSpacestation(){
        List<Planet> planets = new ArrayList<>();
        for(Planet planet : this.planetRepository.findAll()) {
            if (planet.isSpaceStation()) planets.add(planet);
        }

        return planets;
    }

    public List<PlanetDTO> getListOfKnownPlanets(){
        List<PlanetDTO> knownPlanets = new ArrayList<>();
        for(Planet planet : this.planetRepository.findAll()){
            knownPlanets.add(getPlanetDTO(planet));
        }
        return knownPlanets;
    }

    public PlanetDTO getPlanetDTO(Planet planet){
        PlanetDTO knownPlanet = new PlanetDTO();
        knownPlanet.setName(planet.getName());
        knownPlanet.setId(planet.getId());
        return knownPlanet;
    }
 }
