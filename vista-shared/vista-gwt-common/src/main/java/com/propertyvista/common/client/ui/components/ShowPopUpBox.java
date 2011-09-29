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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Implements all that mechanics to show pop-up box in the centre of the current window.
 * 
 * @author Vlad
 * 
 * @param <T>
 *            - popup box which should be showed.
 */
public abstract class ShowPopUpBox<T extends PopupPanel> {

    private final T box;

    public ShowPopUpBox(T box) {
        this.box = box;
        this.box.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                ShowPopUpBox.this.onClose(ShowPopUpBox.this.box);
            }
        });
        this.box.center();
    }

    /**
     * Implements in derived class to get result from your box.
     */
    protected abstract void onClose(T box);
}
