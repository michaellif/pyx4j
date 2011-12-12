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
package com.propertyvista.crm.client.ui.gadgets.demo;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.gadgets.AbstractGadget;
import com.propertyvista.crm.client.ui.gadgets.Directory;
import com.propertyvista.crm.client.ui.gadgets.GadgetInstanceBase;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.demo.Demo;

public class DemoGadget extends AbstractGadget<Demo> {

    private static final I18n i18n = I18n.get(DemoGadget.class);

    public static class DemoGadgetInstance extends GadgetInstanceBase<Demo> {
        private HTML widget;

        public DemoGadgetInstance(GadgetMetadata gmd) {
            super(gmd, Demo.class);
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

    public DemoGadget() {
        super(Demo.class);
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList(Directory.Categories.Demo.toString());
    }

    @Override
    public String getDescription() {
        return i18n.tr("Demo of a gadget");
    }

    @Override
    public boolean isBuildingGadget() {
        return false;
    }

    @Override
    protected GadgetInstanceBase<Demo> createInstance(GadgetMetadata gadgetMetadata) throws Error {
        return new DemoGadgetInstance(gadgetMetadata);
    }
}