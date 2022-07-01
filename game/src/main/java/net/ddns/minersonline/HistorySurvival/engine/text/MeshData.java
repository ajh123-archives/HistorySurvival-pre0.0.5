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

	public void setVao(int vao) {
		this.vao = vao;
	}

	public void setVbo1(int vbo1) {
		this.vbo1 = vbo1;
	}

	public void setVbo2(int vbo2) {
		this.vbo2 = vbo2;
	}
}
