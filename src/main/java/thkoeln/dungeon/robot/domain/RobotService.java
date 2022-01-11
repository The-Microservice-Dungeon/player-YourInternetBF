package thkoeln.dungeon.robot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class RobotService {
    private final RobotRepository robotRepository;

    @Autowired
    public RobotService(
            RobotRepository robotRepository
    ) {
        this.robotRepository = robotRepository;
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
            robot.playRound();
        }
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

    private Robot fetchRobotById(UUID robotId) {
        Optional<Robot> robotResult = this.robotRepository.findById(robotId);

        if (robotResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find a robot with that robot id");
        }

        return robotResult.get();
    }
}
