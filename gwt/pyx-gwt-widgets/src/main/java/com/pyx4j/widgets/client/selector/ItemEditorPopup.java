/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jul 20, 2010
 * @author michaellif
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.gwt.commons.ui.SimplePanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.DropDownPanel;
import com.pyx4j.widgets.client.GroupFocusHandler;
import com.pyx4j.widgets.client.Toolbar;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class ItemEditorPopup extends DropDownPanel implements Focusable, HasAllFocusHandlers {

    private static final I18n i18n = I18n.get(ItemEditorPopup.class);

    private final ContentPanel contentPanel;

    private final GroupFocusHandler groupFocusHandler;

    private final FocusPanel focusPanel;

    private EditableItemHolder<?> itemHolder;

    public ItemEditorPopup() {
        super();
        addStyleName(WidgetsTheme.StyleName.SelectedItemEditor.name());

        focusPanel = new FocusPanel();
        focusPanel.getElement().getStyle().setOutlineStyle(OutlineStyle.NONE);

        groupFocusHandler = new GroupFocusHandler(this);
        groupFocusHandler.addFocusable(focusPanel);
        groupFocusHandler.addFocusable(this);

        contentPanel = new ContentPanel();
        focusPanel.setWidget(contentPanel);

        setWidget(focusPanel);
    }

    public void show(EditableItemHolder<?> itemHolder) {
        this.itemHolder = itemHolder;
        contentPanel.setBody(itemHolder.getEditor().asWidget());
        showRelativeTo(itemHolder);
    }

    @Override
    public void hide(boolean autoClosed) {
        itemHolder = null;
        contentPanel.setBody(null);
        super.hide(autoClosed);
    }

    @Override
    public int getTabIndex() {
        return focusPanel.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        focusPanel.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        focusPanel.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        focusPanel.setTabIndex(index);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return groupFocusHandler.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return groupFocusHandler.addBlurHandler(handler);
    }

    class ContentPanel extends FlowPanel {

        private final SimplePanel bodyHolder;

        private final Toolbar toolbar;

        public ContentPanel() {
            setStylePrimaryName(WidgetsTheme.StyleName.SelectedItemEditorContent.name());

            bodyHolder = new SimplePanel();
            bodyHolder.setStyleName(WidgetsTheme.StyleName.SelectedItemEditorBodyHolder.name());
            add(bodyHolder);

            toolbar = new Toolbar();

            Button okButton = new Button(i18n.tr("OK"), new Command() {
                @Override
                public void execute() {
                    if (itemHolder.onEditingComplete()) {
                        hide(true);
                    }
                }
            });
            groupFocusHandler.addFocusable(okButton);
            toolbar.addItem(okButton);

            Button cancelButton = new Button(i18n.tr("Cancel"), new Command() {
                @Override
                public void execute() {
                    hide(true);
                }
            });
            groupFocusHandler.addFocusable(cancelButton);
            toolbar.addItem(cancelButton);

            add(toolbar);
        }

        public void setBody(IsWidget body) {
            bodyHolder.setWidget(body);
        }

    }
}
