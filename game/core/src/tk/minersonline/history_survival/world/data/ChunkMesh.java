package tk.minersonline.history_survival.world.data;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static tk.minersonline.history_survival.world.World.*;
import static tk.minersonline.history_survival.world.World.CHUNK_SIZE_Z;

public class ChunkMesh {
	public static final boolean USE_PACKED_COLOR = true;
	public static final int VERTEX_SIZE = USE_PACKED_COLOR ? 7 : 10;

	public final int topOffset;
	public final int bottomOffset;
	public final int leftOffset;
	public final int rightOffset;
	public final int frontOffset;
	public final int backOffset;

	public Mesh mesh;
	public Mesh transparentMesh;
	public Material material;
	public int numVertices;
	public int numTransparentVertices;
	public float[] vertices = new float[VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
	public float[] transparentVertices = new float[VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];

	int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
	public short[] indices = new short[len];

	public ChunkMesh(int width, int depth) {
		this.topOffset = width * depth;
		this.bottomOffset = -width * depth;
		this.leftOffset = -1;
		this.rightOffset = 1;
		this.frontOffset = -width;
		this.backOffset = width;
	}

	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		Renderable renderable = pool.obtain();
		renderable.material = material;
		renderable.meshPart.mesh = mesh;
		renderable.meshPart.offset = 0;
		renderable.meshPart.size = numVertices;
		renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
		renderables.add(renderable);
	}

	public void getTransparentRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		Renderable transparentRenderable = pool.obtain();
		Material transparent = material;
		transparent.set(FloatAttribute.createAlphaTest(0.2f));
		transparent.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

		transparentRenderable.material = transparent;
		transparentRenderable.meshPart.mesh = transparentMesh;
		transparentRenderable.meshPart.offset = 0;
		transparentRenderable.meshPart.size = numTransparentVertices;
		transparentRenderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
		renderables.add(transparentRenderable);
	}
}
