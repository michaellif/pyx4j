/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 9, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CSelectorListBox;
import com.pyx4j.widgets.client.dialog.Dialog;

import com.propertyvista.dto.CommunicationEndpointDTO;

public class CommunicationEndpointSelector extends CSelectorListBox<CommunicationEndpointDTO> {

    public CommunicationEndpointSelector() {
        super(new CommunicationEndpointOptionsGrabber(), new Command() {

            @Override
            public void execute() {
                Dialog dialog = new SelectRecipientsDialog();
                dialog.show();

            }
        });
    }
/*-
 @Override
 public boolean isValuesEqual(Collection<CommunicationEndpointDTO> value1, Collection<CommunicationEndpointDTO> value2) {

 return false;
 }
 -*/
}
