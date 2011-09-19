/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 18, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.equifax.model;

/**
 * C - Owner of Business
 * D - Driver
 * E - Executive
 * F - Factory Worker
 * G - Guard
 * I - Skilled Worker
 * J - Unskilled Worker
 * L - Semi-Professional
 * N - Service
 * O - No Information
 * P - Professional
 * R - Retired
 * S - Office Staff
 * T - Construction Trade
 * W - Sales
 * Y - Manager
 * 
 * @author dmitry
 * 
 */
public enum PresentPosition implements EquifaxParameter {

    C, D, E, F, G, I, J, L, N, O, P, R, S, T, W, Y;

    public String getId() {
        return "P0004";
    }

    public String getValue() {
        return name();
    }
}
