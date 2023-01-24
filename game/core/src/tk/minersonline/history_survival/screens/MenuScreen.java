package tk.minersonline.history_survival.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.gwt.thirdparty.guava.common.annotations.GwtIncompatible;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import tk.minersonline.history_survival.HistorySurvival;

public class MenuScreen implements Screen {
	final HistorySurvival game;
	Stage stage;

	public MenuScreen(HistorySurvival game) {
		this.game = game;
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		VisTable group = new VisTable();
		group.setFillParent(true);

		VisTextButton button = new VisTextButton("Play", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				game.setScreen(new GameScreen(game));
			}
		});
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
