/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 19, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp;

import com.pyx4j.site.rpc.AppPlace;

public class WizardStep {

    public enum Status {
        notVisited, current, complete, hasAlert
    }

    private final AppPlace place;

    private Status status;

    public WizardStep(AppPlace place, Status status) {
        this.place = place;
        this.status = status;
    }

    public AppPlace getPlace() {
        return place;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
