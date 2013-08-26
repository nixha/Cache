package de.nixha.cache.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.nixha.cache.Cache;
import de.nixha.cache.CacheException;
import de.nixha.cache.CacheHandler;

public class ExtensibleCacheTest extends SimpleCacheTest {

	protected boolean init = false;
	protected boolean finalize = false;
	protected boolean beforePut = false;
	protected boolean afterPut = false;
	protected boolean beforeGet = false;
	protected boolean afterGet = false;
	protected boolean beforeRemove = false;
	protected boolean afterRemove = false;
	protected boolean beforeClear = false;
	protected boolean afterClear = false;

	protected class TestCacheHandler extends CacheHandler<String> {

		@Override
		public void init(Cache<String> cache) throws CacheException {
			super.init(cache);

			init = true;
		}

		@Override
		public void finalize() throws CacheException {
			super.finalize();

			finalize = true;
		}

		@Override
		public void beforePut(String id, String item) throws CacheException {
			beforePut = true;
		}

		@Override
		public void afterPut(String id, String item) throws CacheException {
			afterPut = true;
		}

		@Override
		public void beforeGet(String id) throws CacheException {
			beforeGet = true;
		}

		@Override
		public void afterGet(String id) throws CacheException {
			afterGet = true;
		}

		@Override
		public void beforeRemove(String id) throws CacheException {
			beforeRemove = true;
		}

		@Override
		public void afterRemove(String id) throws CacheException {
			afterRemove = true;
		}

		@Override
		public void beforeClear() throws CacheException {
			beforeClear = true;
		}

		@Override
		public void afterClear() throws CacheException {
			afterClear = true;
		}

	};

	@Before
	public void setUp() throws Exception {
		cache = new ExtensibleCache<String>();
	}

	@After
	public void tearDown() throws Exception {
		if (cache != null) {
			cache.clear();
			cache = null;
		}
	}

	@Test
	public void testHandlerWorksInCache() throws CacheException {
		ExtensibleCache<String> extCache = (ExtensibleCache<String>) cache;

		extCache.addHandler(new TestCacheHandler());

		Assert.assertTrue(init);

		processAllTests(true);
	}

	@Test
	public void testAddHandler() throws CacheException {
		ExtensibleCache<String> extCache = (ExtensibleCache<String>) cache;

		extCache.addHandler(new TestCacheHandler());

		Assert.assertTrue(extCache.getHandlers().size() == 1);

		processAllTests(true);
	}

	@Test
	public void testSetHandlers() throws CacheException {
		ExtensibleCache<String> extCache = (ExtensibleCache<String>) cache;

		CacheHandler<String> ch1 = new TestCacheHandler();
		CacheHandler<String> ch2 = new TestCacheHandler();
		List<CacheHandler<String>> chs = new ArrayList<CacheHandler<String>>();
		chs.add(ch1);
		chs.add(ch2);

		extCache.setHandlers(chs);

		Assert.assertTrue(extCache.getHandlers().size() == chs.size());
	}

	public void testRemoveHandler() throws CacheException {
		ExtensibleCache<String> extCache = (ExtensibleCache<String>) cache;

		CacheHandler<String> ch = new TestCacheHandler();
		extCache.addHandler(ch);

		Assert.assertTrue(init);

		extCache.removeHandler(ch);
		Assert.assertTrue(finalize);
	}

	@Test
	public void testSetHandlersNull() throws CacheException {
		ExtensibleCache<String> extCache = (ExtensibleCache<String>) cache;

		extCache.setHandlers(null);

		Assert.assertTrue(extCache.getHandlers().isEmpty());
	}

	protected void processAllTests(boolean condition) throws CacheException {
		if (condition) {
			super.testClear();
			Assert.assertTrue(beforeClear);
			Assert.assertTrue(afterClear);

			super.testPutAndGet();
			Assert.assertTrue(beforePut);
			Assert.assertTrue(afterPut);
			Assert.assertTrue(beforeGet);
			Assert.assertTrue(afterGet);

			super.testRemove();
			Assert.assertTrue(beforeRemove);
			Assert.assertTrue(afterRemove);
		} else {
			super.testClear();
			Assert.assertFalse(beforeClear);
			Assert.assertFalse(afterClear);

			super.testPutAndGet();
			Assert.assertFalse(beforePut);
			Assert.assertFalse(afterPut);
			Assert.assertFalse(beforeGet);
			Assert.assertFalse(afterGet);

			super.testRemove();
			Assert.assertFalse(beforeRemove);
			Assert.assertFalse(afterRemove);
		}
	}

}
