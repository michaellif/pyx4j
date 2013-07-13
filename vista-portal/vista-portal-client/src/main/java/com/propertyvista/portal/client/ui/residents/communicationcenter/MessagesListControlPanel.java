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

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.RadioButton;
import com.pyx4j.widgets.client.TextBox;

import com.propertyvista.dto.CommunicationCenterDTO;
import com.propertyvista.portal.client.themes.CommunicationCenterTheme;

/**
 * it will switch the messagesListPanel and the topicsListPanel at Runtime
 * 
 * @author matheszabi
 * 
 */
public class MessagesListControlPanel extends TwoColumnFlexFormPanel {

    private static final I18n i18n = I18n.get(MessagesListControlPanel.class);

    private final Logger log = LoggerFactory.getLogger(MessagesListControlPanel.class);

    private final CommunicationCenterView parentPanel;

    private final VerticalPanel verticalPanel;

    private final TopicsListPanel topicsListPanel;

    private final MessagesListPanel messagesListPanel;

    private final RadioButton btnTopics;

    private final RadioButton btnMessages;

    private final Button btnAllTopics;

    private final Label lblTopic;

    private final TextBox tbFilterContaining;

    private final Vector<CommunicationCenterDTO> myMessages;

    private final CheckBox chckbxHighImportance;

    private final CheckBox chckbxMyFavorites;

    public MessagesListControlPanel(CommunicationCenterView parentPanel) {
        this.parentPanel = parentPanel;

        if (parentPanel instanceof VerticalPanel) {
            verticalPanel = (VerticalPanel) parentPanel;
        } else {
            throw new IllegalArgumentException("CommunicationCenterView must be a VerticalPanel");
        }

        myMessages = new Vector<CommunicationCenterDTO>();
        messagesListPanel = new MessagesListPanel(parentPanel);
        topicsListPanel = new TopicsListPanel(parentPanel, this);

        HTMLTable.CellFormatter cf = getCellFormatter();

        getElement().addClassName(CommunicationCenterTheme.StyleName.NewMessageControlPanelBorder.name());

        messagesListPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
        messagesListPanel.setWidth("100%");
        messagesListPanel.setBorderWidth(0);
        messagesListPanel.setStylePrimaryName(CommunicationCenterTheme.StyleName.CommunicationTableBorder.name());

        topicsListPanel.getElement().getStyle().setMarginBottom(20, Unit.PX);
        topicsListPanel.setWidth("100%");
        topicsListPanel.setBorderWidth(0);
        topicsListPanel.setStylePrimaryName(CommunicationCenterTheme.StyleName.CommunicationTableBorder.name());

        int row = -1;// for optimization: ++row is faster than row++

        Label lblViewMode = new Label();
        lblViewMode.setText(i18n.tr("View mode") + ":");
        lblViewMode.setSize("81px", "22px");
        lblViewMode.getElement().getStyle().setMarginTop(5, Unit.PX);
        lblViewMode.getElement().getStyle().setMarginLeft(5, Unit.PX);
        this.setWidget(++row, 0, lblViewMode);
        this.getFlexCellFormatter().setColSpan(row, 0, 2);

        btnTopics = new RadioButton("viewMode", i18n.tr("Topics"));
        this.setWidget(row, 2, btnTopics);
        btnTopics.addClickHandler(new TopicsClickHandler());
        btnTopics.getElement().getStyle().setMarginTop(35, Unit.PX);

        btnMessages = new RadioButton("viewMode", i18n.tr("Messages"));
        btnMessages.setValue(true);//setChecked() is deprecated...
        this.setWidget(row, 3, btnMessages);
        btnMessages.addClickHandler(new MessagesClickHandler());
        btnMessages.getElement().getStyle().setMarginTop(35, Unit.PX);

        Label lblFilter = new Label();
        lblFilter.setText(i18n.tr("Filter"));
        lblFilter.setSize("55px", "19px");
        lblFilter.getElement().getStyle().setMarginTop(5, Unit.PX);
        lblFilter.getElement().getStyle().setMarginLeft(5, Unit.PX);
        cf.addStyleName(row, 4, CommunicationCenterTheme.StyleName.CommunicationTableHeaderVB.name());
        this.setWidget(row, 4, lblFilter);

        chckbxHighImportance = new CheckBox("High Importance");
        chckbxHighImportance.setSize("149px", "20px");
        this.setWidget(row, 5, chckbxHighImportance);
        chckbxHighImportance.addValueChangeHandler(new ChbxHighImportanceHandler());
        chckbxHighImportance.getElement().getStyle().setMarginTop(35, Unit.PX);

        chckbxMyFavorites = new CheckBox("My Favorites");
        chckbxMyFavorites.setSize("149px", "20px");
        this.setWidget(row, 6, chckbxMyFavorites);
        chckbxMyFavorites.getElement().getStyle().setMarginTop(35, Unit.PX);

        //second row:
        btnAllTopics = new Button(i18n.tr("All Topics"), new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub
                Window.alert("btnAllTopics - under construction - should remove the text search value");
            }

        });
        this.setWidget(++row, 0, btnAllTopics);
        btnAllTopics.setEnabled(false);
        btnAllTopics.getElement().getStyle().setFloat(Style.Float.LEFT);
        btnAllTopics.getElement().getStyle().setMarginBottom(5, Unit.PX);
        btnAllTopics.getElement().getStyle().setMarginLeft(5, Unit.PX);

        lblTopic = new Label();
        lblTopic.setText(i18n.tr("Topic") + " > ");
        this.setWidget(row, 1, lblTopic);
        this.getFlexCellFormatter().setColSpan(row, 1, 3);

        Label lblContaining = new Label();
        lblContaining.setText(i18n.tr("Containing") + ":");
        lblContaining.setSize("73px", "19px");
        lblContaining.getElement().getStyle().setMarginLeft(5, Unit.PX);
        cf.addStyleName(row, 3, CommunicationCenterTheme.StyleName.CommunicationTableHeaderVB.name());
        this.setWidget(row, 3, lblContaining);

        tbFilterContaining = new TextBox();
        tbFilterContaining.setWidth("95%");
        this.setWidget(row, 4, tbFilterContaining);
        this.getFlexCellFormatter().setColSpan(row, 4, 3);
        tbFilterContaining.addChangeHandler(new StringFilterHandler());

    }

    public MessagesListPanel getMessagesListPanel() {
        return messagesListPanel;
    }

    public void populateMyMessages(Vector<CommunicationCenterDTO> myMessages) {
        this.myMessages.clear();//clone it:
        this.myMessages.addAll(myMessages);

        messagesListPanel.removeAllRows();
        messagesListPanel.populateMyMessages(this.myMessages);

        topicsListPanel.removeAllRows();
        topicsListPanel.populateMyMessages(this.myMessages);
    }

    private class MessagesClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            verticalPanel.remove(topicsListPanel);
            verticalPanel.add(messagesListPanel);
        }
    }

    private class TopicsClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            verticalPanel.remove(messagesListPanel);
            verticalPanel.add(topicsListPanel);
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

    private class StringFilterHandler implements ChangeHandler {

        @Override
        public void onChange(ChangeEvent event) {
            String filter = tbFilterContaining.getText();
            setOrRemoveTextFilter(filter);
        }

        private void setOrRemoveTextFilter(String filter) {
            messagesListPanel.removeAllRows();
            messagesListPanel.setOrRemoveStringFilter(filter);

            topicsListPanel.removeAllRows();
            topicsListPanel.setOrRemoveStringFilter(filter);
        }
    }

    public void cleanFields() {// after Reply reset the UI state to initial
        chckbxHighImportance.setValue(false);
        tbFilterContaining.setText("");
        btnMessages.setValue(true);
        messagesListPanel.setOrRemoveHighImportanceFilter(false);
        topicsListPanel.setOrRemoveHighImportanceFilter(false);
        messagesListPanel.setOrRemoveStringFilter(null);
        topicsListPanel.setOrRemoveStringFilter(null);
    }

    public void filterByTopic(CommunicationCenterDTO dto) {
        setOrRemoveTopicFilter(dto);
    }

    private void setOrRemoveTopicFilter(CommunicationCenterDTO dto) {
        messagesListPanel.removeAllRows();
        messagesListPanel.setOrRemoveTopicFilter(dto);

        topicsListPanel.removeAllRows();
        topicsListPanel.setOrRemoveTopicFilter(dto);

    }
}
