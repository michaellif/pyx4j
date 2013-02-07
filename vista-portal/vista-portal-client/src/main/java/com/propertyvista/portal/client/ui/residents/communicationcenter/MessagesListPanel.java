/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-06
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.communicationcenter;

import java.util.Vector;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.dto.CommunicationCenterDTO;

public class MessagesListPanel extends FormFlexPanel {

    private Vector<CommunicationCenterDTO> myMessages;

    public void populateMyMessages(Vector<CommunicationCenterDTO> myMessages) {
        this.myMessages = myMessages;
        addHeader();
        addData(false);
    }

    private void addHeader() {
        setHTML(0, 0, "Topic");
        setHTML(0, 1, "Message");
        setHTML(0, 3, "Important");
    }

    private void addData(boolean filterHighImportance) {
        int row = 0;
        for (CommunicationCenterDTO msg : myMessages) {
            if (filterHighImportance && filterHighImportance != msg.isHighImportance().getValue().booleanValue()) {
                continue;
            }
            setHTML(++row, 0, msg.topic().getValue());
            setHTML(row, 1, msg.content().getValue());
            setHTML(row, 3, "" + msg.isHighImportance().getValue());
        }
    }

    public void setOrRemoveHighImportanceFilter(boolean booleanValue) {
        addHeader();
        addData(booleanValue);
    }

}
