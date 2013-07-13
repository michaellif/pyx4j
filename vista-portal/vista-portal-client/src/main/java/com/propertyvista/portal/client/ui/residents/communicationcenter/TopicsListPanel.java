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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Vector;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Anchor;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;

import com.propertyvista.dto.CommunicationCenterDTO;
import com.propertyvista.portal.client.themes.CommunicationCenterTheme;

public class TopicsListPanel extends TwoColumnFlexFormPanel {

    private static final I18n i18n = I18n.get(TopicsListPanel.class);

    private final LinkedHashMap<String, ArrayList<CommunicationCenterDTO>> topicMessages;

    private final ArrayList<String> topics;

    private boolean highRemoveHighImportance;

    private String stringFilter;

    private final CommunicationCenterView parentPanel;

    private final VerticalPanel verticalPanel;

    private final Button btReply;

    private CommunicationCenterDTO onlyOneTopicStarterMessage = null;

    private final MessagesListControlPanel messagesListControlPanel;

    public TopicsListPanel(CommunicationCenterView parentPanel, MessagesListControlPanel messagesListControlPanel) {
        this.parentPanel = parentPanel;
        this.messagesListControlPanel = messagesListControlPanel;
        if (parentPanel instanceof VerticalPanel) {
            verticalPanel = (VerticalPanel) parentPanel;
        } else {
            throw new IllegalArgumentException("CommunicationCenterView must be a VerticalPanel");
        }

        topicMessages = new LinkedHashMap<String, ArrayList<CommunicationCenterDTO>>();
        topics = new ArrayList<String>();

        btReply = new Button(i18n.tr("Reply"));
        btReply.setCommand(new ReplyCommand());
        btReply.getElement().getStyle().setFloat(Style.Float.LEFT);

    }

    public void populateMyMessages(Vector<CommunicationCenterDTO> myMessages) {
        // remark: the myMessages are sorted by creation date: the new one is first
        addHeader();

        topicMessages.clear();
        topics.clear();
        // group by topic:
        for (CommunicationCenterDTO msg : myMessages) {
            String curTopic = msg.topic().getValue();
            if (!topics.contains(curTopic)) {
                topics.add(curTopic);
            }
            ArrayList<CommunicationCenterDTO> messages = topicMessages.get(curTopic);
            if (messages == null) {
                messages = new ArrayList<CommunicationCenterDTO>();
            }
            messages.add(msg);
            topicMessages.put(curTopic, messages);
        }
        addData(false, null);
    }

    private void addHeader() {
        setHTML(0, 0, "Topic");
        setHTML(0, 1, "Started By");
        setHTML(0, 2, "Last post*");
        setHTML(0, 3, "Important");
    }

    private void addData(boolean filterHighImportance, String filter) {
        highRemoveHighImportance = filterHighImportance;
        stringFilter = filter;
        verticalPanel.remove(btReply);

        // add all
        Date now = new Date();
        HTMLTable.RowFormatter rf = getRowFormatter();
        HTMLTable.CellFormatter cf = getCellFormatter();
        int row = 0;
        onlyOneTopicStarterMessage = null;
        for (String curTopic : topics) {
            if (curTopic == null) {// database error
                continue;
            }
            ArrayList<CommunicationCenterDTO> messages = topicMessages.get(curTopic);
            if (messages != null && messages.size() > 0) {
                CommunicationCenterDTO mostRecentMessageInTopic = messages.get(0);
                CommunicationCenterDTO firstMessageInTopic = messages.get(messages.size() - 1);
                // filter by High Importance:
                if (filterHighImportance && firstMessageInTopic.isHighImportance().getValue().booleanValue() != filterHighImportance) {
                    continue;
                }
                //filter by message content:
                if ((stringFilter != null) && (stringFilter.length() > 0)) {

                    if (!curTopic.toLowerCase().contains(stringFilter.toLowerCase())) {
                        boolean foundInMessages = false;
                        for (CommunicationCenterDTO msg : messages) {
                            if (msg.content().getValue().toLowerCase().contains(stringFilter.toLowerCase())) {
                                foundInMessages = true;
                                break;
                            }
                        }
                        if (!foundInMessages) {
                            continue;
                        }
                    }
                }

                onlyOneTopicStarterMessage = firstMessageInTopic;

                long diffMillisec = now.getTime() - mostRecentMessageInTopic.created().getValue().getTime();

                if (row == 0) {
                    rf.addStyleName(row, CommunicationCenterTheme.StyleName.CommunicationTableHeaderRowBg.name());
                    cf.addStyleName(row, 1, CommunicationCenterTheme.StyleName.CommunicationTableHeaderVB.name());
                    cf.addStyleName(row, 2, CommunicationCenterTheme.StyleName.CommunicationTableHeaderVB.name());
                    cf.addStyleName(row, 3, CommunicationCenterTheme.StyleName.CommunicationTableHeaderVB.name());
                }

                //setHTML(++row, 0, firstMessageInTopic.topic().getValue());
                String text = firstMessageInTopic.topic().getValue();
                Anchor anchor = new Anchor(text, new TopicClickedCommand(firstMessageInTopic));
                setWidget(++row, 0, anchor);

                setHTML(row, 1, "" + firstMessageInTopic.senderName().getValue());
                cf.addStyleName(row, 1, CommunicationCenterTheme.StyleName.CommunicationTableVerticalBorder.name());

                setHTML(row, 2, "" + (diffMillisec / 1000) + " seconds ago");//difference it is in seconds
                CheckBox ch = new CheckBox();
                ch.setEditable(false);
                ch.setValue(firstMessageInTopic.isHighImportance().getValue());

                cf.addStyleName(row, 3, CommunicationCenterTheme.StyleName.CommunicationTableChechBox.name());
                setWidget(row, 3, ch);

                if ((row % 2) == 0) {
                    rf.addStyleName(row, CommunicationCenterTheme.StyleName.CommunicationTableSecondRowBg.name());
                } else {
                    rf.addStyleName(row, CommunicationCenterTheme.StyleName.CommunicationTableFirstRowBg.name());
                }
            }
        }

        if (row == 1) {
            if (getParent() != null) {
                verticalPanel.add(btReply);
            }
        } else {
            onlyOneTopicStarterMessage = null;
        }
    }

    public void setOrRemoveHighImportanceFilter(boolean filterValue) {
        addHeader();
        addData(filterValue, stringFilter);
    }

    @Override
    protected void onAttach() {//it will be removed this reply button
        super.onAttach();
        if (getRowCount() == 2 && this.isVisible()) {// header + 1 row
            verticalPanel.add(btReply);
        }
    }

    @Override
    protected void onDetach() {//it will be removed the reply button
        verticalPanel.remove(btReply);
        super.onDetach();
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
        @Override
        public void execute() {
            parentPanel.viewReplyToMessage(onlyOneTopicStarterMessage);
        }
    }

    private class TopicClickedCommand implements Command {
        private final CommunicationCenterDTO dto;

        public TopicClickedCommand(CommunicationCenterDTO dto) {
            this.dto = dto;
        }

        @Override
        public void execute() {
            //Window.alert("Clicked topic is:" + topic);
            if (messagesListControlPanel != null) {
                messagesListControlPanel.filterByTopic(dto);
            }
        }
    }

    public void setOrRemoveTopicFilter(CommunicationCenterDTO dto) {
        // TODO Auto-generated method stub

    }
}
