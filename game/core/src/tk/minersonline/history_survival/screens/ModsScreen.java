package tk.minersonline.history_survival.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.gwt.thirdparty.guava.common.annotations.GwtIncompatible;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.ModContainerImpl;
import tk.minersonline.history_survival.HistorySurvival;

import java.util.Collection;
import java.util.List;

@GwtIncompatible("")
public class ModsScreen implements Screen {
	final HistorySurvival game;
	Stage stage;

	public ModsScreen(HistorySurvival game) {
		this.game = game;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		VisTable group = new VisTable();
		group.setFillParent(true);

		VisTextButton button = new VisTextButton("Back", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				game.setScreen(new MenuScreen(game));
			}
		});


		List<ModContainer> mods = HistorySurvival.INSTANCE.getLoader().getAllMods().stream().toList();

		for (ModContainer mod : mods) {
			VisTextButton btn = new VisTextButton(mod.getMetadata().getName(), new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {

				}
			});
			group.add(btn);
		}

		group.add(button);

		stage.addActor(group);
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
