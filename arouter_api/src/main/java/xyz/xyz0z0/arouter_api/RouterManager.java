package xyz.xyz0z0.arouter_api;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import xyz.xyz0z0.arouter_annotations.bean.RouterBean;

public class RouterManager {

    private final static String FILE_GROUP_NAME = "ARouter$$Group$$";
    private static RouterManager instance;
    private final LruCache<String, ARouterGroup> groupLruCache;
    private final LruCache<String, ARouterPath> pathLruCache;
    private String group;
    private String path;

    private RouterManager() {
        groupLruCache = new LruCache<>(100);
        pathLruCache = new LruCache<>(100);
    }

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("path 不符合规定");
        }
        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("path 不符合规定");
        }
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("path 不符合规定");
        }

        this.path = path;
        this.group = finalGroup;

        return new BundleManager();

    }

    public Object navigation(Context context, BundleManager bundleManager) {
        String groupClassName = context.getPackageName() + "." + FILE_GROUP_NAME + group;
        Log.e("cxg", "navigation:groupClassName = " + groupClassName);

        try {
            ARouterGroup loadGroup = groupLruCache.get(group);
            if (loadGroup == null) {
                Class<?> aClass = Class.forName(groupClassName);
                loadGroup = (ARouterGroup) aClass.newInstance();
                groupLruCache.put(group, loadGroup);
            }
            if (loadGroup.getGroupMap().isEmpty()) {
                throw new RuntimeException("路由表 group 报废了...");
            }

            ARouterPath loadPath = pathLruCache.get(path);
            if (loadPath == null) {
                Class<? extends ARouterPath> clazz = loadGroup.getGroupMap().get(group);
                loadPath = clazz.newInstance();
                pathLruCache.put(path, loadPath);
            }
            if (loadPath != null) {
                if (loadPath.getPathMap().isEmpty()) {
                    throw new RuntimeException("路由表 Path 报废了");
                }
                RouterBean routerBean = loadPath.getPathMap().get(path);
                if (routerBean != null) {
                    if (routerBean.getTypeEnum() == RouterBean.TypeEnum.ACTIVITY) {
                        Intent intent = new Intent(context, routerBean.getMyClass());
                        intent.putExtras(bundleManager.getBundle());
                        context.startActivity(intent, bundleManager.getBundle());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
