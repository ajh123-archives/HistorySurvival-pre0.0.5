package net.ddns.minersonline.HistorySurvival.engine.particles;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import org.joml.Matrix4f;

import java.util.*;

public class ParticleMaster {
	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
	private static ParticleRenderer renderer;
	private static int count;
	private static boolean running = false;

	public static void init(ModelLoader loader, Matrix4f projectionMatrix){
		renderer = new ParticleRenderer(loader, projectionMatrix);
		running = true;
	}

	public static void stop(){
		running = false;
	}

	public static void update(Camera camera, float deltaTime) {
		Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while (mapIterator.hasNext()) {
			List<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator = list.iterator();
			while (iterator.hasNext()) {
				Particle p = iterator.next();
				boolean stillActive = p.update(camera, deltaTime);
				if (!stillActive || !running) {
					count--;
					iterator.remove();
					if (list.isEmpty()) {
						mapIterator.remove();
					}
				}
			}
			InsertionSort.sortHighToLow(list);
		}
	}

	public static void renderParticles(Camera camera){
		renderer.render(particles, camera);
	}

	public static void cleanUp(){
		renderer.cleanUp();
	}

	public static void addParticle(Particle particle){
		List<Particle> particles_list = particles.computeIfAbsent(particle.getTexture(), k -> new ArrayList<>());
		particles_list.add(particle);
		count++;
	}

	public static int getCount(){
		return count;
	}
}
