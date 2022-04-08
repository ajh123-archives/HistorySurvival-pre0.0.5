package net.ddns.minersonline.HistorySurvival.engine.particles;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import org.joml.Matrix4f;

import java.util.*;

public class ParticleMaster {
    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(ModelLoader loader, Matrix4f projectionMatrix){
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }

    public static void update(){
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> parts  = particles.entrySet().iterator();
        while (parts.hasNext()){
            List<Particle> inside_parts = parts.next().getValue();
            Iterator<Particle> iterator = inside_parts.iterator();
            while (iterator.hasNext()){
                Particle p = iterator.next();
                boolean alive = p.update();
                if(!alive){
                    iterator.remove();
                    if(inside_parts.isEmpty()){
                        parts.remove();
                    }
                }
            }
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
    }

    public static int getCount(){
        return particles.size();
    }
}
