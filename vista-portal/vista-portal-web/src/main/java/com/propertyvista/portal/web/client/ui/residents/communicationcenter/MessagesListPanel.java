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
package com.propertyvista.portal.web.client.ui.residents.communicationcenter;

import java.util.Vector;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLTable;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;

import com.propertyvista.dto.CommunicationCenterDTO;
import com.propertyvista.portal.web.client.themes.CommunicationCenterTheme;

public class MessagesListPanel extends FormFlexPanel {

    private static final I18n i18n = I18n.get(MessagesListPanel.class);

    private Vector<CommunicationCenterDTO> myMessages;

    private boolean highRemoveHighImportance;

    private String stringFilter;

    private final CommunicationCenterView parentPanel;

    public MessagesListPanel(CommunicationCenterView parentPanel) {
        this.parentPanel = parentPanel;
    }

    public void populateMyMessages(Vector<CommunicationCenterDTO> myMessages) {
        this.myMessages = myMessages;
        addHeader();
        addData(highRemoveHighImportance, stringFilter);
    }

    private void addHeader() {
        setHTML(0, 0, "Topic");
        setHTML(0, 1, "Message");
        setHTML(0, 2, "Reply");
        setHTML(0, 3, "Important");
    }

    private void addData(boolean filterHighImportance, String filter) {
        highRemoveHighImportance = filterHighImportance;
        stringFilter = filter;
        HTMLTable.RowFormatter rf = getRowFormatter();
        HTMLTable.CellFormatter cf = getCellFormatter();
        int row = 0;
        if (myMessages != null) {
            for (CommunicationCenterDTO msg : myMessages) {
                if (filterHighImportance && filterHighImportance != msg.isHighImportance().getValue().booleanValue()) {
                    continue;
                }
                if (stringFilter != null && stringFilter.length() > 0) {//filter by message content
                    if (msg == null || msg.content() == null || msg.content().getValue() == null) {
                        continue;
                    }
                    if (!msg.content().getValue().toLowerCase().contains(stringFilter.toLowerCase())
                            && !msg.topic().getValue().toLowerCase().contains(stringFilter.toLowerCase())) {//case insensitive
                        continue;
                    }
                }

                if (row == 0) {
                    rf.addStyleName(row, CommunicationCenterTheme.StyleName.CommunicationTableHeaderRowBg.name());
                    cf.addStyleName(row, 1, CommunicationCenterTheme.StyleName.CommunicationTableHeaderVB.name());
                    cf.addStyleName(row, 2, CommunicationCenterTheme.StyleName.CommunicationTableHeaderVB.name());
                    cf.addStyleName(row, 3, CommunicationCenterTheme.StyleName.CommunicationTableHeaderVB.name());
                }

                setHTML(++row, 0, msg.topic().getValue());
                setHTML(row, 1, msg.content().getValue());
                cf.addStyleName(row, 1, CommunicationCenterTheme.StyleName.CommunicationTableVerticalBorder.name());

                Button btReply = new Button(i18n.tr("Reply"));
                btReply.getElement().addClassName(CommunicationCenterTheme.StyleName.CommunicationTableButton.name());
                setWidget(row, 2, btReply);
                btReply.setCommand(new ReplyCommand(msg));

                //setHTML(row, 3, "" + msg.isHighImportance().getValue());
                CheckBox ch = new CheckBox();
                ch.setValue(msg.isHighImportance().getValue());
                ch.setEditable(false);
                cf.addStyleName(row, 3, CommunicationCenterTheme.StyleName.CommunicationTableChechBox.name());

                setWidget(row, 3, ch);

                if ((row % 2) == 0) {
                    rf.addStyleName(row, CommunicationCenterTheme.StyleName.CommunicationTableSecondRowBg.name());
                    btReply.getElement().addClassName(CommunicationCenterTheme.StyleName.CommunicationTableSecondButtonBG.name());
                } else {
                    rf.addStyleName(row, CommunicationCenterTheme.StyleName.CommunicationTableFirstRowBg.name());
                    btReply.getElement().addClassName(CommunicationCenterTheme.StyleName.CommunicationTableFirstButtonBG.name());
                }

            }
        }
    }

    public void setOrRemoveHighImportanceFilter(boolean filterValue) {
        highRemoveHighImportance = filterValue;
        addHeader();
        addData(highRemoveHighImportance, stringFilter);
    }

    public void setOrRemoveStringFilter(String filterValue) {
        stringFilter = filterValue;
        if (stringFilter != null) {
            stringFilter = stringFilter.trim();
        }
        addHeader();
        addData(highRemoveHighImportance, stringFilter);
    }

    private class ReplyCommand implements Command {

        private final CommunicationCenterDTO msgOrig;

        private ReplyCommand(CommunicationCenterDTO msgOrig) {
            this.msgOrig = msgOrig;
        }

        @Override
        public void execute() {
            parentPanel.viewReplyToMessage(msgOrig);
        }
    }

    public void setOrRemoveTopicFilter(CommunicationCenterDTO dto) {
        // TODO Auto-generated method stub

    }

}
