package au.edu.bond.classgroups.service;

import au.edu.bond.classgroups.groupext.GroupExtension;
import au.edu.bond.classgroups.model.Group;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by Shane Argo on 20/06/2014.
 */
public class EscapingGroupTitleService implements GroupTitleService {

    private GroupTitleService internalGroupTitleService;

    public EscapingGroupTitleService(GroupTitleService internalGroupTitleService) {
        this.internalGroupTitleService = internalGroupTitleService;
    }

    @Override
    public String getGroupTitle(Group group, GroupExtension extension) {
        return StringEscapeUtils.escapeHtml(internalGroupTitleService.getGroupTitle(group, extension));
    }
}
