package de.nixha.cache;

/**
 * Abstract handler to use for custom cache behavior.
 * 
 * @author Hans Nix
 * 
 * @param <T>
 * @since 1.0.0
 */
public abstract class CacheHandler<T> {

	protected Cache<T> cache = null;

	public void init(Cache<T> cache) throws CacheException {
		this.cache = cache;
	}

	protected Cache<T> getCache() {
		return cache;
	}

	public void beforePut(String id, T item) throws CacheException {
		;
	}

	public void afterPut(String id, T item) throws CacheException {
		;
	}

	public void beforeGet(String id) throws CacheException {
		;
	}

	public void afterGet(String id) throws CacheException {
		;
	}

	public void beforeRemove(String id) throws CacheException {
		;
	}

	public void afterRemove(String id) throws CacheException {
		;
	}

	public void beforeClear() throws CacheException {
		;
	}

	public void afterClear() throws CacheException {
		;
	}

	public void finalize() throws CacheException {
		cache = null;
	}

}
