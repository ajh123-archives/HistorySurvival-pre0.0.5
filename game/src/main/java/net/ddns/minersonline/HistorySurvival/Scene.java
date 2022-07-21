package net.ddns.minersonline.HistorySurvival;

import net.ddns.minersonline.HistorySurvival.engine.entities.Camera;
import net.ddns.minersonline.HistorySurvival.api.entities.ClientEntity;
import net.ddns.minersonline.HistorySurvival.engine.entities.Light;
import net.ddns.minersonline.HistorySurvival.engine.entities.ClientPlayer;
import net.ddns.minersonline.HistorySurvival.engine.guis.GuiTexture;
import net.ddns.minersonline.HistorySurvival.engine.io.KeyEvent;
import net.ddns.minersonline.HistorySurvival.engine.terrains.World;

import java.util.List;

public abstract class Scene {
	public abstract void init();
	public abstract void update(KeyEvent keyEvent);

	public abstract void doGui();
	public abstract void stop();

	public abstract World getWorld();
	public abstract Camera getCamera();
	public abstract ClientPlayer getPlayer();

	public abstract List<GuiTexture> getGUIs();
	public abstract List<ClientEntity> getEntities();
	public abstract List<Light> getLights();
	public abstract Light getSun();
}
