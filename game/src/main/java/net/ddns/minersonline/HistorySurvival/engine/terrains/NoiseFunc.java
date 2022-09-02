package net.ddns.minersonline.HistorySurvival.engine.terrains;

@FunctionalInterface
public interface NoiseFunc {
	public float apply(float x, float y, float z);
}
