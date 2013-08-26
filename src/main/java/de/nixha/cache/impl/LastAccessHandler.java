package de.nixha.cache.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.nixha.cache.Cache;
import de.nixha.cache.CacheException;
import de.nixha.cache.CacheHandler;

/**
 * A handler providing free space in cache removing items ranked by their last
 * access when cache runs out of free space.
 * 
 * TODO-1.0.x: use any available logger (maybe using commons logging)
 * 
 * @author Hans Nix
 * 
 * @param <T>
 * @since 1.0.0
 */
public class LastAccessHandler<T> extends CacheHandler<T> {

	protected float reqFreePercent;
	protected final ExecutorService threads;

	protected boolean freeing = false;

	protected List<String> order;

	/**
	 * 
	 * @param reqFreePercent
	 * @since 1.0.0
	 */
	public LastAccessHandler(float reqFreePercent) {
		if (1f <= reqFreePercent || reqFreePercent == 0f) {
			throw new IllegalArgumentException("reqFreePercent=" + reqFreePercent);
		}

		this.reqFreePercent = reqFreePercent;
		this.threads = Executors.newFixedThreadPool(128);
	}

	@Override
	public void init(Cache<T> cache) throws CacheException {
		super.init(cache);

		order = Collections.synchronizedList(new ArrayList<String>());
	}

	protected Runnable freeRequired() {
		return new Runnable() {

			public void run() {
				if (!freeing && order != null && cache.getMaxSize() - getRequiredFreeSize() <= cache.size()) {
					freeing = true;
					String remKey;
					while (!order.isEmpty() && cache.getFreeSize() < getRequiredFreeSize()) {
						try {
							// remove oldest item (first in order)
							remKey = order.get(0);
							if (remKey != null) {
								cache.remove(remKey);
								order.remove(remKey);
							}
						} catch (CacheException c) {
							System.out.println("c.message=" + c.getMessage());
						} catch (NoSuchElementException n) {
							// empty in action
							System.out.println("n.message=" + n.getMessage());
						}
					}

					freeing = false;
				}
			}

		};
	}

	/**
	 * 
	 * @param percent
	 * @return
	 * @since 1.0.0
	 */
	public boolean isFull() {
		return isFull(1.0f - reqFreePercent);
	}

	/**
	 * 
	 * @param percent
	 * @return
	 * @since 1.0.0
	 */
	protected boolean isFull(float percent) {
		int fullSize = (int) (cache.getMaxSize() * percent);

		return fullSize <= cache.size();
	}

	/**
	 * 
	 * @return
	 * @since 1.0.0
	 */
	public float getRequiredFreePercent() {
		return reqFreePercent;
	}

	/**
	 * Computes how much items should be free in cache.
	 * 
	 * @return
	 * @since 1.0.0
	 */
	public int getRequiredFreeSize() {
		int maxSize = cache.getMaxSize();
		int reqFree = (int) (maxSize * reqFreePercent);

		return reqFree;
	}

	@Override
	public void beforePut(String id, T item) throws CacheException {
		threads.execute(freeRequired());
	}

	@Override
	public void afterPut(String id, T item) throws CacheException {
		order.remove(id);
		order.add(id);
	}

	@Override
	public void beforeGet(String id) throws CacheException {
		if (order.remove(id)) {
			order.add(id);
		}
	}

	@Override
	public void beforeRemove(String id) throws CacheException {
		order.remove(id);
	}

	@Override
	public void afterClear() {
		order.clear();
	}

	@Override
	public void finalize() throws CacheException {
		super.finalize();

		order = null;
	}

}
