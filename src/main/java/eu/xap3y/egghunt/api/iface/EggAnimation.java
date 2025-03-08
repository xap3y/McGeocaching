package eu.xap3y.egghunt.api.iface;

import eu.xap3y.egghunt.api.enums.EggAnimationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface EggAnimation {

    String getName();

    EggAnimationType getType();

    void start(Location loc, Player player);
}
