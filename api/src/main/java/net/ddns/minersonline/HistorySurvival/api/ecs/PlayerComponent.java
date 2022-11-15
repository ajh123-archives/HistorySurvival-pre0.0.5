package net.ddns.minersonline.HistorySurvival.api.ecs;

import net.ddns.minersonline.HistorySurvival.api.auth.GameProfile;

public class PlayerComponent extends Component{
	public GameProfile profile;

	public TransformComponent transformComponent;

	public PlayerComponent() {
	}

	public PlayerComponent(GameProfile profile) {
		this.profile = profile;
	}

	@Override
	public void start() {
		super.start();
		this.transformComponent = gameObject.getComponent(TransformComponent.class);
	}
}
