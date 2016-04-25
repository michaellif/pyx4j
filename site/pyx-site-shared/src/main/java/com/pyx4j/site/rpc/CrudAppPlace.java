/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * Created on Jun 11, 2011
 * @author michaellif
 */
package com.pyx4j.site.rpc;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.criterion.EntityFiltersBuilder;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;

@I18n(strategy = I18nStrategy.IgnoreAll)
public abstract class CrudAppPlace extends AppPlace {

    public static final String ARG_NAME_CRUD_TYPE = "crud";

    public static final String ARG_NAME_PARENT_ID = "parentId";

    public static final String ARG_NAME_PARENT_CLASS = "parentClass";

    public static final String ARG_NAME_TAB_IDX = "tabIdx";

    // Used in New Entity
    private InitializationData initializationData;

    // Used in Filter
    private EntityFiltersBuilder<?> listerInitializeFilters;

    public static enum Type {
        lister, viewer, editor
    }

    public CrudAppPlace() {
        this(Type.lister);
    }

    public CrudAppPlace(Type type) {
        setType(type);
    }

    public CrudAppPlace(Key itemID) {
        super(itemID);
    }

    public void setType(Type type) {
        addQueryArg(ARG_NAME_CRUD_TYPE, type.name());
    }

    public Type getType() {
        return Type.valueOf(getFirstArg(ARG_NAME_CRUD_TYPE));
    }

    public Key getParentId() {
        String val;
        if ((val = getFirstArg(CrudAppPlace.ARG_NAME_PARENT_ID)) != null) {
            return new Key(val);
        }
        return null;
    }

    public CrudAppPlace formListerPlace() {
        setType(Type.lister);
        return this;
    }

    // TODO remove this: See why AbstractPrimeListerActivity parentEntityId
    @Deprecated
    public CrudAppPlace formListerPlace(Key parentID) {
        setType(Type.lister);
        if (parentID != null) {
            addQueryArg(ARG_NAME_PARENT_ID, parentID.toString());
        }
        return this;
    }

    public CrudAppPlace formViewerPlace(Key itemID) {
        setType(Type.viewer);
        return (CrudAppPlace) formPlace(itemID);
    }

    public CrudAppPlace formViewerPlace(Key itemID, int tabIndex) {
        if (tabIndex >= 0) {
            addPlaceArg(ARG_NAME_TAB_IDX, String.valueOf(tabIndex));
        }
        return formViewerPlace(itemID);
    }

    public CrudAppPlace formEditorPlace(Key itemID) {
        setType(Type.editor);
        return (CrudAppPlace) formPlace(itemID);
    }

    public CrudAppPlace formEditorPlace(Key itemID, int tabIndex) {
        if (tabIndex >= 0) {
            addPlaceArg(ARG_NAME_TAB_IDX, String.valueOf(tabIndex));
        }
        return formEditorPlace(itemID);
    }

    public CrudAppPlace formNewItemPlace(Key parentID) {
        setType(Type.editor);
        setStable(false);
        if (parentID != null) {
            addPlaceArg(ARG_NAME_PARENT_ID, parentID.toString());
        }
        return this;
    }

    public CrudAppPlace formNewItemPlace(Key parentID, Class<?> parentClass) {
        if (parentClass != null) {
            addPlaceArg(ARG_NAME_PARENT_CLASS, parentClass.getName());
        }
        return formNewItemPlace(parentID);
    }

    public CrudAppPlace formNewItemPlace(InitializationData initializationData) {
        this.initializationData = initializationData;
        setType(Type.editor);
        setStable(false);
        return this;
    }

    public InitializationData getInitializationData() {
        return initializationData;
    }

    /**
     * This filters are preserved by Lister Memento once parsed. The same way as used entered filters.
     */
    public EntityFiltersBuilder<?> getListerInitializeFilters() {
        return listerInitializeFilters;
    }

    public void setListerInitializeFilters(EntityFiltersBuilder<?> filters) {
        this.listerInitializeFilters = filters;
    }

    // I don't believe we ever used this.
    @Override
    public CrudAppPlace copy(AppPlace place) {
        super.copy(place);
        if (place instanceof CrudAppPlace) {
            initializationData = ((CrudAppPlace) place).initializationData;
            listerInitializeFilters = ((CrudAppPlace) place).listerInitializeFilters;
        }
        return this;
    }
}
