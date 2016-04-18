/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Mar 22, 2016
 * @author arminea
 */
package com.pyx4j.widgets.client.richtext;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.Toolbar;

public class JasperCompatibleRichTextToolbar extends RichTextToolbar {
    private static final I18n i18n = I18n.get(RichTextToolbar.class);

    public JasperCompatibleRichTextToolbar(RichTextEditor richTextEditor) {
        super(richTextEditor);
        hideInsertToolbar();
    }

    private void hideInsertToolbar() {
        insertButton.setVisible(false);
    }

    @Override
    protected ListBox createFontList() {
        ListBox lb = new ListBox();
        lb.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.setFontName(fonts.getValue(fonts.getSelectedIndex()));
            }
        });
        lb.setVisibleItemCount(1);

        lb.addItem(i18n.tr("Font Family"), "");
        lb.addItem(i18n.tr("Normal"), "");
        lb.getStyle().setMarginRight(4, Unit.PX);

        groupFocusHandler.addFocusable(lb);

        return lb;
    }

    @Override
    protected void initFormatToolbar() {
        formatToolbar = new FlowPanel();
        formatToolbar.setStyleName(RichTextTheme.StyleName.RteToolbarBottom.name());
        formatToolbar.setVisible(false);

        topButtonBar.addItem(formatButton = createButton(i18n.tr("Format"), i18n.tr("Format"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                if (formatButton.isActive()) {
                    if (fontButton.isActive()) {
                        fontButton.toggleActive();
                    }
                    if (insertButton.isActive()) {
                        insertButton.toggleActive();
                    }
                }
                formatToolbar.setVisible(formatButton.isActive());
            }
        }, true));
        formatButton.addStyleName(RichTextTheme.StyleName.RteToolbarButton.name());
        groupFocusHandler.addFocusable(formatButton);

        Toolbar formatPanel = new Toolbar();

        formatPanel.addItem(boldButton = createButton(images.bold(), i18n.tr("Bold"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.toggleBold();
            }
        }, true));
        formatPanel.addItem(italicButton = createButton(images.italic(), i18n.tr("Italic"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.toggleItalic();
            }
        }, true));
        formatPanel.addItem(underlineButton = createButton(images.underline(), i18n.tr("Underline"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.toggleUnderline();
            }
        }, true));
        formatPanel.addItem(new HTML("&emsp;"));
        formatPanel.addItem(createButton(images.removeFormat(), i18n.tr("Remove Format"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.removeFormat();
            }
        }, false));

        formatToolbar.add(formatPanel);

        add(formatToolbar);
    }
}
