package thkoeln.dungeon.robot.domain;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController("/robots")
public class RobotController {
    private final RobotService robotService;

    public RobotController(RobotService robotService) {
        this.robotService = robotService;
    }

    @GetMapping("/{robot-id}/state")
    public RobotStateDTO getRobotInformation(@PathVariable("robot-id") UUID robotId) {
        return robotService.getRobotStateById(robotId);
    }

    @GetMapping("/state")
    public List<RobotStateDTO> getRobotInformation() {
        return robotService.getRobotStateOfAllRobots();
    }

    @PutMapping("/{robot-id}/mode")
    public Robot changeMode(@RequestBody ChangeModeDTO changeModeDTO, @PathVariable("robot-id") UUID robotId) {
        return robotService.changeMode(robotId, changeModeDTO.getMode());
    }
}
