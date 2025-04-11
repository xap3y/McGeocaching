package eu.xap3y.egghunt.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeocachingConfig {

    private Boolean enabled;

    private Integer minDistance;

    public GeocachingConfig() {
        this.enabled = true;
        this.minDistance = 5;
    }
}
