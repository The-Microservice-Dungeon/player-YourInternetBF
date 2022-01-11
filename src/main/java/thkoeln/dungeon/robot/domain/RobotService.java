package thkoeln.dungeon.robot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        Optional<Robot> robot = this.robotRepository.findById(robotId);

        if (robot.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find a robot with that robot id");
        }

        robot.get().setMode(mode);
        return this.robotRepository.findById(robotId).get();
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
}
