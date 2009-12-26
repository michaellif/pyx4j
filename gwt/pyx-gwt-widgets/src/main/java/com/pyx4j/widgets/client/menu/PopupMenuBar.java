/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Jun 3, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.menu;

import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

public class PopupMenuBar extends Menu {

    private final PopupPanel popupPanel;

    enum Location {
        EAST, WEST, NORTH, SOUTH, NONE
    }

    public PopupMenuBar() {
        super(true);
        popupPanel = new PopupPanel() {
            @Override
            protected void onPreviewNativeEvent(NativePreviewEvent event) {
                if (!event.isCanceled() && event.getTypeInt() == Event.ONMOUSEUP) {
                    DeferredCommand.addCommand(new com.google.gwt.user.client.Command() {
                        public void execute() {
                            hide();
                        }
                    });

                }

                super.onPreviewNativeEvent(event);
            }
        };
        popupPanel.add(this);

    }

    public void showRelativeTo(final UIObject target) {
        popupPanel.showRelativeTo(target);
    }

    @Override
    protected void position(PopupPanel popup, SubMenuItem item, int offsetWidth, int offsetHeight) {

        Location ewLocation = Location.NONE;
        Location nsLocation = Location.NONE;

        if (Window.getClientWidth() - item.getAbsoluteLeft() - item.getOffsetWidth() > offsetWidth) {
            ewLocation = Location.EAST;
        } else if (item.getAbsoluteLeft() > offsetWidth) {
            ewLocation = Location.WEST;
        }

        if (Window.getClientHeight() - item.getAbsoluteTop() > offsetHeight) {
            nsLocation = Location.SOUTH;
        } else if (item.getAbsoluteTop() - item.getOffsetHeight() > offsetHeight) {
            nsLocation = Location.NORTH;
        }

        if (ewLocation == Location.EAST && nsLocation == Location.SOUTH) {
            popup.setPopupPosition(item.getAbsoluteLeft() + item.getOffsetWidth() + 1, item.getAbsoluteTop());
        } else if (ewLocation == Location.WEST && nsLocation == Location.SOUTH) {
            popup.setPopupPosition(item.getAbsoluteLeft() - offsetWidth - 1, item.getAbsoluteTop());
        } else if (ewLocation == Location.EAST && nsLocation == Location.NORTH) {
            popup.setPopupPosition(item.getAbsoluteLeft() + item.getOffsetWidth() + 1, item.getAbsoluteTop() + item.getOffsetHeight() - offsetHeight);
        } else if (ewLocation == Location.WEST && nsLocation == Location.NORTH) {
            popup.setPopupPosition(item.getAbsoluteLeft() - offsetWidth - 1, item.getAbsoluteTop() + item.getOffsetHeight() - offsetHeight);
        }

    }

    @Override
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
        super.onPopupClosed(sender, autoClosed);
        popupPanel.hide();
    }
}