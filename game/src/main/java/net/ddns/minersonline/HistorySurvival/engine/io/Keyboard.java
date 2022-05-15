package net.ddns.minersonline.HistorySurvival.engine.io;

import net.ddns.minersonline.HistorySurvival.engine.DisplayManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard extends GLFWKeyCallback {
	private static final boolean[] keys = new boolean[GLFW_KEY_LAST];
	private static final boolean[] keys_pressed = new boolean[GLFW_KEY_LAST];
	private static final boolean[] keys_was_pressed = new boolean[GLFW_KEY_LAST];

	// Maximum event queue size
	private static final Queue<KeyEvent> keyEvents = new ArrayBlockingQueue<>(110);

	public void invoke(long window, int key, int scancode, int action, int mods) {
		keys[key] = action != GLFW.GLFW_RELEASE;
		KeyEvent currentEvent = new KeyEvent(action, key, 1);
		keyEvents.add(currentEvent);
	}

	public static void invoke2(int key) {
		KeyEvent currentEvent = new KeyEvent(0, key, 2);
		keyEvents.add(currentEvent);
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

	public static boolean hasKeyEvents() {
		return !keyEvents.isEmpty();
	}

	public static KeyEvent getKeyEvent() {
		return keyEvents.poll();
	}
}
