/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.communicationcenter;

import java.util.Vector;

import com.google.gwt.user.client.ui.IsWidget;

import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.dto.CommunicationCenterDTO;

public interface CommunicationCenterView extends IsWidget {

    interface Presenter {

        void sendNewMessage(String topic, String messageContent, boolean isHighImportance, AbstractUser[] destinations);

        void sendReply(String topic, String messageContent, boolean isHighImportance, CommunicationCenterDTO parentMessage);

    }

    void setPresenter(Presenter presenter);

    /**
     * Data is coming from server side, the viewers should display and cache it
     * 
     * @param myMessages
     */
    void populateMyMessages(Vector<CommunicationCenterDTO> myMessages);

    /**
     * View new massage and the list of massages and topics
     */
    void viewDefault();

    /**
     * Display a component where the user will see the original message and is able to reply it
     * 
     * @param msg
     */
    void viewReplyToMessage(CommunicationCenterDTO msg);

}
