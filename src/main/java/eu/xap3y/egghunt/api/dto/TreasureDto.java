package eu.xap3y.egghunt.api.dto;

import org.bukkit.Location;

public record TreasureDto (
        Location location,
        String name
) {
}
