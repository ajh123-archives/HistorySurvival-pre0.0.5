package net.ddns.minersonline.HistorySurvival.api.registries;

import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.voxel.Voxel;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class VoxelType<T extends Voxel> {
	protected ResourceLocation registryName;

	public static VoxelType<Voxel> GRASS = register(
			"grass",
			VoxelType.Builder.of(Voxel::new)
	);

	public static VoxelType<Voxel> MUD = register(
			"mud",
			VoxelType.Builder.of(Voxel::new)
	);

	public static void init(){}

	@SuppressWarnings("unchecked")
	private static <T extends Voxel> VoxelType<T> register(String id, VoxelType.Builder<T> builder) {
		ResourceLocation registryName = new ResourceLocation(id);
		VoxelType<T> type = (VoxelType<T>) Registry.register(Registries.VOXEL_REGISTRY, registryName, builder.build());
		type.registryName = registryName;
		return type;
	}

	@Nullable
	public final ResourceLocation getRegistryName()
	{
		return registryName;
	}



	private final VoxelType.VoxelFactory<T> factory;

	public VoxelType(VoxelType.VoxelFactory<T> factory) {
		this.factory = factory;
	}

	public static class Builder<T extends Voxel> {
		private final VoxelType.VoxelFactory<T> factory;

		private Builder(VoxelType.VoxelFactory<T> factory) {
			this.factory = factory;
		}

		public static <T extends Voxel> VoxelType.Builder<T> of(VoxelType.VoxelFactory<T> factory) {
			return new VoxelType.Builder<>(factory);
		}

		public VoxelType<T> build() {
			return new VoxelType<>(this.factory);
		}
	}

	@FunctionalInterface
	public interface VoxelFactory<T extends Voxel> {
		T create();
	}

	@Nullable
	public T create(Vector3f position) {
		T type = this.factory.create();
		type.setModelType(registryName);
		type.setPosition(position);
		return type;
	}
}
