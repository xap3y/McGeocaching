package eu.xap3y.egghunt.api.dto;

import java.util.List;

public record EggDto(
        String name,
        String animation,
        Boolean randomReward,
        List<String> rewards,
        List<String> textures
) {
}
