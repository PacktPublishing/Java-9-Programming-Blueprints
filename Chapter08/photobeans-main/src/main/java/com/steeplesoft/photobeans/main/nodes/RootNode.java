package com.steeplesoft.photobeans.main.nodes;

import com.steeplesoft.photobeans.manager.reload.ReloadImagesAction;
import com.steeplesoft.photobeans.main.factories.YearChildFactory;
import com.steeplesoft.photobeans.manager.PhotoManager;
import com.steeplesoft.photobeans.manager.reload.ReloadCookie;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author jason
 */
@NbBundle.Messages({
    "HINT_RootNode=Show all years",
    "LBL_RootNode=Photos"
})
public class RootNode extends AbstractNode {

    private final InstanceContent instanceContent;
    private Lookup.Result reloadResult = null;

    public RootNode() {
        this(new InstanceContent());
    }

    protected RootNode(InstanceContent ic) {
        super(Children.create(new YearChildFactory(), true),
                new AbstractLookup(ic));
        PhotoManager photoManager = Lookup.getDefault().lookup(PhotoManager.class);
        reloadResult = photoManager.getLookup().lookup(new Lookup.Template(ReloadCookie.class));
        reloadResult.addLookupListener(event -> setChildren(Children.create(new YearChildFactory(), true)));

        setDisplayName(Bundle.LBL_RootNode());
        setShortDescription(Bundle.HINT_RootNode());

        instanceContent = ic;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new ReloadImagesAction(getLookup())};
    }
}
