package net.ddns.minersonline.HistorySurvival.api.registries;

import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.voxel.Voxel;

public class Registries {
	public static Registry<ModelType<? extends TexturedModel>> MODEL_REGISTRY = new Registry<>();
	public static Registry<VoxelType<? extends Voxel>> VOXEL_REGISTRY = new Registry<>();
}
