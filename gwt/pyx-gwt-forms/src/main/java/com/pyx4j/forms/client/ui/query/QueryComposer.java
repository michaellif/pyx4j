/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jun 6, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.forms.client.ui.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.query.ICondition;
import com.pyx4j.entity.core.query.IQuery;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.dialog.OkCancelOption;
import com.pyx4j.widgets.client.selector.ItemHolderFactory;
import com.pyx4j.widgets.client.selector.SelectorListBox;
import com.pyx4j.widgets.client.selector.SelectorListBoxValuePanel;

@SuppressWarnings("rawtypes")
public class QueryComposer<E extends IQuery> extends Composite implements IFocusWidget {

    private static final I18n i18n = I18n.get(QueryComposer.class);

    private IQuery query;

    private FilterListBox content;

    private List<FilterItem> options;

    public QueryComposer() {

        options = new ArrayList<>();

        content = new FilterListBox();

        initWidget(content);

        content.setAction(new Command() {
            @Override
            public void execute() {
                final FilterItemAddDialog dialog = new FilterItemAddDialog(QueryComposer.this);

                dialog.setDialogOptions(new OkCancelOption() {

                    @Override
                    public boolean onClickOk() {
                        FilterItem selectedItem = dialog.getSelectedItem();
                        if (selectedItem != null) {
                            List<FilterItem> items = new ArrayList<>(content.getValue());
                            selectedItem.setEditorShownOnAttach(true);
                            items.add(selectedItem);
                            content.setValue(items);
                        }
                        return true;
                    }

                    @Override
                    public boolean onClickCancel() {
                        return true;
                    }
                });
                dialog.show();
            }
        });

        content.setWatermark(i18n.tr("+ Add Filter"));
    }

    public IQuery getQuery() {
        return query;
    }

    public void setQuery(IQuery query) {
        this.query = query;

        List<FilterItem> items = new ArrayList<>();
        options.clear();

        if (query != null) {
            for (String memberName : query.getEntityMeta().getMemberNames()) {
                IObject<?> member = query.getMember(memberName);
                if (member instanceof ICondition) {
                    FilterItem item = new FilterItem((ICondition) member);
                    options.add(item);
                    if (!member.isNull()) {
                        items.add(item);
                    }
                }
            }
        }

        Collections.sort(items);
        content.setValue(items);

        ((FilterOptionsGrabber) content.getOptionsGrabber()).updateFilterOptions(options);

    }

    public List<FilterItem> getOptions() {
        return options;
    }

    public Collection<FilterItem> getValue() {
        return content.getValue();
    }

    public void addValueChangeHandler(ValueChangeHandler<Collection<FilterItem>> handler) {
        content.addValueChangeHandler(handler);
    }

    @Override
    public void setEnabled(boolean enabled) {
        content.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return content.isEnabled();
    }

    @Override
    public void setEditable(boolean editable) {
        content.setEditable(editable);
    }

    @Override
    public boolean isEditable() {
        return content.isEditable();
    }

    @Override
    public void setDebugId(IDebugId debugId) {
        content.setDebugId(debugId);
    }

    @Override
    public int getTabIndex() {
        return content.getTabIndex();
    }

    @Override
    public void setAccessKey(char key) {
        content.setAccessKey(key);
    }

    @Override
    public void setFocus(boolean focused) {
        content.setFocus(focused);
    }

    @Override
    public void setTabIndex(int index) {
        content.setTabIndex(index);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return content.addFocusHandler(handler);
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return content.addBlurHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return content.addKeyUpHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return content.addKeyDownHandler(handler);
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return content.addKeyPressHandler(handler);
    }

    class FilterListBox extends SelectorListBox<FilterItem> {
        public FilterListBox() {
            super(new FilterOptionsGrabber(), new IFormatter<FilterItem, SafeHtml>() {
                @Override
                public SafeHtml format(FilterItem value) {
                    SafeHtmlBuilder builder = new SafeHtmlBuilder();
                    builder.appendHtmlConstant(SimpleMessageFormat.format("<div style=\"padding:2px;\">{0}</div>", value.toString()));
                    return builder.toSafeHtml();
                }
            }, new ItemHolderFactory<FilterItem>() {

                @Override
                public FilterItemHolder createItemHolder(FilterItem item, SelectorListBoxValuePanel<FilterItem> valuePanel) {
                    return new FilterItemHolder(item, valuePanel);
                }
            });
        }

        @Override
        public void setSelection(FilterItem item) {
            item.setEditorShownOnAttach(true);
            for (int i = 0; i < getValue().size(); i++) {
                getValue().get(i).getCondition().displayOrder().setValue(i);
            }
            super.setSelection(item);
        }

        @Override
        public void removeItem(FilterItem item) {
            super.removeItem(item);
            item.getCondition().set(null);
        }
    }
}
