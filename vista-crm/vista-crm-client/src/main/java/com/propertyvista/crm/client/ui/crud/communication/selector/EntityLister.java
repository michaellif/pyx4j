/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.communication.selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityListCriteria;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.forms.client.ui.CRadioGroupEnum;
import com.pyx4j.forms.client.ui.datatable.DataItem;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.site.client.backoffice.activity.EntitySelectorTableVisorController.VersionDisplayMode;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractLister;
import com.pyx4j.widgets.client.RadioGroup.Layout;

public class EntityLister<E extends IEntity> extends AbstractLister<E> {

    private VersionDisplayMode versionDisplayMode = VersionDisplayMode.displayFinal;

    private final Collection<E> alreadySelected;

    private final Collection<E> selectedOnTab;

    private final SelectRecipientsDialogForm parent;

    private final Class<E> entityClass;

    private final CRadioGroupEnum<VersionDisplayMode> displayModeButton = new CRadioGroupEnum<VersionDisplayMode>(VersionDisplayMode.class, Layout.HORISONTAL);
    {
        displayModeButton.setValue(versionDisplayMode);
        displayModeButton.addValueChangeHandler(new ValueChangeHandler<VersionDisplayMode>() {
            @Override
            public void onValueChange(ValueChangeEvent<VersionDisplayMode> event) {
                onVersionDisplayModeChange(event.getValue());
            }
        });
    }

    public EntityLister(Class<E> clazz, boolean isVersioned, SelectRecipientsDialogForm parent, Collection<E> alreadySelected) {
        super(clazz);
        this.entityClass = clazz;
        this.parent = parent;
        this.selectedOnTab = new ArrayList<E>();
        this.alreadySelected = (alreadySelected != null ? alreadySelected : new ArrayList<E>());

        getDataTablePanel().setPageSizeOptions(Arrays.asList(new Integer[] { PAGESIZE_SMALL, PAGESIZE_MEDIUM }));
        if (isVersioned) {
            getDataTablePanel().addUpperActionItem(displayModeButton.asWidget());
        }

        addItemSelectionHandler(new ItemSelectionHandler<E>() {
            @Override
            public void onSelect(E selectedItem) {
                identifySelected();
            }
        });
    }

    public VersionDisplayMode getVersionDisplayMode() {
        return versionDisplayMode;
    }

    @Override
    public List<Sort> getDefaultSorting() {
        List<Sort> sort = new ArrayList<Sort>();
        return sort;
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

    protected List<Criterion> createRestrictionFilterForAlreadySelected() {
        List<Criterion> restrictAlreadySelected = new ArrayList<>(alreadySelected.size());

        E proto = EntityFactory.getEntityPrototype(entityClass);

        for (E entity : alreadySelected) {
            restrictAlreadySelected.add(PropertyCriterion.ne(proto.id(), entity.getPrimaryKey()));
        }

        return restrictAlreadySelected;
    }

    @Override
    protected void onObtainSuccess() {
        super.onObtainSuccess();
        setRowsSelected();
        setOnTabSelected();
    }

    private void setOnTabSelected() {
        selectedOnTab.clear();
        for (DataItem<E> dataItem : getLister().getDataTablePanel().getDataTable().getDataTableModel().getSelectedRows()) {
            selectedOnTab.add(dataItem.getEntity());
        }
    }

    public void setRowsSelected() {

        if (alreadySelected == null || alreadySelected.size() == 0)
            return;
        DataTableModel<E> model = getLister().getDataTablePanel().getDataTable().getDataTableModel();

        for (DataItem<E> dataItem : model.getData()) {
            if (alreadySelected.contains(dataItem.getEntity())) {
                model.selectRow(true, model.indexOf(dataItem));
            }
        }
    }

    private void identifySelected() {
        Collection<E> changed = new ArrayList<E>();

        for (DataItem<E> dataItem : getLister().getDataTablePanel().getDataTable().getDataTableModel().getSelectedRows()) {
            changed.add(dataItem.getEntity());
        }

        if (changed.size() > selectedOnTab.size()) {
            changed.removeAll(selectedOnTab);
            addItems((changed));
        } else {
            selectedOnTab.removeAll(changed);
            removeItems(new ArrayList<E>(selectedOnTab));
        }
    }

    private void addItems(Collection<E> addItems) {
        selectedOnTab.addAll(addItems);
        alreadySelected.addAll(addItems);
        parent.addSelected((Collection<IEntity>) addItems);
    }

    private void removeItems(Collection<E> removeItems) {
        selectedOnTab.removeAll(removeItems);
        alreadySelected.removeAll(removeItems);
        parent.removeSelected((Collection<IEntity>) removeItems, entityClass);
    }
}
