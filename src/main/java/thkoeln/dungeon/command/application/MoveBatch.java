package thkoeln.dungeon.command.application;

import lombok.Getter;
import lombok.Setter;
import thkoeln.dungeon.planet.domain.Planet;
import thkoeln.dungeon.robot.domain.Robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MoveBatch {
    @Getter
    @Setter
    public static class Movement {
        private Robot robot;
        private Planet targetPlanet;
        private UUID transactionID = UUID.randomUUID();

        @Override
        public String toString () {
            return this.robot.getId() + " " + this.targetPlanet.getId() + " " + this.transactionID;
        }
    }

    private List<Movement> movements = new ArrayList<Movement>();

    public void addMovement(Movement movement) {
        this.movements.add(movement);
    }

    public void addMovement(Optional<Movement> movement) {
        if (movement.isPresent()) this.movements.add(movement.get());
    }

    public List<String> toArrayString () {
        List<String> commands = new ArrayList<>();
        for (Movement movement : movements) {
            commands.add(movement.toString());
        }
        return commands;
    }
}
