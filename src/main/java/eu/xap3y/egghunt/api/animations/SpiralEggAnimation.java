package eu.xap3y.egghunt.api.animations;

import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.EggAnimationType;
import eu.xap3y.egghunt.api.iface.EggAnimation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpiralEggAnimation implements EggAnimation {

    @Override
    public String getName() {
        return "spiral";
    }

    @Override
    public EggAnimationType getType() {
        return EggAnimationType.SPIRAL;
    }

    @Override
    public void start(Location loc, Player player) {

        player.playSound(player, Sound.BLOCK_LAVA_POP, 1f ,1f);

        new BukkitRunnable() {
            double t = 0; // Animation timer

            public void run() {
                if (t > Math.PI * 2) { // Stop after one full rotation
                    cancel();
                    return;
                }

                for (int i = 0; i < 4; i++) { // 4 particles per frame for better visibility
                    double angle = t + (i * Math.PI / 2); // Spread particles around center
                    double x = Math.cos(angle) * 0.4; // Small radius
                    double y = t * 0.15; // Moves slightly up
                    double z = Math.sin(angle) * 0.4;

                    Location particleLoc = loc.clone().add(x, y, z);
                    //loc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0);

                    EggHunt.getParApi().LIST_1_8.FLAME
                            .packet(true, particleLoc)
                            .sendTo(player);
                }

                t += 1.2;
            }
        }.runTaskTimer(EggHunt.getInstance(), 0, 2);
    }

}
