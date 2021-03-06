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
 * Created on Dec 22, 2014
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.gwt.commons.ui.FlowPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.CheckBox;
import com.pyx4j.widgets.client.CheckGroup;
import com.pyx4j.widgets.client.OptionGroup.Layout;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class MultiSelectFilterEditor extends FilterEditorBase {

    private static final I18n i18n = I18n.get(MultiSelectFilterEditor.class);

    private Selector<?> checkGroup;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MultiSelectFilterEditor(IObject<?> member) {
        super(member);
        MemberMeta mm = member.getMeta();
        if (mm.getValueClass().isEnum()) {
            checkGroup = new Selector<Enum>(Layout.VERTICAL);

            ArrayList options = new ArrayList(EnumSet.allOf((Class<Enum>) mm.getValueClass()));
            if (!mm.isAnnotationPresent(NotNull.class)) {
                options.add(null);
            }
            checkGroup.setOptions(options);

        } else if (mm.getValueClass().equals(Boolean.class)) {

            Selector<Boolean> booleanGroup = new Selector<>(Layout.HORIZONTAL);
            booleanGroup.setFormatter(new IFormatter<Boolean, SafeHtml>() {

                @Override
                public SafeHtml format(Boolean value) {
                    String title;
                    if (value == null) {
                        title = i18n.tr("<i>Empty</i>");
                    } else if (value) {
                        title = i18n.tr("Yes");
                    } else {
                        title = i18n.tr("No");
                    }
                    return SafeHtmlUtils.fromTrustedString(title);
                }
            });
            if (mm.isAnnotationPresent(NotNull.class)) {
                booleanGroup.setOptions(Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE }));
            } else {
                booleanGroup.setOptions(Arrays.asList(new Boolean[] { Boolean.FALSE, Boolean.TRUE, null }));
            }
            checkGroup = booleanGroup;
        }

        initWidget(checkGroup);
    }

    @Override
    public PropertyCriterion getCriterion() {
        if (checkGroup.isAllSelected() || checkGroup.isAllDeselected()) {
            return null;
        }
        return PropertyCriterion.in(getMember(), checkGroup.getValue());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setCriterion(Criterion criterion) {
        if (criterion == null) {
            checkGroup.setAllSelected();
        } else {
            if (!(criterion instanceof PropertyCriterion)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            PropertyCriterion propertyCriterion = (PropertyCriterion) criterion;

            if (!(propertyCriterion.getRestriction() == PropertyCriterion.Restriction.IN
                    || propertyCriterion.getRestriction() == PropertyCriterion.Restriction.EQUAL)) {
                throw new Error("Filter criterion isn't supported by editor");
            }

            if (!getMember().getPath().equals(propertyCriterion.getPropertyPath())) {
                throw new Error("Filter editor member doesn't match filter criterion path");
            }

            Collection value;
            if (propertyCriterion.getValue() instanceof Collection) {
                value = (Collection) propertyCriterion.getValue();
            } else {
                value = Arrays.asList(propertyCriterion.getValue());
            }

            checkGroup.setValue(value);
        }
    }

    @Override
    public void onShown() {
        super.onShown();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                checkGroup.setFocus(true);
            }
        });
    }

    @Override
    public void clear() {
        checkGroup.setValue(null);
    }

    class Selector<E> extends FlowPanel {

        private final CheckBox selectAll;

        private final CheckGroup<E> selectGroup;

        public Selector(Layout layout) {

            selectAll = new CheckBox("All");

            selectAll.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue()) {
                        setAllSelected();
                    } else {
                        deselectAll();
                    }
                }
            });

            selectGroup = new CheckGroup<E>(layout);
            selectGroup.setFormatter(new IFormatter<E, SafeHtml>() {

                @Override
                public SafeHtml format(E value) {
                    String title;
                    if (value == null) {
                        title = i18n.tr("<i>Empty</i>");
                    } else {
                        title = value.toString();
                    }
                    return SafeHtmlUtils.fromTrustedString(title);
                }
            });
            selectGroup.setStyleName(WidgetsTheme.StyleName.SelectorCheckGroup.name());

            selectGroup.addValueChangeHandler(new ValueChangeHandler<E>() {

                @Override
                public void onValueChange(ValueChangeEvent<E> event) {
                    if (isAllSelected()) {
                        selectAll.setValue(Boolean.TRUE);
                    } else {
                        selectAll.setValue(Boolean.FALSE);
                    }
                }
            });

            add(selectAll);
            add(selectGroup);
        }

        public void setAllSelected() {
            for (E item : selectGroup.getButtons().keySet()) {
                selectGroup.getButtons().get(item).setValue(Boolean.TRUE);
            }
            selectAll.setValue(Boolean.TRUE);
        }

        public void deselectAll() {
            for (E item : selectGroup.getButtons().keySet()) {
                selectGroup.getButtons().get(item).setValue(Boolean.FALSE);
            }
        }

        public boolean isAllSelected() {
            for (E item : selectGroup.getButtons().keySet()) {
                if (!selectGroup.getButtons().get(item).getValue()) {
                    return false;
                }
            }
            return true;
        }

        public boolean isAllDeselected() {
            for (E item : selectGroup.getButtons().keySet()) {
                if (selectGroup.getButtons().get(item).getValue()) {
                    return false;
                }
            }
            return true;
        }

        public void setValue(Collection<E> value) {
            selectGroup.setValue(value);
        }

        public Collection<E> getValue() {
            return selectGroup.getValue();
        }

        public void setFocus(boolean focused) {
            selectGroup.setFocus(focused);
        }

        public void setOptions(List<E> options) {
            selectGroup.setOptions(options);
        }

        public void setFormatter(IFormatter<E, SafeHtml> formatter) {
            selectGroup.setFormatter(formatter);
        }

    }

}
