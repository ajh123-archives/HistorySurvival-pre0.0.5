package net.ddns.minersonline.HistorySurvival.engine.text;

public class MeshData {
	int vao;
	int vbo1;
	int vbo2;

	public MeshData(int vao, int vbo1, int vbo2) {
		this.vao = vao;
		this.vbo1 = vbo1;
		this.vbo2 = vbo2;
	}

	public int getVao() {
		return vao;
	}

	public int getVbo1() {
		return vbo1;
	}

	public int getVbo2() {
		return vbo2;
	}
}
