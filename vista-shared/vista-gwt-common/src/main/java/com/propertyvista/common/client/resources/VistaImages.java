/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.common.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.forms.client.ImageFactory.FormsImageBundle;
import com.pyx4j.forms.client.images.EntityFolderImages;
import com.pyx4j.widgets.client.images.WidgetsImages;

public interface VistaImages extends FormsImageBundle, EntityFolderImages, WidgetsImages {

    VistaImages INSTANCE = GWT.create(VistaImages.class);

    @Override
    @Source("date.png")
    ImageResource datePicker();

    @Override
    @Source("clear.png")
    ImageResource clear();

    @Override
    @Source("action.png")
    ImageResource triggerDown();

    @Source("clip.png")
    ImageResource clip();

    @Override
    @Source("qv.png")
    ImageResource formTooltipInfo();

    @Source("qv_hover.png")
    ImageResource formTooltipHoverInfo();

    // -----------------------------------------------

    @Source("logo.png")
    ImageResource logo();

    @Source("logo_bmo.gif")
    ImageResource logoBMO();

    @Source("logo_rbc.gif")
    ImageResource logoRBC();

    @Source("logo_scotia.jpg")
    ImageResource logoScotia();

    @Source("logo_td.jpg")
    ImageResource logoTD();

    // -----------------------------------------------

    @Source("PaymentOptionIcons/Banks/bfg.png")
    ImageResource linkTD();

    @Source("PaymentOptionIcons/Banks/bmo.png")
    ImageResource linkBMO();

    @Source("PaymentOptionIcons/Banks/cibc.png")
    ImageResource linkCIBC();

    @Source("PaymentOptionIcons/Banks/laurentian.png")
    ImageResource linkLaurentian();

    @Source("PaymentOptionIcons/Banks/manulife.png")
    ImageResource linkManulife();

    @Source("PaymentOptionIcons/Banks/nbc.png")
    ImageResource linkNBC();

    @Source("PaymentOptionIcons/Banks/pcf.png")
    ImageResource linkPCF();

    @Source("PaymentOptionIcons/Banks/rbc.png")
    ImageResource linkRBC();

    @Source("PaymentOptionIcons/Banks/scotia.png")
    ImageResource linkScotia();

    // -----------------------------------------------

    @Source("PaymentOptionIcons/Small/Mastercard.png")
    ImageResource paymentMC();

    @Source("PaymentOptionIcons/Small/Visa.png")
    ImageResource paymentVISA();

    @Source("PaymentOptionIcons/Small/Discover.png")
    ImageResource paymentDiscover();

    @Source("PaymentOptionIcons/Small/eCheck.png")
    ImageResource paymentECheque();

    @Source("PaymentOptionIcons/Small/Cash.png")
    ImageResource paymentCash();

    @Source("PaymentOptionIcons/Small/Credit-Card.png")
    ImageResource paymentCredit();

    @Source("PaymentOptionIcons/Small/DirectBanking.png")
    ImageResource paymentDirectBanking();

    @Source("PaymentOptionIcons/Small/Interac.png")
    ImageResource paymentInterac();

    @Source("PaymentOptionIcons/Small/RecurringCredit.png")
    ImageResource recurringCredit();

    @Source("cheque-guide.jpg")
    ImageResource chequeGuide();

    @Source("e_cheque-guide.png")
    ImageResource eChequeGuide();

    @Source("profile-picture.png")
    ImageResource profilePicture();
}
