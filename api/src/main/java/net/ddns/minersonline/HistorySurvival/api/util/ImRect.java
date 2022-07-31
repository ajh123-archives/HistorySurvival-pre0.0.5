package net.ddns.minersonline.HistorySurvival.api.util;

import imgui.ImVec2;
import imgui.ImVec4;

// 2D axis aligned bounding-box
// NB: we can't rely on ImVec2 math operators being available here
public class ImRect {
	private ImVec2 Min = new ImVec2(); // Upper-left
	private ImVec2 Max = new ImVec2(); // Lower-right

	public ImRect() {
		this.Min = new ImVec2(0.0f, 0.0f);
		this.Max = new ImVec2(0.0f, 0.0f);
	}

	public ImRect(final ImVec2 min, final ImVec2 max) {
		this.Min = min;
		this.Max = max;
	}

	public ImRect(final ImVec4 v) {
		this.Min = new ImVec2(v.x, v.y);
		this.Max = new ImVec2(v.z, v.w);
	}

	public ImRect(float x1, float y1, float x2, float y2) {
		this.Min = new ImVec2(x1, y1);
		this.Max = new ImVec2(x2, y2);
	}

	public ImVec2 getCenter() {
		return new ImVec2((Min.x + Max.x) * 0.5f, (Min.y + Max.y) * 0.5f);
	}

	public ImVec2 getSize() {
		return new ImVec2(Max.x - Min.x, Max.y - Min.y);
	}

	public float getWidth() {
		return Max.x - Min.x;
	}

	public float getHeight() {
		return Max.y - Min.y;
	}

	public ImVec2 getTL() {
		return new ImVec2(Min);
	} // Top-left

	public ImVec2 getTR() {
		return new ImVec2(Max.x, Min.y);
	} // Top-right

	public ImVec2 getBL() {
		return new ImVec2(Min.x, Max.y);
	} // Bottom-left

	public ImVec2 getBR() {
		return new ImVec2(Max);
	} // Bottom-right

	public boolean contains(final ImVec2 p) {
		return p.x >= Min.x && p.y >= Min.y && p.x < Max.x && p.y < Max.y;
	}

	public boolean contains(final ImRect r) {
		return r.Min.x >= Min.x && r.Min.y >= Min.y && r.Max.x <= Max.x && r.Max.y <= Max.y;
	}

	public boolean overlaps(final ImRect r) {
		return r.Min.y < Max.y && r.Max.y > Min.y && r.Min.x < Max.x && r.Max.x > Min.x;
	}

	public void add(final ImVec2 p) {
		if (Min.x > p.x) {
			Min.x = p.x;
		}

		if (Min.y > p.y) {
			Min.y = p.y;
		}

		if (Max.x < p.x) {
			Max.x = p.x;
		}

		if (Max.y < p.y) {
			Max.y = p.y;
		}
	}

	public void add(final ImRect r) {
		if (Min.x > r.Min.x) {
			Min.x = r.Min.x;
		}

		if (Min.y > r.Min.y) {
			Min.y = r.Min.y;
		}

		if (Max.x < r.Max.x) {
			Max.x = r.Max.x;
		}

		if (Max.y < r.Max.y) {
			Max.y = r.Max.y;
		}
	}

	public void expand(final float amount) {
		Min.x -= amount;
		Min.y -= amount;
		Max.x += amount;
		Max.y += amount;
	}

	public void expand(final ImVec2 amount) {
		Min.x -= amount.x;
		Min.y -= amount.y;
		Max.x += amount.x;
		Max.y += amount.y;
	}

	public void translate(final ImVec2 d) {
		Min.x += d.x;
		Min.y += d.y;
		Max.x += d.x;
		Max.y += d.y;
	}

	public void translateX(float dx) {
		Min.x += dx;
		Max.x += dx;
	}

	public void translateY(float dy) {
		Min.y += dy;
		Max.y += dy;
	}

	public void clipWith(final ImRect r) {
		Min = max(Min, r.Min);
		Max = min(Max, r.Max);
	} // Simple version, may lead to an inverted rectangle, which is fine for Contains/Overlaps test but not for display.

	public void clipWithFull(final ImRect r) {
		Min = clamp(Min, r.Min, r.Max);
		Max = clamp(Max, r.Min, r.Max);
	} // Full version, ensure both points are fully clipped.

	public void floor() {
		Min.x = (int)(Min.x);
		Min.y = (int)(Min.y);
		Max.x = (int)(Max.x);
		Max.y = (int)(Max.y);
	}

	public boolean isInverted() {
		return Min.x > Max.x || Min.y > Max.y;
	}

	private static ImVec2 clamp(ImVec2 v, ImVec2 mn, ImVec2 mx) {
		float x = (v.x < mn.x) ? mn.x : Math.min(v.x, mx.x);
		float y = (v.y < mn.y) ? mn.y : Math.min(v.y, mx.y);
		return new ImVec2(x, y);
	}

	private static ImVec2 max(ImVec2 lhs, ImVec2 rhs) {
		float x = Math.max(lhs.x, rhs.x);
		float y = Math.max(lhs.y, rhs.y);
		return new ImVec2(x, y);
	}

	private static ImVec2 min(ImVec2 lhs, ImVec2 rhs) {
		float x = Math.min(lhs.x, rhs.x);
		float y = Math.min(lhs.y, rhs.y);
		return new ImVec2(x, y);
	}
}
