package tk.minersonline.history_survival.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.JsonReader;
import tk.minersonline.history_survival.componments.ModelComponent;
import tk.minersonline.history_survival.componments.TransformComponent;

import java.util.HashMap;
import java.util.Map;

public class ModelRenderer extends IteratingSystem{
	ComponentMapper<ModelComponent> models;
	ComponentMapper<TransformComponent> transforms;
	private final Map<Entity, ModelInstance> instances = new HashMap<>();

	public ModelRenderer() {
		super(Family.all(ModelComponent.class, TransformComponent.class).get());
		models = ComponentMapper.getFor(ModelComponent.class);
		transforms = ComponentMapper.getFor(TransformComponent.class);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ModelComponent modelComponent = models.get(entity);
		TransformComponent transformComponent = transforms.get(entity);

		if (!instances.containsKey(entity)) {
			Model model = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal(modelComponent.getPath()));
			ModelInstance instance = new ModelInstance(model);
			instance.transform.translate(transformComponent.getPos());
			instance.transform.scale(transformComponent.getScale(), transformComponent.getScale(), transformComponent.getScale());
			instances.put(entity, instance);
		}
	}


	public void render (ModelBatch batch, Environment environment) {
		for (Entity entity : instances.keySet()) {
			ModelInstance instance = instances.get(entity);
			batch.render(instance, environment);
		}
	}

	public void dispose() {
		instances.clear();
	}
}
