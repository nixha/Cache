package de.nixha.cache.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nixha.cache.CacheException;

public class LastAccessHandlerTest {

	protected ExtensibleCache<String> cache = null;
	protected final static int maxSize = 100;

	@Before
	public void setUp() throws Exception {
		cache = new ExtensibleCache<String>();
		cache.setMaxSize(maxSize);
	}

	@After
	public void tearDown() throws Exception {
		if (cache != null) {
			cache.clear();
			cache = null;
		}
	}

	@Test
	public void testRequiredFreePercent() {
		float rfp = .5f;
		LastAccessHandler<String> lah = new LastAccessHandler<String>(rfp);
		Assert.assertEquals(rfp, lah.getRequiredFreePercent(), .0);
	}

	@Test
	public void testRequiredFreePercentTooLarge() {
		float rfp = 1.0f;
		try {
			new LastAccessHandler<String>(rfp);
			Assert.fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException i) {
			;
		}
	}

	@Test
	public void testRequiredFreePercentTooLess() {
		float rfp = 0.0f;
		try {
			new LastAccessHandler<String>(rfp);
			Assert.fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException i) {
			;
		}

	}

	@Test
	public void testRequiredFreeSize50Percent() throws CacheException {
		int maxSize = 100;
		int reqSize = 50;
		float rfp = .5f;

		cache.setMaxSize(maxSize);

		LastAccessHandler<String> handler = new LastAccessHandler<String>(rfp);
		cache.addHandler(handler);

		Assert.assertEquals(reqSize, handler.getRequiredFreeSize());
	}

	@Test
	public void testRequiredFreeSize20Percent() throws CacheException {
		int maxSize = 150;
		int reqSize = 30;
		float rfp = .20f;

		cache.setMaxSize(maxSize);

		LastAccessHandler<String> handler = new LastAccessHandler<String>(rfp);
		cache.addHandler(handler);

		Assert.assertEquals(reqSize, handler.getRequiredFreeSize());
	}

	@Test
	public void testIsFull100By30Percent() throws CacheException {
		int maxSize = 100;
		float rfp = .3f;
		int lim = (int) (maxSize * (1.0f - rfp));

		cache.setMaxSize(maxSize);

		LastAccessHandler<String> handler = new LastAccessHandler<String>(rfp);
		cache.addHandler(handler);

		for (int i = 1; i <= maxSize; i++) {
			cache.put("id" + i, "item" + i);

			Assert.assertTrue("cache.size=" + cache.size() + ", handler.isFull=" + handler.isFull(),
					lim <= cache.size() == handler.isFull());
		}
	}

	@Test
	public void testIsFull150By20Percent() throws CacheException {
		int maxSize = 150;
		float rfp = .2f;
		int lim = (int) (maxSize * (1.0f - rfp));

		cache.setMaxSize(maxSize);

		LastAccessHandler<String> handler = new LastAccessHandler<String>(rfp);
		cache.addHandler(handler);

		for (int i = 1; i <= maxSize; i++) {
			cache.put("id" + i, "item" + i);

			Assert.assertTrue("cache.size=" + cache.size() + ", handler.isFull=" + handler.isFull(),
					lim <= cache.size() == handler.isFull());
		}
	}

	@Test
	public void testFreeingSpaceInCache() throws CacheException, InterruptedException {
		int maxSize = 10;
		float rfp = .2f;

		cache.setMaxSize(maxSize);

		LastAccessHandler<String> handler = new LastAccessHandler<String>(rfp);
		cache.addHandler(handler);

		// fill to the top
		for (int i = 1; i <= maxSize; i++) {
			cache.put("id" + i, "item" + i);
		}

		// overflow cache, check required free size <= free size
		cache.put("last", "too much");
		// asynchronous freeing thread might need some time to perform
		Thread.sleep(100);

		Assert.assertTrue(handler.getRequiredFreeSize() <= cache.getFreeSize());
	}
}
