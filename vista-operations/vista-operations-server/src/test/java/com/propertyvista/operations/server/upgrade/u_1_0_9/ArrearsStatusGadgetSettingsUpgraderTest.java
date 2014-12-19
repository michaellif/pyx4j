/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author ArtyomB
 */
package com.propertyvista.operations.server.upgrade.u_1_0_9;

import junit.framework.Assert;

import org.junit.Test;

public class ArrearsStatusGadgetSettingsUpgraderTest {//@formatter:off
    // debit types:
        // lease, parking, pet, addOn, utility, locker, booking, deposit, accountCharge, nsf, latePayment, other, total;
    

    // ARCode.Type (only debits):
        // Residential(ActionType.Debit)
        // ResidentialShortTerm(ActionType.Debit)
        // Commercial(ActionType.Debit)
        // Parking(ActionType.Debit),
        // Locker(ActionType.Debit),
        // Pet(ActionType.Debit),
        // Utility(ActionType.Debit),
        // AddOn(ActionType.Debit),
        // OneTime(ActionType.Debit),
        // Deposit(ActionType.Debit),
        // AccountCharge(ActionType.Debit),
        // CarryForwardCharge(ActionType.Debit),
        // NSF(ActionType.Debit),
        // LatePayment(ActionType.Debit),
        // ExternalCharge(ActionType.Debit);

    @Test
    public void testTotal() {
        assertSettings(
                "<arrearsStatusGadgetMetadata>"
                + "<refreshInterval>Never</refreshInterval>"
                + "<gadgetId>f5b686ca-02c5-4d90-8558-950e9541ed2e</gadgetId>"
                + "<category>total</category>"
                + "<arrearsStatusListerSettings>"
                + "<pageSize>10</pageSize>"
                + "</arrearsStatusListerSettings>"
                + "</arrearsStatusGadgetMetadata>",
                
                "<arrearsStatusGadgetMetadata>"
                + "<refreshInterval>Never</refreshInterval>"
                + "<gadgetId>f5b686ca-02c5-4d90-8558-950e9541ed2e</gadgetId>"
                + "<filterByCategory>false</filterByCategory>"
                + "<category></category>"
                + "<arrearsStatusListerSettings>"
                + "<pageSize>10</pageSize>"
                + "</arrearsStatusListerSettings>"
                + "</arrearsStatusGadgetMetadata>"                
        );
    }
    
    @Test
    public void testLease() {
        assertSettings(
                "<arrearsStatusGadgetMetadata>"
                + "<refreshInterval>Never</refreshInterval>"
                + "<gadgetId>f5b686ca-02c5-4d90-8558-950e9541ed2e</gadgetId>"
                + "<category>lease</category>"
                + "<arrearsStatusListerSettings>"
                + "<pageSize>10</pageSize>"
                + "</arrearsStatusListerSettings>"
                + "</arrearsStatusGadgetMetadata>",
                
                "<arrearsStatusGadgetMetadata>"
                + "<refreshInterval>Never</refreshInterval>"
                + "<gadgetId>f5b686ca-02c5-4d90-8558-950e9541ed2e</gadgetId>"
                + "<filterByCategory>true</filterByCategory>"
                + "<category>Residential</category>"
                + "<arrearsStatusListerSettings>"
                + "<pageSize>10</pageSize>"
                + "</arrearsStatusListerSettings>"
                + "</arrearsStatusGadgetMetadata>"                
        );        
    }
    
    @Test
    public void testParking() {
        assertSettings(
                "<arrearsStatusGadgetMetadata>"
                        + "<refreshInterval>Never</refreshInterval>"
                        + "<gadgetId>f5b686ca-02c5-4d90-8558-950e9541ed2e</gadgetId>"
                        + "<category>parking</category>"
                        + "<arrearsStatusListerSettings>"
                        + "<pageSize>10</pageSize>"
                        + "</arrearsStatusListerSettings>"
                        + "</arrearsStatusGadgetMetadata>",
                        
                        "<arrearsStatusGadgetMetadata>"
                        + "<refreshInterval>Never</refreshInterval>"
                        + "<gadgetId>f5b686ca-02c5-4d90-8558-950e9541ed2e</gadgetId>"
                        + "<filterByCategory>true</filterByCategory>"
                        + "<category>Parking</category>"
                        + "<arrearsStatusListerSettings>"
                        + "<pageSize>10</pageSize>"
                        + "</arrearsStatusListerSettings>"
                        + "</arrearsStatusGadgetMetadata>"                
                );        
    }
    
    @Test
    public void testUnknownCategory() {
        assertSettings(
                "<arrearsStatusGadgetMetadata>"
                        + "<refreshInterval>Never</refreshInterval>"
                        + "<gadgetId>f5b686ca-02c5-4d90-8558-950e9541ed2e</gadgetId>"
                        + "<category>abracadabra</category>"
                        + "<arrearsStatusListerSettings>"
                        + "<pageSize>10</pageSize>"
                        + "</arrearsStatusListerSettings>"
                        + "</arrearsStatusGadgetMetadata>",
                        
                        "<arrearsStatusGadgetMetadata>"
                                + "<refreshInterval>Never</refreshInterval>"
                                + "<gadgetId>f5b686ca-02c5-4d90-8558-950e9541ed2e</gadgetId>"
                                + "<filterByCategory>false</filterByCategory>"
                                + "<category></category>"
                                + "<arrearsStatusListerSettings>"
                                + "<pageSize>10</pageSize>"
                                + "</arrearsStatusListerSettings>"
                                + "</arrearsStatusGadgetMetadata>"                
                );        
    }
    
    public void assertSettings(String beforeUpgrade, String expected) {
        Assert.assertEquals(expected, ArrearsStatusGadgetSettingsUpgrader.upgradeSettings(beforeUpgrade));
    }
    
}//@formatter:on
