package net.ddns.minersonline.HistorySurvival.api.entities;

import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.registries.Registries;
import net.ddns.minersonline.HistorySurvival.api.registries.Registry;

import javax.annotation.Nullable;

public class EntityType<T extends Entity> {
	protected ResourceLocation registryName;

	public static EntityType<EmptyEntity> EMPTY_ENTITY = register(
			"empty",
			EntityType.Builder.of(EmptyEntity::new)
	);

	public static EntityType<PlayerEntity> PLAYER_ENTITY = register(
			"player",
			EntityType.Builder.of(PlayerEntity::new)
	);


	@SuppressWarnings("unchecked")
	private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
		ResourceLocation registryName = new ResourceLocation(id);
		EntityType<T> type = (EntityType<T> ) Registry.register(Registries.ENTITY_REGISTRY, registryName, builder.build());
		type.registryName = registryName;
		return type;
	}

	@Nullable
	public final ResourceLocation getRegistryName()
	{
		return registryName;
	}



	private final EntityFactory<T> factory;

	public EntityType(EntityFactory<T> factory) {
		this.factory = factory;
	}

	public static class Builder<T extends Entity> {
		private final EntityFactory<T> factory;

		private Builder(EntityFactory<T> factory) {
			this.factory = factory;
		}

		public static <T extends Entity> EntityType.Builder<T> of(EntityFactory<T> factory) {
			return new EntityType.Builder<>(factory);
		}

		public EntityType<T> build() {
			return new EntityType<>(this.factory);
		}
	}

	public interface EntityFactory<T extends Entity> {
		T create(EntityType<T> type);//TODO: Add world parameter
	}

	@Nullable
	public T create() {
		return this.factory.create(this);
	}
}
