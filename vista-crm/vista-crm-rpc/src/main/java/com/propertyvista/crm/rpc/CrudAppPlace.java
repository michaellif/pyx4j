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

    public static CrudAppPlace formViewerPlace(CrudAppPlace itemPlace, Key itemID) {
        itemPlace.setType(Type.viewer);
        itemPlace.putArg(CrmSiteMap.ARG_NAME_ITEM_ID, itemID.toString());
        return itemPlace;
    }

    public static CrudAppPlace formEditorPlace(CrudAppPlace itemPlace, Key itemID) {
        itemPlace.setType(Type.editor);
        itemPlace.putArg(CrmSiteMap.ARG_NAME_ITEM_ID, itemID.toString());
        return itemPlace;
    }

    public static AppPlace formNewItemPlace(AppPlace itemPlace, Key parentID) {
        itemPlace.putArg(CrmSiteMap.ARG_NAME_ITEM_ID, ARG_VALUE_NEW_ITEM);
        itemPlace.putArg(CrmSiteMap.ARG_NAME_PARENT_ID, parentID.toString());
        return itemPlace;
    }

    public CrudAppPlace() {
        setType(Type.lister);
    }

    public CrudAppPlace setType(Type type) {
        putArg(ARG_NAME_CRUD_TYPE, type.name());
        return this;
    }

    public Type getType() {
        return Type.valueOf(getArg(ARG_NAME_CRUD_TYPE));
    }

}
