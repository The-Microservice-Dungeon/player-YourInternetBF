package thkoeln.dungeon.command.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * So far, only the supported commands - enhance when needed!
 */
public enum CommandType {
    @JsonProperty("buying")
    BUYING,
    @JsonProperty("movement")
    MOVEMENT;
}
