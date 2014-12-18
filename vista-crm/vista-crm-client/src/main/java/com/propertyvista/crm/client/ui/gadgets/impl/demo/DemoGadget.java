/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2012
 * @author ArtyomB
 */
package com.propertyvista.crm.client.ui.gadgets.impl.demo;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import com.propertyvista.crm.client.ui.gadgets.common.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.demo.DemoGadgetMetadata;

public class DemoGadget extends GadgetInstanceBase<DemoGadgetMetadata> {

    private HTML widget;

    public DemoGadget(DemoGadgetMetadata gmd) {
        super(gmd, DemoGadgetMetadata.class);
        setDefaultPopulator(new Populator() {
            @Override
            public void populate() {
                populateSucceded();
            }
        });
    }

    // flags:

    @Override
    public boolean isSetupable() {
        return true;
    }

    @Override
    public boolean isFullWidth() {
        return false;
    }

    // setup:

    @Override
    public ISetup getSetup() {
        class MySetup implements ISetup {

            private final FlowPanel setupPanel = new FlowPanel();

            private final TextArea content = new TextArea();

            public MySetup() {
                super();
                setupPanel.add(new Label("Enter new gadget content:"));

                content.setText(widget.getHTML());
                content.setWidth("100%");
                setupPanel.add(content);

                setupPanel.getElement().getStyle().setPadding(10, Unit.PX);
                setupPanel.getElement().getStyle().setPaddingBottom(0, Unit.PX);
            }

            @Override
            public Widget asWidget() {
                return setupPanel;
            }

            @Override
            public boolean onStart() {
                return true;
            }

            @Override
            public boolean onOk() {
                widget.setHTML(content.getText());
                return true;
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
            }
        }

        return new MySetup();
    }

    @Override
    public Widget initContentPanel() {
        widget = new HTML();
        widget.setText(getName() + " content");
        widget.setHeight(Random.nextInt(8) + 2 + "em");
        widget.setWidth("100%");

        return widget;
    }

}