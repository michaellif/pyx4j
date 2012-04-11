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
 * @version $Id$
 */
package com.pyx4j.site.rpc;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.I18n.I18nStrategy;

@I18n(strategy = I18nStrategy.IgnoreAll)
public abstract class CrudAppPlace extends AppPlace {

    public static final String ARG_NAME_CRUD_TYPE = "crud";

    public static final String ARG_VALUE_NEW = "new";

    public static final String ARG_NAME_PARENT_ID = "parentId";

    public static final String ARG_NAME_PARENT_CLASS = "parentClass";

    public static final String ARG_NAME_TAB_IDX = "tabIdx";

    private IEntity newItem;

    public static enum Type {
        lister, viewer, editor
    }

    public CrudAppPlace() {
        setType(Type.lister);
    }

    public CrudAppPlace(Type type) {
        setType(type);
    }

    public void setType(Type type) {
        queryArg(ARG_NAME_CRUD_TYPE, type.name());
    }

    public Type getType() {
        return Type.valueOf(getFirstArg(ARG_NAME_CRUD_TYPE));
    }

    public CrudAppPlace formViewerPlace(Key itemID) {
        setType(Type.viewer);
        return (CrudAppPlace) formPlace(itemID);
    }

    public CrudAppPlace formViewerPlace(Key itemID, int tabIndex) {
        if (tabIndex >= 0) {
            placeArg(ARG_NAME_TAB_IDX, String.valueOf(tabIndex));
        }
        return formViewerPlace(itemID);
    }

    public CrudAppPlace formEditorPlace(Key itemID) {
        setType(Type.editor);
        return (CrudAppPlace) formPlace(itemID);
    }

    public CrudAppPlace formEditorPlace(Key itemID, int tabIndex) {
        if (tabIndex >= 0) {
            placeArg(ARG_NAME_TAB_IDX, String.valueOf(tabIndex));
        }
        return formEditorPlace(itemID);
    }

    public CrudAppPlace formNewItemPlace(Key parentID) {
        setType(Type.editor);
        if (parentID != null) {
            placeArg(ARG_NAME_PARENT_ID, parentID.toString());
        }
        return (CrudAppPlace) placeArg(ARG_NAME_ID, ARG_VALUE_NEW);
    }

    public CrudAppPlace formNewItemPlace(Key parentID, Class<?> parentClass) {
        if (parentClass != null) {
            placeArg(ARG_NAME_PARENT_CLASS, parentClass.getName());
        }
        return formNewItemPlace(parentID);
    }

    public CrudAppPlace formNewItemPlace(IEntity newItem) {
        this.newItem = newItem;
        setType(Type.editor);
        setStable(false);
        return (CrudAppPlace) placeArg(ARG_NAME_ID, ARG_VALUE_NEW);
    }

    public IEntity getNewItem() {
        return newItem;
    }
}
