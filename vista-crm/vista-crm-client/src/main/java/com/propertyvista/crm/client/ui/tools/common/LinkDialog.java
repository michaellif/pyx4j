/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-20
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;

public abstract class LinkDialog extends Dialog implements CancelOption {

    private final Anchor downloadAnchor;

    public LinkDialog(String caption, String anchorLabel, String url) {
        super(caption);
        FlowPanel panel = new FlowPanel();
        downloadAnchor = new Anchor(anchorLabel, url, "_blank");
        downloadAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                LinkDialog.this.hide(false);
            }
        });
        panel.add(downloadAnchor);
        setBody(panel);
    }

}