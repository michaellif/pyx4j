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
import com.google.gwt.user.client.ui.SplitLayoutPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.dto.CommunicationCenterDTO;

public class CommunicationCenterViewImpl extends SplitLayoutPanel implements CommunicationCenterView {

    private final Logger log = LoggerFactory.getLogger(CommunicationCenterViewImpl.class);

    private static final I18n i18n = I18n.get(CommunicationCenterViewImpl.class);

    private final FormFlexPanel messagesPanel;

    public CommunicationCenterViewImpl() {
        setWidth("100%");
        messagesPanel = new FormFlexPanel();
        messagesPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
        add(messagesPanel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        //list.setPresenter(presenter);
    }

    @Override
    public void populateMyMessages(Vector<CommunicationCenterDTO> myMessages) {
        messagesPanel.removeAllRows();
        messagesPanel.setWidth("100%");
        // server sending the results, first time
        //log.info("Vector<CommunicationCenterDTO> myMessages result is null : " + (myMessages == null));
        if (myMessages != null && myMessages.size() > 0) {
            //log.info("there are {} messages from / to you.", myMessages.size());            

            //TODO: group messages by Topic!
            messagesPanel.getColumnFormatter().setWidth(0, "240px");
            messagesPanel.getColumnFormatter().setWidth(1, "75px");
            messagesPanel.getColumnFormatter().setWidth(2, "145px");
            messagesPanel.getColumnFormatter().setWidth(3, "40px");

            int row = -1;
            messagesPanel.setHTML(++row, 0, i18n.tr("Topic"));
            //messagesPanel.getCellFormatter().getElement(row, 0).getStyle().setPaddingLeft(4, Unit.PX);
            messagesPanel.setHTML(row, 1, i18n.tr("Started By"));
            messagesPanel.setHTML(row, 2, "Last Post");
            messagesPanel.setHTML(row, 3, "Important");// TODO need a checkbox renderer, read only modes

            for (CommunicationCenterDTO msgDto : myMessages) {
                //log.info("msg: " + msgDto.toString());
                messagesPanel.setHTML(++row, 0, msgDto.topic().getValue());
                messagesPanel.setHTML(row, 1, msgDto.sender().userId().getValue().toString());// TODO get the username for this, if this message has no parent..

                messagesPanel.setHTML(row, 2, msgDto.created().getValue().toString());// TODO need to calculate the difference from today
                messagesPanel.setHTML(row, 3, msgDto.isHighImportance().getValue().toString());
            }

            log.info("messagesPanel: " + messagesPanel.toString());
        }//if

    }
}
