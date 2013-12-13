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

import com.pyx4j.widgets.client.dialog.MessageDialog;

public interface HasMessages {

    void displayMessage(String meesage, MessageDialog.Type messageType);

    void confirm(String message, Command onConfirmed, Command onDeclined);

}
