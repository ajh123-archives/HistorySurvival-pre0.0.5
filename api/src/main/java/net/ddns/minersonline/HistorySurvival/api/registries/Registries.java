package net.ddns.minersonline.HistorySurvival.api.registries;

import net.ddns.minersonline.HistorySurvival.api.entities.Entity;
import net.ddns.minersonline.HistorySurvival.api.entities.EntityType;

public class Registries {
	public static Registry<EntityType<? extends Entity>> ENTITY_REGISTRY = new Registry<>();
}
