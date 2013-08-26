package de.nixha.cache.impl;

import java.util.ArrayList;
import java.util.List;

import de.nixha.cache.CacheException;
import de.nixha.cache.CacheHandler;

/**
 * Provides a simple cache extensible by <code>CacheHandler</code>s, being
 * called for any method called within this cache.
 * 
 * @author Hans Nix
 * 
 * @param <T>
 * @since 1.0.0
 */
public class ExtensibleCache<T> extends SimpleCache<T> {

	protected List<CacheHandler<T>> handlers;

	public ExtensibleCache() throws CacheException {
		this(null);
	}

	public ExtensibleCache(List<CacheHandler<T>> handlers) throws CacheException {
		super();

		setHandlers(handlers);
	}

	/**
	 * Returns a copy of current handler list.
	 * 
	 * @return
	 * @since 1.0.0
	 */
	public List<CacheHandler<T>> getHandlers() {
		List<CacheHandler<T>> copy = new ArrayList<CacheHandler<T>>();

		for (CacheHandler<T> handler : this.handlers) {
			copy.add(handler);
		}

		return copy;
	}

	/**
	 * Replaces all handlers by given list.
	 * 
	 * @param handlers
	 * @throws CacheException
	 * @since 1.0.0
	 */
	public void setHandlers(List<CacheHandler<T>> handlers) throws CacheException {
		if (handlers == null) {
			handlers = new ArrayList<CacheHandler<T>>();
		}

		for (CacheHandler<T> handler : handlers) {
			handler.init(this);
		}

		this.handlers = handlers;
	}

	/**
	 * 
	 * @param handler
	 * @throws CacheException
	 * @since 1.0.0
	 */
	public void addHandler(CacheHandler<T> handler) throws CacheException {
		handler.init(this);

		this.handlers.add(handler);
	}

	/**
	 * 
	 * @param handler
	 * @return
	 * @throws CacheException
	 * @since 1.0.0
	 */
	public boolean removeHandler(CacheHandler<T> handler) throws CacheException {
		handler.finalize();

		return this.handlers.remove(handler);
	}

	@Override
	public void put(String id, T item) throws CacheException {
		validateID(id);
		validateItem(item);

		for (CacheHandler<T> h : this.handlers) {
			h.beforePut(id, item);
		}

		super.put(id, item);

		for (CacheHandler<T> h : this.handlers) {
			h.afterPut(id, item);
		}
	}

	@Override
	public T get(String id) throws CacheException {
		validateID(id);

		for (CacheHandler<T> h : this.handlers) {
			h.beforeGet(id);
		}

		T item = super.get(id);

		for (CacheHandler<T> h : this.handlers) {
			h.afterGet(id);
		}

		return item;
	}

	@Override
	public void clear() throws CacheException {
		try {
			for (CacheHandler<T> h : this.handlers) {
				h.beforeClear();
			}

			super.clear();

			for (CacheHandler<T> h : this.handlers) {
				h.afterClear();
			}
		} catch (CacheException c) {
			throw new CacheException("Failed to process clear", c);
		}
	}

	@Override
	public T remove(String id) throws CacheException {
		validateID(id);

		for (CacheHandler<T> h : this.handlers) {
			h.beforeRemove(id);
		}

		T item = super.remove(id);

		for (CacheHandler<T> h : this.handlers) {
			h.afterRemove(id);
		}

		return item;
	}

	@Override
	public String toString() {
		return ExtensibleCache.class.getSimpleName() + " [size=" + size() + ",maxSize=" + getMaxSize() + ",handlers.size="
				+ handlers.size() + "]";
	}

}
