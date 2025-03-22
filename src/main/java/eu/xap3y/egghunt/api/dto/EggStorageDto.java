package eu.xap3y.egghunt.api.dto;

import lombok.Data;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class EggStorageDto {

    private Map<String, EggDto> eggs;
    private List<EggLocationDto> locations;
    private Map<String, String> textures;


    public EggStorageDto() {
        this.eggs = new HashMap<String, EggDto>();
        this.locations = new ArrayList<EggLocationDto>();
        this.textures = new HashMap<String, String>();
    }
}
