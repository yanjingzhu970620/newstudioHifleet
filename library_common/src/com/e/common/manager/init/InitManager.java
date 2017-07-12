/**    
 * @{#} InitManager.java Create on 2014年11月13日 下午2:52:53    
 *    
 * @author <a href="mailto:evan0502@qq.com">Evan</a>   
 * @version 1.0    
 * @description
 *	
 */
package com.e.common.manager.init;

import android.content.Context;
import android.graphics.Bitmap;

import com.e.common.manager.net.NetManager;
import com.e.common.manager.net.NetOptions;
import com.e.common.utility.CommonUtility.Utility;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class InitManager {

	private static InitManager manager;

	private InitManager() {
	}

	public static InitManager getInstance() {
		if (Utility.isNull(manager)) {
			manager = new InitManager();
		}
		return manager;
	}

	/**
	 * 初始化ImageLoader配置
	 * 
	 * @param context
	 */
	public void initImageLoaderConfig(Context context) {
		DisplayImageOptions options = getOptions(0);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.defaultDisplayImageOptions(options)
				.memoryCacheSize(10 * 1024 * 1024)
				.diskCacheSize(50 * 1024 * 1024)
				.memoryCache(new WeakMemoryCache())
				.imageDownloader(
						new BaseImageDownloader(context, 30 * 1000, 30 * 1000))
				// connectTimeout
				// (5
				// s),
				// readTimeout
				// (30
				// s)超时时间
				.diskCacheFileCount(100)// 缓存一百张图片
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */

	public DisplayImageOptions getOptions(int roundSize) {
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
				.cacheInMemory(false).cacheOnDisk(true)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565);
		if (roundSize > 0) {
			builder.displayer(new RoundedBitmapDisplayer(roundSize));
		}
		DisplayImageOptions options = builder.build();
		return options;
	}

	public void initNetOption(Context context) {
		NetOptions options = new NetOptions.NetBuild().setCache(true) // 需要缓存
				.setMaxStale(10 * 60).build(); // 默认每个请求缓存时间为10分钟
		NetManager.getInstance(context).initOptions(options);
	}
}
