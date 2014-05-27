/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 11, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.prospect.ui.application.steps;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.RevalidationTrigger;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.BasicValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.validators.BirthdayDateValidator;
import com.propertyvista.domain.tenant.CustomerPicture;
import com.propertyvista.domain.tenant.prospect.OnlineApplicationWizardStepMeta;
import com.propertyvista.portal.prospect.ui.application.ApplicationWizardStep;
import com.propertyvista.portal.prospect.ui.application.editors.IdUploaderFolder;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationDTO;
import com.propertyvista.portal.rpc.portal.shared.services.CustomerPicturePortalUploadService;
import com.propertyvista.portal.shared.ui.PortalFormPanel;
import com.propertyvista.portal.shared.ui.util.editors.NameEditor;

public class AboutYouStep extends ApplicationWizardStep {

    private static final I18n i18n = I18n.get(AboutYouStep.class);

    private final IdUploaderFolder fileUpload = new IdUploaderFolder();

    public AboutYouStep() {
        super(OnlineApplicationWizardStepMeta.AboutYou);
    }

    @Override
    public IsWidget createStepContent() {
        PortalFormPanel formPanel = new PortalFormPanel(getWizard());

        CImage imageHolder = new CImage(GWT.<CustomerPicturePortalUploadService> create(CustomerPicturePortalUploadService.class), new VistaFileURLBuilder(
                CustomerPicture.class));
        imageHolder.setImageSize(150, 200);
        imageHolder.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.profilePicture()));
        formPanel.append(Location.Left, proto().applicant().picture().file(), imageHolder).decorate().customLabel("");

        formPanel.h3(i18n.tr("Personal Information"));
        formPanel.append(Location.Left, proto().applicant().person().name(), new NameEditor(i18n.tr("Full Name")));
        formPanel.append(Location.Left, proto().applicant().person().sex()).decorate().componentWidth(100);
        formPanel.append(Location.Left, proto().applicant().person().birthDate()).decorate().componentWidth(150);

        formPanel.h3(i18n.tr("Contact Information"));
        formPanel.append(Location.Left, proto().applicant().person().homePhone()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().applicant().person().mobilePhone()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().applicant().person().workPhone()).decorate().componentWidth(180);
        formPanel.append(Location.Left, proto().applicant().person().email()).decorate().componentWidth(230);
        get(proto().applicant().person().email()).setMandatory(true);

        formPanel.h3(i18n.tr("Identification Documents"));
        formPanel.append(Location.Left, proto().applicant().documents(), fileUpload);

        return formPanel;
    }

    @Override
    public void onValueSet(boolean populate) {
        super.onValueSet(populate);

        if (getWizard().isEditable()) {
            fileUpload.setDocumentsPolicy(getValue().applicant().documentsPolicy());
        }

        get(proto().applicant().person().birthDate()).setMandatory(getWizard().getValue().enforceAgeOfMajority().getValue(false));
    }

    @Override
    public void addValidations() {
        super.addValidations();

        get(proto().applicant().person().birthDate()).addComponentValidator(new BirthdayDateValidator());
        get(proto().applicant().person().birthDate()).addComponentValidator(new AbstractComponentValidator<LogicalDate>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() != null && getValue() != null) {
                    if (enforceAgeOfMajority()) {
                        if (!TimeUtils.isOlderThan(getComponent().getValue(), ageOfMajority())) {
                            return new BasicValidationError(getComponent(), i18n.tr("The minimum age requirement is {0} years", ageOfMajority()));
                        }
                    }
                }
                return null;
            }
        });

        get(proto().applicant().person().homePhone()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().applicant().person().workPhone())));
        get(proto().applicant().person().mobilePhone()).addValueChangeHandler(new RevalidationTrigger<String>(get(proto().applicant().person().workPhone())));
        get(proto().applicant().person().workPhone()).addComponentValidator(new AbstractComponentValidator<String>() {
            @Override
            public BasicValidationError isValid() {
                if (getComponent().getValue() == null && hasNoOtherPhone(getWizard().getValue())) {
                    return new BasicValidationError(getComponent(), i18n.tr("At least one phone number is required for applicant"));
                }

                return null;
            }

            private boolean hasNoOtherPhone(OnlineApplicationDTO value) {
                return (value.applicant().person().homePhone().isNull() && value.applicant().person().mobilePhone().isNull());
            }
        });
    }

    public Integer ageOfMajority() {
        return getWizard().getValue().ageOfMajority().getValue();
    }

    public boolean enforceAgeOfMajority() {
        return getWizard().getValue().enforceAgeOfMajority().getValue(false);
    }
}
