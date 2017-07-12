package com.e.common.manager.net;

/**
 * @{# NetOptions.java Create on 2015年1月24日 下午5:23:33
 * 
 * @author <a href="mailto:evan0502@qq.com">Evan</a>
 * @version 1.0
 * @description
 *
 */
public class NetOptions {

	private int mMaxStale = 0;
	private boolean mCache = false;

	public NetOptions(NetBuild build) {
		this.mCache = build.mCache;
		this.mMaxStale = build.mMaxStale;
	}
	
	/**
	 * @return the mMaxStale
	 */
	public int getmMaxStale() {
		return mMaxStale;
	}

	/**
	 * @param mMaxStale the mMaxStale to set
	 */
	public void setmMaxStale(int mMaxStale) {
		this.mMaxStale = mMaxStale;
	}

	/**
	 * @return the mCache
	 */
	public boolean ismCache() {
		return mCache;
	}

	/**
	 * @param mCache the mCache to set
	 */
	public void setmCache(boolean mCache) {
		this.mCache = mCache;
	}

	public static class NetBuild {

		private int mMaxStale = 0;
		private boolean mCache = false;

		public NetBuild setMaxStale(int maxStale) {
			this.mMaxStale = maxStale;
			return this;
		}

		public NetBuild setCache(boolean isCache) {
			this.mCache = isCache;
			return this;
		}

		public NetOptions build() {
			return new NetOptions(this);
		}
	}
}
