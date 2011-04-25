package com.pyx4j.dashboard.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * Dashboard Gadget interface. User-defined dashboard gadgets should extend GWT Widget
 * and implement this interface.
 */
public interface IGadget {

    // info:
    Widget getWidget(); // should be implemented meaningful!

    String getName();

    String getDescription();

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