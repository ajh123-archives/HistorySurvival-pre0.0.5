package net.ddns.minersonline.HistorySurvival.api.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;


public class IncrementalIdMap<K> implements IdMap<K> {
	private static final int NOT_FOUND = -1;
	private static final Object EMPTY_SLOT = null;
	private static final float LOADFACTOR = 0.8F;
	private K[] keys;
	private int[] values;
	private K[] byId;
	private int nextId;
	private int size;

	private IncrementalIdMap(int id) {
		this.keys = (K[])(new Object[id]);
		this.values = new int[id];
		this.byId = (K[])(new Object[id]);
	}

	private IncrementalIdMap(K[] keys, int[] values, K[] ids, int nextId, int size) {
		this.keys = keys;
		this.values = values;
		this.byId = ids;
		this.nextId = nextId;
		this.size = size;
	}

	public static <A> IncrementalIdMap<A> create(int id) {
		return new IncrementalIdMap<>((int)((float)id / LOADFACTOR));
	}

	public int getId(@Nullable K obj) {
		return this.getValue(this.indexOf(obj, this.hash(obj)));
	}

	@Nullable
	public K byId(int id) {
		return (K)(id >= 0 && id < this.byId.length ? this.byId[id] : null);
	}

	private int getValue(int value) {
		return value == NOT_FOUND ? -1 : this.values[value];
	}

	public boolean contains(K dataSerializer) {
		return this.getId(dataSerializer) != -1;
	}

	public boolean contains(int id) {
		return this.byId(id) != null;
	}

	public int add(K dataSerializer) {
		int i = this.nextId();
		this.addMapping(dataSerializer, i);
		return i;
	}

	private int nextId() {
		while(this.nextId < this.byId.length && this.byId[this.nextId] != null) {
			++this.nextId;
		}

		return this.nextId;
	}

	private void grow(int p_13572_) {
		K[] ak = this.keys;
		int[] aint = this.values;
		IncrementalIdMap<K> crudeincrementalintidentityhashbimap = new IncrementalIdMap<>(p_13572_);

		for(int i = 0; i < ak.length; ++i) {
			if (ak[i] != null) {
				crudeincrementalintidentityhashbimap.addMapping(ak[i], aint[i]);
			}
		}

		this.keys = crudeincrementalintidentityhashbimap.keys;
		this.values = crudeincrementalintidentityhashbimap.values;
		this.byId = crudeincrementalintidentityhashbimap.byId;
		this.nextId = crudeincrementalintidentityhashbimap.nextId;
		this.size = crudeincrementalintidentityhashbimap.size;
	}

	public void addMapping(K obj, int id) {
		int i = Math.max(id, this.size + 1);
		if ((float)i >= (float)this.keys.length * 0.8F) {
			int j;
			for(j = this.keys.length << 1; j < id; j <<= 1) {
			}

			this.grow(j);
		}

		int k = this.findEmpty(this.hash(obj));
		this.keys[k] = obj;
		this.values[k] = id;
		this.byId[id] = obj;
		++this.size;
		if (id == this.nextId) {
			++this.nextId;
		}

	}

	private int hash(@Nullable K obj) {
		return (MathHelper.murmurHash3Mixer(System.identityHashCode(obj)) & Integer.MAX_VALUE) % this.keys.length;
	}

	private int indexOf(@Nullable K obj, int key) {
		for(int i = key; i < this.keys.length; ++i) {
			if (this.keys[i] == obj) {
				return i;
			}

			if (this.keys[i] == EMPTY_SLOT) {
				return -1;
			}
		}

		for(int j = 0; j < key; ++j) {
			if (this.keys[j] == obj) {
				return j;
			}

			if (this.keys[j] == EMPTY_SLOT) {
				return -1;
			}
		}

		return -1;
	}

	private int findEmpty(int key) {
		for(int i = key; i < this.keys.length; ++i) {
			if (this.keys[i] == EMPTY_SLOT) {
				return i;
			}
		}

		for(int j = 0; j < key; ++j) {
			if (this.keys[j] == EMPTY_SLOT) {
				return j;
			}
		}

		throw new RuntimeException("Overflowed :(");
	}

	public Iterator<K> iterator() {
		return Iterators.filter(Iterators.forArray(this.byId), Predicates.notNull());
	}

	public void clear() {
		Arrays.fill(this.keys, (Object)null);
		Arrays.fill(this.byId, (Object)null);
		this.nextId = 0;
		this.size = 0;
	}

	public int size() {
		return this.size;
	}

	public IncrementalIdMap<K> copy() {
		return new IncrementalIdMap<>((K[])((Object[])this.keys.clone()), (int[])this.values.clone(), (K[])((Object[])this.byId.clone()), this.nextId, this.size);
	}
}