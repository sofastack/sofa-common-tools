package com.alipay.sofa.common.space;

import com.alipay.sofa.common.log.LogSpace;
import com.alipay.sofa.common.thread.space.ThreadPoolSpace;
import com.alipay.sofa.common.utils.ReportUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/4
 */
public class SpaceManager {
    private static final ConcurrentHashMap<SpaceId, Space> SPACES_MAP = new ConcurrentHashMap<>();

    /**
     * Get space specified by spaceId
     * This will create a new Space if it doesn't exist
     * @param spaceId space ID
     * @return Space
     */
    public static Space getSpace(SpaceId spaceId) {
        return SPACES_MAP.computeIfAbsent(spaceId, key -> {
            ReportUtil.reportInfo("Space is created for " + spaceId.getSpaceName());
            return new Space();
        });
    }

    public static Space getSpace(String spaceName) {
        return getSpace(SpaceId.withSpaceName(spaceName));
    }
}
