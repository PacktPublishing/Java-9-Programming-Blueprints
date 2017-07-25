package com.steeplesoft.photobeans.main.factories;

import com.steeplesoft.photobeans.main.nodes.PhotoNode;
import com.steeplesoft.photobeans.manager.PhotoManager;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.LifecycleManager;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author jason
 */
public class PhotoNodeFactory  extends ChildFactory<String> {

    private final PhotoManager photoManager;
    private static final Logger LOGGER = Logger.getLogger(YearChildFactory.class.getName());
    private final int year;
    private final int month;
    
    public PhotoNodeFactory(int year, int month) {
        this.year = year;
        this.month = month;
        this.photoManager = Lookup.getDefault().lookup(PhotoManager.class);
        if (photoManager == null) {
            LOGGER.log(Level.SEVERE, "Cannot get PhotoManager object");
            LifecycleManager.getDefault().exit();
        }
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        toPopulate.addAll(photoManager.getPhotos(year, month));
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        return new PhotoNode(key);
    }
}