package thkoeln.dungeon.robot.domain;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/robots")
public class RobotController {
    private final RobotService robotService;

    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }

    @PutMapping("/{robot-id}/mode")
    public Robot changeMode(@RequestBody ChangeModeDTO changeModeDTO, @PathVariable("robot-id") UUID robotId) {
        return robotService.changeMode(robotId, changeModeDTO.getMode());
    }
}
