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
import com.google.gwt.user.client.ui.Button;

import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.OkCancelOption;

/**
 * Draws pop-up dialog box with OK and Cancel buttons and
 * user definable content (by means of {@link #createContent()}) inside.
 * 
 * @author Vlad
 * 
 */
public abstract class OkCancelBox extends OkBox implements OkCancelOption {

    public OkCancelBox(String caption) {
        super(caption);
    }

    public Button getCancelButton() {
        return dialog.getCancelButton();
    }

    public void run(final OkCancelOption okCancelOption) {
        run(okCancelOption);
    }

    /**
     * Override for some meaningful action.
     * 
     * Note: always call super.onClickCancel() last.
     * 
     * @return true if dialog close allowed.
     */
    @Override
    public boolean onClickCancel() {
        if (options instanceof CancelOption) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    ((CancelOption) options).onClickCancel();
                }
            });
        }
        return true;
    }
}