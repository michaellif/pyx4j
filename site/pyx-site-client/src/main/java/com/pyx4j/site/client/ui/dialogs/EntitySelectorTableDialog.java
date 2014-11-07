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
 * Created on Jan 19, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.site.client.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.site.client.backoffice.ui.prime.lister.EntityDataTablePanel;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.widgets.client.RadioGroup.Layout;

public abstract class EntitySelectorTableDialog<E extends IEntity> extends AbstractEntitySelectorDialog<E> implements IShowable {

    private final Class<E> entityClass;

    private final boolean isMultiselect;

    private final SelectEntityLister lister;

    private final ListerDataSource<E> dataSource;

    private final Collection<E> alreadySelected;

    public EntitySelectorTableDialog(Class<E> entityClass, boolean isMultiselect, String caption) {
        this(entityClass, false, isMultiselect, null, caption);
    }

    public EntitySelectorTableDialog(Class<E> entityClass, boolean isMultiselect, Collection<E> alreadySelected, String caption) {
        this(entityClass, false, isMultiselect, alreadySelected, caption);
    }

    public EntitySelectorTableDialog(Class<E> entityClass, boolean isVersioned, boolean isMultiselect, Collection<E> alreadySelected, String caption) {
        super(caption);

        this.entityClass = entityClass;
        this.isMultiselect = isMultiselect;
        this.alreadySelected = (alreadySelected != null ? alreadySelected : Collections.<E> emptyList());
        this.lister = new SelectEntityLister(this.entityClass, isVersioned) {
            @Override
            protected void onObtainSuccess() {
                super.onObtainSuccess();
                EntitySelectorTableDialog.super.show();
            }
        };
        lister.getDataTablePanel().getDataTable().addItemSelectionHandler(new ItemSelectionHandler() {
            @Override
            public void onChange() {
                getOkButton().setEnabled(lister.getDataTablePanel().getDataTable().getDataTableModel().isAnyRowSelected());
            }
        });

        dataSource = new ListerDataSource<E>(entityClass, getSelectService());
        setFilters(createRestrictionFilterForAlreadySelected());
        lister.setDataSource(dataSource);
        lister.setHeight("500px");

        setBody(createBody());
    }

    protected abstract List<ColumnDescriptor> defineColumnDescriptors();

    protected abstract AbstractListCrudService<E> getSelectService();

    protected List<Sort> getDefaultSorting() {
        return null;
    }

    protected Widget createBody() {
        getOkButton().setEnabled(!lister.getSelectedItems().isEmpty());
        VerticalPanel vPanel = new VerticalPanel();
        vPanel.add(lister.asWidget());
        vPanel.setWidth("100%");
        return vPanel;
    }

    protected SelectEntityLister getLister() {
        return lister;
    }

    protected Collection<E> getSelectedItems() {
        return lister.getSelectedItems();
    }

    protected E getSelectedItem() {
        if (getSelectedItems().size() == 1) {
            return getSelectedItems().iterator().next();
        } else {
            return null;
        }
    }

    protected E proto() {
        return EntityFactory.getEntityPrototype(entityClass);
    }

    protected List<Criterion> createRestrictionFilterForAlreadySelected() {
        List<Criterion> restrictAlreadySelected = new ArrayList<>(alreadySelected.size());

        E proto = EntityFactory.getEntityPrototype(entityClass);

        for (E entity : alreadySelected) {
            restrictAlreadySelected.add(PropertyCriterion.ne(proto.id(), entity.getPrimaryKey()));
        }

        return restrictAlreadySelected;
    }

    protected void setParentFiltering(Key parentID) {
        dataSource.setParentFiltering(parentID);
    }

    protected void setParentFiltering(Key parentID, Class<? extends IEntity> parentClass) {
        dataSource.setParentFiltering(parentID, parentClass);
    }

    protected void addFilter(Criterion filter) {
        dataSource.addPreDefinedFilter(filter);
    }

    protected void addFilters(List<Criterion> filters) {
        dataSource.addPreDefinedFilters(filters);
    }

    /**
     * Called from within constructor.
     * In order to add additional filters - overwrite it in your class
     * and use addFilter(s) AFTER call to super.setFilters(filters)!..
     * 
     * @param filters
     */
    protected void setFilters(List<Criterion> filters) {
        dataSource.setPreDefinedFilters(filters);
    }

    @I18n(context = "Version Display Mode")
    public enum VersionDisplayMode {
        displayDraft, displayFinal;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    protected class SelectEntityLister extends EntityDataTablePanel<E> {

        private VersionDisplayMode versionDisplayMode = VersionDisplayMode.displayFinal;

        private final CRadioGroupEnum<VersionDisplayMode> displayModeButton = new CRadioGroupEnum<VersionDisplayMode>(VersionDisplayMode.class,
                Layout.HORISONTAL);
        {
            displayModeButton.setValue(versionDisplayMode);
            displayModeButton.addValueChangeHandler(new ValueChangeHandler<VersionDisplayMode>() {
                @Override
                public void onValueChange(ValueChangeEvent<VersionDisplayMode> event) {
                    onVersionDisplayModeChange(event.getValue());
                }
            });
        }

        public SelectEntityLister(Class<E> clazz, boolean isVersioned) {
            super(clazz);

            getDataTablePanel().setPageSizeOptions(Arrays.asList(new Integer[] { PAGESIZE_SMALL, PAGESIZE_MEDIUM }));
            if (isVersioned) {
                getDataTablePanel().addUpperActionItem(displayModeButton.asWidget());
            }

            DataTableModel<E> dataTableModel = new DataTableModel<E>(EntitySelectorTableDialog.this.defineColumnDescriptors());
            dataTableModel.setPageSize(PAGESIZE_SMALL);
            dataTableModel.setMultipleSelection(EntitySelectorTableDialog.this.isMultiselect);
            setDataTableModel(dataTableModel);
        }

        @Override
        public List<Sort> getDefaultSorting() {
            List<Sort> sort = EntitySelectorTableDialog.this.getDefaultSorting();
            if (sort == null) {
                sort = super.getDefaultSorting();
            }
            return sort;
        }

        public VersionDisplayMode getVersionDisplayMode() {
            return versionDisplayMode;
        }

        protected void onVersionDisplayModeChange(VersionDisplayMode mode) {
            versionDisplayMode = mode;
            obtain(0);
        }

        @Override
        protected EntityListCriteria<E> updateCriteria(EntityListCriteria<E> criteria) {
            switch (getVersionDisplayMode()) {
            case displayDraft:
                criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
                break;
            case displayFinal:
                criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
                break;
            }
            return super.updateCriteria(criteria);
        }
    }
}