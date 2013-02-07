/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-04
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.communicationcenter;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.RadioButton;
import com.pyx4j.widgets.client.TextBox;

import com.propertyvista.dto.CommunicationCenterDTO;

/**
 * it will switch the messagesListPanel and the topicsListPanel at Runtime
 * 
 * @author matheszabi
 * 
 */
public class MessagesListControlPanel extends FormFlexPanel {

    private static final I18n i18n = I18n.get(MessagesListControlPanel.class);

    private final Logger log = LoggerFactory.getLogger(MessagesListControlPanel.class);

    private final VerticalPanel parentPanel;

    private final TopicsListPanel topicsListPanel;

    private final MessagesListPanel messagesListPanel;

    private final RadioButton btnTopics;

    private final RadioButton btnMessages;

    private final Button btnAllTopics;

    private final Label lblTopic;

    private final TextBox tbFilterContaining;

    private final Vector<CommunicationCenterDTO> myMessages;// data Model:

    private final CheckBox chckbxHighImportance;

    private final CheckBox chckbxMyFavorites;

    public MessagesListControlPanel(VerticalPanel parentPanel) {
        this.parentPanel = parentPanel;

        myMessages = new Vector<CommunicationCenterDTO>();
        messagesListPanel = new MessagesListPanel();
        topicsListPanel = new TopicsListPanel();

        messagesListPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
        messagesListPanel.setWidth("100%");// will broke the layout!!!
        messagesListPanel.setBorderWidth(1);

        topicsListPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
        topicsListPanel.setWidth("100%");
        topicsListPanel.setBorderWidth(1);

        int row = -1;// for optimization: ++row is faster than row++

        Label lblViewMode = new Label();
        lblViewMode.setText(i18n.tr("View mode") + ":");
        lblViewMode.setSize("81px", "22px");
        this.setWidget(++row, 0, lblViewMode);
        this.getFlexCellFormatter().setColSpan(row, 0, 2);

        btnTopics = new RadioButton("viewMode", i18n.tr("Topics"));
        this.setWidget(row, 2, btnTopics);
        btnTopics.addClickHandler(new TopicsClickHandler());

        btnMessages = new RadioButton("viewMode", i18n.tr("Messages"));
        btnMessages.setValue(true);//setChecked() is deprecated...
        this.setWidget(row, 3, btnMessages);
        btnMessages.addClickHandler(new MessagesClickHandler());

        Label lblFilter = new Label();
        lblFilter.setText(i18n.tr("Filter"));
        lblFilter.setSize("55px", "19px");
        this.setWidget(row, 4, lblFilter);

        chckbxHighImportance = new CheckBox("High Importance");
        chckbxHighImportance.setSize("149px", "20px");
        this.setWidget(row, 5, chckbxHighImportance);
        chckbxHighImportance.addValueChangeHandler(new ChbxHighImportanceHandler());

        chckbxMyFavorites = new CheckBox("My Favorites");
        chckbxMyFavorites.setSize("149px", "20px");
        this.setWidget(row, 6, chckbxMyFavorites);

        //second row:
        btnAllTopics = new Button(i18n.tr("All Topics"));
        this.setWidget(++row, 0, btnAllTopics);
        btnAllTopics.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.alert("btnAllTopics - under construction");
            }
        });
        btnAllTopics.setEnabled(false);

        lblTopic = new Label();
        lblTopic.setText(i18n.tr("Topic") + " > ");
        this.setWidget(row, 1, lblTopic);
        this.getFlexCellFormatter().setColSpan(row, 1, 3);

        Label lblContaining = new Label();
        lblContaining.setText(i18n.tr("Containing") + ":");
        lblContaining.setSize("73px", "19px");
        this.setWidget(row, 3, lblContaining);

        tbFilterContaining = new TextBox();
        tbFilterContaining.setWidth("100%");
        this.setWidget(row, 4, tbFilterContaining);
        this.getFlexCellFormatter().setColSpan(row, 4, 3);

    }

    public MessagesListPanel getMessagesListPanel() {
        return messagesListPanel;
    }

    public void populateMyMessages(Vector<CommunicationCenterDTO> myMessages) {
        this.myMessages.clear();//clone it:
        this.myMessages.addAll(myMessages);

        // TODO based on what is selected: Topic or Messages, set the data model to the viewer

        messagesListPanel.removeAllRows();
        messagesListPanel.populateMyMessages(this.myMessages);

        topicsListPanel.removeAllRows();
        topicsListPanel.populateMyMessages(this.myMessages);

    }

    private class MessagesClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            //Window.alert("btnMessages - under construction");
            parentPanel.remove(topicsListPanel);
            parentPanel.add(messagesListPanel);
        }
    }

    private class TopicsClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            //Window.alert("btnTopics - under construction");
            parentPanel.remove(messagesListPanel);
            parentPanel.add(topicsListPanel);
        }
    }

    private class ChbxHighImportanceHandler implements ValueChangeHandler<Boolean> {

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            setOrRemoveHighImportanceFilter(chckbxHighImportance.getValue().booleanValue());
        }

        private void setOrRemoveHighImportanceFilter(boolean booleanValue) {
            messagesListPanel.removeAllRows();
            messagesListPanel.setOrRemoveHighImportanceFilter(booleanValue);
            topicsListPanel.removeAllRows();
            topicsListPanel.setOrRemoveHighImportanceFilter(booleanValue);
        }

    }
}
