package au.edu.bond.classgroups.feed.csv;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Shane Argo on 17/06/2014.
 */
public class ParseCollection extends CellProcessorAdaptor {

    private String regex = "\\|";

    public ParseCollection() {
    }

    public ParseCollection(String regex) {
        this.regex = regex;
    }

    public ParseCollection(CellProcessor next) {
        super(next);
    }

    public ParseCollection(String regex, CellProcessor next) {
        super(next);
        this.regex = regex;
    }

    @Override
    public Object execute(Object value, CsvContext context) {

        validateInputNotNull(value, context);

        Collection<String> collection = new HashSet<String>();
        for(String elem : value.toString().split(regex)) {
            collection.add(elem);
        }

        return collection;
    }

}
