package com.steeplesoft.photobeans.main.nodes;

import java.io.File;
import java.util.Set;
import org.openide.cookies.OpenCookie;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import com.steeplesoft.photobeans.main.PhotoViewerTopComponent;
import javax.swing.Action;
import org.openide.actions.OpenAction;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;

/**
 *
 * @author jason
 */
public class PhotoNode extends AbstractNode {

    public PhotoNode(String photo) {
        this(photo, new InstanceContent());
    }

    private PhotoNode(String photo, InstanceContent ic) {
        super(Children.LEAF, new AbstractLookup(ic));
        final String name = new File(photo).getName();
        setName(name);
        setDisplayName(name);
        setShortDescription(photo);

        ic.add((OpenCookie) () -> {
            TopComponent tc = findTopComponent(photo);
            if (tc == null) {
                tc = new PhotoViewerTopComponent(photo);
                tc.open();
            }
            tc.requestActive();
        });
    }

    private TopComponent findTopComponent(String photo) {
        Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
        for (TopComponent tc : openTopComponents) {
            if (photo.equals(tc.getLookup().lookup(String.class))) {
                return tc;
            }
        }
        return null;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{SystemAction.get(OpenAction.class)};
    }

    @Override
    public Action getPreferredAction() {
        return SystemAction.get(OpenAction.class);
    }
    
    
}
