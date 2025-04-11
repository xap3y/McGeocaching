package eu.xap3y.egghunt.api.animations;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.EggAnimationType;
import eu.xap3y.egghunt.api.iface.EggAnimation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SparkleShellEggAnimation implements EggAnimation {

    @Override
    public String getName() {
        return "sparkle_shell";
    }

    @Override
    public EggAnimationType getType() {
        return EggAnimationType.SPARKLE_SHELL;
    }

    @Override
    public void start(Location loc, Player player) {

        // EggHunt.getParApi() to spawn particle

        // Example: EggHunt.getParApi().LIST_1_8.FLAME.packet(true, loc).sendTo(player);

        new BukkitRunnable() {
            double t = 0;

            @Override
            public void run() {
                t += 0.45;
                if (t > 1 * Math.PI) {
                    cancel();
                    return;
                }

                for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 6) {
                    double radius = 0.5;
                    double x = Math.cos(angle + t) * radius;
                    double y = Math.sin(t * 1.5) * 0.5;
                    double z = Math.sin(angle + t) * radius;

                    Location particleLoc = loc.clone().add(x, y, z);

                    EggHunt.getParApi().LIST_1_8.FIREWORKS_SPARK.packet(true, particleLoc).sendTo(player);
                    EggHunt.getParApi().LIST_1_8.VILLAGER_HAPPY.packet(true, particleLoc).sendTo(player);
                    EggHunt.getParApi().LIST_1_8.END_ROD.packet(true, particleLoc).sendTo(player);
                }
            }
        }.runTaskTimer(EggHunt.getInstance(), 0L, 2L);
    }
}
