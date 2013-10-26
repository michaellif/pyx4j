/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

public class MultiDisclosurePanel implements IsWidget {

    private final static I18n i18n = I18n.get(MultiDisclosurePanel.class);

    private final ArrayList<DisclosurePanel> disclosurePanels;

    private final TwoColumnFlexFormPanel panel;

    private int currentRow;

    public MultiDisclosurePanel() {
        panel = new TwoColumnFlexFormPanel();
        disclosurePanels = new ArrayList<DisclosurePanel>();
        currentRow = -1;
    }

    public void add(IsWidget widget, String header) {
        add(widget, header, null, null);
    }

    public void add(IsWidget widget, String header, String customButtonLabel, final Command customCommand) {
        ++currentRow;
        final DisclosurePanel disclosurePanel = new DisclosurePanel(header);
        disclosurePanel.setWidth("100%");
        disclosurePanel.setOpen(currentRow == 0);
        disclosurePanel.setAnimationEnabled(true);
        disclosurePanels.add(disclosurePanel);
        disclosurePanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

            @Override
            public void onOpen(OpenEvent<DisclosurePanel> event) {
                for (DisclosurePanel disclosurePanel : disclosurePanels) {
                    if (disclosurePanel != event.getTarget()) {
                        disclosurePanel.setOpen(false);
                    }
                }
            }
        });

        TwoColumnFlexFormPanel continuationPanel = new TwoColumnFlexFormPanel();
        continuationPanel.setWidget(0, 0, widget);

        continuationPanel.setWidget(1, 0, new Button((customButtonLabel == null ? i18n.tr("Continue") : customButtonLabel), new Command() {

            @Override
            public void execute() {
                boolean openNext = false;
                for (DisclosurePanel panel : disclosurePanels) {
                    if (openNext) {
                        panel.setOpen(true);
                        break;
                    }
                    if (disclosurePanel == panel) {
                        openNext = true;
                    }

                }

                if (customCommand != null) {
                    customCommand.execute();
                }
            }
        }));
        continuationPanel.getFlexCellFormatter().getElement(1, 0).getStyle().setPaddingTop(0.5d, Unit.EM);
        continuationPanel.getFlexCellFormatter().getElement(1, 0).getStyle().setPaddingBottom(1d, Unit.EM);

        disclosurePanel.add(continuationPanel);
        panel.setWidget(currentRow, 0, disclosurePanel);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

}
