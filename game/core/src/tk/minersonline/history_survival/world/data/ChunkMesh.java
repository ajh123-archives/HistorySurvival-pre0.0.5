package tk.minersonline.history_survival.world.data;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
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

	final int topOffset;
	final int bottomOffset;
	final int leftOffset;
	final int rightOffset;
	final int frontOffset;
	final int backOffset;

	Mesh mesh;
	Mesh transparentMesh;
	Material material;

	int numVertices;
	int numTransparentVertices;
//	float[] vertices = new float[VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
//	float[] transparentVertices = new float[VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];

	int numRawVertices;
	int numRawTransparentVertices;
//	float[] rawVertices = new float[3 * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
//	float[] rawTransparentVertices = new float[3 * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];

	static int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
	static short[] indices = new short[len];
	static {
		short j = 0;
		for (int i = 0; i < len; i += 6, j += (short) 4) {
			indices[i + 0] = (short) (j + 0);
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = (short) (j + 0);
		}
	}


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

	public void begin() {
		VertexAttribute colorAttribute = ChunkMesh.USE_PACKED_COLOR ? VertexAttribute.ColorPacked() : VertexAttribute.ColorUnpacked();
		VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal(), colorAttribute);

		mesh = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * attributes.vertexSize * 4,
				CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3, attributes);
		mesh.setIndices(indices);
		transparentMesh = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * attributes.vertexSize * 4,
				CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3, attributes);
		transparentMesh.setIndices(indices);

		numVertices = 0;
		numTransparentVertices = 0;
		material = new Material();
	}

	public int getNumVertices() {
		return numVertices;
	}

	public int getNumTransparentVertices() {
		return numTransparentVertices;
	}
}
