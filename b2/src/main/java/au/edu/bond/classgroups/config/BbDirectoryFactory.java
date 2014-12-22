package au.edu.bond.classgroups.config;

import au.edu.bond.classgroups.service.DirectoryFactory;
import blackboard.platform.plugin.PlugInException;
import blackboard.platform.plugin.PlugInUtil;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Shane Argo on 12/06/2014.
 */
public class BbDirectoryFactory extends DirectoryFactory {

    @Override
    public File getConfigurationDirectory() {
        File rootConfigDir;
        try {
            rootConfigDir = PlugInUtil.getConfigDirectory("bond", "ClassGroups");
        } catch (PlugInException ex) {
            Logger.getLogger(BbDirectoryFactory.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Problem while trying to get Building Block Config Directory", ex);
        }
        return rootConfigDir;
    }

}
