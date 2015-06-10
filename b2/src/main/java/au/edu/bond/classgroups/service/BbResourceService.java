package au.edu.bond.classgroups.service;

import blackboard.platform.intl.BbResourceBundle;
import blackboard.platform.intl.BundleManager;
import blackboard.platform.intl.BundleManagerFactory;
import blackboard.platform.plugin.PlugIn;
import blackboard.platform.plugin.PlugInManager;
import blackboard.platform.plugin.PlugInManagerFactory;

/**
 * Created by Shane Argo on 13/08/2014.
 */
public class BbResourceService implements ResourceService {

    private BbResourceBundle bundle;

    public BbResourceService(String vendorId, String handle) {
        PlugInManager pluginMgr = PlugInManagerFactory.getInstance();
        PlugIn plugin = pluginMgr.getPlugIn(vendorId, handle);

        BundleManager bm = BundleManagerFactory.getInstance();

        bundle = bm.getPluginBundle(plugin.getId());
    }

    public String getLocalisationString(String key, Object... args) {
        String[] argStr = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            argStr[i] = args[i].toString();
        }
        return bundle.getStringWithFallback(key, key, argStr);
    }

}
