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

import com.pyx4j.commons.css.ClassBasedThemeId;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.commons.css.Style;
import com.pyx4j.commons.css.Theme;
import com.pyx4j.commons.css.ThemeId;

public class TransactionHistoryViewerTheme extends Theme {

    public static enum StyleName implements IStyleName {//@formatter:off
        FinancialTransactionHeaderRow,
        FinancialTransactionRow,
        FinancialTransactionEvenRow,
        FinancialTransactionOddRow,
        
        FinancialTransactionDataColumn,
        FinancialTransactionMoneyColumn,
        FinancialTransactionMoneyCell,
        
        FinancialTransactionTotalRow,
    }//@formatter:on

    public TransactionHistoryViewerTheme() {
        Style style = null;

        style = new Style(".", StyleName.FinancialTransactionHeaderRow.name());
        style.addProperty("font-weight", "bold");
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionRow.name());
        style.addProperty("padding", "1em 2px");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionEvenRow.name());
        style.addProperty("padding", "1em 2px");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionOddRow.name());
        style.addProperty("padding", "1em 2px");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionDataColumn.name());
        style.addProperty("text-align", "left");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionMoneyColumn.name());
        style.addProperty("text-align", "right");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionMoneyCell.name());
        style.addProperty("font-family", "monospace");
        addStyle(style);

        style = new Style(".", StyleName.FinancialTransactionTotalRow.name());
        style.addProperty("font-weight", "bold");
        addStyle(style);

    }

    @Override
    public final ThemeId getId() {
        return new ClassBasedThemeId(getClass());
    }
}
