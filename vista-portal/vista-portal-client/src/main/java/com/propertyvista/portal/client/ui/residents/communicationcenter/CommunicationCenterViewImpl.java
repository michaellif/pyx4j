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
package com.propertyvista.portal.client.ui.residents.communicationcenter;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.dto.CommunicationCenterDTO;

public class CommunicationCenterViewImpl extends VerticalPanel implements CommunicationCenterView {

    private final Logger log = LoggerFactory.getLogger(CommunicationCenterViewImpl.class);

    private static final I18n i18n = I18n.get(CommunicationCenterViewImpl.class);

    private final NewMessagePanel newMessagePanel;

    //private final FormFlexPanel messageListPanel;

    private final MessagesListControlPanel messagesListControlPanel;

    public CommunicationCenterViewImpl() {

        newMessagePanel = new NewMessagePanel();

        messagesListControlPanel = new MessagesListControlPanel(this);

        newMessagePanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
        messagesListControlPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);

        add(newMessagePanel);
        add(messagesListControlPanel);
        add(messagesListControlPanel.getMessagesListPanel());
    }

    @Override
    public void setPresenter(Presenter presenter) {
        newMessagePanel.setPresenter(presenter);
    }

    @Override
    public void populateMyMessages(Vector<CommunicationCenterDTO> myMessages) {

        messagesListControlPanel.populateMyMessages(myMessages);

    }
}
