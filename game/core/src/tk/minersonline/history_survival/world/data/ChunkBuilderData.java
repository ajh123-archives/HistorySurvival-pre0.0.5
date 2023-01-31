package tk.minersonline.history_survival.world.data;

import com.badlogic.gdx.graphics.Mesh;
import tk.minersonline.history_survival.world.Chunk;

import static tk.minersonline.history_survival.world.data.ChunkMesh.VERTEX_SIZE;

public class ChunkBuilderData {
	int vertexOffset;
	int width;
	int height;
	int depth;
	Chunk chunk;
	boolean solid;


	public void begin(Chunk chunk, boolean solid) {
		this.vertexOffset = 0;
		this.chunk = chunk;
		this.width = chunk.width;
		this.height = chunk.height;
		this.depth = chunk.depth;
		this.solid = solid;
		System.out.println("B");
	}

	public void end() {
		if (solid) {
			System.out.println("SO");
			Mesh mesh = chunk.chunkMesh.mesh;
			int numVerts = ChunkMeshBuilder.calculateVertices(this);
			int vertexSize = mesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
			chunk.chunkMesh.numVertices = numVerts / 4 * vertexSize;
			mesh.setVertices(chunk.chunkMesh.vertices, 0, numVerts * VERTEX_SIZE);
		} else {
			System.out.println("TR");
			Mesh transparentMesh = chunk.chunkMesh.transparentMesh;
			int transparentNumVerts = ChunkMeshBuilder.calculateTransparentVertices(this);
			int transparentVertexSize = transparentMesh.getVertexSize() / 4; // Divide by 4 as it is in bytes
			chunk.chunkMesh.numTransparentVertices = transparentNumVerts / 4 * transparentVertexSize;
			transparentMesh.setVertices(chunk.chunkMesh.transparentVertices, 0, transparentNumVerts * VERTEX_SIZE);
		}
		System.out.println("E");
	}

	public void append(float data) {
		System.out.println("a");
		if (solid) {
			chunk.chunkMesh.vertices[vertexOffset++] = data;
			System.out.println("SO =");
		} else {
			chunk.chunkMesh.transparentVertices[vertexOffset++] = data;
			System.out.println("TR =");
		}
	}
}
