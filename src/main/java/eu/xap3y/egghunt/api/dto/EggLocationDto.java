package eu.xap3y.egghunt.api.dto;

import org.bukkit.Location;

public record EggLocationDto(
        Location loc,
        String eggType,
        String texture
) {
}
