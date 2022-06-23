package net.ddns.minersonline.HistorySurvival.api.registries;

import com.google.common.collect.Maps;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceKey;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class Registry<T> {
	public static final ResourceLocation ROOT_REGISTRY_NAME = new ResourceLocation("root");
	private final Map<ResourceLocation, Supplier<?>> entries = Maps.newLinkedHashMap();

	public Registry() {
	}

	protected T register(ResourceLocation registry_key, T obj) {
		entries.put(registry_key, () -> obj);
		return obj;
	}

	public static <T> T register(Registry<T> registry, ResourceLocation registry_key, T obj) {
		return registry.register(registry_key, obj);
	}

	public Supplier<?> getSupplier(ResourceKey<? extends Registry<T>> registry_key){
		ResourceLocation resourcelocation = registry_key.location();
		return entries.get(resourcelocation);
	}
}
