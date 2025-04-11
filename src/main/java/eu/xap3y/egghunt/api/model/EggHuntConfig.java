package eu.xap3y.egghunt.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class EggHuntConfig {
    private Boolean enabled;
    private Boolean allEggsFoundReward;
    private Boolean allEggsFoundRandomReward;
    private List<String> allEggsFoundRewards;
    private String allEggsFoundMessage;
}
