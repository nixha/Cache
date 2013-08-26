package de.nixha.cache.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nixha.cache.CacheException;

public class ExpirationCacheTest extends SimpleCacheTest {

	protected static long maxAgeMillis = 3000;

	@Before
	public void setUp() throws Exception {
		cache = new ExpirationCache<String>(ExpirationCacheTest.maxAgeMillis);
	}

	@After
	public void tearDown() throws Exception {
		cache.clear();

		cache = null;
	}

	@Test
	public void testGetMaxAgeMillis() {
		Assert.assertEquals(ExpirationCacheTest.maxAgeMillis,
				((ExpirationCache<String>) cache).getMaxAgeMillis());
	}

	@Test
	public void testExpiration() throws CacheException, InterruptedException {
		for (int i = 1; i <= 10; i++) {
			cache.put("id" + i, "value" + i);
		}

		Thread.sleep(ExpirationCacheTest.maxAgeMillis + 1);

		for (int i = 10; 0 < i; i--) {
			Assert.assertNull(cache.get("id" + i));
		}
	}

}
