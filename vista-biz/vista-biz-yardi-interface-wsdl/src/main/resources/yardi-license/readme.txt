# @version $Revision$ ($Author$) $Date$

ILS/GC Data Exchange Interface license  [PIN 100055318] License is good through 12/31/2014
YSIInstall-ILSGuestCard.lic

Collections Data Exchange Interface license [PIN 100055319] License is good through 12/31/2014
YSIInstall-Collections.lic

BillingAndPayments  License [PIN 100052673] License is good through 12/31/2014
YSIInstall-BillingAndPayments.lic

Maintenance License [PIN 100055548]  License is good through 02/28/2014
YSIInstall-Maintenance.lic



=========
At runtime place the file to folder 

on server
	conf/vista33/yardi-license/
or	
	conf/vista55/yardi-license/

in dev ENV
   vista\vista-server\conf\vista\yardi-license
   
From Operations "System Maintenance"   run "Reset Global Cache"
  New licens should take effect
  
==========
License Less SystemsUrl
   in file "config.properties"  
yardi.licenseLessSystemsUrl=https://yardi.starlightinvest.com/;http://yardi.birchwoodsoftwaregroup.com/