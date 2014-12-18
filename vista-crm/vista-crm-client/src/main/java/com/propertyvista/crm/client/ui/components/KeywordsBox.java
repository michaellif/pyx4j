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
 */
package com.propertyvista.crm.client.ui.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.widgets.client.Button;

public abstract class KeywordsBox extends Composite {

    public static String DEFAULT_STYLE_PREFIX = "-vista-KeywordsBox";

    public enum StyleSuffix implements IStyleName {
        ActiveKeywords, KeywordsAdder, KeywordsAdderList, KeywordsAdderButton, Keyword, KeywordLabel, KeywordUnselectButton;
    };

    private class Keyword extends Composite {

        public Keyword(String keyword) {
            FlowPanel p = new FlowPanel();
            p.add(makeKeywordLabel(keyword));
            p.add(makeUnselectButton(keyword));
            initWidget(p);
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.Keyword);
        }

        private Label makeKeywordLabel(String keyword) {
            Label label = new Label();
            label.setTitle(keyword);
            label.setText(new SafeHtmlBuilder().appendEscaped(keyword).toSafeHtml().asString());
            label.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.KeywordLabel);
            return label;
        }

        private Label makeUnselectButton(final String keyword) {
            Label button = new Label();
            button.setText("X");
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    KeywordsBox.this.unselect(keyword);
                }
            });
            button.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.KeywordUnselectButton);
            return button;
        }

    }

    private class KeywordAdder extends Composite {

        ListBox keywordsList;

        public KeywordAdder() {
            FlowPanel p = new FlowPanel();
            p.add(keywordsList = makeKeywordsList());
            p.add(makeAddButton());
            initWidget(p);
            setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.KeywordsAdder);
        }

        public void setAvaiableKeywords(Set<String> keywords) {
            keywordsList.clear();
            keywordsList.addItem("");
            List<String> sortedKeywords = new ArrayList<String>(keywords);
            Collections.sort(sortedKeywords);
            for (String keyword : sortedKeywords) {
                keywordsList.addItem(keyword);
            }
        }

        private ListBox makeKeywordsList() {
            ListBox listBox = new ListBox(false);
            listBox.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.KeywordsAdderList);
            return listBox;
        }

        private Widget makeAddButton() {
            Button button = new Button("+", new Command() {
                @Override
                public void execute() {
                    // TODO Auto-generated method stub
                    if (keywordsList.getSelectedIndex() > 0) { // the first item is blank
                        KeywordsBox.this.select(keywordsList.getItemText(keywordsList.getSelectedIndex()));
                    }
                }

            });
            button.addStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.KeywordsAdderButton);
            return button;
        }

    }

    FlowPanel activeKeywordsPanel;

    KeywordAdder keywordAdder;

    private final List<String> activeKeywords;

    private final List<String> potentialKeywords;

    public KeywordsBox() {
        activeKeywords = new ArrayList<String>();
        potentialKeywords = new ArrayList<String>();

        FlowPanel p = new FlowPanel();
        p.add(activeKeywordsPanel = makeActiveKeywordsPanel());
        p.add(keywordAdder = new KeywordAdder());
        initWidget(p);
    }

    private FlowPanel makeActiveKeywordsPanel() {
        FlowPanel p = new FlowPanel();
        p.setStyleName(DEFAULT_STYLE_PREFIX + StyleSuffix.ActiveKeywords);
        return p;
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

    private void redraw(boolean execCallback) {
        activeKeywordsPanel.clear();
        for (String keyword : activeKeywords) {
            activeKeywordsPanel.add(new Keyword(keyword));
        }
        keywordAdder.setAvaiableKeywords(new HashSet<String>(potentialKeywords));
        keywordAdder.setVisible(!potentialKeywords.isEmpty());

        if (execCallback) {
            onKeywordsChanged(new HashSet<String>(activeKeywords));
        }
    }

}