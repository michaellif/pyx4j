/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.rpc.pt.experimental;

import java.util.ArrayList;
import java.util.List;

public class ServiceCall {
    public enum Name {
        Pet, Charges, Building, Summary
    }

    protected Name name;

    protected List<Object> args = new ArrayList<Object>();

    public void addArg(Object arg) {
        args.add(arg);
    }

    public Object getArg(int index) {
        return args.get(index);
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }
}
