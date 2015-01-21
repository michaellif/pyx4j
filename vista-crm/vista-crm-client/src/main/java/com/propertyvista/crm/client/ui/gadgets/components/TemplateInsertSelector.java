/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 21, 2015
 * @author stanp
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.crm.rpc.services.policies.emailtemplates.EmailTemplateManagerService;
import com.propertyvista.domain.communication.EmailTemplateType;
import com.propertyvista.domain.policy.dto.emailtemplates.EmailTemplateTypeDTO;
import com.propertyvista.domain.policy.dto.emailtemplates.EmailTemplateTypesDTO;

public class TemplateInsertSelector extends PopupPanel {

    private final static I18n i18n = I18n.get(TemplateInsertSelector.class);

    private final Map<EmailTemplateType, List<String>> templateObjects = new HashMap<EmailTemplateType, List<String>>();

    private String selectedValue;

    private Command selectionHandler;

    private final TemplateInsertMenu menu = new TemplateInsertMenu();

    public TemplateInsertSelector() {
        super(true);
        // get the template object list
        ((EmailTemplateManagerService) GWT.create(EmailTemplateManagerService.class)).getTemplateDataObjects( //
                new AsyncCallback<EmailTemplateTypesDTO>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        MessageDialog.error(i18n.tr("Server Error"), i18n.tr("Data not available.") + " " + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(EmailTemplateTypesDTO result) {
                        for (EmailTemplateTypeDTO tplType : result.types) {
                            templateObjects.put(tplType.type, tplType.objectNames);
                        }
                    }
                });
    }

    public void setSelectionHandler(Command handler) {
        selectionHandler = handler;
    }

    public String getSelectedValue() {
        return selectedValue != null ? "${" + selectedValue + "}" : "";
    }

    public List<String> getTemplateObjects(EmailTemplateType type) {
        return templateObjects.get(type);
    }

    public void showBelow(UIObject target) {
        selectedValue = null;
        if (getWidget() == null) {
            addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override
                public void onClose(CloseEvent<PopupPanel> event) {
                    if (selectionHandler != null) {
                        selectionHandler.execute();
                    }
                }
            });

            add(menu);
        }
        addAutoHidePartner(target.getElement());
        showRelativeTo(target);
    }

    public void setItems(List<String> items) {
        if (items == null) {
            return;
        }
        menu.clearItems();
        for (final String item : items) {
            TemplateInsertMenu parent = menu;
            String[] segments = item.split("\\.");
            for (int i = 0; i < segments.length; i++) {
                String segment = segments[i];
                MenuItem mi = parent.findItem(segment);
                if (mi == null) {
                    final MenuItem menuItem = new MenuItem(segment, (Command) null);
                    menuItem.setTitle(item);
                    if (i == segments.length - 1) {
                        // last item can be selected
                        menuItem.setCommand(new Command() {
                            @Override
                            public void execute() {
                                selectedValue = menuItem.getTitle();
                                hide();
                            }
                        });
                    } else {
                        menuItem.setSubMenu(new TemplateInsertMenu());
                    }
                    parent.addItem(menuItem);
                    mi = menuItem;
                }
                parent = (TemplateInsertMenu) mi.getSubMenu();
            }
        }
    }

    static class TemplateInsertMenu extends MenuBar {
        public TemplateInsertMenu() {
            super(true);
            setAutoOpen(true);
            setAnimationEnabled(true);
        }

        public MenuItem findItem(String text) {
            for (MenuItem item : getItems()) {
                if (text.equals(item.getText())) {
                    return item;
                }
            }
            return null;
        }
    }
}
