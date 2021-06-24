package xyz.xyz0z0.arouter_api;

import java.util.Map;

public interface ARouterGroup {

    Map<String,Class<? extends ARouterPath>> getGroupMap();

}
