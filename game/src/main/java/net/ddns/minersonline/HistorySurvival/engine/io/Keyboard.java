package net.ddns.minersonline.HistorySurvival.engine.io;

import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard extends GLFWKeyCallback {
	private static boolean[] keys = new boolean[GLFW_KEY_LAST];
	private static boolean[] keys_pressed = new boolean[GLFW_KEY_LAST];
	private static boolean[] keys_was_pressed = new boolean[GLFW_KEY_LAST];

	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action != GLFW.GLFW_RELEASE;
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
}
