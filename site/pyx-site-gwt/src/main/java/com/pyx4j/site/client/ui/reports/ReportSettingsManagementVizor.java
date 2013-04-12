/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.client.ui.reports;

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.visor.AbstractVisorPane;
import com.pyx4j.widgets.client.Button;

public abstract class ReportSettingsManagementVizor extends AbstractVisorPane {

    private static final I18n i18n = I18n.get(ReportSettingsManagementVizor.class);

    private final DockLayoutPanel panel;

    private final ListBox settingsIdsList;

    private final Button loadButton;

    private final Button deleteButton;

    public ReportSettingsManagementVizor(Controller controller) {
        super(controller);
        panel = new DockLayoutPanel(Unit.EM);

        setCaption(i18n.tr("Load report configuration preset"));

        HorizontalPanel buttonsPanel = new HorizontalPanel();
        buttonsPanel.setHeight("100%");
        buttonsPanel.getElement().getStyle().setProperty("margin", "auto");
        loadButton = new Button(i18n.tr("Load"), new Command() {
            @Override
            public void execute() {
                if (settingsIdsList.getSelectedIndex() != -1) {
                    onLoadRequest(settingsIdsList.getItemText(settingsIdsList.getSelectedIndex()));
                }
            }
        });
        loadButton.getElement().getStyle().setProperty("marginRight", "1em");
        buttonsPanel.add(loadButton);
        buttonsPanel.setCellVerticalAlignment(loadButton, HasVerticalAlignment.ALIGN_MIDDLE);

        deleteButton = new Button(i18n.tr("Delete"), new Command() {
            @Override
            public void execute() {
                if (settingsIdsList.getSelectedIndex() != -1) {
                    onDeleteRequest(settingsIdsList.getItemText(settingsIdsList.getSelectedIndex()));
                }
            }
        });
        deleteButton.getElement().getStyle().setProperty("marginLeft", "1em");
        buttonsPanel.add(deleteButton);
        buttonsPanel.setCellVerticalAlignment(deleteButton, HasVerticalAlignment.ALIGN_MIDDLE);
        panel.addSouth(buttonsPanel, 3);

        settingsIdsList = new ListBox(true);
        settingsIdsList.setSize("100%", "100%");

        panel.add(settingsIdsList);

        setContentPane(panel);
    }

    /**
     * @param availableReportSettings
     *            can be <code>null</code> while settings are not available (i.e. will display "retrieving settings message") or list of the available ids
     */
    public void setAvailableReportSettingsIds(List<String> availableReportSettings) {
        settingsIdsList.clear();
        if (availableReportSettings != null) {
            for (String id : availableReportSettings) {
                settingsIdsList.addItem(id);
            }
            if (!availableReportSettings.isEmpty()) {
                settingsIdsList.setEnabled(true);
                loadButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } else {
            settingsIdsList.addItem(i18n.tr("retrieving settings..."));
            settingsIdsList.setEnabled(false);
            loadButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    public abstract void onLoadRequest(String selectedReportSettingsId);

    public abstract void onDeleteRequest(String selectedReportSettingsId);

}
