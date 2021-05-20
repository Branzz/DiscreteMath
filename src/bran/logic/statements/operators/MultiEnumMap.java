package bran.logic.statements.operators;

/*
 * not fully mine; based off of java.util.EnumMap
 */
public class MultiEnumMap<K extends Enum<K>, V> {

	// Set<K>        keySet;
	// Collection<V> values;
	private final Class<K> keyType;
	private K[] keyUniverse;
	private Object[][] vals;
	private int size = 0;

	public MultiEnumMap(Class<K> keyType, K[] values) {
		int length = values.length;
		this.keyType = keyType;
		keyUniverse = values;
		vals = new Object[keyUniverse.length][keyUniverse.length];
	}

	// Query Operations

	public int size() {
		return size;
	}

	// public boolean containsValue(Object value) {
	// 	for (Object[] valRow : vals)
	// 		for (Object val : valRow)
	// 			if (value.equals(val))
	// 				return true;
	// 	return false;
	// }
	//
	// public boolean containsKey(Object key) {
	// 	return isValidKey(key) && vals[((Enum<?>) key).ordinal()] != null;
	// }
	//
	// private boolean containsMapping(Object key1, Object key2, Object value) {
	// 	return isValidKey(key1) && isValidKey(key2) && value.equals(vals[((Enum<?>) key1).ordinal()][((Enum<?>) key2).ordinal()]);
	// }

	public V get(Object key1, Object key2) {
		return (isValidKey(key1) && isValidKey(key2)) ? (V) vals[((Enum<?>) key1).ordinal()][((Enum<?>) key2).ordinal()] : null;
	}

	// Modification Operations

	public V put(K key1, K key2, V value) {
		typeCheck(key1);
		typeCheck(key2);
		int index1 = key1.ordinal(), index2 = key2.ordinal();
		Object oldValue = vals[index1][index2];
		vals[index1][index2] = value;
		if (oldValue == null)
			size++;
		return (V) oldValue;
	}

	// public V remove(Object key1, Object key2) {
	// 	if (!isValidKey(key1) || !isValidKey(key2))
	// 		return null;
	// 	int index = ((Enum<?>) key1).ordinal();
	// 	Object oldValue = vals[index];
	// 	vals[index] = null;
	// 	if (oldValue != null)
	// 		size--;
	// 	return unmaskNull(oldValue);
	// }

	// private boolean removeMapping(Object key, Object value) {
	// 	if (!isValidKey(key))
	// 		return false;
	// 	int index = ((Enum<?>) key).ordinal();
	// 	if (maskNull(value).equals(vals[index])) {
	// 		vals[index] = null;
	// 		size--;
	// 		return true;
	// 	}
	// 	return false;
	// }

	private boolean isValidKey(Object key) {
		if (key == null)
			return false;

		// Cheaper than instanceof Enum followed by getDeclaringClass
		Class<?> keyClass = key.getClass();
		return keyClass == keyType || keyClass.getSuperclass() == keyType;
	}

	// // Bulk Operations
	//
	// public void clear() {
	// 	Arrays.fill(vals, null);
	// 	size = 0;
	// }
	//
	// // Views
	//
	// private transient Set<Map.Entry<K, V>> entrySet;
	//
	// public Set<K> keySet() {
	// 	Set<K> ks = keySet;
	// 	if (ks == null) {
	// 		ks = new KeySet();
	// 		keySet = ks;
	// 	}
	// 	return ks;
	// }
	//
	// private class KeySet extends AbstractSet<K> {
	//
	// 	public Iterator<K> iterator() {
	// 		return new KeyIterator();
	// 	}
	//
	// 	public int size() {
	// 		return size;
	// 	}
	//
	// 	public boolean contains(Object o) {
	// 		return containsKey(o);
	// 	}
	//
	// 	public boolean remove(Object o) {
	// 		int oldSize = size;
	// 		MultiEnumMap.this.remove(o);
	// 		return size != oldSize;
	// 	}
	//
	// 	public void clear() {
	// 		MultiEnumMap.this.clear();
	// 	}
	//
	// }
	//
	// /**
	//  * Returns a {@link Collection} view of the values contained in this map.
	//  * The returned collection obeys the general contract outlined in
	//  * {@link Map#values()}.  The collection's iterator will return the
	//  * values in the order their corresponding keys appear in map,
	//  * which is their natural order (the order in which the enum constants
	//  * are declared).
	//  *
	//  * @return a collection view of the values contained in this map
	//  */
	// public Collection<V> values() {
	// 	Collection<V> vs = values;
	// 	if (vs == null) {
	// 		vs = new Values();
	// 		values = vs;
	// 	}
	// 	return vs;
	// }
	//
	// private class Values extends AbstractCollection<V> {
	//
	// 	public Iterator<V> iterator() {
	// 		return new ValueIterator();
	// 	}
	//
	// 	public int size() {
	// 		return size;
	// 	}
	//
	// 	public boolean contains(Object o) {
	// 		return containsValue(o);
	// 	}
	//
	// 	public boolean remove(Object o) {
	// 		o = maskNull(o);
	//
	// 		for (int i = 0; i < vals.length; i++) {
	// 			if (o.equals(vals[i])) {
	// 				vals[i] = null;
	// 				size--;
	// 				return true;
	// 			}
	// 		}
	// 		return false;
	// 	}
	//
	// 	public void clear() {
	// 		MultiEnumMap.this.clear();
	// 	}
	//
	// }
	//
	// /**
	//  * Returns a {@link Set} view of the mappings contained in this map.
	//  * The returned set obeys the general contract outlined in
	//  * {@link Map#keySet()}.  The set's iterator will return the
	//  * mappings in the order their keys appear in map, which is their
	//  * natural order (the order in which the enum constants are declared).
	//  *
	//  * @return a set view of the mappings contained in this enum map
	//  */
	// public Set<Map.Entry<K, V>> entrySet() {
	// 	Set<Map.Entry<K, V>> es = entrySet;
	// 	if (es != null)
	// 		return es;
	// 	else
	// 		return entrySet = new EntrySet();
	// }
	//
	// private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
	//
	// 	public Iterator<Map.Entry<K, V>> iterator() {
	// 		return new EntryIterator();
	// 	}
	//
	// 	public boolean contains(Object o) {
	// 		if (!(o instanceof Map.Entry))
	// 			return false;
	// 		Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
	// 		return containsMapping(entry.getKey(), entry.getValue());
	// 	}
	//
	// 	public boolean remove(Object o) {
	// 		if (!(o instanceof Map.Entry))
	// 			return false;
	// 		Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
	// 		return removeMapping(entry.getKey(), entry.getValue());
	// 	}
	//
	// 	public int size() {
	// 		return size;
	// 	}
	//
	// 	public void clear() {
	// 		MultiEnumMap.this.clear();
	// 	}
	//
	// 	public Object[] toArray() {
	// 		return fillEntryArray(new Object[size]);
	// 	}
	//
	// 	@SuppressWarnings("unchecked")
	// 	public <T> T[] toArray(T[] a) {
	// 		int size = size();
	// 		if (a.length < size)
	// 			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
	// 														   .getComponentType(), size);
	// 		if (a.length > size)
	// 			a[size] = null;
	// 		return (T[]) fillEntryArray(a);
	// 	}
	//
	// 	private Object[] fillEntryArray(Object[] a) {
	// 		int j = 0;
	// 		for (int i = 0; i < vals.length; i++)
	// 			if (vals[i] != null)
	// 				a[j++] = new AbstractMap.SimpleEntry<>(keyUniverse[i], unmaskNull(vals[i]));
	// 		return a;
	// 	}
	//
	// }
	//
	// private abstract class EnumMapIterator<T> implements Iterator<T> {
	//
	// 	// Lower bound on index of next element to return
	// 	int index = 0;
	//
	// 	// Index of last returned element, or -1 if none
	// 	int lastReturnedIndex = -1;
	//
	// 	public boolean hasNext() {
	// 		while (index < vals.length && vals[index] == null)
	// 			index++;
	// 		return index != vals.length;
	// 	}
	//
	// 	public void remove() {
	// 		checkLastReturnedIndex();
	//
	// 		if (vals[lastReturnedIndex] != null) {
	// 			vals[lastReturnedIndex] = null;
	// 			size--;
	// 		}
	// 		lastReturnedIndex = -1;
	// 	}
	//
	// 	private void checkLastReturnedIndex() {
	// 		if (lastReturnedIndex < 0)
	// 			throw new IllegalStateException();
	// 	}
	//
	// }
	//
	// private class KeyIterator extends EnumMapIterator<K> {
	//
	// 	public K next() {
	// 		if (!hasNext())
	// 			throw new NoSuchElementException();
	// 		lastReturnedIndex = index++;
	// 		return keyUniverse[lastReturnedIndex];
	// 	}
	//
	// }
	//
	// private class ValueIterator extends EnumMapIterator<V> {
	//
	// 	public V next() {
	// 		if (!hasNext())
	// 			throw new NoSuchElementException();
	// 		lastReturnedIndex = index++;
	// 		return unmaskNull(vals[lastReturnedIndex]);
	// 	}
	//
	// }
	//
	// private class EntryIterator extends EnumMapIterator<Map.Entry<K, V>> {
	//
	// 	private EntryIterator.Entry lastReturnedEntry;
	//
	// 	public Map.Entry<K, V> next() {
	// 		if (!hasNext())
	// 			throw new NoSuchElementException();
	// 		lastReturnedEntry = new EntryIterator.Entry(index++);
	// 		return lastReturnedEntry;
	// 	}
	//
	// 	public void remove() {
	// 		lastReturnedIndex = ((null == lastReturnedEntry) ? -1 : lastReturnedEntry.index);
	// 		super.remove();
	// 		lastReturnedEntry.index = lastReturnedIndex;
	// 		lastReturnedEntry = null;
	// 	}
	//
	// 	private class Entry implements Map.Entry<K, V> {
	//
	// 		private int index;
	//
	// 		private Entry(int index) {
	// 			this.index = index;
	// 		}
	//
	// 		public K getKey() {
	// 			checkIndexForEntryUse();
	// 			return keyUniverse[index];
	// 		}
	//
	// 		public V getValue() {
	// 			checkIndexForEntryUse();
	// 			return unmaskNull(vals[index]);
	// 		}
	//
	// 		public V setValue(V value) {
	// 			checkIndexForEntryUse();
	// 			V oldValue = unmaskNull(vals[index]);
	// 			vals[index] = maskNull(value);
	// 			return oldValue;
	// 		}
	//
	// 		public boolean equals(Object o) {
	// 			if (index < 0)
	// 				return o == this;
	//
	// 			if (!(o instanceof Map.Entry))
	// 				return false;
	//
	// 			Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
	// 			V ourValue = unmaskNull(vals[index]);
	// 			Object hisValue = e.getValue();
	// 			return (e.getKey() == keyUniverse[index] && (Objects.equals(ourValue, hisValue)));
	// 		}
	//
	// 		public int hashCode() {
	// 			if (index < 0)
	// 				return super.hashCode();
	//
	// 			return entryHashCode(index);
	// 		}
	//
	// 		public String toString() {
	// 			if (index < 0)
	// 				return super.toString();
	//
	// 			return keyUniverse[index] + "=" + unmaskNull(vals[index]);
	// 		}
	//
	// 		private void checkIndexForEntryUse() {
	// 			if (index < 0)
	// 				throw new IllegalStateException("Entry was removed");
	// 		}
	//
	// 	}
	//
	// }
	//
	// // Comparison and hashing
	//
	// /**
	//  * Compares the specified object with this map for equality.  Returns
	//  * {@code true} if the given object is also a map and the two maps
	//  * represent the same mappings, as specified in the {@link
	//  * Map#equals(Object)} contract.
	//  *
	//  * @param o the object to be compared for equality with this map
	//  * @return {@code true} if the specified object is equal to this map
	//  */
	// public boolean equals(Object o) {
	// 	if (this == o)
	// 		return true;
	// 	if (o instanceof EnumMap)
	// 		return equals((EnumMap<?, ?>) o);
	// 	if (!(o instanceof Map))
	// 		return false;
	//
	// 	Map<?, ?> m = (Map<?, ?>) o;
	// 	if (size != m.size())
	// 		return false;
	//
	// 	for (int i = 0; i < keyUniverse.length; i++) {
	// 		if (null != vals[i]) {
	// 			K key = keyUniverse[i];
	// 			V value = unmaskNull(vals[i]);
	// 			if (null == value) {
	// 				if (!((null == m.get(key)) && m.containsKey(key)))
	// 					return false;
	// 			} else {
	// 				if (!value.equals(m.get(key)))
	// 					return false;
	// 			}
	// 		}
	// 	}
	//
	// 	return true;
	// }
	//
	// private boolean equals(MultiEnumMap<?, ?> em) {
	// 	if (em.size != size)
	// 		return false;
	//
	// 	if (em.keyType != keyType)
	// 		return size == 0;
	//
	// 	// Key types match, compare each value
	// 	for (int i = 0; i < keyUniverse.length; i++) {
	// 		Object ourValue = vals[i];
	// 		Object hisValue = em.vals[i];
	// 		if (hisValue != ourValue && (hisValue == null || !hisValue.equals(ourValue)))
	// 			return false;
	// 	}
	// 	return true;
	// }
	//
	// /**
	//  * Returns the hash code value for this map.  The hash code of a map is
	//  * defined to be the sum of the hash codes of each entry in the map.
	//  */
	// public int hashCode() {
	// 	int h = 0;
	//
	// 	for (int i = 0; i < keyUniverse.length; i++) {
	// 		if (null != vals[i]) {
	// 			h += entryHashCode(i);
	// 		}
	// 	}
	//
	// 	return h;
	// }
	//
	// private int entryHashCode(int index) {
	// 	return (keyUniverse[index].hashCode() ^ vals[index].hashCode());
	// }
	//
	// @SuppressWarnings("unchecked")
	// public MultiEnumMap<K, V> clone() {
	// 	MultiEnumMap<K, V> result = null;
	// 	try {
	// 		result = (MultiEnumMap<K, V>) super.clone();
	// 	} catch (CloneNotSupportedException e) {
	// 		throw new AssertionError();
	// 	}
	// 	result.vals = result.vals.clone();
	// 	result.entrySet = null;
	// 	return result;
	// }

	private void typeCheck(K key) {
		Class<?> keyClass = key.getClass();
		if (keyClass != keyType && keyClass.getSuperclass() != keyType)
			throw new ClassCastException(keyClass + " != " + keyType);
	}

	// private static <K extends Enum<K>> K[] getKeyUniverse(Class<K> keyType) {
	// 	return SharedSecrets.getJavaLangAccess()
	// 						.getEnumConstantsShared(keyType);
	// }


}
