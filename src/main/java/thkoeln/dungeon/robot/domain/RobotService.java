package thkoeln.dungeon.robot.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RobotService {
    private final RobotRepository robotRepository;

    @Autowired
    public RobotService(
            RobotRepository robotRepository
    ) {
        this.robotRepository = robotRepository;
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
