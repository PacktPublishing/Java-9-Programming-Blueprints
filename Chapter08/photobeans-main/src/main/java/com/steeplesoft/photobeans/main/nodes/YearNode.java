package com.steeplesoft.photobeans.main.nodes;

import com.steeplesoft.photobeans.main.factories.MonthNodeFactory;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author jason
 */
public class YearNode extends AbstractNode {
    public YearNode(int year) {
        super(Children.create(new MonthNodeFactory(year), true), Lookups.singleton(year));
        setName("" + year);
        setDisplayName("" + year);
    }
}
