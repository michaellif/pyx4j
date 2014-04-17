/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.service;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.activity.EntitySelectorTableVisorController;
import com.pyx4j.site.client.ui.IPane;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.services.selections.SelectFeatureListService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.misc.VistaTODO;

class ServiceFeatureFolder extends VistaTableFolder<Feature> {

    private static final I18n i18n = I18n.get(ServiceFeatureFolder.class);

    private final CrmEntityForm<Service> parent;

    public ServiceFeatureFolder(boolean modifyable, CrmEntityForm<Service> parent) {
        super(Feature.class, modifyable);
        this.parent = parent;
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().code(), "25em"),
                new EntityFolderColumnDescriptor(proto().version().name(), "25em"),
                new EntityFolderColumnDescriptor(proto().version().recurring(), "5em"),
                new EntityFolderColumnDescriptor(proto().version().mandatory(), "5em"),
                new EntityFolderColumnDescriptor(proto().version().availableOnline(), "10em")
                );//@formatter:on	
    }

    @Override
    public <T extends CComponent<T, ?>> T create(IObject<?> member) {
        if (member instanceof Feature) {
            return (T) new FeatureEditor();
        }
        return super.create(member);
    }

    private class FeatureEditor extends CEntityFolderRowEditor<Feature> {

        public FeatureEditor() {
            super(Feature.class, columns());
            setEditable(false);
            setViewable(true);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public <T extends CComponent<T, ?>> T create(IObject<?> member) {
            CComponent<?, ?> comp = null;
            if (member.equals(proto().code())) {
                comp = new CEntityLabel<ARCode>();
                if (!ServiceFeatureFolder.this.isEditable()) {
                    ((CField) comp).setNavigationCommand(new Command() {
                        @Override
                        public void execute() {
                            AppSite.getPlaceController().goTo(AppPlaceEntityMapper.resolvePlace(Feature.class).formViewerPlace(getValue().getPrimaryKey()));
                        }
                    });
                }
            } else {
                comp = super.create(member);
            }
            return (T) comp;
        }
    }

    @Override
    protected void addItem() {
        new FeatureSelectorDialog(parent.getParentView()).show();
    }

    private class FeatureSelectorDialog extends EntitySelectorTableVisorController<Feature> {

        public FeatureSelectorDialog(IPane parentView) {
            super(parentView, Feature.class, true, getValue(), i18n.tr("Select Feature"));
            setParentFiltering(parent.getValue().catalog().getPrimaryKey());
            if (!VistaTODO.VISTA_2256_Default_Product_Catalog_Show) {
                addFilter(PropertyCriterion.eq(proto().defaultCatalogItem(), Boolean.FALSE));
            }
        }

        @Override
        public void onClickOk() {
            for (Feature selected : getSelectedItems()) {
                addItem(selected);
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().code()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().mandatory()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().recurring()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().versionNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().version().availableOnline()).build(),
                    new MemberColumnDescriptor.Builder(proto().defaultCatalogItem()).build()
            );//@formatter:on
        }

        @Override
        public List<Sort> getDefaultSorting() {
            return Arrays.asList(new Sort(proto().code(), false), new Sort(proto().version().name(), false));
        }

        @Override
        protected AbstractListService<Feature> getSelectService() {
            return GWT.<AbstractListService<Feature>> create(SelectFeatureListService.class);
        }
    }

}