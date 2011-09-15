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

public abstract class CrudAppPlace extends AppPlace {

    public static final String ARG_NAME_CRUD_TYPE = "crud";

    public static final String ARG_NAME_ID = "Id";

    public static final String ARG_VALUE_NEW = "new";

    public static final String ARG_NAME_PARENT_ID = "parentId";

    public static final String ARG_NAME_TAB_IDX = "tabIdx";

    public static enum Type {
        editor, viewer, lister, dashboard, report
    }

    private String name;

    public CrudAppPlace() {
        setType(Type.lister);
    }

    public CrudAppPlace(Type type) {
        setType(type);
    }

    public void setType(Type type) {
        putArg(ARG_NAME_CRUD_TYPE, type.name());
    }

    public Type getType() {
        return Type.valueOf(getFirstArg(ARG_NAME_CRUD_TYPE));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void formViewerPlace(Key itemID) {
        setType(Type.viewer);
        putArg(ARG_NAME_ID, itemID.toString());
    }

    public void formViewerPlace(Key itemID, int tabIndex) {
        formViewerPlace(itemID);
        if (tabIndex >= 0) {
            putArg(ARG_NAME_TAB_IDX, String.valueOf(tabIndex));
        }
    }

    public void formEditorPlace(Key itemID) {
        setType(Type.editor);
        putArg(ARG_NAME_ID, itemID.toString());
    }

    public void formEditorPlace(Key itemID, int tabIndex) {
        formEditorPlace(itemID);
        if (tabIndex >= 0) {
            putArg(ARG_NAME_TAB_IDX, String.valueOf(tabIndex));
        }
    }

    public void formNewItemPlace(Key parentID) {
        setType(Type.editor);
        putArg(ARG_NAME_ID, ARG_VALUE_NEW);
        if (parentID != null) {
            putArg(ARG_NAME_PARENT_ID, parentID.toString());
        }
    }

    public void formDashboardPlace(Key itemID) {
        setType(Type.dashboard);
        putArg(ARG_NAME_ID, itemID.toString());
    }

    public void formDashboardPlace(Key itemID, String name) {
        setType(Type.dashboard);
        putArg(ARG_NAME_ID, itemID.toString());
        setName(name);
    }
}
