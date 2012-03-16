/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.arrears;

public class ArrearsManagerTest extends ArrearsManagerTestBase {

    public void test() {
        //defBilling().startOn("2012-01-05").amountPerMonth("500"); // or maybe define lease????        
        bill(); // implicit: due to 2012-01-05

        arrears().on("2012-01-06").thisMonth("0").arr0to30("0").arr30to60("0").arr60to90("0").arrOver90("0").total("0").arBalance("0").assrt();

        pay("2012-02-20", "100");

        bill(); // implicit, ends a billing cycle, starts a new one and due to "2012-02-05"
        arrears().on("2012-01-05").thisMonth("300").arr0to30("300").arr30to60("0").arr60to90("0").arrOver90("0").total("300").arBalance("0").assrt();

        pay("2012-02-10", "100");

        arrears().on("2012-01-10").thisMonth("200").arr0to30("200").arr30to60("0").arr60to90("0").arrOver90("0").total("200").arBalance("0").assrt();
        arrears().on("2012-01-11").thisMonth("200").arr0to30("200").arr30to60("0").arr60to90("0").arrOver90("0").total("200").arBalance("0").assrt();

        pay("2012-02-12", "200");

        arrears().on("2012-01-12").thisMonth("100").arr0to30("100").arr30to60("0").arr60to90("0").arrOver90("0").total("100").arBalance("0").assrt();

        arrears().on("2012-02-01").thisMonth("0").arr0to30("100").arr30to60("0").arr60to90("0").arrOver90("0").total("100").arBalance("0").assrt();

        bill(); // implicitly happens at "2012-02-05", ends a billing cycle, starts a new one due to "2012-03-05");

        arrears().on("2012-02-06").thisMonth("500").arr0to30("500").arr30to60("100").arr60to90("0").arrOver90("0").total("600").arBalance("0").assrt();

        pay("2012-02-07", "50");

        arrears().on("2012-02-07").thisMonth("500").arr0to30("500").arr30to60("50").arr60to90("0").arrOver90("0").total("550").arBalance("0").assrt();

        bill(); // happens at "2012-03-05" "29" days since the last bill...

        arrears().on("2012-03-05").thisMonth("500").arr0to30("1000").arr30to60("50").arr60to90("0").arrOver90("0").total("1050").arBalance("0").assrt();
        arrears().on("2012-03-07").thisMonth("500").arr0to30("500").arr30to60("500").arr60to90("50").arrOver90("0").total("1050").arBalance("0").assrt();

        pay("2012-03-15", "25");

        bill(); // happens at "2012-04-05"
        arrears().on("2012-03-05").thisMonth("500").arr0to30("500").arr30to60("500").arr60to90("500").arrOver90("25").total("1525").arBalance("0").assrt();

    }
}
