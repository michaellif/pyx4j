/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 25, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.landlord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.ValidationUtils;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.ImageViewport.ScaleMode;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.VistaFileURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.editors.AddressStructuredEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.building.LandlordMediaUploadService;
import com.propertyvista.domain.property.LandlordMedia;
import com.propertyvista.dto.LandlordDTO;

public class LandlordForm extends CrmEntityForm<LandlordDTO> {

    private static final I18n i18n = I18n.get(LandlordForm.class);

    public LandlordForm(IForm<LandlordDTO> view) {
        super(LandlordDTO.class, view);

        Tab tab = addTab(createGeneralPanel(), i18n.tr("General"));
        selectTab(tab);

        addTab(createBuildingsPanel(), i18n.tr("Buildings"));

    }

    private IsWidget createGeneralPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().name()).decorate();
        formPanel.append(Location.Right, proto().website()).decorate();

        formPanel.h1(proto().address().getMeta().getCaption());
        formPanel.append(Location.Full, proto().address(), new AddressStructuredEditor(false));

        formPanel.br();
        CImage logo = new CImage(GWT.<LandlordMediaUploadService> create(LandlordMediaUploadService.class), new VistaFileURLBuilder(LandlordMedia.class));
        logo.setScaleMode(ScaleMode.Contain);
        logo.setImageSize(368, 60);
        formPanel.append(Location.Full, proto().logo().file(), logo).decorate().customLabel(i18n.tr("Logo"));

        formPanel.br();
        CImage signature = new CImage(GWT.<LandlordMediaUploadService> create(LandlordMediaUploadService.class), new VistaFileURLBuilder(LandlordMedia.class));
        signature.setScaleMode(ScaleMode.Contain);
        signature.setImageSize(368, 60);
        signature.setThumbnailPlaceholder(new Image(VistaImages.INSTANCE.signaturePlaceholder()));
        formPanel.append(Location.Full, proto().signature().file(), signature).decorate().customLabel(i18n.tr("Signature"));

        return formPanel;
    }

    private IsWidget createBuildingsPanel() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);
        formPanel.append(Location.Full, proto().buildings(), new LandlordBuildingFolder(this));
        return formPanel;
    }

    @Override
    public void addValidations() {
        get(proto().website()).addComponentValidator(new AbstractComponentValidator<String>() {

            @Override
            public FieldValidationError isValid() {
                if (getComponent().getValue() != null) {
                    if (ValidationUtils.isSimpleUrl(getComponent().getValue())) {
                        return null;
                    } else {
                        return new FieldValidationError(getComponent(), i18n.tr("Please use proper URL format, e.g. www.propertyvista.com"));
                    }
                }
                return null;
            }
        });
        ((CField) get(proto().website())).setNavigationCommand(new Command() {
            @Override
            public void execute() {
                String url = getValue().website().getValue();
                if (!ValidationUtils.urlHasProtocol(url)) {
                    url = "http://" + url;
                }
                if (!ValidationUtils.isCorrectUrl(url)) {
                    throw new Error(i18n.tr("The URL is not in proper format"));
                }

                Window.open(url, proto().website().getMeta().getCaption(), "status=1,toolbar=1,location=1,resizable=1,scrollbars=1");
            }
        });
    }

}
