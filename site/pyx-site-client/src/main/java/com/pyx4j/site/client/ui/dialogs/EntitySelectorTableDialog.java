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
 */
package com.pyx4j.site.client.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.pyx4j.gwt.commons.ui.ScrollPanel;
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
import com.pyx4j.forms.client.ui.datatable.DataTable.ItemSelectionHandler;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.DataTablePanel;
import com.pyx4j.forms.client.ui.datatable.ListerDataSource;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.site.client.ui.IShowable;
import com.pyx4j.widgets.client.OptionGroup.Layout;

public abstract class EntitySelectorTableDialog<E extends IEntity> extends AbstractEntitySelectorDialog<E> implements IShowable {

    private final Class<E> entityClass;

    private final boolean isMultiselect;

    private final SelectEntityLister lister;

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
        this.lister = new SelectEntityLister(this.entityClass, getSelectService(), isVersioned) {
            @Override
            protected void onPopulate() {
                super.onPopulate();
                EntitySelectorTableDialog.super.show();
                getOkButton().setEnabled(lister.getDataTable().getDataTableModel().isAnyRowSelected());
            }
        };
        lister.getDataTable().addItemSelectionHandler(new ItemSelectionHandler() {
            @Override
            public void onChange() {
                getOkButton().setEnabled(lister.getDataTable().getDataTableModel().isAnyRowSelected());
            }
        });

        setFilters(createRestrictionFilterForAlreadySelected());
        lister.setHeight("500px");

        getOkButton().setEnabled(false);
        setBody(createBody());
    }

    @Override
    public void show() {
        lister.populate();
        super.show();
    }

    protected abstract List<ColumnDescriptor> defineColumnDescriptors();

    protected abstract AbstractListCrudService<E> getSelectService();

    protected List<Sort> getDefaultSorting() {
        return null;
    }

    protected Widget createBody() {
        ScrollPanel scrollPanel = new ScrollPanel(lister);
        return scrollPanel;
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
        lister.getDataSource().setParentEntityId(parentID);
    }

    protected void setParentFiltering(Key parentID, Class<? extends IEntity> parentClass) {
        lister.getDataSource().setParentEntityId(parentID, parentClass);
    }

    protected void addFilter(Criterion filter) {
        lister.getDataSource().addPreDefinedFilter(filter);
    }

    protected void addFilters(List<Criterion> filters) {
        lister.getDataSource().addPreDefinedFilters(filters);
    }

    /**
     * Called from within constructor.
     * In order to add additional filters - overwrite it in your class
     * and use addFilter(s) AFTER call to super.setFilters(filters)!..
     *
     * @param filters
     */
    protected void setFilters(List<Criterion> filters) {
        lister.getDataSource().setPreDefinedFilters(filters);
    }

    @I18n(context = "Version Display Mode")
    public enum VersionDisplayMode {
        displayDraft, displayFinal;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    }

    protected class SelectEntityLister extends DataTablePanel<E> {

        private VersionDisplayMode versionDisplayMode = VersionDisplayMode.displayFinal;

        private CRadioGroupEnum<VersionDisplayMode> displayModeButton;

        public SelectEntityLister(Class<E> entityClass, AbstractListCrudService<E> service, boolean isVersioned) {
            super(entityClass);

            setPageSizeOptions(Arrays.asList(new Integer[] { PAGESIZE_SMALL, PAGESIZE_MEDIUM }));
            if (isVersioned) {
                displayModeButton = new CRadioGroupEnum<VersionDisplayMode>(VersionDisplayMode.class, Layout.HORIZONTAL);
                displayModeButton.populate(versionDisplayMode);
                displayModeButton.addValueChangeHandler(new ValueChangeHandler<VersionDisplayMode>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<VersionDisplayMode> event) {
                        onVersionDisplayModeChange(event.getValue());
                    }
                });
                addUpperActionItem(displayModeButton.asWidget());
            }

            setDataSource(new ListerDataSource<E>(entityClass, service));

            setColumnDescriptors(EntitySelectorTableDialog.this.defineColumnDescriptors());

            DataTableModel<E> dataTableModel = new DataTableModel<E>();
            dataTableModel.setMultipleSelection(EntitySelectorTableDialog.this.isMultiselect);
            setDataTableModel(dataTableModel);

            getDataTable().setHasColumnClickSorting(true);

        }

        // Selections are enforced on server
        @Override
        protected boolean isSecurityEnabled() {
            return false;
        };

        @Override
        public void setDataTableModel(DataTableModel<E> dataTableModel) {
            dataTableModel.setPageSize(DataTablePanel.PAGESIZE_SMALL);
            super.setDataTableModel(dataTableModel);
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
            setPageNumber(0);
            populate();
        }

        @Override
        public EntityListCriteria<E> updateCriteria(EntityListCriteria<E> criteria) {
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