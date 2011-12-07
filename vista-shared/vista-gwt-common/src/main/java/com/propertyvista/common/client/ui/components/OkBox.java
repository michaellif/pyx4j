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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.dialog.OkOption;

/**
 * Draws pop-up dialog box with OK and Cancel buttons and
 * user definable content (by means of {@link #createContent()}) inside.
 * 
 * @author Vlad
 * 
 */
public abstract class OkBox extends SimplePanel implements OkOption {

    protected final static I18n i18n = I18n.get(OkBox.class);

    protected final SimplePanel content = new SimplePanel();

    protected final Dialog dialog;

    private Command okCommand;

    public OkBox(String caption) {
        dialog = new Dialog(caption, this);
        dialog.setBody(content);
        dialog.setSize("200px", "100px");

        content.getElement().getStyle().setMargin(6, Unit.PX);
    }

    /**
     * Call in derived class - supply your inner content of the box.
     * 
     * @return widget with user's content.
     */
    protected void setContent(Widget w) {
        this.content.setWidget(w);
    }

    @Override
    public void setSize(String width, String height) {
        dialog.setSize(width, height);
    }

    public Button getOkButton() {
        return dialog.getOkButton();
    }

    /**
     * Call to show the dialog and process result.
     * 
     * @param okResult
     */
    public void run(Command okResult) {
        okCommand = okResult;
        show();
    }

    /**
     * Call to show the dialog.
     */
    public void show() {
        dialog.show();
    }

    /**
     * Override for some meaningful action.
     * 
     * Note: always call super.onClickOk() last.
     * 
     * @return true if dialog close allowed.
     */
    @Override
    public boolean onClickOk() {
        if (okCommand != null) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    okCommand.execute();
                }
            });
        }

        return true;
    }
}