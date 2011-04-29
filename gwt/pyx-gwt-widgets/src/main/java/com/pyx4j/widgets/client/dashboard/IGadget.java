package com.pyx4j.widgets.client.dashboard;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dashboard Gadget interface. User-defined dashboard gadgets should extend GWT Widget
 * and implement this interface.
 */
public interface IGadget extends IsWidget {

    @Override
    Widget asWidget();

    String getName();

    String getDescription();

    // runtime scope:
    public void start();

    public void suspend();

    public void resume();

    public void stop();

    // flags:	
    boolean isMaximizable();

    boolean isMinimizable();

    boolean isSetupable();

    boolean isFullWidth(); // should be 'true' for regular gadgets/layouts...

    /**
     * Dashboard Gadget Setup interface. User-defined gadgets may implement this
     * interface in order to get gadget setup functionality.
     */
    interface ISetup {
        Widget getWidget(); // should be implemented meaningful!

        // notifications:
        boolean onStart();

        boolean onOk();

        void onCancel();
    }

    // setup:
    IGadget.ISetup getSetup(); // should be implemented meaningful if isSetupable!

    // notifications:
    void onMaximize(boolean maximized_restored); // true for max-ed, false - restored

    void onMinimize(boolean minimized_restored); // true for min-ed, false - restored

    void onDelete();
}