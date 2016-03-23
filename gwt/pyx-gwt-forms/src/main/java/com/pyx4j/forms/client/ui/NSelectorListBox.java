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
 * Created on Jan 11, 2010
 * @author Michael
 */
package com.pyx4j.forms.client.ui;

import java.text.ParseException;
import java.util.Collection;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.IWatermarkWidget;
import com.pyx4j.widgets.client.Label;
import com.pyx4j.widgets.client.selector.SelectorListBox;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class NSelectorListBox<E> extends NFocusField<Collection<E>, SelectorListBox<E>, CSelectorListBox<E>, FlowPanel> {

    private NavigationCommand<E> navigationCommand;

    public NSelectorListBox(final CSelectorListBox<E> cSuggestBox) {
        super(cSuggestBox);

    }

    @Override
    protected FlowPanel createViewer() {
        return new FlowPanel();
    }

    @Override
    protected SelectorListBox<E> createEditor() {
        SelectorListBox<E> editor = new SelectorListBox<E>(getCComponent().getOptionsGrabber(), new Command() {

            @Override
            public void execute() {
                if (getCComponent().getAddItemCommand() != null) {
                    getCComponent().getAddItemCommand().execute();
                }
            }
        }, new IFormatter<E, SafeHtml>() {

            @Override
            public SafeHtml format(E value) {
                return getCComponent().getOptionFormatter().format(value);
            }
        }, new IFormatter<E, String>() {

            @Override
            public String format(E value) {
                return getCComponent().getFormatter().format(value);
            }
        }, new IFormatter<E, String>() {

            @Override
            public String format(E value) {
                return getCComponent().getTooltipFormatter().format(value);
            }
        });

        editor.addValueChangeHandler(new ValueChangeHandler<Collection<E>>() {

            @Override
            public void onValueChange(ValueChangeEvent<Collection<E>> event) {
                getCComponent().stopEditing();
            }
        });

        return editor;
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        if (getEditor() instanceof IWatermarkWidget) {
            getEditor().setWatermark(getCComponent().getWatermark());
        }
    }

    @Override
    public void setNativeValue(Collection<E> value) {
        if (isViewable()) {
            getViewer().clear();
            if (value != null) {
                for (E val : value) {
                    getViewer().add(new Link(val));
                }
            }
        } else {
            getEditor().setValue(value);
        }
    }

    @Override
    public Collection<E> getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getCComponent().convertCollectionType(getEditor().getValue());
        }
    }

    public void setNavigationCommand(NavigationCommand<E> navigationCommand) {
        this.navigationCommand = navigationCommand;
    }

    class Link extends SimplePanel {
        private static final String DEFAULT_HREF = "javascript:;";

        public Link(final E item) {
            super(DOM.createAnchor());
            setWidget(new Label(getCComponent().getFormatter().format(item)));
            if (navigationCommand != null) {
                addDomHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        navigationCommand.navigate(item);
                    }
                }, ClickEvent.getType());

                AnchorElement.as(getElement()).setHref(DEFAULT_HREF);
                setStylePrimaryName(WidgetsTheme.StyleName.Anchor.name());
            }
        }
    }
}
