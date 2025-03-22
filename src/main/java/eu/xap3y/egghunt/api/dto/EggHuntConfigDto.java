package eu.xap3y.egghunt.api.dto;

import java.util.List;

public record EggHuntConfigDto(
    Boolean enabled,
    Boolean allEggsFoundReward,
    Boolean allEggsFoundRandomReward,
    List<String> allEggsFoundRewards,
    String allEggsFoundMessage
) {
}
