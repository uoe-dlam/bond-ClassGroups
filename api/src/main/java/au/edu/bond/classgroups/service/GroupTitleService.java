package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.groupext.GroupExtension;
import au.edu.bond.classgroups.model.Group;

/**
 * Created by Shane Argo on 20/06/2014.
 */
public interface GroupTitleService {

    public String getGroupTitle(String baseTitle, GroupExtension extension);


}
