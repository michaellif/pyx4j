/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2015
 * @author ernestog
 */
package com.propertyvista.biz.preloader;

import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.annotations.Translate;
import com.pyx4j.i18n.shared.I18nEnum;

@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public enum ResetType {

    @Translate("Drop All and Configure Vista Operations")
    prodReset,

    @Translate("Drop All and Preload all demo PMC (~3 min 24 seconds) [No Mockup]")
    all,

    @Translate("Drop All and Preload all demo PMC (+<b>star</b> tenants and buildings) : Mini version for UI Design (~1 min 22 seconds)")
    allMini,

    @Translate("Drop All and Preload One 'vista' PMC : Mini version for UI Design (~30 seconds)")
    vistaMini,

    @Translate("Drop All and Preload One 'vista' PMC : Default preload")
    vista,

    @Translate("Drop All and Preload One 'vista' PMC : Perfomance tests version (~25 minutes)")
    vistaMax3000,

    @Translate("Drop All and Preload all demo PMC : Mockup version  (~5 minutes)")
    allWithMockup,

    @Translate("For All PMC Generate Mockup on top of existing data")
    allAddMockup,

    @Translate("Drop All Tables")
    clear,

    @Translate("Drop PMC Tables and Preload one PMC")
    resetPmc(true),

    @Translate("Drop <b>Operations</b> and PMC Tables and Preload one PMC")
    resetOperationsAndPmc(true),

    @Translate("Preload this PMC")
    preloadPmc(true),

    @Translate("Preload this PMC : Mockup version  (~5 minutes)")
    preloadPmcWithMockup,

    @Translate("Generate Mockup on top of existing data")
    addPmcMockup,

    @Translate("Generate Mockup on top of existing data - Only MockupTenantPreloader")
    addPmcMockupTest1,

    clearPmc(true),

    dropForeignKeys,

    dbIntegrityCheck,

    @Translate("Reset Data Cache for this PMC")
    resetPmcCache,

    @Translate("Reset Data Cache for All PMC")
    resetAllCache;

    private final boolean pmcParam;

    ResetType() {
        this.pmcParam = false;
    }

    ResetType(boolean pmcParam) {
        this.pmcParam = pmcParam;
    }

    public boolean getPmcParam() {
        return this.pmcParam;
    }

    @Override
    public String toString() {
        return I18nEnum.toString(this);
    }
}
