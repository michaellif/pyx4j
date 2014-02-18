/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 1, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.validators.ValidationResults;

import com.propertyvista.domain.maintenance.IssueElementType;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.shared.config.VistaFeatures;

public class MaintenanceRequestCategoryChoice extends CComboBox<MaintenanceRequestCategory> {

    public enum LoadOptionsMode {
        EMPTY_SET, // clear options
        USE_PARENT, // set options based on the the parent selector value
        REFRESH; // set options using parent member of selector's value
    }

    private MaintenanceRequestCategoryChoice child;

    private MaintenanceRequestCategoryChoice parent;

    private MaintenanceRequestMetadata meta;

    private boolean isForUnit = true;

    private LoadOptionsMode optionsMode = LoadOptionsMode.EMPTY_SET;

    public MaintenanceRequestCategoryChoice() {
        super(NotInOptionsPolicy.DISCARD);
        setMandatory(true);
        setVisible(false);
    }

    @Override
    public String getItemName(MaintenanceRequestCategory o) {
        if (o == null) {
            return super.getItemName(o);
        } else {
            return o.getStringView();
        }
    }

    @Override
    protected MaintenanceRequestCategory preprocessValue(MaintenanceRequestCategory value, boolean fireEvent, boolean populate) {
        if (isEditable() && populate) {
            optionsMode = LoadOptionsMode.REFRESH;
        }
        return super.preprocessValue(value, fireEvent, populate);
    }

    @Override
    protected void setEditorValue(MaintenanceRequestCategory value) {
        // when parent container populates children, refresh options based on given value before the value is set
        if (isEditable() && optionsMode == LoadOptionsMode.REFRESH) {
            // this is a hack to avoid clearing CComp value, that we are trying to set, from
            // NComboBox#refreshOptions() when new options are loaded
            getWidget().setNativeValue(null);
            resetOptions();
        }
        // set visibility based on value pushed by parent container or child selector via onValueSet()
        setVisible(parent == null || (value != null && !value.name().isNull()));
        // in edit mode only call super if options are ready - see note above regarding NComboBox#refreshOptions()
        if (isViewable() || getOptions().size() > 0) {
            super.setEditorValue(value);
        }
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);
        // propagate value upstream
        if (populate && parent != null) {
            parent.setValue(getValue() != null ? getValue().parent() : null, true, true);
        }
    }

    @Override
    public boolean isValuesEquals(MaintenanceRequestCategory value1, MaintenanceRequestCategory value2) {
        // compare categories using PK
        return value1 != null && value2 != null && !value1.isValueDetached() && !value2.isValueDetached() && value1.getPrimaryKey() != null
                && value1.getPrimaryKey().equals(value2.getPrimaryKey());
    }

    @Override
    public boolean isValueEmpty() {
        return getValue() == null || getValue().isEmpty();
    }

    // TODO - here for testing; remove...
    @Override
    public void revalidate() {
        super.revalidate();
    }

    @Override
    public void setVisited(boolean flag) {
        super.setVisited(flag);
        if (parent != null) {
            parent.setVisited(flag);
        }
    }

    @Override
    public ValidationResults getValidationResults() {
        ValidationResults results = super.getValidationResults();
        if (parent != null) {
            results.appendValidationResults(parent.getValidationResults());
        }
        return results;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && (parent != null ? parent.isValid() : true);
    }

    protected boolean isLeaf(MaintenanceRequestCategory opt) {
        return !opt.isEmpty() && opt.name().isNull();
    }

    public void setOptionsMeta(MaintenanceRequestMetadata meta, boolean isForUnit) {
        this.meta = meta;
        this.isForUnit = isForUnit;
        if (isEditable()) {
            // refresh options in case this call comes after setValue()
            optionsMode = LoadOptionsMode.REFRESH;
            resetOptions();
            // set initial visibility
            setVisible(parent == null);
        }
        if (parent != null) {
            parent.setOptionsMeta(meta, isForUnit);
        }
    }

    public void assignParent(MaintenanceRequestCategoryChoice parent) {
        if (parent == null) {
            return;
        }
        this.parent = parent;
        parent.setViewable(isViewable());
        parent.setEditable(isEditable());
        parent.acceptChild(this);
    }

    public MaintenanceRequestCategoryChoice getParentSelector() {
        return parent;
    }

    protected void acceptChild(final MaintenanceRequestCategoryChoice child) {
        this.child = child;
        if (isEditable()) {
            getWidget().getEditor().addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    child.onParentChange();
                }
            });
        }
    }

    public int getLevel() {
        return parent == null ? 1 : 1 + parent.getLevel();
    }

    @Override
    public void retriveOptions(final AsyncOptionsReadyCallback<MaintenanceRequestCategory> callback) {
        // don't bother with options in view mode
        if (!isViewable()) {
            super.retriveOptions(callback);
        }
    }

    protected void resetOptions() {
        switch (optionsMode) {
        case EMPTY_SET:
            // set empty-set criteria to prevent extra option loads for parent-controlled selectors
            setOptions(null);
            break;
        case USE_PARENT:
            // set options based on parent member
            if (parent != null && parent.getValue() != null) {
                MaintenanceRequestCategory metaValue = findMetaEntry(parent.getValue(), null);
                if (metaValue != null) {
                    setOptions(filterCategories(metaValue.subCategories()));

                }
            }
            break;
        case REFRESH:
            if (getOptions() != null && getOptions().size() > 0 && (getValue() == null || getOptions().contains(getValue()))) {
                // if options has the value, then we are ok
                break;
            } else if (parent != null) {
                MaintenanceRequestCategory value = getValue();
                if (value != null && !value.isNull() && value.parent() != null) {
                    MaintenanceRequestCategory metaValue = findMetaEntry(value.parent(), null);
                    if (metaValue != null) {
                        setOptions(filterCategories(metaValue.subCategories()));
                    }
                }
            } else {
                setOptions(meta != null ? filterCategories(meta.rootCategory().subCategories()) : null);
            }
            break;
        }
    }

    private List<MaintenanceRequestCategory> filterCategories(IList<MaintenanceRequestCategory> subCategories) {
        if (subCategories == null) {
            return null;
        }
        if (VistaFeatures.instance().yardiIntegration()) {
            return subCategories;
        }
        // performance optimization: filter only 1t level
        if (parent != null && parent.parent != null) {
            return subCategories;
        }
        List<MaintenanceRequestCategory> categories = new ArrayList<MaintenanceRequestCategory>();
        for (MaintenanceRequestCategory c : subCategories) {
            if ((c.type() == null || c.type().isNull() || (c.type().getValue().equals(IssueElementType.ApartmentUnit) && isForUnit) || (!isForUnit && !c.type()
                    .getValue().equals(IssueElementType.ApartmentUnit))))
                categories.add(c);
        }
        return categories;
    }

    protected void onParentChange() {
        optionsMode = parent.getValue() != null ? LoadOptionsMode.USE_PARENT : LoadOptionsMode.EMPTY_SET;
        resetOptions();
        // set value and visibility (the order is important) based on the new options
        List<MaintenanceRequestCategory> options = getOptions();
        boolean autoSelect = (options != null && options.size() == 1 && isLeaf(options.get(0)) && !options.get(0).equals(getValue()));
        if (autoSelect) {
            // auto-select first value and load options for child selector
            setValue(options.get(0), true, false);
            setVisible(false);
        } else {
            setValue(null, true, false);
            setVisible(options != null && options.size() > 0 && !isLeaf(options.get(0)));
        }
        // propagate value change downstream, as there will be no native Change event
        if (child != null) {
            child.onParentChange();
        }
    }

    private MaintenanceRequestCategory findMetaEntry(MaintenanceRequestCategory entry, MaintenanceRequestCategory parent) {
        if (parent == null) {
            parent = meta.rootCategory();
        }
        if (entry == null || parent.subCategories().isNull()) {
            return null;
        }
        // start meta lookup; we only look for matches on the lowest level
        MaintenanceRequestCategory found = null;
        for (MaintenanceRequestCategory subCat : parent.subCategories()) {
            // if not this one, go to the next level down the tree
            if (isValuesEquals(entry, subCat)) {
                return subCat;
            } else if ((found = findMetaEntry(entry, subCat)) != null) {
                return found;
            }
        }
        return null;
    }
}
