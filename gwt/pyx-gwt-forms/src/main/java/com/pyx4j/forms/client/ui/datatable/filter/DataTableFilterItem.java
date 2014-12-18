/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Nov 14, 2011
 * @author michaellif
 */
package com.pyx4j.forms.client.ui.datatable.filter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeEvent.PropertyName;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponentTheme;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.datatable.DataTableTheme;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.decorators.WidgetDecoratorTheme;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.widgets.client.ImageButton;

public class DataTableFilterItem<E extends IEntity> extends FlowPanel {

    private static final I18n i18n = I18n.get(DataTableFilterItem.class);

    protected final CComboBox<FieldData> fieldsList;

    protected final CComboBox<Operator> operandsList;

    @SuppressWarnings("rawtypes")
    protected CField valueComponent;

    protected final SimplePanel valueHolder;

    private DataTableFilterGrid<E> parent;

    private final HTML validationLabel;

    private class FieldData {

        private final ColumnDescriptor cd;

        public FieldData(ColumnDescriptor cd) {
            this.cd = cd;
        }

        public String getPath() {
            return cd.getColumnName();
        }

        @Override
        public String toString() {
            return cd.getColumnTitle();
        }
    }

    @com.pyx4j.i18n.annotations.I18n
    public static enum Operator {

        is(Restriction.EQUAL),

        isNot(Restriction.NOT_EQUAL),

        like(Restriction.RDB_LIKE),

        lessThan(Restriction.LESS_THAN),

        lessOrEqualThan(Restriction.LESS_THAN_OR_EQUAL),

        earlierThan(Restriction.LESS_THAN, true),

        earlierOrEqualThan(Restriction.LESS_THAN_OR_EQUAL, true),

        greaterThan(Restriction.GREATER_THAN),

        greaterOrEqualThan(Restriction.GREATER_THAN_OR_EQUAL),

        laterThan(Restriction.GREATER_THAN, true),

        laterOrEqualThan(Restriction.GREATER_THAN_OR_EQUAL, true);

        // TODO These criterias aren't supported by DB search engine currently, so postpone implementation ?
        //        contains,
        //        doesNotContain,
        //        beginsWith,
        //        endsWith,
        //

        private PropertyCriterion.Restriction criterion;

        private boolean isDate;

        private Operator(Restriction criterion) {
            this(criterion, false);
        }

        private Operator(Restriction criterion, boolean isDate) {
            this.criterion = criterion;
            this.isDate = isDate;
        }

        public PropertyCriterion.Restriction getCriterion() {
            return criterion;
        }

        public boolean isDate() {
            return isDate;
        }

        public static Operator getOperator(Restriction restriction, IObject<?> member) {
            for (Operator op : Operator.values()) {
                if (op.getCriterion() == restriction && getOperators(member).contains(op)) {
                    return op;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    }

    public DataTableFilterItem(final DataTableFilterGrid<E> parent) {
        this.parent = parent;

        setStyleName(DataTableTheme.StyleName.DataTableFilterItem.name());

        fieldsList = new CComboBox<FieldData>(true);
        fieldsList.asWidget().getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        fieldsList.asWidget().getElement().getStyle().setProperty("paddingRight", "10px");
        fieldsList.asWidget().setWidth("25%");

        operandsList = new CComboBox<Operator>(true);
        operandsList.asWidget().getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        operandsList.asWidget().getElement().getStyle().setProperty("paddingRight", "10px");
        operandsList.asWidget().setWidth("25%");

        valueHolder = new SimplePanel();
        valueHolder.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        valueHolder.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
        valueHolder.asWidget().getElement().getStyle().setProperty("paddingRight", "10px");
        valueHolder.setWidth("30%");

        ImageButton removeButton = new ImageButton(FolderImages.INSTANCE.delButton(), new Command() {

            @Override
            public void execute() {
                parent.removeFilter(DataTableFilterItem.this);
            }
        });
        removeButton.setTooltip(i18n.tr("Remove Filter"));
        removeButton.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

        Collection<FieldData> fds = new ArrayList<FieldData>();
        for (ColumnDescriptor cd : parent.getDataTablePanel().getDataTable().getColumnDescriptors()) {
            if (cd.isSearchable()) {
                fds.add(new FieldData(cd));
            }
        }

        fieldsList.setOptions(fds);
        if (!fds.isEmpty()) {
            fieldsList.setValue(fds.iterator().next());
            setValueHolder(fieldsList.getValue().getPath(), null);
        } else {
            operandsList.setOptions(EnumSet.allOf(Operator.class));
            operandsList.setValue(Operator.is);
        }
        fieldsList.addValueChangeHandler(new ValueChangeHandler<FieldData>() {
            @Override
            public void onValueChange(ValueChangeEvent<FieldData> event) {
                setValueHolder(event.getValue().getPath(), null);
            }
        });

        validationLabel = new HTML();
        validationLabel.setStyleName(CComponentTheme.StyleName.ValidationLabel.name());

        add(fieldsList);
        add(operandsList);
        add(valueHolder);
        add(removeButton);
        add(validationLabel);

    }

    public DataTableFilterItem(final DataTableFilterGrid<E> parent, PropertyCriterion filterData) {
        this(parent);
        setFilterData(filterData);
    }

    protected void setParent(final DataTableFilterGrid<E> parent) {
        this.parent = parent;
    }

    public PropertyCriterion getFilterData() {
        String path = null;
        if (fieldsList.getValue() != null) {
            path = fieldsList.getValue().getPath();
        }
        Operator operand = operandsList.getValue();

        Serializable value = (Serializable) valueComponent.getValue();

        return new PropertyCriterion(path, operand.getCriterion(), value);
    }

    public void setFilterData(PropertyCriterion filterCriterion) {
        PropertyCriterion filterData = filterCriterion;
        Collection<FieldData> fds = fieldsList.getOptions();
        for (FieldData fd : fds) {
            if (fd.getPath().compareTo(filterData.getPropertyPath()) == 0) {
                fieldsList.setValue(fd);
                setValueHolder(filterData.getPropertyPath(), filterData.getValue());
                IObject<?> member = parent.getDataTablePanel().proto().getMember(new Path(filterData.getPropertyPath()));
                operandsList.setValue(Operator.getOperator(filterData.getRestriction(), member));
                break;
            }
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void setValueHolder(String valuePath, Serializable value) {
        IObject<?> member = parent.getDataTablePanel().proto().getMember(new Path(valuePath));

        valueComponent = parent.getDataTablePanel().getFilterComponentFactory().create(member);
        valueComponent.asWidget().setWidth("100%");
        valueComponent.addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.isEventOfType(PropertyName.valid, PropertyName.editingInProgress, PropertyName.editingCompleted)) {
                    renderValidationMessage();
                }
            }
        });

        valueComponent.setValue(value);
        valueHolder.setWidget(valueComponent);

        Collection<Operator> options = getOperators(member);
        operandsList.setOptions(options);
        if (options.contains(Operator.like)) {
            operandsList.setValue(Operator.like);
        } else {
            operandsList.setValue(Operator.is);
        }

        operandsList.setFocus(true);
    }

    private static Collection<Operator> getOperators(IObject<?> member) {
        Collection<Operator> options;
        // correct operands list:
        Class<?> valueClass = member.getValueClass();
        if (member.getMeta().isEntity() || valueClass.isEnum() || valueClass.equals(Boolean.class)) {
            options = EnumSet.of(Operator.is, Operator.isNot);
        } else if (valueClass.equals(String.class)) {
            options = EnumSet.of(Operator.is, Operator.isNot, Operator.like);
        } else if ((member.getMeta().getObjectClassType() == ObjectClassType.EntityList)
                || (member.getMeta().getObjectClassType() == ObjectClassType.EntitySet)) {
            options = EnumSet.of(Operator.is, Operator.isNot);
        } else if (isDate(valueClass)) {
            options = EnumSet.of(Operator.is, Operator.isNot, Operator.earlierThan, Operator.laterThan, Operator.earlierOrEqualThan, Operator.laterOrEqualThan);
        } else if (valueClass.equals(BigDecimal.class) || valueClass.equals(Key.class) || member.getMeta().isNumberValueClass()) {
            options = EnumSet.of(Operator.is, Operator.greaterThan, Operator.greaterOrEqualThan, Operator.lessThan, Operator.lessOrEqualThan);
        } else {
            options = EnumSet.allOf(Operator.class);
            // remove duplicate entries:
            Iterator<Operator> it = options.iterator();
            while (it.hasNext()) {
                Operator op = it.next();
                if (op.isDate()) {
                    it.remove();
                }
            }
        }
        return options;
    }

    private static boolean isDate(Class<?> valueClass) {
        return (valueClass.equals(Date.class) || valueClass.equals(java.sql.Date.class) || valueClass.equals(LogicalDate.class));
    }

    private void renderValidationMessage() {
        if (!valueComponent.isValid()) {
            validationLabel.setHTML(valueComponent.getValidationResults().getValidationMessage(true));
            valueComponent.asWidget().addStyleDependentName(WidgetDecoratorTheme.StyleDependent.invalid.name());
            validationLabel.setVisible(true);
        } else {
            validationLabel.setText(null);
            valueComponent.asWidget().removeStyleDependentName(WidgetDecoratorTheme.StyleDependent.invalid.name());
            validationLabel.setVisible(false);
        }
    }
}
