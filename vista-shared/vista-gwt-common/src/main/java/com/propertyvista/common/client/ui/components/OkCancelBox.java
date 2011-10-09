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
package com.propertyvista.common.client.ui.components;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.DialogPanel;

/**
 * Draws pop-up dialog box with OK and Cancel buttons and
 * user definable content (by means of {@link #createContent()}) inside.
 * 
 * @author Vlad
 * 
 */
public abstract class OkCancelBox extends DialogPanel {

    protected final static I18n i18n = I18n.get(OkCancelBox.class);

    protected Button okButton;

    protected Button clButton;

    public OkCancelBox(String caption) {
        this(caption, false);
    }

    public OkCancelBox(String caption, boolean hideCancel) {
        super(false, true);
        setCaption(caption);

        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(okButton = new Button(i18n.tr("OK"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (onOk()) {
                    hide();
                }
            }
        }));
        if (!hideCancel) {
            buttons.add(clButton = new Button(i18n.tr("Cancel"), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    onCancel();
                    hide();
                }
            }));
        }
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
     * Note: called from within parent's constructor!!!
     * 
     * @return widget with user's content.
     */
    protected abstract Widget createContent();

    /**
     * Override to set your desired size
     */
    protected void setSize() {
        setSize("200px", "100px");
    }

    /**
     * Override for some meaningful action.
     * 
     * @return true if dialog close allowed.
     */
    protected boolean onOk() {
        return true;
    }

    /**
     * Override for some meaningful action.
     */
    protected void onCancel() {
    }
}