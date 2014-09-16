/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.mock.cards;

import java.math.BigDecimal;

import com.propertyvista.biz.system.SftpTransportConnectionException;
import com.propertyvista.domain.payment.AbstractPaymentMethod;
import com.propertyvista.operations.domain.eft.cards.to.CardsReconciliationTO;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportTO;

public interface CreditCardMockFacade {

    public BigDecimal getAccountBalance(AbstractPaymentMethod paymentMethod);

    public BigDecimal getConvenienceFeeBalance();

    public CardsReconciliationTO receiveCardsReconciliationFiles(String cardsReconciliationId) throws SftpTransportConnectionException;

    public DailyReportTO receiveCardsDailyReportFile(String cardsReconciliationId) throws SftpTransportConnectionException;

}
