/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.rpc;

import com.pyx4j.commons.Key;
import com.pyx4j.site.rpc.AppPlace;

public abstract class CrudAppPlace extends AppPlace {

    public static final String ARG_NAME_CRUD_TYPE = "crud";

    public static final String ARG_NAME_ITEM_ID = "itemID";

    public static final String ARG_NAME_PARENT_ID = "parentID";

    public static final String ARG_VALUE_NEW_ITEM = "new";

    public static enum Type {
        editor, viewer, lister
    }

    public CrudAppPlace() {
        setType(Type.lister);
    }

    public void setType(Type type) {
        putArg(ARG_NAME_CRUD_TYPE, type.name());
    }

    public Type getType() {
        return Type.valueOf(getArg(ARG_NAME_CRUD_TYPE));
    }

    public void formViewerPlace(Key itemID) {
        setType(Type.viewer);
        putArg(ARG_NAME_ITEM_ID, itemID.toString());
    }

    public void formEditorPlace(Key itemID) {
        setType(Type.editor);
        putArg(ARG_NAME_ITEM_ID, itemID.toString());
    }

    public void formNewItemPlace(Key parentID) {
        setType(Type.editor);
        putArg(ARG_NAME_ITEM_ID, ARG_VALUE_NEW_ITEM);
        putArg(ARG_NAME_PARENT_ID, parentID.toString());
    }
}
