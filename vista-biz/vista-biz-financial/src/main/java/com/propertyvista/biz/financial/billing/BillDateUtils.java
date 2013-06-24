/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillDateUtils {

    private static final I18n i18n = I18n.get(BillDateUtils.class);

    public static final DateRange getOverlappingRange(DateRange range1, DateRange range2) {

        if (range1 == null || range2 == null) {
            return null;
        }

        LogicalDate fromDate1 = range1.getFromDate() == null ? new LogicalDate(0) : range1.getFromDate();
        LogicalDate toDate1 = range1.getToDate() == null ? new LogicalDate(Long.MAX_VALUE) : range1.getToDate();

        LogicalDate fromDate2 = range2.getFromDate() == null ? new LogicalDate(0) : range2.getFromDate();
        LogicalDate toDate2 = range2.getToDate() == null ? new LogicalDate(Long.MAX_VALUE) : range2.getToDate();

        LogicalDate fromDate;
        LogicalDate toDate;

        if (fromDate1.after(toDate2) || fromDate2.after(toDate1)) {
            return null;
        }

        if (fromDate1.after(fromDate2)) {
            fromDate = fromDate1;
        } else {
            fromDate = fromDate2;
        }

        if (toDate1.before(toDate2)) {
            toDate = toDate1;
        } else {
            toDate = toDate2;
        }

        return new DateRange(fromDate, toDate);
    }

    public static final DateRange getOverlappingRange(DateRange range1, DateRange range2, DateRange range3) {
        return getOverlappingRange(getOverlappingRange(range1, range2), range3);
    }

    public static final DateRange getOverlappingRange(DateRange range1, DateRange range2, DateRange range3, DateRange range4) {
        return getOverlappingRange(getOverlappingRange(range1, range2), getOverlappingRange(range3, range4));
    }

    public static LogicalDate calculateBillingCycleDateByOffset(int offset, LogicalDate billingCycleStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingCycleStartDate);
        calendar.add(Calendar.DATE, offset);
        return new LogicalDate(calendar.getTime());
    }

    public static DateRange calculateBillingPeriodRange(Bill bill) {
        LogicalDate start = calculateBillingPeriodStartDate(bill);
        LogicalDate end = calculateBillingPeriodEndDate(bill);

        // check interdependency:
        if (start == null) {
            end = null;
        } else if (end == null) {
            start = null;
        }

        return new DateRange(start, end);
    }

    private static LogicalDate calculateBillingPeriodStartDate(Bill bill) {
        LogicalDate date = null;

        if (Bill.BillType.First == bill.billType().getValue()) {
            date = bill.billingAccount().lease().currentTerm().termFrom().getValue();
        } else if (Bill.BillType.ZeroCycle == bill.billType().getValue()) {
            // start with lease if billing start date has passed
            date = bill.billingCycle().billingCycleStartDate().getValue();
            if (date.compareTo(bill.billingAccount().lease().currentTerm().termFrom().getValue()) < 0) {
                date = bill.billingAccount().lease().currentTerm().termFrom().getValue();
            }
        } else if (Bill.BillType.Regular == bill.billType().getValue()) {
            date = bill.billingCycle().billingCycleStartDate().getValue();
        } else if (Bill.BillType.External == bill.billType().getValue()) {
            // start with lease if billing cycle starts earlier
            date = bill.billingCycle().billingCycleStartDate().getValue();
            if (date.compareTo(bill.billingAccount().lease().currentTerm().termFrom().getValue()) < 0) {
                date = bill.billingAccount().lease().currentTerm().termFrom().getValue();
            }
        }

        // see if lease has been terminated
        if (date != null && !bill.billingAccount().lease().terminationLeaseTo().isNull()) {
            if (date.compareTo(bill.billingAccount().lease().terminationLeaseTo().getValue()) > 0) {
                date = null;
            }
        }

        return date;
    }

    /**
     * Returns BillingPeriodEndDate:
     * - if lease ends after billingCycleEndDate - billingCycleEndDate;
     * - if lease ends during billingCycle period - lease end date;
     * - if lease ends before billingCycleEndDate - null
     * then:
     * - if lease has been terminated and termination date is before calculated BillingPeriodEndDate - the latter set to leaseTermination date.
     */
    private static LogicalDate calculateBillingPeriodEndDate(Lease lease, BillingCycle cycle) {
        LogicalDate date = null;

        if (lease.currentTerm().termTo().isNull() || (lease.currentTerm().termTo().getValue().compareTo(cycle.billingCycleEndDate().getValue()) >= 0)) {
            date = cycle.billingCycleEndDate().getValue();
        } else if (lease.currentTerm().termTo().getValue().compareTo(cycle.billingCycleStartDate().getValue()) >= 0) {
            date = lease.currentTerm().termTo().getValue();
        }
        // see if lease has been terminated
        if (date != null && !lease.terminationLeaseTo().isNull()) {
            if (date.compareTo(lease.terminationLeaseTo().getValue()) > 0) {
                date = lease.terminationLeaseTo().getValue();
            }
        }

        return date;
    }

    private static LogicalDate calculateBillingPeriodEndDate(Bill bill) {
        return calculateBillingPeriodEndDate(bill.billingAccount().lease(), bill.billingCycle());
    }

    public static LogicalDate calculateBillDueDate(Bill bill) {
        LogicalDate startDate;
        int dueDateOffset;
        if (bill.billType().getValue() == Bill.BillType.Final) {
            startDate = bill.billingAccount().lease().leaseTo().getValue();
            dueDateOffset = bill.billingAccount().finalDueDayOffset().getValue();
        } else {
            startDate = bill.billingPeriodStartDate().getValue();
            dueDateOffset = bill.billingAccount().paymentDueDayOffset().getValue();
        }
        return calculateBillingCycleDateByOffset(dueDateOffset, startDate);
    }

    public static String formatDays(InvoiceLineItem lineItem) {
        if (lineItem instanceof InvoiceProductCharge) {
            return formatDays(((InvoiceProductCharge) lineItem).fromDate().getValue(), ((InvoiceProductCharge) lineItem).toDate().getValue());
        } else if (lineItem instanceof InvoiceAccountCredit) {
            return formatDays(((InvoiceAccountCredit) lineItem).postDate().getValue(), null);
        } else if (lineItem instanceof InvoiceAccountCharge) {
            return formatDays(((InvoiceAccountCharge) lineItem).postDate().getValue(), null);
        } else {
            return formatDays(lineItem.postDate().getValue(), null);
        }
    }

    public static String formatDays(LogicalDate fromDate, LogicalDate toDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        if (fromDate != null) {
            if (toDate != null) {
                if (fromDate.equals(toDate)) {
                    return formatter.format(fromDate);
                } else {
                    return formatter.format(fromDate) + " - " + formatter.format(toDate);
                }
            } else {
                return formatter.format(fromDate);
            }
        }
        return "";
    }

}
