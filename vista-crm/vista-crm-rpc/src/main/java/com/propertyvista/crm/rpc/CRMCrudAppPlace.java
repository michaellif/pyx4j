/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 5, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.rpc;

import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class CRMCrudAppPlace extends CrudAppPlace {

    @Override
    public void setType(Type type) {
        arg(ARG_NAME_CRUD_TYPE, type.name());
        if (Type.editor.equals(type)) {
            setStable(false);
        }
    }
}
