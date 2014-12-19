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
 */
package com.propertyvista.common.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.forms.client.ImageFactory.FormsImageBundle;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;

public interface VistaImages extends FormsImageBundle, FolderImages, WidgetsImageBundle {

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

    @Source("PaymentOptionIcons/Banks/TD.jpg")
    ImageResource linkTD();

    @Source("PaymentOptionIcons/Banks/BMO.jpg")
    ImageResource linkBMO();

    @Source("PaymentOptionIcons/Banks/CIBC.jpg")
    ImageResource linkCIBC();

    @Source("PaymentOptionIcons/Banks/Laurentian.jpg")
    ImageResource linkLaurentian();

    @Source("PaymentOptionIcons/Banks/NBC.jpg")
    ImageResource linkNBC();

    @Source("PaymentOptionIcons/Banks/PC.jpg")
    ImageResource linkPCF();

    @Source("PaymentOptionIcons/Banks/RBC.jpg")
    ImageResource linkRBC();

    @Source("PaymentOptionIcons/Banks/Scotiabank.jpg")
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

    @Source("e_cheque-guide-narrow.png")
    ImageResource eChequeGuideNarrow();

    @Source("profile-picture.png")
    ImageResource profilePicture();

    @Source("signature-placeholder.jpeg")
    ImageResource signaturePlaceholder();

}
