package net.ddns.minersonline.HistorySurvival.engine;

import net.ddns.minersonline.HistorySurvival.engine.io.Keyboard;
import net.ddns.minersonline.HistorySurvival.engine.io.Mouse;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

public class DisplayManager {
    private static int WINDOW_WIDTH = 1280;
    private static int WINDOW_HEIGHT = 760;
    private static long window;
    private static String title = "History Survival";
    private static int frames;
    private static long time;
    private static boolean showFPSTitle;
    private static double lastFrameTime;
    private static double deltaInSeconds;
    private static Keyboard keyboard;
    private static Mouse mouse;

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

        keyboard = new Keyboard();
        mouse = new Mouse();

        // register keyboard input callback
        glfwSetKeyCallback(window, keyboard);
        glfwSetCursorPosCallback(window, mouse.getMouseMoveCallback());
        glfwSetMouseButtonCallback(window, mouse.getMouseButtonsCallback());
        glfwSetScrollCallback(window, mouse.getMouseScrollCallback());

        glfwMakeContextCurrent(window);
        createCapabilities();
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

    public static void updateDisplay() {
        glfwPollEvents();
        glfwSwapBuffers(window);

        if (showFPSTitle) {
            frames++;

            if (System.currentTimeMillis() > time + 1000 && showFPSTitle) {
                glfwSetWindowTitle(window, title + " | FPS: " + frames);
                time = System.currentTimeMillis();
                frames = 0;
            }
        } else {
            glfwSetWindowTitle(window, title);
        }

        double currentFrameTime = getCurrentTime();
        deltaInSeconds = (currentFrameTime - lastFrameTime) / 1000;
        lastFrameTime = currentFrameTime;
    }

    public static void closeDisplay() {
        mouse.destroy();
        keyboard.close();
        glfwWindowShouldClose(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static boolean shouldDisplayClose() {
        return !glfwWindowShouldClose(window);
    }

    public static String getOpenGlVersionMessage() {
        return glGetString(GL_VERSION);
    }

    public static void setShowFPSTitle(boolean showFPSTitle) {
        DisplayManager.showFPSTitle = showFPSTitle;

        if (!showFPSTitle) {
            frames = 0;
            time = 0;
        }
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
