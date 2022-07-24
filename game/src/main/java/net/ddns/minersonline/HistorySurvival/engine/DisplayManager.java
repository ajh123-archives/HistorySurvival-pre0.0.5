package net.ddns.minersonline.HistorySurvival.engine;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import net.ddns.minersonline.HistorySurvival.Scene;
import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.io.Mouse;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

public class DisplayManager {
	private static int WINDOW_WIDTH = 1280;
	private static int WINDOW_HEIGHT = 760;
	private static int FPS;
	private static long window;
	private static String title = "History Survival";
	private static int frames;
	private static long time;
	private static boolean showFPSTitle;
	private static double lastFrameTime;
	private static double deltaInSeconds;
	private static Keyboard keyboard;
	private static Mouse mouse;
	public static final GuiManager guiManager = new GuiManager();
	private static final GuiRenderer guiRenderer = new GuiRenderer();
	private static Scene currentScene = null;

	public static void createDisplay() {
		if (!glfwInit()) {
			throw new RuntimeException("ERROR: GLFW wasn't initialized");
		}

		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		assert vidMode != null;
		WINDOW_WIDTH = (vidMode.width()/2 + (vidMode.width()/8)*2);
		WINDOW_HEIGHT = (vidMode.height()/2 + (vidMode.height()/8)*2);

		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, title, 0, 0);

		if (window == 0) {
			throw new RuntimeException("Failed to create window");
		}

		glfwSetWindowPos(window, (vidMode.width() - WINDOW_WIDTH) / 2, (vidMode.height() - WINDOW_HEIGHT) / 2);

		ImGui.createContext();

		glfwMakeContextCurrent(window);
		createCapabilities();

		guiManager.init(window, false);
		guiRenderer.init("#version 410 core");
		keyboard = new Keyboard();
		mouse = new Mouse(guiManager);

		glfwSetCursorPosCallback(window, mouse.getMouseMoveCallback());
		glfwSetMouseButtonCallback(window, mouse.getMouseButtonsCallback());
		glfwSetScrollCallback(window, mouse.getMouseScrollCallback());

		// register keyboard input callback
		glfwSetKeyCallback(window, keyboard);
		glfwSetCharCallback(window, keyboard::invoke2);

		glfwSetWindowFocusCallback(window, guiManager::windowFocusCallback);
		glfwSetCursorEnterCallback(window, guiManager::cursorEnterCallback);
		glfwSetMonitorCallback(GLFWMonitorCallback.create(guiManager::monitorCallback));

		glfwShowWindow(window);

		// Setting the value to 1 should limit to 60 FPS
		glfwSwapInterval(1);

		glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback(){
			@Override
			public void invoke(long window, int width, int height){
				DisplayManager.setWindowWidth(width);
				DisplayManager.setWindowHeight(height);
				glViewport(0, 0, DisplayManager.getWindowWidth(), DisplayManager.getWindowHeight());
			}
		});

		lastFrameTime = getCurrentTime();
	}

	public static void preUpdate(Scene scene) {
		DisplayManager.currentScene = scene;
		guiManager.newFrame();
		ImGui.newFrame();
	}
	public static void updateDisplay() {
		ImGui.render();
		guiRenderer.renderDrawData(ImGui.getDrawData());

		if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			final long backupWindowPtr = GLFW.glfwGetCurrentContext();
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			GLFW.glfwMakeContextCurrent(backupWindowPtr);
		}

		glfwSwapBuffers(window);
		glfwPollEvents();
		getFrames();

		if (showFPSTitle) {
			glfwSetWindowTitle(window, title + " | FPS: " + FPS);
		} else {
			glfwSetWindowTitle(window, title);
		}

		double currentFrameTime = getCurrentTime();
		deltaInSeconds = (currentFrameTime - lastFrameTime) / 1000;
		lastFrameTime = currentFrameTime;
	}

	public static void dispose() {
		guiManager.dispose();
		guiRenderer.dispose();
		ImGui.destroyContext();

		glfwSetCursorPosCallback(window, null);
		glfwSetMouseButtonCallback(window, null);
		glfwSetScrollCallback(window, null);
		glfwSetKeyCallback(window, null);
		glfwSetCharCallback(window, null);
		glfwSetWindowFocusCallback(window, null);
		glfwSetCursorEnterCallback(window, null);
		glfwSetMonitorCallback(null);

		GL.setCapabilities(null);
		glfwWindowShouldClose(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		mouse.destroy();
		keyboard.close();
	}

	public static boolean shouldDisplayClose() {
		return !glfwWindowShouldClose(window);
	}

	public static String getOpenGlVersionMessage() {
		return glGetString(GL_VERSION);
	}

	public static void setShowFPSTitle(boolean showFPSTitle) {
		DisplayManager.showFPSTitle = showFPSTitle;
	}

	public static void getFrames() {
		frames++;
		if (System.currentTimeMillis() > time + 1000) {
			time = System.currentTimeMillis();
			FPS = frames;
			frames = 0;
		}
	}

	public static int getFPS() {
		return FPS;
	}

	public static boolean getShowFPSTitle() {
		return showFPSTitle;
	}

	public static void setWindowWidth(int windowWidth) {
		WINDOW_WIDTH = windowWidth;
	}

	public static void setWindowHeight(int windowHeight) {
		WINDOW_HEIGHT = windowHeight;
	}

	public static int getWindowWidth() {
		return WINDOW_WIDTH;
	}

	public static int getWindowHeight() {
		return WINDOW_HEIGHT;
	}

	public static long getWindow(){
		return window;
	}

	public static double getDeltaInSeconds() {
		return deltaInSeconds;
	}

	private static double getCurrentTime() {
		return glfwGetTime() * 1000;
	}
}
