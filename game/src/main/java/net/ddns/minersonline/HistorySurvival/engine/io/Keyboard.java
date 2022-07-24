package net.ddns.minersonline.HistorySurvival.engine.io;

import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard extends GLFWKeyCallback {
	private static final boolean[] keys = new boolean[GLFW_KEY_LAST];
	private static final boolean[] keys_pressed = new boolean[GLFW_KEY_LAST];
	private static final boolean[] keys_was_pressed = new boolean[GLFW_KEY_LAST];

	public void invoke(long window, int key, int scancode, int action, int mods) {
		if(key != -1) {
			keys[key] = action != GLFW.GLFW_RELEASE;
		}
		DisplayManager.guiManager.keyCallback(window, key, scancode, action, mods);
	}

	public void invoke2(long window, int codepoint) {
		DisplayManager.guiManager.charCallback(window, codepoint);
	}

	public static boolean isKeyDown(int keycode) {
		return keys[keycode];
	}
	public static boolean isKeyPressed(int keycode) {
		boolean pressed = false;
		keys_pressed[keycode] = glfwGetKey(DisplayManager.getWindow(), keycode) == GLFW_PRESS;
		if (!keys_was_pressed[keycode] && keys_pressed[keycode]) pressed = true;
		keys_was_pressed[keycode] = keys_pressed[keycode];
		return pressed;
	}

	public static void clear(){
		Arrays.fill(keys, false);
		Arrays.fill(keys_pressed, false);
		Arrays.fill(keys_was_pressed, false);
	}
}
