/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.view;

import com.google.gwt.user.client.Command;

import com.pyx4j.site.client.ui.backoffice.prime.AbstractPrimePane;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.MessageDialog.Type;

public abstract class AbstractPrimePaneWithMessagesPopup extends AbstractPrimePane implements HasMessages {

    @Override
    public void displayMessage(String message, Type messageType) {
        MessageDialog.show("", message, messageType);
    }

    @Override
    public void confirm(String message, Command onConfirmed, Command onDeclined) {
        MessageDialog.confirm("", message, onConfirmed, onDeclined);
    }
}
