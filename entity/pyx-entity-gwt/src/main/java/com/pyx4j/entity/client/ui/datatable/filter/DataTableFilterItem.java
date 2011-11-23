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
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.CriteriaEditableComponentFactory;
import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.DefaultDataTableTheme;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageButton;

public class DataTableFilterItem<E extends IEntity> extends HorizontalPanel {

    protected static I18n i18n = I18n.get(DataTableFilterItem.class);

    protected final CComboBox<FieldData> fieldsList = new CComboBox<FieldData>(true);

    protected final CComboBox<Operators> operandsList = new CComboBox<Operators>(true);

    protected final SimplePanel valueHolder = new SimplePanel();

    private DataTableFilterGrid<E> parent;

    private final IEditableComponentFactory compFactory = new CriteriaEditableComponentFactory();

    private class FieldData {
        private final ColumnDescriptor<E> cd;

        public FieldData(ColumnDescriptor<E> cd) {
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

    public DataTableFilterItem(final DataTableFilterGrid<E> parent) {
        this.parent = parent;

        setStyleName(DefaultDataTableTheme.StyleName.DataTableFilterItem.name());

        Image btnDel = new ImageButton(EntityFolderImages.INSTANCE.del(), EntityFolderImages.INSTANCE.delHover(), i18n.tr("Remove Filter"));
        btnDel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                parent.removeFilter(DataTableFilterItem.this);
            }
        });

        Collection<FieldData> fds = new ArrayList<FieldData>();
        for (ColumnDescriptor<E> cd : parent.getDataTablePanel().getDataTableModel().getColumnDescriptors()) {
            //only sortable columns can be searchable 
            if (cd.isSortable()) {
                fds.add(new FieldData(cd));
            }
        }

        fieldsList.setOptions(fds);
        if (!fds.isEmpty()) {
            fieldsList.setValue(fds.iterator().next());
            setValueHolder(fieldsList.getValue().getPath());
        } else {
            operandsList.setOptions(EnumSet.allOf(Operators.class));
            operandsList.setValue(Operators.is);
        }
        fieldsList.addValueChangeHandler(new ValueChangeHandler<FieldData>() {
            @Override
            public void onValueChange(ValueChangeEvent<FieldData> event) {
                setValueHolder(event.getValue().getPath());
            }
        });
        fieldsList.setWidth("100%");

        add(fieldsList);

        setCellWidth(fieldsList, "40%");

        operandsList.setWidth("100%");

        add(operandsList);

        setCellWidth(operandsList, "20%");
        valueHolder.setWidth("100%");

        add(valueHolder);

        setCellWidth(valueHolder, "40%");

        add(btnDel);

        setWidth("100%");
    }

    protected void setParent(final DataTableFilterGrid<E> parent) {
        this.parent = parent;
    }

    @SuppressWarnings("rawtypes")
    public DataTableFilterData getFilterData() {
        String path = null;
        if (fieldsList.getValue() != null) {
            path = fieldsList.getValue().getPath();
        }
        Operators operand = operandsList.getValue();
        Serializable value = (Serializable) ((CComponent) ((INativeEditableComponent<?>) valueHolder.getWidget()).getCComponent()).getValue();

        return new DataTableFilterData(path, operand, value);
    }

    public void setFilterData(DataTableFilterData filterData) {
        Collection<FieldData> fds = fieldsList.getOptions();
        for (FieldData fd : fds) {
            if (fd.getPath().compareTo(filterData.getMemberPath()) == 0) {
                fieldsList.setValue(fd);
                operandsList.setValue(filterData.getOperand());
                setValueHolder(filterData.getMemberPath(), filterData.getValue());
                break;
            }
        }
    }

    private void setValueHolder(String valuePath) {
        setValueHolder(valuePath, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setValueHolder(String valuePath, Serializable value) {

        IObject<?> member = parent.getDataTablePanel().proto().getMember(new Path(valuePath));
        CComponent comp = compFactory.create(member);
        comp.setValue(value);
        valueHolder.setWidget(comp);

        operandsList.setOptions(EnumSet.allOf(Operators.class));
        operandsList.setValue(Operators.is);

        // correct operands list:
        Class<?> valueClass = member.getValueClass();
        if (member.getMeta().isEntity() || valueClass.isEnum() || valueClass.equals(Boolean.class)) {

            operandsList.removeOption(Operators.like);
            operandsList.removeOption(Operators.greaterThan);
            operandsList.removeOption(Operators.lessThan);

        } else if (valueClass.equals(String.class)) {

            operandsList.removeOption(Operators.greaterThan);
            operandsList.removeOption(Operators.lessThan);

        }
    }
}
