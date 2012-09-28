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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class KeywordsBox extends Composite {

    interface KeywordsBoxUiBinder extends UiBinder<Widget, KeywordsBox> {
    };

    private static KeywordsBoxUiBinder uiBinder = GWT.create(KeywordsBoxUiBinder.class);

    @UiField
    FlowPanel activeKeywordsPanel;

    @UiField
    KeywordAdder keywordAdder;

    private final List<String> activeKeywords;

    private final List<String> potentialKeywords;

    public KeywordsBox() {
        activeKeywords = new ArrayList<String>();
        potentialKeywords = new ArrayList<String>();

        initWidget(uiBinder.createAndBindUi(this));
    }

    public void select(String keyword) {
        activeKeywords.add(keyword);
        potentialKeywords.remove(keyword);

        redraw(true);
    }

    public void unselect(String keyword) {
        activeKeywords.remove(keyword);
        potentialKeywords.add(keyword);

        redraw(true);
    }

    public void setKeywords(Set<String> keywords, boolean execCallback) {
        potentialKeywords.clear();
        potentialKeywords.addAll(keywords);
        activeKeywords.clear();
        redraw(execCallback);
    }

    protected abstract void onKeywordsChanged(Set<String> keywords);

    @UiFactory
    KeywordAdder makeKeywordAdder() {
        return new KeywordAdder(this);
    }

    private void redraw(boolean execCallback) {
        activeKeywordsPanel.clear();
        for (String keyword : activeKeywords) {
            activeKeywordsPanel.add(new Keyword(this, keyword));
        }
        keywordAdder.setAvaiableKeywords(new HashSet<String>(potentialKeywords));
        keywordAdder.setVisible(!potentialKeywords.isEmpty());

        if (execCallback) {
            onKeywordsChanged(new HashSet<String>(activeKeywords));
        }
    }

}