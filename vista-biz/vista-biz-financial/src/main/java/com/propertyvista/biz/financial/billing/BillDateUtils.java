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

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.financial.billing.InvoiceAccountCharge;
import com.propertyvista.domain.financial.billing.InvoiceAccountCredit;
import com.propertyvista.domain.financial.billing.InvoiceLineItem;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.portal.rpc.shared.BillingException;

public class BillDateUtils {

    private static final I18n i18n = I18n.get(BillDateUtils.class);

    private static final long MILIS_IN_DAY = 1000 * 60 * 60 * 24;

    /**
     * Use 1-Jan-2012 as odd week Sunday ref to calculate odd/even week
     */
    private static final long REF_SUNDAY = new LogicalDate(112, 0, 1).getTime();

    public static final DateRange getOverlappingRange(DateRange range1, DateRange range2) {
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

    /**
     * For @see BillingPeriodCorrelationMethod.LeaseStart
     * 
     * When billing period required to start on lease start date we have one special case:
     * - for 'monthly' or 'semimonthly' PaymentFrequency and if lease date starts on 29, 30, or 31 we correspond this lease to cycle
     * with billingPeriodStartDay = 1 and prorate days of 29/30/31.
     */
    static int calculateBillingTypeStartDay(PaymentFrequency frequency, LogicalDate leaseStartDate) {
        int billingPeriodStartDay = 0;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(leaseStartDate);
        switch (frequency) {
        case Monthly:
            billingPeriodStartDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (billingPeriodStartDay > 28) {
                billingPeriodStartDay = 1;
            }
            break;
        case Weekly:
            billingPeriodStartDay = calendar.get(Calendar.DAY_OF_WEEK);
            break;
        case BiWeekly:
            billingPeriodStartDay = calendar.get(Calendar.DAY_OF_WEEK);
            if ((leaseStartDate.getTime() - REF_SUNDAY) / MILIS_IN_DAY % 14 >= 7) {
                billingPeriodStartDay += 7;
            }
            break;
        case SemiMonthly:
            billingPeriodStartDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (billingPeriodStartDay > 28) {
                billingPeriodStartDay = 1;
            } else if (billingPeriodStartDay > 14) {
                billingPeriodStartDay = billingPeriodStartDay - 14;
            }
            break;
        case SemiAnnyally:
        case Annually:
            throw new Error("Not implemented");
        }
        return billingPeriodStartDay;
    }

    static LogicalDate calculateInitialBillingCycleStartDate(BillingType billingType, LogicalDate leaseStartDate, boolean useCyclePeriodStartDay) {
        LogicalDate billingCycleStartDate = null;
        if (useCyclePeriodStartDay) {
            switch (billingType.paymentFrequency().getValue()) {
            case Monthly:
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(leaseStartDate);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (billingType.billingCycleStartDay().getValue() < 1 || billingType.billingCycleStartDay().getValue() > 28) {
                    throw new BillingException("Wrong billing period start day");
                }
                while (dayOfMonth != billingType.billingCycleStartDay().getValue()) {
                    calendar.add(Calendar.DATE, -1);
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                }
                billingCycleStartDate = new LogicalDate(calendar.getTime());
                break;
            case Weekly:
            case BiWeekly:
            case SemiMonthly:
            case SemiAnnyally:
            case Annually:
                //TODO
                throw new Error("Not implemented");
            }
        } else {
            if (PaymentFrequency.Monthly.equals(billingType.paymentFrequency().getValue())) {
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(leaseStartDate);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                if (dayOfMonth > 28) {
                    calendar.add(Calendar.DATE, -dayOfMonth + 1);
                }
                billingCycleStartDate = new LogicalDate(calendar.getTime());
            } else {
                billingCycleStartDate = leaseStartDate;
            }
        }

        return billingCycleStartDate;
    }

    public static LogicalDate calculateSubsiquentBillingCycleStartDate(PaymentFrequency frequency, LogicalDate previousBillingCycleStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(calculateBillingCycleEndDate(frequency, previousBillingCycleStartDate));
        calendar.add(Calendar.DATE, 1);
        return new LogicalDate(calendar.getTime());
    }

    public static LogicalDate calculateBillingCycleTargetExecutionDate(BillingType cycle, LogicalDate billingCycleStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingCycleStartDate);
        calendar.add(Calendar.DATE, -cycle.paymentFrequency().getValue().getBillRunTargetDayOffset());
        return new LogicalDate(calendar.getTime());
    }

    public static LogicalDate calculateBillingCycleEndDate(PaymentFrequency frequency, LogicalDate billingCycleStartDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingCycleStartDate);
        switch (frequency) {
        case Monthly:
            calendar.add(Calendar.MONTH, 1);
            break;
        case Weekly:
        case BiWeekly:
        case SemiMonthly:
        case Annually:
            throw new Error("Not implemented");
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return new LogicalDate(calendar.getTime());
    }

    public static LogicalDate calculateBillingPeriodStartDate(Bill bill) {
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
        }
        return date;
    }

    public static LogicalDate calculateBillingPeriodEndDate(Bill bill) {
        LogicalDate date = null;
        if (Bill.BillType.Final != bill.billType().getValue()) {
            if (bill.billingAccount().lease().currentTerm().termTo().isNull()
                    || (bill.billingAccount().lease().currentTerm().termTo().getValue().compareTo(bill.billingCycle().billingCycleEndDate().getValue()) >= 0)) {
                date = bill.billingCycle().billingCycleEndDate().getValue();
            } else if (bill.billingAccount().lease().currentTerm().termTo().getValue().compareTo(bill.billingCycle().billingCycleStartDate().getValue()) >= 0) {
                date = bill.billingAccount().lease().currentTerm().termTo().getValue();
            } else {
                throw new BillingException(i18n.tr("Lease already ended"));
            }
        }
        return date;
    }

    public static LogicalDate calculateBillDueDate(Bill bill) {
        if (bill.billType().getValue() == Bill.BillType.Final) {
            //TODO add policy for final bill duedate - for now 7 days after bill run
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(SysDateManager.getSysDate());
            calendar.add(Calendar.DATE, 7);
            return new LogicalDate(calendar.getTime());
        } else {
            return bill.billingPeriodStartDate().getValue();
        }
    }

    public static String formatDays(InvoiceLineItem lineItem) {
        if (lineItem instanceof InvoiceProductCharge) {
            return formatDays(((InvoiceProductCharge) lineItem).fromDate().getValue(), ((InvoiceProductCharge) lineItem).toDate().getValue());
        } else if (lineItem instanceof InvoiceAccountCredit) {
            return formatDays(((InvoiceAccountCredit) lineItem).targetDate().getValue(), null);
        } else if (lineItem instanceof InvoiceAccountCharge) {
            return formatDays(((InvoiceAccountCharge) lineItem).targetDate().getValue(), null);
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
