package eu.xap3y.egghunt.util;

import eu.xap3y.egghunt.api.animations.RevealEggAnimation;
import eu.xap3y.egghunt.api.animations.SpiralEggAnimation;
import eu.xap3y.egghunt.api.enums.EggAnimationType;
import eu.xap3y.egghunt.api.iface.EggAnimation;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ConfigDb {

    @Getter
    private static final Map<EggAnimationType, EggAnimation> animations = new HashMap<EggAnimationType, EggAnimation>() {{
        put(EggAnimationType.SPIRAL, new SpiralEggAnimation());
        put(EggAnimationType.REVEAL, new RevealEggAnimation());
    }};
}
