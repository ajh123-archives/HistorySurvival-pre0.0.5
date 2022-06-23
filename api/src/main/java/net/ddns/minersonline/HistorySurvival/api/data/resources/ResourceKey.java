package net.ddns.minersonline.HistorySurvival.api.data.resources;

import com.google.common.collect.Maps;
import net.ddns.minersonline.HistorySurvival.api.registries.Registry;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ResourceKey<T> implements Comparable<ResourceKey<?>> {
	private static final Map<String, ResourceKey<?>> VALUES = Collections.synchronizedMap(Maps.newIdentityHashMap());
	private final ResourceLocation registryName;
	private final ResourceLocation location;

	public static <T> ResourceKey<T> create(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation location) {
		return create(registryKey.location, location);
	}

	public static <T> ResourceKey<Registry<T>> createRegistryKey(ResourceLocation location) {
		return create(Registry.ROOT_REGISTRY_NAME, location);
	}

	private static <T> ResourceKey<T> create(ResourceLocation registryName, ResourceLocation location) {
		String s = (registryName + ":" + location).intern();
		return (ResourceKey<T>)VALUES.computeIfAbsent(s, (p_195971_) -> {
			return new ResourceKey(registryName, location);
		});
	}

	private ResourceKey(ResourceLocation registryName, ResourceLocation location) {
		this.registryName = registryName;
		this.location = location;
	}

	public String toString() {
		return "ResourceKey[" + this.registryName + " / " + this.location + "]";
	}

	public boolean isFor(ResourceKey<? extends Registry<?>> key) {
		return this.registryName.equals(key.location());
	}

	public <E> Optional<ResourceKey<E>> cast(ResourceKey<? extends Registry<E>> key) {
		return this.isFor(key) ? Optional.of((ResourceKey<E>)this) : Optional.empty();
	}

	public ResourceLocation location() {
		return this.location;
	}

	public ResourceLocation registry() {
		return this.registryName;
	}

	public static <T> Function<ResourceLocation, ResourceKey<T>> elementKey(ResourceKey<? extends Registry<T>> key) {
		return (location) -> {
			return create(key, location);
		};
	}

	public ResourceLocation getRegistryName() {
		return this.registryName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return registryName.equals(((ResourceKey<?>) o).registryName) && location.equals(((ResourceKey<?>) o).location);
	}

	@Override
	public int compareTo(ResourceKey<?> o) {
		int ret = this.getRegistryName().compareTo(o.getRegistryName());
		if (ret == 0) ret = this.location().compareTo(o.location());
		return ret;
	}
}
