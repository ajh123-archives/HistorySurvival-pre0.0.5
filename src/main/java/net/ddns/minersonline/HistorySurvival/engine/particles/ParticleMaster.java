package net.ddns.minersonline.HistorySurvival.engine.particles;

import net.ddns.minersonline.HistorySurvival.engine.ModelLoader;
import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticleMaster {
    private static List<Particle> particles = new ArrayList<>();
    private static ParticleRenderer renderer;

    public static void init(ModelLoader loader, Matrix4f projectionMatrix){
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }

    public static void update(){
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()){
            Particle p = iterator.next();
            boolean alive = p.update();
            if(!alive) iterator.remove();
        }
    }

    public static void renderParticles(Camera camera){
        renderer.render(particles, camera);
    }

    public static void cleanUp(){
        renderer.cleanUp();
    }

    public static void addParticle(Particle particle){
        particles.add(particle);
    }

    public static int getCount(){
        return particles.size();
    }
}
