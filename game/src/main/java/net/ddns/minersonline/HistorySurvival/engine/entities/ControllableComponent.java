package net.ddns.minersonline.HistorySurvival.engine.entities;

import net.ddns.minersonline.HistorySurvival.api.auth.GameProfile;
import net.ddns.minersonline.HistorySurvival.api.ecs.Component;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.api.voxel.Voxel;
import net.ddns.minersonline.HistorySurvival.api.voxel.VoxelWorld;

import static org.lwjgl.glfw.GLFW.*;

public class ControllableComponent extends Component {
	private transient static final float RUN_SPEED = 24;   // units per second
	private transient static final float TURN_SPEED = 160; // degrees per second
	public transient static final float GRAVITY = -50;
	private transient static final float JUMP_POWER = 18;
	private transient boolean GRAVITY_ENABLED = false;
	private transient VoxelWorld world;

	public float currentSpeed;
	public float currentTurnSpeed;
	public float upwardsSpeed;
	public boolean isJump;
	public transient GameProfile profile;

	private transient TransformComponent transformComponent;

	public ControllableComponent() {}

	public ControllableComponent(VoxelWorld world, float currentSpeed, float currentTurnSpeed, float upwardsSpeed, boolean isJump, GameProfile profile) {
		this.currentSpeed = currentSpeed;
		this.currentTurnSpeed = currentTurnSpeed;
		this.upwardsSpeed = upwardsSpeed;
		this.isJump = isJump;
		this.profile = profile;
		this.world = world;
	}

	public ControllableComponent(VoxelWorld world) {
		this.world = world;
	}

	@Override
	public void start() {
		super.start();
		this.transformComponent = gameObject.getComponent(TransformComponent.class);
	}

	private void jump(boolean down) {
		if (this.upwardsSpeed == 0 && !down) {
			this.upwardsSpeed = JUMP_POWER;
		} else {
			this.upwardsSpeed = -JUMP_POWER;
		}
		this.isJump = true;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		if(this.world != null) {
			if (Keyboard.isKeyDown(GLFW_KEY_W) || Keyboard.isKeyDown(GLFW_KEY_UP)) {
				this.currentSpeed = RUN_SPEED;
			} else if (Keyboard.isKeyDown(GLFW_KEY_S) || Keyboard.isKeyDown(GLFW_KEY_DOWN)) {
				this.currentSpeed = -RUN_SPEED;
			} else {
				this.currentSpeed = 0;
			}

			if (Keyboard.isKeyDown(GLFW_KEY_D) || Keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
				this.currentTurnSpeed = -TURN_SPEED;
			} else if (Keyboard.isKeyDown(GLFW_KEY_A) || Keyboard.isKeyDown(GLFW_KEY_LEFT)) {
				this.currentTurnSpeed = TURN_SPEED;
			} else {
				this.currentTurnSpeed = 0;
			}

			if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
				if (!isJump) {
					if (!GRAVITY_ENABLED) {
						upwardsSpeed = 0;
					}
					this.jump(false);
				}
			}

			if (!GRAVITY_ENABLED) {
				if (Keyboard.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
					if (!isJump) {
						upwardsSpeed = 1;
						this.jump(true);
					}
				}
			}

			// Calculate movement
			this.transformComponent.increaseRotation(0, this.currentTurnSpeed * deltaTime, 0);
			float distance = this.currentSpeed * deltaTime;
			float dx = (float) (distance * Math.sin(Math.toRadians(this.transformComponent.rotation.y)));
			float dz = (float) (distance * Math.cos(Math.toRadians(this.transformComponent.rotation.y)));
			this.transformComponent.increasePosition(dx, 0, dz);

			// Calculate jump
			if (GRAVITY_ENABLED) {
				this.upwardsSpeed += GRAVITY * deltaTime;
				this.transformComponent.increasePosition(0, this.upwardsSpeed * deltaTime, 0);
			}

			// Player terrain collision detection
			Voxel voxel = world.getBlock(transformComponent.position);
			if (voxel != null && GRAVITY_ENABLED) {
				float terrainHeight = voxel.getPosition().y;
				if ((this.transformComponent.position.y - 0.5f) < terrainHeight) {
					this.upwardsSpeed = 0;
					this.isJump = false;
					this.transformComponent.position.y = 0.5f + terrainHeight;
				}
			}
		}
	}

	public void setWorld(VoxelWorld world) {
		this.world = world;
	}
}
