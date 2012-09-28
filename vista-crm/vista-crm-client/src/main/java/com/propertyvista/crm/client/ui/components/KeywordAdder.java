/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 28, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class KeywordAdder extends Composite {

    private static KeywordAdderUiBinder uiBinder = GWT.create(KeywordAdderUiBinder.class);

    interface KeywordAdderUiBinder extends UiBinder<Widget, KeywordAdder> {
    }

    @UiField
    Button button;

    @UiField
    ListBox keywordsList;

    private final KeywordsBox parentSelectionBox;

    public KeywordAdder(KeywordsBox parentSelectionBox) {
        this.parentSelectionBox = parentSelectionBox;
        initWidget(uiBinder.createAndBindUi(this));

    }

    public void setAvaiableKeywords(Set<String> keywords) {
        keywordsList.clear();
        keywordsList.addItem("");
        for (String keyword : keywords) {
            keywordsList.addItem(keyword);
        }
    }

    @UiHandler("button")
    void onClick(ClickEvent e) {
        if (keywordsList.getSelectedIndex() > 0) { // the first item is blank
            parentSelectionBox.select(keywordsList.getItemText(keywordsList.getSelectedIndex()));
        }
    }

}
