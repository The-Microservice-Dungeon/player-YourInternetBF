package thkoeln.dungeon.robot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import thkoeln.dungeon.planet.domain.CompassDirection;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.planet.domain.PlanetService;

import java.util.*;

@Service
public class RobotService {
    private final RobotRepository robotRepository;
    private final PlanetService planetService;

    @Autowired
    public RobotService(
            RobotRepository robotRepository,
            PlanetService  planetService
    ) {
        this.robotRepository = robotRepository;
        this.planetService = planetService;
    }

    public Robot changeMode(UUID robotId, ROBOT_MODE mode) {
        Robot robot = fetchRobotById(robotId);
        robot.setMode(mode);
        return fetchRobotById(robotId);
    }

    /***
     * This method is executed at the beginning of each round
     */
    // TODO execute this method at the beginning of a round
    public void playRound() {
        Iterable<Robot> robots = this.robotRepository.findAll();

        for (Robot robot : robots) {
            // in case the robot is in ROBOT_MODE.IDLE,
            // we ignore that since that is what we expect in that mode

            if (robot.getMode().equals(ROBOT_MODE.SERENDIPITY)) doExplorationWith(robot);
            if (robot.getMode().equals(ROBOT_MODE.GO_HOME)) goHomeWith(robot);
            if (robot.getMode().equals(ROBOT_MODE.BUY_ROBOT)) buyNewRobotWith(robot);

            // save the new position
            robotRepository.save(robot);
        }
    }

    private void doExplorationWith(Robot robot) {
        // if the robot's planet visits == 0 then add one visit
        // this only happens when the robot just spawned on that planet
        if (robot.getCurrentPlanet().getNumberOfVisits().equals(0)) {
            planetService.addOneVisit(robot.getCurrentPlanet());
        }

        Planet newPlanet = planetService.addOneVisit(robot.getCurrentPlanet().randomLeastKnownNeighbourPlanet());
        robot.setCurrentPlanet(newPlanet);
    }

    private void goHomeWith(Robot robot) {
        // this is the end goal of this mode
        if (robot.getCurrentPlanet().isSpaceStation()) return;

        List<Planet> planets = this.planetService.getPlanetsWithSpacestation();
        // TODO: implement coordinate system
        // TODO: & then calculate distance between Robot and Spacestation using Pythagoras (a^2 + b^2 = c^2)
        Planet destinationPlanet = planets.get(0);
        Planet departurePlanet = robot.getCurrentPlanet();

        // if one of the neighbours of the robot's planet is the destination planet,
        // then just move on that planet
        if (destinationPlanet.getEastNeighbour().equals(departurePlanet)) {
            robot.move(CompassDirection.east);
            return;
        }
        if (destinationPlanet.getSouthNeighbour().equals(departurePlanet)) {
            robot.move(CompassDirection.south);
            return;
        }
        if (destinationPlanet.getWestNeighbour().equals(departurePlanet)) {
            robot.move(CompassDirection.west);
            return;
        }
        if (destinationPlanet.getNorthNeighbour().equals(departurePlanet)) {
            robot.move(CompassDirection.north);
            return;
        }

        // if the destination planet is out of reach then move to a random planet
        // if we would have more time for this class, we would have implemented another more sophisticated
        // way of moving to a spacestation
        robot.move(CompassDirection.getRandomDirection());
    }

    private void buyNewRobotWith(Robot robot) {
        // in order to be able to buy a new roboter, one robot of us needs to be in a spacestation
        // therefor we are ordering the robot to go home, if it isn't on a space-station planet
        if (!robot.getCurrentPlanet().isSpaceStation()) {
            this.goHomeWith(robot);
        } else {
            this.buyRobot();
        }
    }

    private void buyRobot () {
        // TODO: implement this
    }


    public List<RobotStateDTO> getRobotStateOfAllRobots() {
        Iterable<Robot> robots = this.robotRepository.findAll();
        List<RobotStateDTO> robotStateDTOS = new ArrayList<>();
        for (Robot robot : robots) {
            RobotStateDTO robotState = getRobotState(robot);
            robotStateDTOS.add(robotState);
        }

        return robotStateDTOS;
    }

    public RobotStateDTO getRobotStateById(UUID robotId) {
        return getRobotState(fetchRobotById(robotId));
    }

    private RobotStateDTO getRobotState(Robot robot) {
        RobotStateDTO robotState = new RobotStateDTO();

        robotState.setRobotId(robot.getId());
        robotState.setEnergyPoints(robot.getEnergyPoints());
        robotState.setLevel(robot.getLevel());
        robotState.setHp(robot.getHp());
        robotState.setCoal(robot.getCoal());
        robotState.setIron(robot.getIron());
        robotState.setGem(robot.getGem());
        robotState.setGold(robot.getGold());
        robotState.setPlatin(robot.getPlatin());

        return robotState;
    }

    private RobotPositionDTO getRobotPosition(Robot robot){
        RobotPositionDTO robotPosition = new RobotPositionDTO();
        robotPosition.setRobotID(robot.getId());
        robotPosition.setCurrentPlanetName(robot.getCurrentPlanet().getName());
        robotPosition.setCurrentPlanetID((robot.getCurrentPlanet().getId()));

        return robotPosition;
    }

    private List<RobotPositionDTO> getRobotPositions(){
        Iterable<Robot> allRobots = this.robotRepository.findAll();
        List<RobotPositionDTO> robotPositionDTOS = new ArrayList<>();
        for(Robot robot: allRobots){
            RobotPositionDTO robotPosition = getRobotPosition(robot);
            robotPositionDTOS.add(robotPosition);
        }
        return robotPositionDTOS;
    }

    private Robot fetchRobotById(UUID robotId) {
        Optional<Robot> robotResult = this.robotRepository.findById(robotId);

        if (robotResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find a robot with that robot id");
        }

        return robotResult.get();
    }
}
