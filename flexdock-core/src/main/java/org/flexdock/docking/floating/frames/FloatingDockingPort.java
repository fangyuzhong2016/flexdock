/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.flexdock.docking.floating.frames;

import java.awt.Component;
import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.event.DockingEvent;

/**
 * @author Christopher Butler
 */
@SuppressWarnings(value = { "serial" })
public class FloatingDockingPort extends DefaultDockingPort {
    private static final Set EMPTY_SET = new HashSet(0);
    protected DockingFrame frame;
    protected FrameDragListener dragListener;

    public FloatingDockingPort(DockingFrame frame, String persistentId) {
        super(persistentId);
        getDockingProperties().setSingleTabsAllowed(true);
        setTabsAsDragSource(true);
        this.frame = frame;
        dragListener = new FrameDragListener(frame);
    }

    @Override
    public String getRegion(Point p) {
        // only allow docking in CENTER
        return CENTER_REGION;
    }

    public boolean isDockingAllowed(String region, Component comp) {
        // only allow docking in CENTER
        if(!CENTER_REGION.equals(region)) {
            return false;
        }
        return super.isDockingAllowed(comp, region);
    }

    @Override
    public boolean dock(Dockable dockable, String region) {        // only dock to the CENTER region
        boolean ret = super.dock(dockable, CENTER_REGION);
        if(ret) {
            toggleListeners(dockable.getComponent(), true);
        }
        return ret;
    }

    @Override
    public boolean undock(Component comp) {
        boolean ret = super.undock(comp);
        if(ret) {
            toggleListeners(comp, false);
        }
        return ret;
    }

    @Override
    public void dragStarted(DockingEvent evt) {
        super.dragStarted(evt);

        Component dragSrc = (Component)evt.getTriggerSource();
        Dockable dockable = (Dockable)evt.getSource();

        boolean listenerEnabled = getFrameDragSources(dockable).contains(dragSrc);
        dragListener.setEnabled(listenerEnabled);
        if(listenerEnabled) {
            evt.consume();
        }
    }

    @Override
    public void undockingComplete(DockingEvent evt) {
        super.undockingComplete(evt);
        if(evt.getOldDockingPort()==this && getDockableCount()==0) {
            frame.destroy();
            frame = null;
        }
    }

    protected void toggleListeners(Component comp, boolean add) {
        Dockable dockable = DockingManager.getDockable(comp);
        if(add) {
            installListeners(dockable);
        } else {
            uninstallListeners(dockable);
        }
    }

    protected void installListeners(Dockable dockable) {
        Set frameDraggers = getFrameDragSources(dockable);
        for(Iterator it=frameDraggers.iterator(); it.hasNext();) {
            Component frameDragSrc = (Component)it.next();
            frameDragSrc.addMouseListener(dragListener);
            frameDragSrc.addMouseMotionListener(dragListener);
        }

        dockable.addDockingListener(this);
    }

    protected void uninstallListeners(Dockable dockable) {
        Set frameDraggers = getFrameDragSources(dockable);
        for(Iterator it=frameDraggers.iterator(); it.hasNext();) {
            Component frameDragSrc = (Component)it.next();
            frameDragSrc.removeMouseListener(dragListener);
            frameDragSrc.removeMouseMotionListener(dragListener);
        }
        dockable.removeDockingListener(this);
    }

    public int getDockableCount() {
        Component comp = getDockedComponent();
        if(!(comp instanceof JTabbedPane)) {
            return 0;
        }
        return ((JTabbedPane)comp).getTabCount();
    }

    protected Set getFrameDragSources(Dockable dockable) {
        Set set = dockable==null? null: dockable.getFrameDragSources();
        return set==null? EMPTY_SET: set;
    }

}
