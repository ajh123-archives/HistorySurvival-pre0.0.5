package net.ddns.minersonline.HistorySurvival.engine.worldOld.types;

@FunctionalInterface
public interface NoiseFunc {
	public float apply(float x, float y, float z);
}
