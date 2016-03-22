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
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ListBox;
import com.pyx4j.widgets.client.Toolbar;

public class SimpleRichTextToolbar extends RichTextToolbar {
    private static final I18n i18n = I18n.get(RichTextToolbar.class);

    public SimpleRichTextToolbar(RichTextEditor richTextEditor) {
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
        lb.getElement().getStyle().setMarginRight(4, Unit.PX);

        groupFocusHandler.addFocusable(lb);

        return lb;
    }

    @Override
    protected Toolbar createIndentPanel() {
        Toolbar indentPanel = new Toolbar();
        indentPanel.addItem(createButton(images.indent(), i18n.tr("Indent More"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.rightIndent();
            }
        }, false));
        indentPanel.addItem(createButton(images.outdent(), i18n.tr("Indent Less"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.leftIndent();
            }
        }, false));

        indentPanel.addItem(new HTML("&emsp;"));
        indentPanel.addItem(createButton(images.ol(), i18n.tr("Numbered List"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.insertOrderedList();
            }
        }, false));
        indentPanel.addItem(createButton(images.ul(), i18n.tr("Bulleted List"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.insertUnorderedList();
            }
        }, false));
        indentPanel.addItem(new HTML("&emsp;"));
        indentPanel.addItem(createButton(images.removeFormat(), i18n.tr("Remove Format"), new Command() {

            @Override
            public void execute() {
                richTextEditor.getRichTextArea().restoreSelectionAndRange();
                formatter.removeFormat();
            }
        }, false));

        return indentPanel;
    }

}
