/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-04-20
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.dashboard.client.IGadget;

public class DemoGadget extends HTML implements IGadget {

    boolean fullWidth = true;

    public DemoGadget(String s) {
        super(s);
    }

    // info:

    @Override
    public Widget getWidget() {
        return this;
    }

    @Override
    public String getName() {
        return (getText() + " Title");
    }

    // flags:

    @Override
    public boolean isMaximizable() {
        return true;
    }

    @Override
    public boolean isMinimizable() {
        return true;
    }

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public boolean isFullWidth() {
        return fullWidth;
    }

    // setup:

    public void setFullWidth(boolean fullWidth) {
        this.fullWidth = fullWidth;
    }

    @Override
    public ISetup getSetup() {
        class MySetup implements ISetup {
            private final TextArea content = new TextArea();

            @Override
            public Widget getWidget() {
                FlowPanel setupPanel = new FlowPanel();
                setupPanel.add(new Label("Enter new gadget content:"));

                content.setText(getHTML());
                content.setWidth("100%");
                setupPanel.add(content);

                setupPanel.getElement().getStyle().setPadding(10, Unit.PX);
                setupPanel.getElement().getStyle().setPaddingBottom(0, Unit.PX);
                return setupPanel;
            }

            @Override
            public boolean onOk() {
                setHTML(content.getText());
                return true;
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub

            }
        }

        return new MySetup();
    }

    // notifications:

    @Override
    public void onMaximize(boolean maximized_restored) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMinimize(boolean minimized_restored) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDelete() {
        // TODO Auto-generated method stub

    }
}