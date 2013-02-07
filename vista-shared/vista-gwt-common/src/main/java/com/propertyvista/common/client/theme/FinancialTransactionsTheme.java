/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-07
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.theme;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;

public class FinancialTransactionsTheme extends Theme {

    public static enum StyleName implements IStyleName {//@formatter:off
        FinancialTransactionHeaderRow,
        FinancialTransactionRow,
        FinancialTransactionEvenRow,
        FinancialTransactionOddRow,
        
        FinancialTransactionMoneyColumn,
        FinancialTransactionMoneyCell,
        
        FinancialTransactionTotalRow,
    }//@formatter:on

    public FinancialTransactionsTheme() {
        Style style = null;

        style = new Style(".", StyleName.FinancialTransactionHeaderRow);
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionRow);
        style.addProperty("padding", "1em 2px");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionEvenRow);
        style.addProperty("padding", "1em 2px");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionOddRow);
        style.addProperty("padding", "1em 2px");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionMoneyColumn);
        style.addProperty("text-align", "right");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionMoneyCell);
        style.addProperty("font-family", "monospace");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionTotalRow);
        style.addProperty("font-weight", "bold");
        addStyle(style);

    }
}
