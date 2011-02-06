/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.admin.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.widgets.client.GlassPanel;
import com.pyx4j.widgets.client.GlassPanel.GlassStyle;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class AsyncEntryPoints {

    private static boolean loginGlassOnce = true;

    public static void showLogInPanel(final boolean prefetch) {
        final boolean glassShown;
        if (!prefetch && loginGlassOnce) {
            GlassPanel.show(GlassStyle.SemiTransparent);
            glassShown = true;
        } else {
            glassShown = false;
        }
        GWT.runAsync(LogInPanel.class, new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable reason) {
                if (prefetch) {
                    return;
                }
                if (glassShown) {
                    GlassPanel.hide();
                }
                throw new UnrecoverableClientError(reason);
            }

            @Override
            public void onSuccess() {
                loginGlassOnce = false;
                if (!prefetch) {
                    LogInPanel.show();
                }
                if (glassShown) {
                    GlassPanel.hide();
                }
            }

        });
    }

    public static void prefetch() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                MessageDialog.prefetch();
                showLogInPanel(true);
            }
        });
    }
}
