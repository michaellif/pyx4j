/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.floorplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.security.DataModelPermission;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.CEnumLabel;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CImage;
import com.pyx4j.forms.client.ui.CImageSlider;
import com.pyx4j.forms.client.ui.IAccessAdapter;
import com.pyx4j.forms.client.ui.PermitEditAccessAdapter;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.ui.dialogs.SelectEnumDialog;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.widgets.client.tabpanel.Tab;

import com.propertyvista.common.client.PublicMediaURLBuilder;
import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.folders.VistaBoxFolder;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.activity.crud.floorplan.FloorplanEditorActivity;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.MediaUploadFloorplanService;
import com.propertyvista.domain.MediaFile;
import com.propertyvista.domain.marketing.Marketing;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.marketing.ils.ILSSummaryFloorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.dto.FloorplanDTO;
import com.propertyvista.misc.VistaTODO;

public class FloorplanForm extends CrmEntityForm<FloorplanDTO> {

    private static final I18n i18n = I18n.get(FloorplanForm.class);

    private final Tab marketingTab;

    public FloorplanForm(IForm<FloorplanDTO> view) {
        super(FloorplanDTO.class, view);

        Tab tab = addTab(createGeneralTab(), i18n.tr("General"));
        selectTab(tab);

        marketingTab = addTab(createMarketingTab(), i18n.tr("Marketing"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        marketingTab.setTabVisible(SecurityController.check(DataModelPermission.permissionRead(Marketing.class)));
    }

    private CFolder<FloorplanAmenity> createAmenitiesListEditor() {
        return new VistaTableFolder<FloorplanAmenity>(FloorplanAmenity.class, isEditable()) {
            @Override
            public List<FolderColumnDescriptor> columns() {
                ArrayList<FolderColumnDescriptor> columns = new ArrayList<FolderColumnDescriptor>();
                columns.add(new FolderColumnDescriptor(proto().type(), "15em"));
                columns.add(new FolderColumnDescriptor(proto().name(), "15em"));
                columns.add(new FolderColumnDescriptor(proto().description(), "45em"));
                return columns;
            }
        };
    }

    private IsWidget createGeneralTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Floorplan Information"));

        formPanel.append(Location.Left, proto().name()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().floorCount()).decorate().componentWidth(50);
        formPanel.append(Location.Dual, new HTML());

        formPanel.append(Location.Left, proto().bedrooms()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().bathrooms()).decorate().componentWidth(50);
        formPanel.append(Location.Dual, new HTML());

        formPanel.append(Location.Left, proto().dens()).decorate().componentWidth(50);
        formPanel.append(Location.Right, proto().halfBath()).decorate().componentWidth(50);
        formPanel.append(Location.Dual, new HTML());

        formPanel.append(Location.Left, proto().area()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().areaUnits()).decorate().componentWidth(120);
        formPanel.append(Location.Dual, proto().description()).decorate();

        formPanel.h1(proto().amenities().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().amenities(), createAmenitiesListEditor());

        return formPanel;
    }

    private IsWidget createMarketingTab() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.h1(i18n.tr("Marketing Summary"));

        formPanel.append(Location.Left, proto().marketingName()).decorate();

        if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
            formPanel.h1(proto().ilsSummary().getMeta().getCaption());
            formPanel.append(Location.Dual, proto().ilsSummary(), new ILSSummaryFolder());
        }

        formPanel.h1(i18n.tr("Images"));
        CImageSlider<MediaFile> imageSlider = new CImageSlider<MediaFile>(MediaFile.class,
                GWT.<MediaUploadFloorplanService> create(MediaUploadFloorplanService.class), new PublicMediaURLBuilder()) {
            @Override
            protected FolderImages getFolderIcons() {
                return VistaImages.INSTANCE;
            }

            @Override
            public Widget getImageEntryView(CForm<MediaFile> entryForm) {
                FormPanel formPanel = new FormPanel(entryForm);

                formPanel.append(Location.Dual, entryForm.proto().caption()).decorate().labelWidth(100).componentWidth(180);
                formPanel.append(Location.Dual, entryForm.proto().description()).decorate().labelWidth(100).componentWidth(180);
                formPanel.append(Location.Dual, entryForm.proto().visibility()).decorate().labelWidth(100).componentWidth(70);

                return formPanel.asWidget();
            }
        };
        imageSlider.setImageSize(240, 180);
        imageSlider.setOrganizerWidth(550);
        formPanel.append(Location.Left, proto().media(), imageSlider);

        // set ReadOnly

        IAccessAdapter editAccessAdapter = new PermitEditAccessAdapter(DataModelPermission.permissionUpdate(Marketing.class));
        get(proto().marketingName()).addAccessAdapter(editAccessAdapter);
        get(proto().ilsSummary()).addAccessAdapter(editAccessAdapter);
        get(proto().media()).addAccessAdapter(editAccessAdapter);

        if (ApplicationMode.isDevelopment() || !VistaTODO.pendingYardiConfigPatchILS) {
            formPanel.h1(i18n.tr("ILS Profile"));
            formPanel.append(Location.Dual, proto().ilsProfile(), new ILSProfileFloorplanFolder());
            get(proto().ilsProfile()).addAccessAdapter(editAccessAdapter);
        }

        return formPanel;
    }

    private class ILSSummaryFolder extends VistaBoxFolder<ILSSummaryFloorplan> {

        public ILSSummaryFolder() {
            super(ILSSummaryFloorplan.class);
        }

        @Override
        protected CForm<ILSSummaryFloorplan> createItemForm(IObject<?> member) {
            return new ILSSummaryEditor();
        }

        private class ILSSummaryEditor extends CForm<ILSSummaryFloorplan> {
            public ILSSummaryEditor() {
                super(ILSSummaryFloorplan.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                CImage frontImage = new CImage(GWT.<MediaUploadFloorplanService> create(MediaUploadFloorplanService.class), new PublicMediaURLBuilder());
                frontImage.setImageSize(240, 180);

                formPanel.append(Location.Left, proto().frontImage().file(), frontImage);
                formPanel.append(Location.Right, proto().title()).decorate();
                formPanel.append(Location.Right, proto().description()).decorate();

                return formPanel;
            }
        }
    }

    private class ILSProfileFloorplanFolder extends VistaBoxFolder<ILSProfileFloorplan> {
        public ILSProfileFloorplanFolder() {
            super(ILSProfileFloorplan.class);
        }

        @Override
        protected CForm<ILSProfileFloorplan> createItemForm(IObject<?> member) {
            return new ILSProfileFloorplanEditor();
        }

        @Override
        protected void addItem() {
            // get allowed providers
            ((FloorplanEditorView.Presenter) getParentView().getPresenter()).getILSVendors(new DefaultAsyncCallback<Vector<ILSVendor>>() {
                @Override
                public void onSuccess(Vector<ILSVendor> vendors) {
                    // clear used providers
                    for (ILSProfileFloorplan pr : FloorplanForm.this.getValue().ilsProfile()) {
                        vendors.remove(pr.vendor().getValue());
                    }
                    // show selection dialog
                    new SelectEnumDialog<ILSVendor>(i18n.tr("Select ILS Vendor"), vendors) {
                        @Override
                        public boolean onClickOk() {
                            if (getSelectedType() != null) {
                                ILSProfileFloorplan item = EntityFactory.create(ILSProfileFloorplan.class);
                                item.vendor().setValue(getSelectedType());
                                addItem(item);
                            }
                            return true;
                        }

                        @Override
                        public String getEmptySelectionMessage() {
                            return i18n.tr("No Vendors to choose from.");
                        }
                    }.show();
                }
            }, ((FloorplanEditorActivity) getParentView().getPresenter()).getParentId());
        }

        private class ILSProfileFloorplanEditor extends CForm<ILSProfileFloorplan> {
            public ILSProfileFloorplanEditor() {
                super(ILSProfileFloorplan.class);
            }

            @Override
            protected IsWidget createContent() {
                FormPanel formPanel = new FormPanel(this);

                formPanel.append(Location.Left, proto().vendor(), new CEnumLabel()).decorate();
                formPanel.append(Location.Right, proto().priority()).decorate();

                return formPanel;
            }
        }
    }
}