package tk.minersonline.history_survival.voxels;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import tk.minersonline.history_survival.ecs.VoxelChunkComponent;
import tk.minersonline.history_survival.ecs.VoxelComponent;
import tk.minersonline.history_survival.ecs.VoxelTypeComponent;

public class VoxelWorld implements RenderableProvider {
	public static final int CHUNK_SIZE_X = 16;
	public static final int CHUNK_SIZE_Y = 16;
	public static final int CHUNK_SIZE_Z = 16;

	public final VoxelChunkComponent[] chunks;
	public final Mesh[] meshes;
	public final Mesh[] transparentMeshes;
	public final Material[] materials;
	public final boolean[] dirty;
	public final int[] numVertices;
	public final int[] numTransparentVertices;
	public float[] vertices;
	public float[] transparentVertices;
	public final int chunksX;
	public final int chunksY;
	public final int chunksZ;
	public final int voxelsX;
	public final int voxelsY;
	public final int voxelsZ;
	public int renderedChunks;
	public int numChunks;

	public VoxelWorld (int chunksX, int chunksY, int chunksZ) {
		this.chunks = new VoxelChunkComponent[chunksX * chunksY * chunksZ];
		this.chunksX = chunksX;
		this.chunksY = chunksY;
		this.chunksZ = chunksZ;
		this.numChunks = chunksX * chunksY * chunksZ;
		this.voxelsX = chunksX * CHUNK_SIZE_X;
		this.voxelsY = chunksY * CHUNK_SIZE_Y;
		this.voxelsZ = chunksZ * CHUNK_SIZE_Z;
		int i = 0;
		for (int y = 0; y < chunksY; y++) {
			for (int z = 0; z < chunksZ; z++) {
				for (int x = 0; x < chunksX; x++) {
					VoxelChunkComponent chunk = new VoxelChunkComponent(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z, this);
					chunk.offset.set(
							(x * VoxelComponent.VOXEL_SIZE) * CHUNK_SIZE_X,
							(y * VoxelComponent.VOXEL_SIZE) * CHUNK_SIZE_Y,
							(z * VoxelComponent.VOXEL_SIZE) * CHUNK_SIZE_Z
					);
					chunks[i++] = chunk;
				}
			}
		}
		int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
		short[] indices = new short[len];
		short j = 0;
		for (i = 0; i < len; i += 6, j += (short) 4) {
			indices[i + 0] = (short)(j + 0);
			indices[i + 1] = (short)(j + 1);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 3);
			indices[i + 5] = (short)(j + 0);
		}

		VertexAttribute colorAttribute = VoxelChunkComponent.USE_PACKED_COLOR ? VertexAttribute.ColorPacked() : VertexAttribute.ColorUnpacked();
		VertexAttributes attributes = new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal(), colorAttribute);

		this.meshes = new Mesh[chunksX * chunksY * chunksZ];
		this.transparentMeshes = new Mesh[chunksX * chunksY * chunksZ];
		for (i = 0; i < meshes.length; i++) {
			meshes[i] = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * attributes.vertexSize * 4,
					CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3, attributes);
			meshes[i].setIndices(indices);
		}
		for (i = 0; i < transparentMeshes.length; i++) {
			transparentMeshes[i] = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * attributes.vertexSize * 4,
					CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3, attributes);
			transparentMeshes[i].setIndices(indices);
		}
		this.dirty = new boolean[chunksX * chunksY * chunksZ];
		for (i = 0; i < dirty.length; i++)
			dirty[i] = true;

		this.numVertices = new int[chunksX * chunksY * chunksZ];
		for (i = 0; i < numVertices.length; i++)
			numVertices[i] = 0;

		this.numTransparentVertices = new int[chunksX * chunksY * chunksZ];
		for (i = 0; i < numTransparentVertices.length; i++)
			numTransparentVertices[i] = 0;

		this.vertices = new float[VoxelChunkComponent.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
		this.transparentVertices = new float[VoxelChunkComponent.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
		this.materials = new Material[chunksX * chunksY * chunksZ];
		for (i = 0; i < materials.length; i++) {
			materials[i] = new Material();
		}
	}

	public VoxelComponent set (float x, float y, float z, VoxelTypeComponent voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if (chunkX < 0 || chunkX >= chunksX) return null;
		int chunkY = iy / CHUNK_SIZE_Y;
		if (chunkY < 0 || chunkY >= chunksY) return null;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if (chunkZ < 0 || chunkZ >= chunksZ) return null;
		return getChunk(x, y, z).set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z,
				voxel);
	}

	public VoxelComponent get (float x, float y, float z) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if (chunkX < 0 || chunkX >= chunksX) return null;
		int chunkY = iy / CHUNK_SIZE_Y;
		if (chunkY < 0 || chunkY >= chunksY) return null;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if (chunkZ < 0 || chunkZ >= chunksZ) return null;
		return getChunk(x, y, z).get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y,
				iz % CHUNK_SIZE_Z);
	}

	public VoxelChunkComponent getChunk (float x, float y, float z) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int chunkX = ix / CHUNK_SIZE_X;
		if (chunkX < 0 || chunkX >= chunksX) return null;
		int chunkY = iy / CHUNK_SIZE_Y;
		if (chunkY < 0 || chunkY >= chunksY) return null;
		int chunkZ = iz / CHUNK_SIZE_Z;
		if (chunkZ < 0 || chunkZ >= chunksZ) return null;
		return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ];
	}

	public float getHighest (float x, float z) {
		int ix = (int)x;
		int iz = (int)z;
		if (ix < 0 || ix >= voxelsX) return 0;
		if (iz < 0 || iz >= voxelsZ) return 0;
		// FIXME optimize
		for (int y = voxelsY - 1; y > 0; y--) {
			VoxelComponent voxel = get(ix, y, iz);
			if (voxel != null && voxel.getType() != VoxelTypeComponent.AIR) {
				return y + 1;
			}
		}
		return 0;
	}

	public void setColumn (float x, float y, float z, VoxelTypeComponent voxel) {
		setColumn(x, 0, y, z, voxel);
	}

	public void setColumn (float x, float startY, float endY, float z, VoxelTypeComponent voxel) {
		int ix = (int)x;
		int iy = (int)endY;
		int iz = (int)z;
		if (ix < 0 || ix >= voxelsX) return;
		if (iy < 0 || iy >= voxelsY) return;
		if (iz < 0 || iz >= voxelsZ) return;
		// FIXME optimize
		for (; iy > startY; iy--) {
			set(ix, iy, iz, voxel);
		}
	}

	public void setCube (float x, float y, float z, float width, float height, float depth, VoxelTypeComponent voxel) {
		int ix = (int)x;
		int iy = (int)y;
		int iz = (int)z;
		int iwidth = (int)width;
		int iheight = (int)height;
		int idepth = (int)depth;
		int startX = Math.max(ix, 0);
		int endX = Math.min(voxelsX, ix + iwidth);
		int startY = Math.max(iy, 0);
		int endY = Math.min(voxelsY, iy + iheight);
		int startZ = Math.max(iz, 0);
		int endZ = Math.min(voxelsZ, iz + idepth);
		// FIXME optimize
		for (iy = startY; iy < endY; iy++) {
			for (iz = startZ; iz < endZ; iz++) {
				for (ix = startX; ix < endX; ix++) {
					set(ix, iy, iz, voxel);
				}
			}
		}
	}

	@Override
	public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
		renderedChunks = 0;
		for (int i = 0; i < chunks.length; i++) {
			VoxelChunkComponent chunk = chunks[i];
			Mesh mesh = meshes[i];
			Mesh transparentMesh = transparentMeshes[i];
			if (dirty[i]) {
				int numVerts = chunk.calculateVertices(vertices);
				int vertexSize = mesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
				numVertices[i] = numVerts / 4 * vertexSize;
				mesh.setVertices(vertices, 0, numVerts * VoxelChunkComponent.VERTEX_SIZE);

				int transparentNumVerts = chunk.calculateTransparentVertices(transparentVertices);
				int transparentVertexSize = transparentMesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
				numTransparentVertices[i] = transparentNumVerts / 4 * transparentVertexSize;
				transparentMesh.setVertices(transparentVertices, 0, transparentNumVerts * VoxelChunkComponent.VERTEX_SIZE);
				dirty[i] = false;
			}
			if (numVertices[i] != 0) {
				Renderable renderable = pool.obtain();
				renderable.material = materials[i];
				renderable.meshPart.mesh = mesh;
				renderable.meshPart.offset = 0;
				renderable.meshPart.size = numVertices[i];
				renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
				renderables.add(renderable);
			}
			if (numTransparentVertices[i] != 0) {
				Renderable transparentRenderable = pool.obtain();
				Material transparent = materials[i].copy();
				transparent.set(FloatAttribute.createAlphaTest(0.2f));
				transparent.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

				transparentRenderable.material = transparent;
				transparentRenderable.meshPart.mesh = transparentMesh;
				transparentRenderable.meshPart.offset = 0;
				transparentRenderable.meshPart.size = numTransparentVertices[i];
				transparentRenderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
				renderables.add(transparentRenderable);
			}
			renderedChunks++;
		}
	}
}