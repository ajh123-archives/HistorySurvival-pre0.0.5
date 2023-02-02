package tk.minersonline.history_survival.world.data;

import tk.minersonline.history_survival.world.Chunk;

import static tk.minersonline.history_survival.world.World.*;
import static tk.minersonline.history_survival.world.data.ChunkMesh.VERTEX_SIZE;

public class ChunkBuilderData {
	int vertexOffset;
	int rawVertexOffset;
	int width;
	int height;
	int depth;
	float[] vertices = new float[VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
	float[] rawVertices = new float[3 * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
	Chunk chunk;

	public void begin(Chunk chunk) {
		this.vertexOffset = 0;
		this.rawVertexOffset = 0;
		this.chunk = chunk;
		this.width = chunk.width;
		this.height = chunk.height;
		this.depth = chunk.depth;
	}
}
