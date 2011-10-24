/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-10-24
 * @author artyom
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.vacancyreport.util;

public class Tuple<X, Y> {
    private final X car;

    private final Y cdr;

    public Tuple(X car, Y cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public X car() {
        return car;
    }

    public Y cdr() {
        return cdr;
    }

}
