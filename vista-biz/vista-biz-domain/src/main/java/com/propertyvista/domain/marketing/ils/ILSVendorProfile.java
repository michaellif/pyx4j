/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 24, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.domain.marketing.ils;

public interface ILSVendorProfile {
    public enum ServiceType {
        /** ILS pulls data from our service */
        pull,
        /** ILS provides service to send data to */
        push
    }

    /** Defines how long ILS will keep an Ad before removal */
    public enum RenewFrequency {
        daily, weekly, monthly, byRequest
    }

    public enum AdTarget {
        unit, floorplan
    }

    ServiceType serviceType();

    String serviceUrl();

    RenewFrequency renewFrequency();

    AdTarget target();

    // daily limits, if any
    Integer maxAdsTotal();

    Integer maxAdsPerLocation();

}
