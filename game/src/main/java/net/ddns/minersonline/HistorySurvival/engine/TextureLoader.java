package net.ddns.minersonline.HistorySurvival.engine;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.api.data.models.ModelTexture;
import net.ddns.minersonline.HistorySurvival.api.data.models.TexturedModel;
import net.ddns.minersonline.HistorySurvival.api.data.resources.ResourceLocation;
import net.ddns.minersonline.HistorySurvival.api.exceptions.ResourceLocationException;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import net.ddns.minersonline.HistorySurvival.api.registries.Registries;
import net.ddns.minersonline.HistorySurvival.engine.utils.BufferUtils;
import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL11.*;

public class TextureLoader {
	private int width, height;
	private int textureId;
	public static Map<String, Map<Integer, int[]>> images = new HashMap<>();
	public static ModelTexture textureAtlas;

	public TextureLoader(String path) {
		textureId = load(path);
	}

	public TextureLoader() {
	}

	public int load(String path) {
		int[] pixels = null;
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classloader.getResourceAsStream(path);

		try {
			assert stream != null;
			BufferedImage image = ImageIO.read(stream);
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int[] data = new int[width * height];

		for (int i = 0; i < width * height; i++) {
			assert pixels != null;
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);

			data[i] = a << 24 | b << 16 | g << 8 | r;
		}

		int result = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, result);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
		glBindTexture(GL_TEXTURE_2D, 0);

		Map<Integer, int[]> image = new HashMap<>();
		image.put(result, data);
		images.put(path, image);

		return result;
	}

	public static void createTextureAtlas(){
		Game.logger.info("Atlas stitching");

		int result = glGenTextures();
		int width = images.size() * 256;
		int height = 256;
		int[] data = new int[width * height];
		IntBuffer buffer = BufferUtils.createIntBuffer(data);

		int[] real_data = new int[width * height];
//		for (int x = 0; x < 256; x ++) {
//			for (int y = 0; y < 256; y ++) {
//				real_data[x * y] = 256;
//			}
//		}

		Game.logger.info("Using texture id '"+result+"' for atlas");
		int index = 0;
		for (String path : images.keySet()) {
			try {
				Registries.MODEL_REGISTRY.get(
						ResourceLocation.of(
								path.replace(".png", ""),
								ResourceLocation.NAMESPACE_SEPARATOR
						)).create().getModelTexture().setAtlasOffset(index);

				Game.logger.info("Stitching "+path);
				Map<Integer, int[]> subData = images.get(path);

				for (int[] imgData : subData.values()) {
					int[] realData = new int[256 * 256];
					glTexSubImage2D(
							GL_TEXTURE_2D,
							0,
							1,
							0,
							256,
							256,
							GL_RGBA,
							GL_UNSIGNED_BYTE,
							BufferUtils.createIntBuffer(realData)
					);
				}
				index++;
			} catch (ResourceLocationException ignored){}


		}
		buffer.put(real_data);
		glBindTexture(GL_TEXTURE_2D, result);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glBindTexture(GL_TEXTURE_2D, 0);

		textureAtlas = new ModelTexture(result);
		textureAtlas.setUseFakeLighting(true);
		textureAtlas.setHasTransparency(true);
	}

	public static ModelTexture getTextureAtlas(){
		return textureAtlas;
	}

	public int load(byte[] imageData) {
		int[] pixels;

		BufferedImage image = createImageFromBytes(imageData);
		width = image.getWidth();
		height = image.getHeight();
		pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);

		int[] data = new int[width * height];

		for (int i = 0; i < width * height; i++) {
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);

			data[i] = a << 24 | b << 16 | g << 8 | r;
		}

		int result = glGenTextures();

		glBindTexture(GL_TEXTURE_2D, result);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
		glBindTexture(GL_TEXTURE_2D, 0);

		return result;
	}

	private BufferedImage createImageFromBytes(byte[] imageData) {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		try {
			return ImageIO.read(bais);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTextureId() {
		return textureId;
	}
}
