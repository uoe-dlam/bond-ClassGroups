package au.edu.bond.classgroups.feed.csv;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shane Argo on 2/06/2014.
 */
public class CellProcessorUtil {

    public static final Map<String, CellProcessor> GROUP_CELL_PROCESSOR_LOOKUP;
    public static final Map<String, CellProcessor> MEMBER_CELL_PROCESSOR_LOOKUP;
    static {
        GROUP_CELL_PROCESSOR_LOOKUP = new HashMap<String, CellProcessor>();
        GROUP_CELL_PROCESSOR_LOOKUP.put("courseId", new NotNull());
        GROUP_CELL_PROCESSOR_LOOKUP.put("groupId", new NotNull());
        GROUP_CELL_PROCESSOR_LOOKUP.put("title", new NotNull());
        GROUP_CELL_PROCESSOR_LOOKUP.put("leader", new Optional());
        GROUP_CELL_PROCESSOR_LOOKUP.put("groupSet", new Optional());
        GROUP_CELL_PROCESSOR_LOOKUP.put("available", new Optional(new ParseBool()));
        GROUP_CELL_PROCESSOR_LOOKUP.put("tools", new Optional(new ParseCollection()));

        MEMBER_CELL_PROCESSOR_LOOKUP = new HashMap<String, CellProcessor>();
        MEMBER_CELL_PROCESSOR_LOOKUP.put("groupId", new NotNull());
        MEMBER_CELL_PROCESSOR_LOOKUP.put("userId", new NotNull());
    }

    public static CellProcessor[] getCellProcessorsForGroupColumns(String[] columnNames) {
        return lookupProcessors(GROUP_CELL_PROCESSOR_LOOKUP, columnNames);
    }

    public static CellProcessor[] getCellProcessorsForMemberColumns(String[] columnNames) {
        return lookupProcessors(MEMBER_CELL_PROCESSOR_LOOKUP, columnNames);
    }

    private static CellProcessor[] lookupProcessors(Map<String, CellProcessor> lookup, String[] columnNames) {
        CellProcessor[] result = new CellProcessor[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            result[i] = lookup.get(columnNames[i]);
        }
        return result;
    }
}
