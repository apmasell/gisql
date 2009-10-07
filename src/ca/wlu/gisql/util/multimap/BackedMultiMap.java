package ca.wlu.gisql.util.multimap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BackedMultiMap implements SimpleMultiMap {

	private static final long LevelMarker = -1;

	private final LongBuffer buffer;

	private final Map<Long, Set<Long>> cache = new HashMap<Long, Set<Long>>();

	public BackedMultiMap(File file) throws IOException {
		FileChannel channel = new FileInputStream(file).getChannel();
		MappedByteBuffer bytebuffer = channel.map(MapMode.READ_ONLY, 0,
				(int) channel.size());
		bytebuffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer = bytebuffer.asLongBuffer();

	}

	public BackedMultiMap(String filename) throws IOException {
		this(new File(filename));
	}

	public boolean contains(long key) {
		return load(key) != null;
	}

	public int distanceBetween(long identfier1, long identfier2) {
		Map<Long, Integer> distances = new HashMap<Long, Integer>();

		load(identfier1);
		load(identfier2);

		Queue<Long> queue = new LinkedList<Long>();
		queue.add(identfier1);
		queue.add(identfier2);
		queue.add(LevelMarker);
		int depth = 1;

		while (queue.size() > 1) {
			long current = queue.poll();
			if (current == LevelMarker) {
				depth++;
				queue.add(LevelMarker);
			} else {
				if (distances.containsKey(current)) {
					return distances.get(current) + depth;
				} else {
					distances.put(current, depth);
				}
			}
		}
		return -1;
	}

	public Set<Long> getAncestors(long key) {
		Set<Long> set = new HashSet<Long>();
		Queue<Long> queue = new LinkedList<Long>();
		queue.add(key);
		while (queue.size() > 0) {
			Set<Long> current = load(queue.poll());
			for (Long value : current) {
				if (!set.contains(value) && !queue.contains(value)) {
					queue.add(value);
				}
			}
			set.addAll(current);
		}
		return set;
	}

	public Set<Long> getParents(long key) {
		return Collections.unmodifiableSet(load(key));
	}

	public boolean isAncestor(long child, long parent) {
		Queue<Long> queue = new LinkedList<Long>();
		queue.add(child);
		while (queue.size() > 0) {
			Set<Long> current = load(queue.poll());
			for (Long value : current) {
				if (value == parent) {
					return true;
				} else {
					queue.add(value);
				}
			}
		}
		return false;

	}

	private Set<Long> load(long key) {
		Set<Long> set = cache.get(key);
		if (set == null) {
			/* Do a binary search to find an matching index. */
			int left = 0;
			int right = buffer.limit() / 2 - 1;
			int index = -1;
			while (left <= right) {
				index = (left + right) / 2;

				long value = buffer.get(index * 2);
				if (value < key) {
					left = index + 1;
				} else if (value > key) {
					right = index - 1;
				} else {
					break;
				}
			}
			if (index == -1) {
				set = Collections.emptySet();
			} else {
				set = new HashSet<Long>();

				int originalindex = index;
				/*
				 * Keys can be multiply defined, so scroll back through the file
				 * and add matching pairs to the set.
				 */
				while (index > 0 && buffer.get(index * 2) == key) {
					set.add(buffer.get(index * 2 + 1));
					index--;
				}

				/* Then scroll forward and gobble up those pairs. */
				index = originalindex + 1;
				while (index < buffer.limit() && buffer.get(index * 2) == key) {
					set.add(buffer.get(index * 2 + 1));
					index++;
				}

				/* Now, load the heirarchy. */
				for (Long parent : set) {
					load(parent);
				}
			}
			cache.put(key, set);
		}
		return set;
	}
}
