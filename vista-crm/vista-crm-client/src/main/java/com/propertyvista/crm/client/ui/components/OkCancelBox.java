/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-27
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.widgets.client.dialog.DialogPanel;

/**
 * Draws pop-up dialog box with OK and Cancel buttons and
 * user definable content (by means of {@link #createContent()}) inside.
 * 
 * @author Vlad
 * 
 */
public abstract class OkCancelBox extends DialogPanel {

    protected final I18n i18n = I18nFactory.getI18n(OkCancelBox.class);

    protected Button okButton;

    public OkCancelBox(String caption) {
        super(false, true);
        setCaption(i18n.tr(caption));

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(okButton = new Button(i18n.tr("OK"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onOk();
                hide();
            }
        }));
        buttons.add(new Button(i18n.tr("Cancel"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onCancel();
                hide();
            }
        }));
        buttons.setSpacing(8);

        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(createContent());
        vPanel.add(buttons);
        vPanel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_CENTER);
        vPanel.setSpacing(8);
        vPanel.setSize("100%", "100%");

        setContentWidget(vPanel);
        setSize();
    }

    /**
     * Implement in derived class - your inner content of the box.
     * 
     * @return widget with user's content.
     */
    protected abstract Widget createContent();

    /**
     * Override to set your desired size
     */
    protected void setSize() {
        setSize("400px", "300px");
    }

    /**
     * Override for some meaningful action.
     */
    protected void onOk() {
    }

    /**
     * Override for some meaningful action.
     */
    protected void onCancel() {
    }
}