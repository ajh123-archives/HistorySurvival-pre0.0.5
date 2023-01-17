package tk.minersonline.history_survival.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;

import java.util.Comparator;

public class WorldRenderer extends SortedIteratingSystem {
	public WorldRenderer(Family family, Comparator<Entity> comparator) {
		super(family, comparator);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {

	}
}
