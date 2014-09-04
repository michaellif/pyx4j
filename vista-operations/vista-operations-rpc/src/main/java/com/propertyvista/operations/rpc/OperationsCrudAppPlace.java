/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 4, 2014
 * @author arminea
 * @version $Id$
 */
package com.propertyvista.operations.rpc;

import com.pyx4j.site.rpc.CrudAppPlace;

public abstract class OperationsCrudAppPlace extends CrudAppPlace{

    public OperationsCrudAppPlace() {
        super();
    }

    public OperationsCrudAppPlace(Type type) {
        super(type);
    }

    @Override
    public void setType(Type type) {
        super.setType(type);
        if (Type.editor.equals(type)) {
            setStable(false);
        }
    }

}
