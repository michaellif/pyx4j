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

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;

import com.propertyvista.dto.CommunicationCenterDTO;

public class TopicsListPanel extends FormFlexPanel {

    private final LinkedHashMap<String, ArrayList<CommunicationCenterDTO>> topicMessages;

    private final ArrayList<String> topics;

    public TopicsListPanel() {
        topicMessages = new LinkedHashMap<String, ArrayList<CommunicationCenterDTO>>();
        topics = new ArrayList<String>();
    }

    public void populateMyMessages(Vector<CommunicationCenterDTO> myMessages) {
        // rem: the myMessages are sorted by creation date: the new one is first
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

        addData(false);

    }

    private void addHeader() {
        setHTML(0, 0, "Topic");
        setHTML(0, 1, "Started By(ID*)");
        setHTML(0, 2, "Last post*");
        setHTML(0, 3, "Important");
    }

    private void addData(boolean filterHighImportance) {
        // add all
        Date now = new Date();

        int row = 0;
        for (String curTopic : topics) {
            ArrayList<CommunicationCenterDTO> messages = topicMessages.get(curTopic);
            if (messages != null && messages.size() > 0) {
                CommunicationCenterDTO mostRecentMessageInTopic = messages.get(0);
                CommunicationCenterDTO firstMessageInTopic = messages.get(messages.size() - 1);
                if (filterHighImportance && firstMessageInTopic.isHighImportance().getValue().booleanValue() != filterHighImportance) {
                    continue;
                }
                long diffMillisec = now.getTime() - mostRecentMessageInTopic.created().getValue().getTime();
                setHTML(++row, 0, firstMessageInTopic.topic().getValue());
                setHTML(row, 1, "" + firstMessageInTopic.sender().userId().getValue());
                setHTML(row, 2, "" + (diffMillisec / 1000) + " seconds ago");//difference it is in seconds         
                setHTML(row, 3, "" + firstMessageInTopic.isHighImportance().getValue());
            }
        }
    }

    @SuppressWarnings("unused")
    private void displayMessagesAsIs(Vector<CommunicationCenterDTO> myMessages) {
        addHeader();
        int row = 0;

        for (CommunicationCenterDTO msg : myMessages) {
            setHTML(++row, 0, msg.topic().getValue());
            setHTML(row, 1, "" + msg.sender().userId().getValue());
            setHTML(row, 2, msg.created().getValue().toString());//TODO format date
            setHTML(row, 3, "" + msg.isHighImportance().getValue());
        }
    }

    public void setOrRemoveHighImportanceFilter(boolean filterValue) {
        addHeader();
        addData(filterValue);
    }
}
