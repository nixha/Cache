package de.nixha.cache.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.nixha.cache.Cache;
import de.nixha.cache.CacheException;

/**
 * Implements a simple cache providing all basic methods to add, get and remove
 * items.
 * 
 * @author Hans Nix
 * 
 * @param <T>
 * @since 1.0.0
 */
public class SimpleCache<T> implements Cache<T> {

	protected final Map<String, T> items;

	protected int maxSize;

	public SimpleCache() {
		this(1000);
	}

	public SimpleCache(int maxItems) {
		this.items = Collections.synchronizedMap(new HashMap<String, T>());

		setMaxSize(maxItems);
	}

	public void put(String id, T item) throws CacheException {
		validateID(id);
		validateItem(item);

		this.items.put(id, item);

		assertMaxSize();
	}

	/**
	 * 
	 * @param id
	 * @return item or <code>null</code> if item is not in cache
	 * @throws CacheException
	 * @since 1.0.0
	 */
	public T get(String id) throws CacheException {
		validateID(id);

		T item = items.get(id);

		return item;
	}

	/**
	 * 
	 * @param id
	 * @throws CacheException
	 * @since 1.0.0
	 */
	protected void validateID(String id) throws CacheException {
		if (id == null || "".equals(id)) {
			throw new CacheException("ID must be set: id=" + id);
		}
	}

	/**
	 * 
	 * @param item
	 * @throws CacheException
	 * @since 1.0.0
	 */
	protected void validateItem(T item) throws CacheException {
		if (item == null) {
			throw new CacheException("Item must be set: item=" + item);
		}
	}

	/**
	 * 
	 * @since 1.0.0
	 */
	protected void assertMaxSize() {
		while (maxSize < size()) {
			items.remove(items.keySet().iterator().next());
		}
	}

	/**
	 * Clears all cached information.
	 * 
	 * @throws CacheException
	 * 
	 * @since 1.0.0
	 */
	public void clear() throws CacheException {
		this.items.clear();
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws CacheException
	 * @since 1.0.0
	 */
	public T remove(String id) throws CacheException {
		validateID(id);

		T item = items.remove(id);

		return item;
	}

	/**
	 * 
	 * @return
	 * @throws CacheException
	 * @since 1.0.0
	 */
	public int size() {
		return this.items.size();
	}

	@Override
	public String toString() {
		return SimpleCache.class.getSimpleName() + " [size=" + size() + ",maxSize=" + getMaxSize() + "]";
	}

	/**
	 * 
	 * @return
	 * @since 1.0.0
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * 
	 * @return
	 * @since 1.0.0
	 */
	public int getFreeSize() {
		return maxSize - size();
	}

	/**
	 * 
	 * @param maxSize
	 * @since 1.0.0
	 */
	public void setMaxSize(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("Maximum size <= 0: maxSize=" + maxSize);
		}

		this.maxSize = maxSize;

		assertMaxSize();
	}

}
