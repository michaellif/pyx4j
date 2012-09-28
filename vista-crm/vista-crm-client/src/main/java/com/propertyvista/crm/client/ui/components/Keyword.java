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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

class Keyword extends Composite {

    interface KeywordUiBinder extends UiBinder<HTMLPanel, Keyword> {
    }

    private static KeywordUiBinder uiBinder = GWT.create(KeywordUiBinder.class);

    @UiField
    Label keywordLabel;

    @UiField
    Label unselectControl;

    private final KeywordsBox parent;

    private final String keyword;

    public Keyword(KeywordsBox keywordsSelectionBox, String keyword) {
        this.keyword = keyword;
        this.parent = keywordsSelectionBox;

        this.initWidget(uiBinder.createAndBindUi(this));

    }

    @UiHandler("unselectControl")
    void unselectHandler(ClickEvent event) {
        parent.unselect(keyword);
    }

    @UiFactory
    Label makeKeywordLabel() {
        Label label = new Label();
        label.setTitle(keyword);
        label.setText(new SafeHtmlBuilder().appendEscaped(keyword).toSafeHtml().asString());
        return label;
    }

}
