package de.nixha.cache;

/**
 * 
 * @author Hans Nix
 * 
 * @param <T>
 * @since 1.0.0
 */
public interface Cache<T> {

	public abstract void put(String id, T o) throws CacheException;

	public abstract T get(String id) throws CacheException;

	public abstract T remove(String id) throws CacheException;

	public abstract int getMaxSize();

	public abstract int getFreeSize();

	public abstract void setMaxSize(int maxSize);

	public abstract int size();

	public abstract void clear() throws CacheException;

}
