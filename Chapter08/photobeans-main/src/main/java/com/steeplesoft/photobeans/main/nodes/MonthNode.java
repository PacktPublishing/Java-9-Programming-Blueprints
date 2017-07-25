package com.steeplesoft.photobeans.main.nodes;

import com.steeplesoft.photobeans.main.factories.PhotoNodeFactory;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author jason
 */
public class MonthNode extends AbstractNode {
    public MonthNode(int year, int month) {
        super(Children.create(new PhotoNodeFactory(year, month), true), Lookups.singleton(month));
        String display = month + " - " + Month.values()[month-1].getDisplayName(TextStyle.FULL, Locale.getDefault());
        setName(display);
        setDisplayName(display);
    }
}