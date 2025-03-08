package eu.xap3y.egghunt.api.animations;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeMotion;
import eu.xap3y.egghunt.EggHunt;
import eu.xap3y.egghunt.api.enums.EggAnimationType;
import eu.xap3y.egghunt.api.iface.EggAnimation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RevealEggAnimation implements EggAnimation {

    @Override
    public String getName() {
        return "reveal";
    }

    @Override
    public EggAnimationType getType() {
        return EggAnimationType.REVEAL;
    }

    @Override
    public void start(Location loc, Player player) {

        for (int j = 0; j < 7; j++) {

            for (int i = 0; i < 8; i++) {
                double x = (Math.random() - 0.5) * 1.2; // Random spread in X
                double y = Math.random(); // Random height around the egg
                double z = (Math.random() - 0.5) * 1.2; // Random spread in Z

                Location particleLoc = loc.clone().add(x, y-0.2, z);

                // Spawn small, subtle particles with variation
                switch ((int) (Math.random() * 2)) {
                    case 0 -> {
                        EggHunt.getParApi().LIST_1_8.ENCHANTMENT_TABLE.packet(true, particleLoc).sendTo(player);
                    }
                    default -> {
                        EggHunt.getParApi().LIST_1_8.SPELL.packet(true, particleLoc).sendTo(player);
                    }
                };


            }
        }
    }
}
