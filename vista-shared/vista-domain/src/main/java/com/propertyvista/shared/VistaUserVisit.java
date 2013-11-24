/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 23, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.shared;

import com.pyx4j.security.shared.UserVisit;

import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.domain.security.common.VistaApplication;

@SuppressWarnings("serial")
public abstract class VistaUserVisit<E extends AbstractUser> extends UserVisit {

    private E currentUser;

    private VistaApplication application;

    // to make it GWT Serializable ?
    public VistaUserVisit() {
        super();
    }

    public VistaUserVisit(VistaApplication application, E user) {
        super(user.getPrimaryKey(), user.name().getValue());
        setEmail(user.email().getValue());
        setCurrentUser(user);
        this.application = application;
    }

    public VistaApplication getApplication() {
        return application;
    }

    public E getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(E currentUser) {
        this.currentUser = currentUser;
        setChanged();
    }

}
