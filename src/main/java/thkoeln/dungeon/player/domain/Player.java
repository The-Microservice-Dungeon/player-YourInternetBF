package thkoeln.dungeon.player.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Player {
    @Id
    private final UUID id = UUID.randomUUID();

    private String name;
    private String email;
    private UUID bearerToken;
    private UUID playerId;

    /**
     * Choose a random and unique name and email for the player
     */
    public void assignRandomName() {
        String randomNickname = NameGenerator.generateName();
        setName( randomNickname );
        setEmail( randomNickname + "@microservicedungeon.com" );
    }

    public boolean isReadyToPlay() {
        return ( bearerToken != null && playerId != null );
    }


    public void playRound() {
        // todo
    }

    @Override
    public String toString() {
        return "Player '" + name + "' (bearerToken: " + bearerToken + " playerId: " + playerId + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
