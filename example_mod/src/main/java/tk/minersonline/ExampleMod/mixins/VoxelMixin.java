package tk.minersonline.ExampleMod.mixins;

import com.badlogic.gdx.graphics.Color;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import tk.minersonline.history_survival.voxels.VoxelType;

@Mixin(value = VoxelType.class, remap = false)
public class VoxelMixin {

	@Shadow
	public static VoxelType GRASS = new VoxelType("grass", Color.CYAN);
}
