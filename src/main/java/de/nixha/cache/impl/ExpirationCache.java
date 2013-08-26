package de.nixha.cache.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.nixha.cache.CacheException;

/**
 * Extends the <code>SimpleCache</code> with an aging policy.
 * 
 * @author Hans Nix
 * 
 * @since 1.0.0
 */
public class ExpirationCache<T> extends SimpleCache<T> {

	protected final Map<String, Long> creation;

	protected long maxAgeMillis;

	public ExpirationCache(long maxAgeMillis) throws CacheException {
		super();

		this.maxAgeMillis = maxAgeMillis < 0 ? 0 : maxAgeMillis;
		this.creation = Collections.synchronizedMap(new HashMap<String, Long>());

		Executors.newScheduledThreadPool(2).scheduleWithFixedDelay(this.checkExpiration(), this.maxAgeMillis / 2,
				this.maxAgeMillis, TimeUnit.MILLISECONDS);
	}

	@Override
	public void put(String id, T item) throws CacheException {
		super.put(id, item);

		this.creation.put(id, System.currentTimeMillis());
	}

	@Override
	public T get(String id) throws CacheException {
		checkExpiration(id);

		return super.get(id);
	}

	@Override
	public T remove(String id) throws CacheException {
		checkExpiration(id);

		T item = super.remove(id);

		if (item != null) {
			this.creation.remove(id);
		}

		return item;
	}

	public Long getCreationMillis(String id) throws CacheException {
		validateID(id);

		return this.creation.get(id);
	}

	public long getMaxAgeMillis() {
		return this.maxAgeMillis;
	}

	private final Runnable checkExpiration() {
		return new Runnable() {

			public void run() {
				for (Map.Entry<String, Long> entry : creation.entrySet()) {
					if (entry.getValue() != null && entry.getValue() + maxAgeMillis < System.currentTimeMillis()) {
						items.remove(entry.getKey());
						creation.remove(entry.getKey());
					}
				}

			}
		};
	}

	/**
	 * 
	 * @param id
	 * @throws CacheException
	 * @since 1.0.0
	 */
	protected void checkExpiration(String id) throws CacheException {
		if (maxAgeMillis == 0) {
			return;
		}

		validateID(id);

		Long cre = this.creation.get(id);
		if (cre != null && cre + maxAgeMillis < System.currentTimeMillis()) {
			super.remove(id);
			this.creation.remove(id);
		}
	}

	public void clear() throws CacheException {
		super.clear();

		this.creation.clear();
	}

	@Override
	public String toString() {
		return ExpirationCache.class.getSimpleName() + " [size=" + size() + ", maxAgeMillis=" + getMaxAgeMillis() + "]";
	}

}
