package au.edu.bond.classgroups.groupext;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 16/06/2014.
 */
public class GroupExtensionService {

    @Autowired
    private GroupExtensionDAO groupExtensionDAO;

    Map<String, GroupExtension> externalExtMap;
    Map<Long, GroupExtension> internalExtMap;

    public GroupExtension getGroupExtensionByExternalId(String externalId) {
        if(externalExtMap == null) {
            populateCaches();
        }
        return externalExtMap.get(externalId);
    }

    public GroupExtension getGroupExtensionByInternalId(Long internalId) {
        if(internalExtMap == null) {
            populateCaches();
        }
        return internalExtMap.get(internalId);
    }

    public void create(GroupExtension groupExtension) {
        groupExtensionDAO.create(groupExtension);
        cache(groupExtension);
    }

    public void update(GroupExtension groupExtension) {
        groupExtensionDAO.update(groupExtension);
        cache(groupExtension);
    }

    public void delete(GroupExtension groupExtension) {
        groupExtensionDAO.delete(groupExtension);
        uncache(groupExtension);
    }

    private void populateCaches() {
        externalExtMap = new HashMap<String, GroupExtension>();
        internalExtMap = new HashMap<Long, GroupExtension>();
        for(GroupExtension ext : groupExtensionDAO.getAll()) {
            externalExtMap.put(ext.getExternalSystemId(), ext);
            internalExtMap.put(ext.getInternalGroupId(), ext);
        }
    }

    private void cache(GroupExtension ext) {
        if(externalExtMap == null || internalExtMap == null) {
            populateCaches();
        }
        externalExtMap.put(ext.getExternalSystemId(), ext);
        internalExtMap.put(ext.getInternalGroupId(), ext);
    }

    private void uncache(GroupExtension ext) {
        if(externalExtMap != null && internalExtMap != null) {
            internalExtMap.remove(ext.getInternalGroupId());
            externalExtMap.remove(ext.getExternalSystemId());
        }
    }
}
