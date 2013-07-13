/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-11
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.residents.communicationcenter;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.TextArea;
import com.pyx4j.widgets.client.TextBox;

import com.propertyvista.dto.CommunicationCenterDTO;

public class ReplyPanel extends TwoColumnFlexFormPanel {

    private static final I18n i18n = I18n.get(ReplyPanel.class);

    private final TextBox tbTopicTop;

    private final TextArea taMessageTop;

    private final TextBox tbTopicBottom;

    private final TextArea taMessageBottom;

    private final CheckBox chckbxHighImportance;

    private final SendCommand sendCommand;

    private final CancelCommand cancelCommand;

    private CommunicationCenterDTO parentMessage;

    private final CommunicationCenterView parentPanel;

    private CommunicationCenterView.Presenter presenter;

    public ReplyPanel(CommunicationCenterView parentPanel) {
        this.parentPanel = parentPanel;

        sendCommand = new SendCommand();
        cancelCommand = new CancelCommand();

        int row = -1;// for optimization: ++row is faster than row++

        Label lblWriteReply = new Label();
        lblWriteReply.setText(i18n.tr("Write a reply"));
        lblWriteReply.setSize("122px", "22px");
        this.setWidget(++row, 0, lblWriteReply);
        this.getFlexCellFormatter().setColSpan(row, 0, 2);
        lblWriteReply.getElement().getStyle().setMarginBottom(20, Unit.PX);
        //TODO: make a bigger Font:lblWriteReply

        Label lblInReplyTo = new Label();
        lblInReplyTo.setText(i18n.tr("In reply to") + ":");
        lblInReplyTo.setSize("122px", "22px");
        this.setWidget(++row, 0, lblInReplyTo);

        Label lblTopicTop = new Label();
        lblTopicTop.setText(i18n.tr("Topic") + ":");
        lblTopicTop.setSize("67px", "22px");
        this.setWidget(++row, 0, lblTopicTop);

        String msgWidth = "490px";

        tbTopicTop = new TextBox();
        tbTopicTop.setSize(msgWidth, "20px");
        tbTopicTop.setEditable(false);
        this.setWidget(row, 1, tbTopicTop);

        Label lblMessageTop = new Label();
        lblMessageTop.setText(i18n.tr("Message") + ":");
        lblMessageTop.setSize("67px", "22px");
        this.setWidget(++row, 0, lblMessageTop);

        taMessageTop = new TextArea();
        taMessageTop.setSize(msgWidth, "90px");
        taMessageTop.setEditable(false);
        this.setWidget(row, 1, taMessageTop);
        taMessageTop.getElement().getStyle().setMarginBottom(20, Unit.PX);

        Label lblYourReply = new Label();
        lblYourReply.setText(i18n.tr("Your reply") + ":");
        lblYourReply.setSize("122px", "22px");
        this.setWidget(++row, 0, lblYourReply);

        Label lblTopicBottom = new Label();
        lblTopicBottom.setText(i18n.tr("Topic") + ":");
        lblTopicBottom.setSize("67px", "22px");
        this.setWidget(++row, 0, lblTopicBottom);

        tbTopicBottom = new TextBox();
        tbTopicBottom.setSize(msgWidth, "20px");
        tbTopicBottom.setEnabled(false);
        this.setWidget(row, 1, tbTopicBottom);

        Label lblMessageBottom = new Label();
        lblMessageBottom.setText(i18n.tr("Message") + ":");
        lblMessageBottom.setSize("67px", "22px");
        this.setWidget(++row, 0, lblMessageBottom);

        taMessageBottom = new TextArea();
        taMessageBottom.setSize(msgWidth, "90px");
        this.setWidget(row, 1, taMessageBottom);
        taMessageBottom.getElement().getStyle().setMarginBottom(20, Unit.PX);

        FlowPanel buttonsHolder = new FlowPanel();
        this.setWidget(++row, 1, buttonsHolder);

        Button btnSend = new Button(i18n.tr("Send"));
        buttonsHolder.add(btnSend);
        btnSend.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        btnSend.getElement().getStyle().setFloat(Style.Float.LEFT);

        Button btnCancel = new Button(i18n.tr("Cancel"));
        buttonsHolder.add(btnCancel);
        btnCancel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        btnCancel.getElement().getStyle().setMarginLeft(20, Unit.PX);
        btnCancel.getElement().getStyle().setFloat(Style.Float.LEFT);

        chckbxHighImportance = new CheckBox(i18n.tr("High Importance"));
        chckbxHighImportance.setSize("149px", "20px");
        buttonsHolder.add(chckbxHighImportance);
        chckbxHighImportance.getElement().getStyle().setMarginLeft(20, Unit.PX);

        btnSend.setCommand(sendCommand);

        btnCancel.setCommand(cancelCommand);

    }

    public void setPresenter(CommunicationCenterView.Presenter presenter) {
        this.presenter = presenter;
    }

    private class SendCommand implements Command {

        @Override
        public void execute() {
            String topic = tbTopicBottom.getText();
            if (topic == null || topic.length() == 0) {
                Window.alert("Topic must be completed");
                return;
            }
            String messageContent = taMessageBottom.getText();
            if (messageContent == null || messageContent.length() == 0) {
                Window.alert("Message must be completed");
                return;
            }
            boolean isHighImportance = chckbxHighImportance.getValue();

            presenter.sendReply(topic, messageContent, isHighImportance, parentMessage);

        }
    }

    private class CancelCommand implements Command {
        @Override
        public void execute() {
            parentPanel.viewDefault();
        }
    }

    public void setParentMessage(CommunicationCenterDTO msg) {
        parentMessage = msg;
        displayParentMessage();
    }

    private void displayParentMessage() {
        if (parentMessage != null) {
            tbTopicTop.setText(parentMessage.topic().getValue());
            taMessageTop.setText(parentMessage.content().getValue());
            tbTopicBottom.setText(parentMessage.topic().getValue());
            taMessageBottom.setText("");
        } else {
            tbTopicTop.setText("");
            taMessageTop.setText("");
            tbTopicBottom.setText("");
            taMessageBottom.setText("");
        }
    }
}
