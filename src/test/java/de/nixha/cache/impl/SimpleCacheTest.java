package de.nixha.cache.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nixha.cache.Cache;
import de.nixha.cache.CacheException;

public class SimpleCacheTest {

	protected Cache<String> cache = null;
	protected int maxSize = 10;

	@Before
	public void setUp() throws Exception {
		cache = new SimpleCache<String>(maxSize);
	}

	@After
	public void tearDown() throws Exception {
		cache.clear();

		cache = null;
	}

	@Test
	public void testPutAndGet() throws CacheException {
		for (int i = 1; i <= 10; i++) {
			cache.put("id" + i, "value" + i);
		}

		for (int i = 10; 0 < i; i--) {
			Assert.assertEquals("value" + i, cache.get("id" + i));
		}
	}

	@Test
	public void testClear() throws CacheException {
		for (int i = 1; i <= 10; i++) {
			cache.put("id" + i, "value" + i);
		}

		cache.clear();

		for (int i = 10; 0 < i; i--) {
			Assert.assertNull(cache.get("id" + i));
		}

		Assert.assertEquals(0, cache.size());
	}

	@Test
	public void testRemove() throws CacheException {
		for (int i = 1; i <= 10; i++) {
			cache.put("id" + i, "value" + i);
		}

		for (int i = 10; 0 < i; i--) {
			Assert.assertEquals("value" + i, cache.remove("id" + i));
			Assert.assertNull(cache.get("id" + i));
		}
	}

	@Test
	public void testSize() throws CacheException {
		Assert.assertEquals(0, cache.size());

		for (int i = 0; i < 10; i++) {
			cache.put("id" + i, "value" + i);
		}

		Assert.assertEquals(10, cache.size());
	}

	@Test
	public void testMaxSize() throws CacheException {
		int maxSize = cache.getMaxSize();

		for (int i = 1; i <= maxSize; i++) {
			cache.put("id" + i, "value" + i);

			// System.out.println("i=" + i + ", cache.size=" + cache.size());
			Assert.assertTrue(i == cache.size());
		}

		for (int i = maxSize + 1; i <= maxSize + 11; i++) {
			cache.put("id" + i, "value" + i);

			// System.out.println("i=" + i + ", cache.size=" + cache.size());
			Assert.assertTrue(maxSize == cache.size());
		}
	}

}
