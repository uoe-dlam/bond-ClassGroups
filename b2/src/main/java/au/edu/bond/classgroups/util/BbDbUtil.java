package au.edu.bond.classgroups.util;

import au.edu.bond.classgroups.model.Task;
import blackboard.db.BbDatabase;
import org.hibernate.Session;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shane Argo on 23/09/2014.
 */
public class BbDbUtil implements DbUtil {

    @Override
    public String createScheduleQuery(Task task) {

        final List<String> params = new ArrayList<String>(3);
        params.add("p_entered_date");
        params.add("p_scheduled_date");
        params.add("p_entered_node");

        final List<String> out = new ArrayList<String>();

        return BbDatabase.getDefaultInstance().getType().getFunctions()
                .executeProcedure("bond_classgroups_createsched", params, out, false);

    }

}
