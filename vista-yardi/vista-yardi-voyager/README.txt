@version $Revision$ ($Author$) $Date$

 Here is the access information for the development site using Voyager 6.0. 08.22 SP15.4.

  Web service Url:  https://www.iyardiasp.com/8223thirddev/webservices/itfresidenttransactions20.asmx
   Web service user     propertyvistaws
   Password             52673
   Server               aspdb04
   Database             afqoml_live
   Platform             SQL
   Interface Entity     Property Vista (case sensitive)


 Voyager Url: https://www.iyardiasp.com/8223thirddev/pages/Login.aspx
   Database username    propertyvistadb
   Password             52673
   Server               aspdb04
   Database             afqoml_live
   Platform             SQL
   Property List        prvista – [prvista1 and prvista2]
   Property Control     privsta1 – Post month = Current Month

   prvista2 – Post month = Future Month


  We also have another web service available to push in transactions.  This web
service allows you to push in receipts in smaller batches and still have them
appended to a single batch ID.  We created this web method to alleviate the
issue of time-outs when transferring very large transaction files.
https://www.iyardiasp.com/8223thirddev/webservices/ItfResidentTransactions20_SysBatch.asmx