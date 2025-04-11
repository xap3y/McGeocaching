package eu.xap3y.egghunt.api.animations;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.EggAnimationType;
import eu.xap3y.egghunt.api.iface.EggAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MystTwirlEggAnimation implements EggAnimation {

    @Override
    public String getName() {
        return "myst_twirl";
    }

    @Override
    public EggAnimationType getType() {
        return EggAnimationType.MYST_TWIRL;
    }

    @Override
    public void start(Location loc, Player player) {
        Location center = loc.clone().subtract(0.0, 0.25, 0.0);

        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                t += 0.3;
                if (t > Math.PI) {
                    cancel();
                    return;
                }

                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 4) {
                    double radius = 0.4;
                    double x = Math.cos(angle + t) * radius;
                    double y = t * 0.5;
                    double z = Math.sin(angle + t) * radius;

                    Location particleLoc = center.clone().add(x, y, z);

                    EggHunt.getParApi().LIST_1_8.ENCHANTMENT_TABLE.packet(true, particleLoc).sendTo(player);
                    EggHunt.getParApi().LIST_1_8.SPELL_WITCH.packet(true, particleLoc).sendTo(player);
                    EggHunt.getParApi().LIST_1_8.CLOUD.packet(true, particleLoc).sendTo(player);
                }
            }
        }.runTaskTimer(EggHunt.getInstance(), 0L, 2L);
    }
}
