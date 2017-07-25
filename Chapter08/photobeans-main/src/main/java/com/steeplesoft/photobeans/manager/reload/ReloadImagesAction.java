package com.steeplesoft.photobeans.manager.reload;

import com.steeplesoft.photobeans.manager.PhotoManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "File",
        id = "com.steeplesoft.photobeans.main.ReloadImagesAction"
)
@ActionRegistration(
        displayName = "#CTL_ReloadImagesAction",
        lazy = false
)
@ActionReference(path = "Menu/File", position = 1300)
@Messages("CTL_ReloadImagesAction=Reload")
public final class ReloadImagesAction extends AbstractAction {

    public ReloadImagesAction() {
        putValue(AbstractAction.NAME, "Reload");
    }

    public ReloadImagesAction(Lookup lookup) {
        this();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RequestProcessor.getDefault().execute(() -> 
                Lookup.getDefault().lookup(PhotoManager.class).scanSourceDirs());
    }
}
