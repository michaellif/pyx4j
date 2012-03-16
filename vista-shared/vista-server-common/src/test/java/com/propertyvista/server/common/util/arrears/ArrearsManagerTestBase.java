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

public class ArrearsManagerTestBase {

    public ArrearsStatusBuilder arrears() {
        return new ArrearsStatusBuilder();
    }

    public void pay(String date, String amount) {

    }

    public void bill() {

    }

    public class ArrearsStatusBuilder {

        public ArrearsStatusBuilder on(String date) {
            return this;
        }

        public ArrearsStatusBuilder thisMonth(String amount) {
            return this;
        }

        public ArrearsStatusBuilder arr0to30(String amount) {
            return this;
        }

        public ArrearsStatusBuilder arr30to60(String amount) {
            return this;
        }

        public ArrearsStatusBuilder arr60to90(String amount) {
            return this;
        }

        public ArrearsStatusBuilder arrOver90(String amount) {
            return this;
        }

        public ArrearsStatusBuilder total(String amount) {
            return this;
        }

        public ArrearsStatusBuilder arBalance(String amount) {
            return this;
        }

        public void assrt() {

        }

    }
}
