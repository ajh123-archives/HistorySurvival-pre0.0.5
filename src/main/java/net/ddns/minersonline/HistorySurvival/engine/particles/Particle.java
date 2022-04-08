package net.ddns.minersonline.HistorySurvival.engine.particles;


import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import net.ddns.minersonline.HistorySurvival.engine.entities.Player;
import org.joml.Vector3f;

public class Particle {

    private Vector3f position;
    private Vector3f velocity;
    private float gravityComplient;
    private float lifeLength;
    private float generateRotation;
    private float scale;

    private float elapsedTime;

    public Particle(Vector3f position, Vector3f velocity, float gravityComplient, float lifeLength, float generateRotation, float scale) {
        this.position = position;
        this.velocity = velocity;
        this.gravityComplient = gravityComplient;
        this.lifeLength = lifeLength;
        this.generateRotation = generateRotation;
        this.scale = scale;
        ParticleMaster.addParticle(this);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public float getScale() {
        return scale;
    }

    public boolean update(){
        float time = (float) DisplayManager.getDeltaInSeconds();
        velocity.y += Player.GRAVITY * gravityComplient * time;
        Vector3f change = new Vector3f(velocity);
        change.mul(time);
        position.add(change);
        elapsedTime += time;
        return elapsedTime < lifeLength;
    }

    public float getRotation() {
        return generateRotation;
    }
}
