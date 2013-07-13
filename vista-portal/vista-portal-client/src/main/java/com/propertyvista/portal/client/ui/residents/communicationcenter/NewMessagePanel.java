/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-28
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.communicationcenter;

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

import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.portal.client.themes.CommunicationCenterTheme;
import com.propertyvista.portal.client.ui.residents.communicationcenter.CommunicationCenterView.Presenter;

public class NewMessagePanel extends TwoColumnFlexFormPanel {

    private static final I18n i18n = I18n.get(CommunicationCenterViewImpl.class);

    private final TextBox tbTopic;

    private final TextArea taMessage;

    private final CheckBox chckbxHighImportance;

    private final TextArea taDestination;

    private final SendCommand sendCommand;

    private final CancelCommand cancelCommand;

    private Presenter presenter;

    public NewMessagePanel() {

        sendCommand = new SendCommand();
        cancelCommand = new CancelCommand();

        getElement().addClassName(CommunicationCenterTheme.StyleName.NewMessagePanelBorder.name());

        int row = -1;// for optimization: ++row is faster than row++

        Label lblNewMessage = new Label();
        lblNewMessage.setText(i18n.tr("Post new message"));
        lblNewMessage.setSize("122px", "22px");
        this.setWidget(++row, 0, lblNewMessage);
        this.getFlexCellFormatter().setColSpan(row, 0, 4);

        Label lblTopic = new Label();
        lblTopic.setText(i18n.tr("Topic") + ":");
        lblTopic.setSize("67px", "22px");
        this.setWidget(++row, 0, lblTopic);

        String msgWidth = "490px";

        tbTopic = new TextBox();
        tbTopic.setSize(msgWidth, "20px");
        this.setWidget(row, 1, tbTopic);
        this.getFlexCellFormatter().setColSpan(row, 1, 3);

        chckbxHighImportance = new CheckBox("High Importance");
        chckbxHighImportance.setSize("149px", "20px");
        this.setWidget(row, 2, chckbxHighImportance);

        Label lblMessage = new Label();
        lblMessage.setText(i18n.tr("Message") + ":");
        lblMessage.setSize("67px", "22px");
        this.setWidget(++row, 0, lblMessage);

        taMessage = new TextArea();
        taMessage.setSize(msgWidth, "90px");
        this.setWidget(row, 1, taMessage);
        this.getFlexCellFormatter().setColSpan(row, 1, 3);
        this.getFlexCellFormatter().setRowSpan(row, 1, 2);

        Label lblDestination = new Label();
        lblDestination.setText(i18n.tr("Destination") + ":");
        lblDestination.setSize("110px", "22px");
        this.setWidget(row, 2, lblDestination);
        lblDestination.getElement().getStyle().setMarginLeft(20, Unit.PX);

        taDestination = new TextArea();
        taDestination.setSize("100px", "62px");
        this.setWidget(++row, 1, taDestination);
        taDestination.getElement().getStyle().setMarginLeft(20, Unit.PX);

        FlowPanel buttonsHolder = new FlowPanel();
        this.setWidget(++row, 1, buttonsHolder);

        Button btnSend = new Button("Send");
        buttonsHolder.add(btnSend);
        btnSend.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        btnSend.getElement().getStyle().setFloat(Style.Float.LEFT);
        btnSend.getElement().getStyle().setMarginBottom(20, Unit.PX);

        Button btnCancel = new Button("Cancel");
        buttonsHolder.add(btnCancel);
        btnCancel.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        btnCancel.getElement().getStyle().setMarginLeft(20, Unit.PX);
        btnCancel.getElement().getStyle().setFloat(Style.Float.LEFT);
        btnCancel.getElement().getStyle().setMarginBottom(20, Unit.PX);

        btnSend.setCommand(sendCommand);

        btnCancel.setCommand(cancelCommand);
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    // TODO Destination chooser from Issue / New MaintenanceRequest
    // IssueClassificationChoice -> CEntityComboBox de tip AbstractUser sau  AbstractPmcUser(CrmUser+CustomerUser)

    private class SendCommand implements Command {

        @Override
        public void execute() {
            //TODO: do a verification and a message: copy-paste from system, somewhere else.
            String topic = tbTopic.getText();
            if (topic == null || topic.length() == 0) {
                Window.alert("Topic must be completed");
                return;
            }
            String messageContent = taMessage.getText();
            if (messageContent == null || messageContent.length() == 0) {
                Window.alert("Message must be completed");
                return;
            }

            boolean isHighImportance = chckbxHighImportance.getValue();

            if (presenter != null) {
                //CrmUser[] destinationsCrm = new CrmUser[0];
                //CustomerUser[] destinationsCustomer = new CustomerUser[0];
                AbstractUser[] destinations = new AbstractUser[0];

                presenter.sendNewMessage(topic, messageContent, isHighImportance, null);

            }
        }
    }

    private class CancelCommand implements Command {

        @Override
        public void execute() {
            tbTopic.setText("");
            taMessage.setText("");
            chckbxHighImportance.setValue(Boolean.FALSE);
            taDestination.setText("");
        }
    }

    public void cleanFields() {
        tbTopic.setText("");
        taMessage.setText("");
    }

}
