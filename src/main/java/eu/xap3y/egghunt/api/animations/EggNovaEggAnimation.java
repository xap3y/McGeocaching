package eu.xap3y.egghunt.api.animations;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.EggAnimationType;
import eu.xap3y.egghunt.api.iface.EggAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EggNovaEggAnimation implements EggAnimation {
    @Override
    public String getName() {
        return "egg_nova";
    }

    @Override
    public EggAnimationType getType() {
        return EggAnimationType.EGG_NOVA;
    }

    @Override
    public void start(Location loc, Player player) {
        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                t += 0.25;  // Slower speed for ethereal feel
                if (t > 1 * Math.PI) {
                    cancel();
                    return;
                }

                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 6) {
                    double radius = 0.6;
                    double x = Math.cos(angle + t) * radius;
                    double y = t * 0.4; // slower upward movement
                    double z = Math.sin(angle + t) * radius;

                    Location particleLoc = loc.clone().add(x, y, z);

                    EggHunt.getParApi().LIST_1_13.GLOW.packet(true, particleLoc).sendTo(player);
                    EggHunt.getParApi().LIST_1_8.FIREWORKS_SPARK .packet(true, particleLoc).sendTo(player);
                    EggHunt.getParApi().LIST_1_8.EXPLOSION_NORMAL.packet(true, particleLoc).sendTo(player);
                }
            }
        }.runTaskTimer(EggHunt.getInstance(), 0L, 2L);
    }
}
