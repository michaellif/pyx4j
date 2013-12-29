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

import java.io.Serializable;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion.Restriction;
import com.pyx4j.forms.client.ui.CEntityComboBox;

public abstract class IssueClassificationChoice<E extends IEntity> extends CEntityComboBox<E> {

    public enum LoadOptionsMode {
        EMPTY_SET, // clear options
        USE_PARENT, // set options based on the the parent selector value
        REFRESH; // set options using parent member of selector's value
    }

    private IssueClassificationChoice<?> child;

    private IssueClassificationChoice<?> parent;

    private Path parentPath;

    private LoadOptionsMode optionsMode = LoadOptionsMode.EMPTY_SET;

    public IssueClassificationChoice(Class<E> entityClass) {
        super(entityClass);
        addValueChangeHandler(new ValueChangeHandler<E>() {
            @Override
            public void onValueChange(ValueChangeEvent<E> event) {
                if (child != null) {
                    child.onParentChange();
                }
            }
        });
        setMandatory(true);
        setVisible(false);
    }

    protected abstract boolean isLeaf(E opt);

    public void init() {
        resetLoadingMode();
        if (child != null) {
            child.init();
        }
    }

    public void resetLoadingMode() {
        optionsMode = LoadOptionsMode.REFRESH;
    }

    public void assignParent(IssueClassificationChoice<?> parent, IObject<?> member) {
        if (parent == null) {
            return;
        }
        assert (proto().getMember(member.getPath()) != null);
        this.parent = parent;
        this.parentPath = member.getPath();
        parent.acceptChild(this);
    }

    public void acceptChild(final IssueClassificationChoice<?> child) {
        this.child = child;
    }

    private PropertyCriterion getOptionsCriterion() {
        PropertyCriterion crit = null;
        switch (optionsMode) {
        case EMPTY_SET:
            // set empty-set criteria to prevent extra option loads for parent-controlled selectors
            crit = PropertyCriterion.eq(proto().id(), (Serializable) null);
            break;
        case USE_PARENT:
            // set options based on parent member
            if (parent != null) {
                crit = new PropertyCriterion(parentPath.toString(), Restriction.EQUAL, parent.getValue());
            }
            break;
        case REFRESH:
            E value = getValue();
            if (parent != null) {
                IEntity pValue = null;
                if (value != null && value.getMember(parentPath) != null) {
                    pValue = ((IEntity) value.getMember(parentPath)).cast();
                }
                crit = new PropertyCriterion(parentPath.toString(), Restriction.EQUAL, pValue);
            }
            break;
        }
        return crit;
    }

    public void onParentChange() {
        optionsMode = LoadOptionsMode.USE_PARENT;
        setValue(null, true, true);
    }

    @Override
    public void retriveOptions(final AsyncOptionsReadyCallback<E> callback) {
        final PropertyCriterion crit = getOptionsCriterion();
        resetCriteria();
        if (crit != null) {
            addCriterion(crit);
        }
        resetOptions();
        super.retriveOptions(new AsyncOptionsReadyCallback<E>() {
            @Override
            public void onOptionsReady(List<E> opt) {
                if (callback != null) {
                    callback.onOptionsReady(opt);
                }

                boolean autoSelect = (opt != null && opt.size() == 1 && isLeaf(opt.get(0)) && !opt.get(0).equals(getValue()));
                if (autoSelect) {
                    setVisible(false);
                    // auto-select first value and load options for child selector
                    setValue(opt.get(0), true, true);
                } else {
                    setVisible(opt != null && opt.size() > 0 && !isLeaf(opt.get(0)));
                }
            }
        });
        // restore mode to default
        resetLoadingMode();
    }
}
