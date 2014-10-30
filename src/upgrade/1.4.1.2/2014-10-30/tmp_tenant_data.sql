--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = _dba_, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: tmp_tenant_data; Type: TABLE; Schema: _dba_; Owner: -; Tablespace: 
--

CREATE TABLE tmp_tenant_data (
    id bigint,
    customer_id bigint,
    registered_in_portal boolean,
    person_name_first_name character varying(500),
    person_name_last_name character varying(500),
    person_email character varying(500),
    lease_id character varying(14),
    participant_id character varying(14),
    pap bigint,
    payment_method bigint
);


--
-- Data for Name: tmp_tenant_data; Type: TABLE DATA; Schema: _dba_; Owner: -
--

COPY tmp_tenant_data (id, customer_id, registered_in_portal, person_name_first_name, person_name_last_name, person_email, lease_id, participant_id, pap, payment_method) FROM stdin;
47789	47759	t	Carla	Sitter	carla.sitter12@gmail.com	t0042549	t0042549	\N	15465
48305	48273	t	Rachel	Dickson	rachel.dickson@ryerson.ca	t0042749	t0042749	\N	15676
47223	47195	t	Nathan	Honsberger	njh_17@msn.com	t0042212	t0042212	\N	15696
112784	112268	t	Tussyanrh	Alexander	dussyanth_150@hotmail.com	t0056824	r0034637	\N	15700
47823	47793	f	Devon	O'Connor	djoconno.41@gmail.com	t0042564	t0042564	\N	15825
111855	111341	f	Marie	Gilbert	\N	t0056533	r0033886	27841	16053
47843	47813	f	Kristel	Villena	villenak27@yahoo.ca	t0042577	t0042577	27848	16060
46056	46028	f	Matt	Dyson	\N	t0041838	t0041838	16331	9614
39153	39141	f	Angela	Rombis	\N	t0036657	t0036657	25762	15082
46140	46112	t	Alvaro	Martinez	alvaro462@gmail.com	t0041925	t0041925	\N	9168
48305	48273	t	Rachel	Dickson	rachel.dickson@ryerson.ca	t0042749	t0042749	\N	10688
102777	102603	f	Michael	Mellow	\N	t0052190	r0031097	22210	12272
102777	102603	f	Michael	Mellow	\N	t0052190	r0031097	20658	12272
102795	102621	f	Jill	Thompson	\N	t0052191	r0031099	24801	12273
102795	102621	f	Jill	Thompson	\N	t0052191	r0031099	20659	12273
47884	47852	t	Ryan	Teixeira	ryan.d.teixeira@gmail.com	t0042590	t0042590	22246	10894
47884	47852	t	Ryan	Teixeira	ryan.d.teixeira@gmail.com	t0042590	t0042590	18007	10894
39322	39310	f	Erwin	Lizares	\N	t0036731	t0036731	9558	4974
39322	39310	f	Erwin	Lizares	\N	t0036731	t0036731	8757	4974
39322	39310	f	Erwin	Lizares	\N	t0036731	t0036731	7440	4974
39322	39310	f	Erwin	Lizares	\N	t0036731	t0036731	9557	4974
47456	47427	f	Mary	Anderson	\N	t0042419	t0042419	12016	7019
46242	46214	f	Richard	Renaud	\N	t0042010	t0042010	13518	8503
46296	46268	f	Miranda	Hendershott	\N	t0042075	t0042075	12019	7022
45864	45840	f	Shahab	Khan	\N	t0041549	t0041549	26421	8504
45864	45840	f	Shahab	Khan	\N	t0041549	t0041549	13520	8504
46164	46136	f	Jaesun	Jung	\N	t0041949	t0041949	13550	8513
47184	47156	f	Sarah	List	\N	t0042161	t0042161	15352	8514
47184	47156	f	Sarah	List	\N	t0042161	t0042161	13809	8514
47184	47156	f	Sarah	List	\N	t0042161	t0042161	13551	8514
47270	47242	f	Jin Bog	Kim	\N	t0042250	t0042250	23957	8730
47270	47242	f	Jin Bog	Kim	\N	t0042250	t0042250	14190	8730
47754	47724	f	Walter	Goebel	walter.goebel@bell.net	t0042508	t0042508	14191	8731
46214	46186	f	Barthelemy	Mahieu	\N	t0041965	t0041965	14586	8843
43718	43699	f	Anthony	Chong	\N	t0039698	t0039698	9252	6063
47705	47675	f	Pouya	Nourshoae	\N	t0042483	t0042483	14476	8789
47309	47281	f	Joseph	Pottie	joeypottie@hotmail.com	t0042313	t0042313	27626	8735
47309	47281	f	Joseph	Pottie	joeypottie@hotmail.com	t0042313	t0042313	18217	8735
47309	47281	f	Joseph	Pottie	joeypottie@hotmail.com	t0042313	t0042313	18216	8735
47309	47281	f	Joseph	Pottie	joeypottie@hotmail.com	t0042313	t0042313	14196	8735
47384	47356	f	Jamie Lynn	Bailey	\N	t0042389	t0042389	13536	8509
47773	47743	f	Marina Joelyn	Manni	\N	t0042503	t0042503	13560	8518
46141	46113	f	Stacy	Rowden	\N	t0041882	t0041882	14197	8736
78204	78148	f	Sheyda	Saneinejad	\N	t0041915	t0041915	15726	8740
78204	78148	f	Sheyda	Saneinejad	\N	t0041915	t0041915	14205	8740
47336	47308	f	Ashley	Gallagher	\N	t0042370	t0042370	14488	8799
40500	40486	t	Jenny	Teeter	jenny.teeter@yahoo.com	t0037167	t0037167	26164	8880
40500	40486	t	Jenny	Teeter	jenny.teeter@yahoo.com	t0037167	t0037167	15087	8880
45790	45767	f	Judy	Moyer	\N	t0041495	t0041495	10431	6521
40500	40486	t	Jenny	Teeter	jenny.teeter@yahoo.com	t0037167	t0037167	26164	6297
40500	40486	t	Jenny	Teeter	jenny.teeter@yahoo.com	t0037167	t0037167	15087	6297
45839	45815	f	Rahim	Ismail	\N	t0041524	t0041524	10365	6478
45756	45733	f	Bonnie	Robertshaw	\N	t0041468	t0041468	23959	6535
45756	45733	f	Bonnie	Robertshaw	\N	t0041468	t0041468	13624	6535
45756	45733	f	Bonnie	Robertshaw	\N	t0041468	t0041468	10530	6535
45840	45816	f	Noel	Grange	\N	t0041527	t0041527	10436	6526
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	19464	6655
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	11373	6655
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	10832	6655
102640	39199	t	Prerit	Kukreti	prerit.kukreti@gmail.com	t0051612	t0051612	\N	6557
39211	39199	t	Prerit	Kukreti	prerit.kukreti@gmail.com	t0036360	t0036360	\N	6557
39142	39130	f	Gary	Kraan	gkroom@sympatico.co.ca	t0036646	t0036646	24796	5729
39142	39130	f	Gary	Kraan	gkroom@sympatico.co.ca	t0036646	t0036646	14201	5729
39142	39130	f	Gary	Kraan	gkroom@sympatico.co.ca	t0036646	t0036646	13604	5729
39142	39130	f	Gary	Kraan	gkroom@sympatico.co.ca	t0036646	t0036646	13603	5729
39142	39130	f	Gary	Kraan	gkroom@sympatico.co.ca	t0036646	t0036646	8565	5729
39142	39130	f	Gary	Kraan	gkroom@sympatico.co.ca	t0036646	t0036646	10224	5729
47377	47349	f	Paul	Dube	\N	t0042379	t0042379	14423	8762
47242	47214	f	Charmaine	Mongroo	\N	t0042223	t0042223	14572	8830
48301	48269	f	Jesse	McGuire	\N	t0042709	t0042709	22140	8831
48301	48269	f	Jesse	McGuire	\N	t0042709	t0042709	14573	8831
43736	43717	f	Salayman	Amath	\N	t0039369	t0039369	13986	6692
43736	43717	f	Salayman	Amath	\N	t0039369	t0039369	10865	6692
47247	47219	f	Kiera	Lawson	\N	t0042222	t0042222	13645	8576
47268	47240	f	Bockkyung (Katie)	Lee	\N	t0042238	t0042238	11420	6896
47292	47264	f	Mathavan	Navaneetharajah	\N	t0042266	t0042266	14433	8767
47479	47450	f	Tara	Colwell	\N	t0042442	t0042442	14494	8804
48255	48223	f	Tracey	Love	\N	t0042685	t0042685	14495	8805
47458	47429	f	Douglas	Domonchuk	\N	t0042427	t0042427	27746	6902
47458	47429	f	Douglas	Domonchuk	\N	t0042427	t0042427	13985	6902
47458	47429	f	Douglas	Domonchuk	\N	t0042427	t0042427	11423	6902
46038	46010	f	Mathias	Dombrowski	\N	t0041854	t0041854	10848	6676
45998	45974	f	Pernell	Richards	\N	t0041647	t0041647	11426	6905
47155	47127	f	Oscar	Palacios	\N	t0042145	t0042145	14496	8806
46241	46213	f	Nkeze	Aminkeng	\N	t0042009	t0042009	14498	8807
45827	45803	f	Keith	Loree	\N	t0041525	t0041525	14545	8820
47199	47171	f	Peter	Willems	\N	t0042188	t0042188	11424	6903
46279	46251	f	Diana R.	Dutfield	\N	t0042046	t0042046	11425	6904
78192	78136	f	Shannon	Ryan	\N	t0041903	t0041903	15725	8845
78192	78136	f	Shannon	Ryan	\N	t0041903	t0041903	14588	8845
45326	45300	f	Chinyong	Chong	\N	t0041221	t0041221	10860	6688
45904	45880	f	Douglas	Mccoy	\N	t0041563	t0041563	10862	6689
47278	47250	f	Dawn	Greene	\N	t0042253	t0042253	14053	8696
47338	47310	f	Angelyca	Handsor	\N	t0042358	t0042358	14478	8791
47483	47454	f	Nicole L.	Conrod	nicole.conrod@scotiabank.com	t0042425	t0042425	11912	6980
47714	47684	f	Andrew	Dykstra	\N	t0042472	t0042472	14507	8815
38391	38379	f	Deborah	Brusadin	\N	t0036364	t0036364	7208	4880
47797	47767	f	Shane	Sayers	\N	t0042538	t0042538	14474	8787
47323	47295	f	Ash-Lee	Sabetti	\N	t0042351	t0042351	14479	8792
43716	43697	f	Valerie	Ferro	\N	t0039696	t0039696	9776	5998
43716	43697	f	Valerie	Ferro	\N	t0039696	t0039696	9170	5998
43717	43698	f	Loren	Santarelli	\N	t0039697	t0039697	9777	5996
43717	43698	f	Loren	Santarelli	\N	t0039697	t0039697	9168	5996
43735	43716	f	Mackenzie	Murphy	\N	t0039368	t0039368	9779	6196
43735	43716	f	Mackenzie	Murphy	\N	t0039368	t0039368	9578	6196
45990	45966	t	Paul	De Leonardis	delp1977@gmail.com	t0041660	t0041660	\N	7017
47713	47683	f	Fred	Clarke	\N	t0042482	t0042482	13515	8502
46240	46212	f	Edgardo Gary	Ibanez	\N	t0042013	t0042013	14904	6701
46240	46212	f	Edgardo Gary	Ibanez	\N	t0042013	t0042013	10876	6701
48304	48272	f	Nanci	Walsh	\N	t0042708	t0042708	14993	8979
46167	46139	f	Sharon	Young	\N	t0041950	t0041950	15008	8840
46167	46139	f	Sharon	Young	\N	t0041950	t0041950	14583	8840
46167	46139	f	Sharon	Young	\N	t0041950	t0041950	15008	9005
46167	46139	f	Sharon	Young	\N	t0041950	t0041950	14583	9005
47218	47190	f	Samantha	Schultz	\N	t0042192	t0042192	15344	9122
47184	47156	f	Sarah	List	\N	t0042161	t0042161	15352	9129
47184	47156	f	Sarah	List	\N	t0042161	t0042161	13809	9129
47184	47156	f	Sarah	List	\N	t0042161	t0042161	13551	9129
47264	47236	f	Alex	Josselyn	\N	t0042234	t0042234	15399	9148
47803	47773	f	Tiberiu	Gradisteanu	\N	t0042551	t0042551	15401	9149
48305	48273	t	Rachel	Dickson	rachel.dickson@ryerson.ca	t0042749	t0042749	\N	9183
46204	46176	t	Mike	Ruland	tmruland@hotmail.com	t0041968	t0041968	\N	9261
45990	45966	t	Paul	De Leonardis	delp1977@gmail.com	t0041660	t0041660	\N	9406
45719	45696	t	Dipyendu	Chaudhuri	dipyendu_c@yahoo.co.uk	t0041426	t0041426	\N	9429
47374	47346	f	Laban	Ndungu	\N	t0042406	t0042406	16120	9455
47374	47346	f	Laban	Ndungu	\N	t0042406	t0042406	14455	9455
47243	47215	f	Carlos	Garcia	\N	t0042220	t0042220	16269	9577
48299	48267	f	Liza	Scott	\N	t0042733	t0042733	18538	9604
48299	48267	f	Liza	Scott	\N	t0042733	t0042733	16315	9604
45980	45956	f	Samantha	Schlacter	\N	t0041828	t0041828	26146	9639
45980	45956	f	Samantha	Schlacter	\N	t0041828	t0041828	19191	9639
45980	45956	f	Samantha	Schlacter	\N	t0041828	t0041828	16421	9639
47759	47729	f	Inger	Hansen	\N	t0042516	t0042516	19804	10625
47759	47729	f	Inger	Hansen	\N	t0042516	t0042516	17414	10625
47223	47195	t	Nathan	Honsberger	njh_17@msn.com	t0042212	t0042212	\N	10890
47251	47223	t	Rodney James	Currie	rjc@rodc.ca	t0042232	t0042232	\N	10903
47884	47852	t	Ryan	Teixeira	ryan.d.teixeira@gmail.com	t0042590	t0042590	22246	10893
47884	47852	t	Ryan	Teixeira	ryan.d.teixeira@gmail.com	t0042590	t0042590	18007	10893
46131	46103	t	Ryoko	Aoto	ryoko.aoto@gmail.com	t0041884	t0041884	18218	11005
103209	46103	t	Ryoko	Aoto	ryoko.aoto@gmail.com	t0052801	t0052801	21481	11005
46131	46103	t	Ryoko	Aoto	ryoko.aoto@gmail.com	t0041884	t0041884	18106	11005
46131	46103	t	Ryoko	Aoto	ryoko.aoto@gmail.com	t0041884	t0041884	18218	11113
103209	46103	t	Ryoko	Aoto	ryoko.aoto@gmail.com	t0052801	t0052801	21481	11113
46131	46103	t	Ryoko	Aoto	ryoko.aoto@gmail.com	t0041884	t0041884	18106	11113
45750	45727	f	Ghermatsion	Ghezehey	\N	t0041466	t0041466	23985	11212
45750	45727	f	Ghermatsion	Ghezehey	\N	t0041466	t0041466	18382	11212
47251	47223	t	Rodney James	Currie	rjc@rodc.ca	t0042232	t0042232	\N	11310
99280	99206	f	Rachel	Clail	\N	t0042129	t0042129	19887	12129
99302	99228	f	Jenna	Swan	\N	t0042026	t0042026	19896	12138
99305	99231	f	Dylan	Birley	\N	t0042382	t0042382	19898	12140
99425	99351	f	Minu	Sebastian	\N	t0042513	t0042513	19936	12178
99437	99363	f	Ronald	Frankow	ronaldfrankow@gmail.com	t0041577	t0041577	19942	12184
99477	99403	f	Larry	Clark	\N	t0042540	t0042540	19963	12205
48305	48273	t	Rachel	Dickson	rachel.dickson@ryerson.ca	t0042749	t0042749	\N	12320
47884	47852	t	Ryan	Teixeira	ryan.d.teixeira@gmail.com	t0042590	t0042590	22246	10895
47884	47852	t	Ryan	Teixeira	ryan.d.teixeira@gmail.com	t0042590	t0042590	18007	10895
47884	47852	t	Ryan	Teixeira	ryan.d.teixeira@gmail.com	t0042590	t0042590	22246	12731
47884	47852	t	Ryan	Teixeira	ryan.d.teixeira@gmail.com	t0042590	t0042590	18007	12731
47772	47742	f	Robert	Acres	acres89@yahoo.com	t0042509	t0042509	14585	8842
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	19464	6879
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	11373	6879
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	10832	6879
44972	44947	t	Felipe Faria	Meireles	felipemeireles91@gmail.com	t0040596	t0040596	\N	9517
47789	47759	t	Carla	Sitter	carla.sitter12@gmail.com	t0042549	t0042549	\N	9574
47789	47759	t	Carla	Sitter	carla.sitter12@gmail.com	t0042549	t0042549	\N	11276
47789	47759	t	Carla	Sitter	carla.sitter12@gmail.com	t0042549	t0042549	\N	12094
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	19464	12577
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	11373	12577
41527	41513	t	Sean	Hamilton	seanhamilton24@gmail.com	t0037169	t0037169	10832	12577
47823	47793	f	Devon	O'Connor	djoconno.41@gmail.com	t0042564	t0042564	\N	12712
47789	47759	t	Carla	Sitter	carla.sitter12@gmail.com	t0042549	t0042549	\N	14409
47823	47793	f	Devon	O'Connor	djoconno.41@gmail.com	t0042564	t0042564	\N	14676
47223	47195	t	Nathan	Honsberger	njh_17@msn.com	t0042212	t0042212	\N	14677
47789	47759	t	Carla	Sitter	carla.sitter12@gmail.com	t0042549	t0042549	\N	14890
47223	47195	t	Nathan	Honsberger	njh_17@msn.com	t0042212	t0042212	\N	15243
47823	47793	f	Devon	O'Connor	djoconno.41@gmail.com	t0042564	t0042564	\N	15323
47373	47345	f	Paige (SC)	Hughes	\N	t0042395	t0042395	18107	8530
47373	47345	f	Paige (SC)	Hughes	\N	t0042395	t0042395	13580	8530
47791	47761	f	Brigitte (Daniella)	Vasquez	\N	t0042548	t0042548	19153	8774
47791	47761	f	Brigitte (Daniella)	Vasquez	\N	t0042548	t0042548	14452	8774
47178	47150	f	Michael (SC)	Bisson	\N	t0042180	t0042180	22135	8532
47178	47150	f	Michael (SC)	Bisson	\N	t0042180	t0042180	13583	8532
46238	46210	f	Justin	Godwin	\N	t0042022	t0042022	13585	8534
47872	47840	f	Maria	Mendes	\N	t0042593	t0042593	14450	8773
46220	46192	t	Matthew	Francis	mgfrancis88@gmail.com	t0041981	t0041981	\N	9446
47374	47346	f	Laban	Ndungu	\N	t0042406	t0042406	16120	8776
47374	47346	f	Laban	Ndungu	\N	t0042406	t0042406	14455	8776
46220	46192	t	Matthew	Francis	mgfrancis88@gmail.com	t0041981	t0041981	\N	15248
46220	46192	t	Matthew	Francis	mgfrancis88@gmail.com	t0041981	t0041981	\N	15249
46220	46192	t	Matthew	Francis	mgfrancis88@gmail.com	t0041981	t0041981	\N	15250
46220	46192	t	Matthew	Francis	mgfrancis88@gmail.com	t0041981	t0041981	\N	15251
46220	46192	t	Matthew	Francis	mgfrancis88@gmail.com	t0041981	t0041981	\N	15252
46220	46192	t	Matthew	Francis	mgfrancis88@gmail.com	t0041981	t0041981	\N	15253
42969	42953	t	Wim	Kraan	rainbow-produce@hotmail.com	t0039313	t0039313	\N	\N
45829	45805	f	Zachary Dean	Barrett	\N	t0041536	t0041536	\N	\N
45082	45057	f	Rick	Pebesma	\N	t0040674	t0040674	\N	\N
46058	46030	f	Alisa	Chaly	\N	t0041851	t0041851	\N	\N
107056	106556	f	Noreh	Shakib	\N	t0055661	r0032833	\N	\N
106631	106138	f	Sarah	Odia	\N	t0055362	r0032761	\N	\N
45889	45865	f	Alan	Hanna	\N	t0041576	t0041576	\N	\N
46085	46057	f	Donald	La Rose	donald.larose@hotmail.com	t0041875	t0041875	\N	\N
113469	112952	f	Joseph	Salkouich	\N	t0057667	r0035039	\N	\N
45926	45902	f	Kovan	Mussa	\N	t0041610	t0041610	\N	\N
106504	106013	f	Syed Afzat	Naqvi	\N	t0054746	r0032347	\N	\N
103381	45929	f	Marjorie	Bradley	\N	t0052897	r0031449	\N	\N
45953	45929	f	Marjorie	Bradley	\N	t0035514	r0023095	\N	\N
47146	47118	f	Bashirgol	Majdgol	\N	t0042136	t0042136	\N	\N
46294	46266	f	Megan	Richards	\N	t0042076	t0042076	\N	\N
45988	45964	f	Matt	Kardasiewicz	\N	t0041645	t0041645	\N	\N
45838	45814	f	Jessie	Douglas	\N	t0041516	t0041516	\N	\N
46309	46281	f	Anastasia	Danos	\N	t0042080	t0042080	\N	\N
45859	45835	t	Paul	Hutchinson	printerpaul@hotmail.com	t0041538	t0041538	\N	\N
113464	112947	f	Firas	Mohammad	\N	t0057664	r0035036	\N	\N
78186	78130	f	Mejia	Roger	\N	t0041898	t0041898	\N	\N
113268	112751	f	Nandhan	Nithin	\N	t0057541	r0034912	\N	\N
102993	46180	f	Abdulkadir	Mohamed	\N	t0052537	t0052537	\N	\N
46208	46180	f	Abdulkadir	Mohamed	\N	t0041975	t0041975	\N	\N
112437	111922	f	Riku	Pekkola	\N	t0056614	r0034343	\N	\N
45210	45184	f	Loay	Wady	loaywady@hotmail.com	t0040738	t0040738	\N	\N
46129	46101	f	Caitlyn (SC)	Mosher	\N	t0041886	t0041886	\N	\N
46202	46174	f	Syeda L	Hussain	\N	t0041964	t0041964	\N	\N
45907	45883	f	Mireille	Michaud	\N	t0041570	t0041570	\N	\N
104850	104612	f	Hui Ming	Li	\N	t0054502	r0032096	\N	\N
46300	46272	f	Tim	Smith (OC)	\N	t0042072	t0042072	\N	\N
47203	47175	f	Justin	Breedon	\N	t0042186	t0042186	\N	\N
45862	45838	f	Lisa	Bernard	\N	t0041551	t0041551	\N	\N
45746	45723	f	Kenneth	Taylor	enterpreneur99@hotmail.com	t0041461	t0041461	\N	\N
78206	78150	f	Angela	Maddams	\N	t0041917	t0041917	\N	\N
45906	45882	f	Alexandre	Plouffe	\N	t0041571	t0041571	\N	\N
47202	47174	f	Seng Dao Vong	Phachanh	\N	t0042184	t0042184	\N	\N
47478	47449	f	Kinsey	Cook	\N	t0042417	t0042417	\N	\N
45795	45772	f	Jeannette	Kurtyka	\N	t0041513	t0041513	\N	\N
47711	47681	f	Sheldon	Kuiack	\N	t0042454	t0042454	\N	\N
46158	46130	f	Josh	Gingerich	\N	t0041947	t0041947	\N	\N
39154	39142	f	Monica	Pardo	\N	t0036658	t0036658	\N	\N
44899	43193	f	Amanda Lindsey	Callaghan	lindseycallaghan@hotmail.com	t0040561	t0040561	\N	\N
46302	46274	f	Christopher J.	Ervin	\N	t0042081	t0042081	\N	\N
43211	43193	f	Amanda Lindsey	Callaghan	lindseycallaghan@hotmail.com	t0039763	t0039763	\N	\N
45773	45750	f	Joseph	Marshall	\N	t0041480	t0041480	\N	\N
45828	45804	f	Mike	Atkinson	\N	t0041531	t0041531	\N	\N
47316	47288	f	Saad	Zafar	\N	t0042314	t0042314	\N	\N
45895	45871	f	Gerald	Vezina	\N	t0041573	t0041573	\N	\N
47201	47173	f	Gregory	Hainer (Suite Collections)	\N	t0042182	t0042182	\N	\N
113398	112881	f	Eugenio	Cariati	\N	t0035240	r0034959	\N	\N
28564	28565	f	Ragini	Govindu	\N	t0010911	r0015519	\N	\N
102613	28565	f	Ragini	Govindu	\N	t0051543	r0030518	\N	\N
46285	46257	f	Kyle	Santos	\N	t0042049	t0042049	\N	\N
47307	47279	f	Alistair	Nichol	\N	t0042310	t0042310	\N	\N
45766	45743	f	Te	Shi	\N	t0041478	t0041478	\N	\N
78194	78138	f	Carol	Maclellan	\N	t0041905	t0041905	\N	\N
47305	47277	f	Stacey	Kerfant	stacie.16@hotmail.com	t0042315	t0042315	\N	\N
103278	47277	f	Stacey	Kerfant	stacie.16@hotmail.com	t0052808	t0052808	\N	\N
46139	46111	f	Kane	Van EE	\N	t0041927	t0041927	\N	\N
45763	45740	f	James	Ross-Annett	location@brigil.com	t0041474	t0041474	\N	\N
103715	103481	f	Nicole	Schlueter	\N	t0054458	r0032045	\N	\N
47708	47678	f	Sandra	Laffrenier	\N	t0042477	t0042477	\N	\N
45865	45841	f	Spencer	Warren	\N	t0041547	t0041547	\N	\N
113388	112871	f	Nura	Kalsi	\N	t0057605	r0034972	\N	\N
46273	46245	f	Dale	Girard	\N	t0042050	t0042050	\N	\N
48315	48283	f	Kamal	Prashad (Suite Collections)	k.prashad@yahoo.com	t0042706	t0042706	\N	\N
45905	45881	f	Mireille	Michaud	\N	t0041572	t0041572	\N	\N
104848	104610	f	Dreja	Richard	rich.dreja@gmail.com	t0054509	r0032099	\N	\N
45983	45959	f	Carlos	Zuniga	\N	t0041646	t0041646	\N	\N
103301	45959	f	Carlos	Zuniga	\N	t0052799	t0052799	\N	\N
47472	47443	f	Sabarinath	Balasubramanian	dr.sabari9@gmail.com	t0042430	t0042430	\N	\N
47313	47285	f	Sun Ja	Son	msheat176@gmail.com	t0042307	t0042307	\N	\N
14752	14753	f	Tony	Tan	\N	t0015036	r0006136	\N	\N
102522	14753	f	Tony	Tan	\N	t0051604	r0030577	\N	\N
47349	47321	f	Olivier	Prodjinotho	\N	t0042368	t0042368	\N	\N
46112	46084	f	Lukas	Machaj	\N	t0041858	t0041858	\N	\N
46216	46188	f	Arete	Zafiriou	arete_z@hotmail.com	t0041978	t0041978	\N	\N
103549	46188	f	Arete	Zafiriou	arete_z@hotmail.com	t0053026	t0053026	\N	\N
46270	46242	f	Shawn	Van Loon	\N	t0042032	t0042032	\N	\N
103159	46242	f	Shawn	Van Loon	\N	t0052804	t0052804	\N	\N
47345	47317	f	Sasha	Carr	\N	t0042367	t0042367	\N	\N
47179	47151	f	Alex	Van Mourik	\N	t0042172	t0042172	\N	\N
47468	47439	f	Divya	Issac	divyaissac17@gmail.com	t0042428	t0042428	\N	\N
99412	99338	f	Mark	Sanford	\N	t0042399	t0042399	\N	\N
45212	45186	f	Tina	Wiese	\N	t0041223	t0041223	\N	\N
45198	45172	f	Patrick	Elder	ldp-10@hotmail.com	t0040786	t0040786	\N	\N
47698	45172	f	Patrick	Elder	ldp-10@hotmail.com	t0042453	t0042453	\N	\N
79242	45172	f	Patrick	Elder	ldp-10@hotmail.com	t0043810	t0043810	\N	\N
47297	47269	f	Allan	McGuire	\N	t0042273	t0042273	\N	\N
47806	47776	f	Cheyenne	Dechamplain	\N	t0042550	t0042550	\N	\N
45762	45739	f	Mirzet	Ponjevic	\N	t0041467	t0041467	\N	\N
102778	102604	f	Bore	Savic	\N	t0052190	r0031098	\N	\N
46040	46012	f	Vanessa	Anthony	\N	t0041850	t0041850	\N	\N
103297	48260	f	Brocklyn Vate	Johnson	\N	t0052814	t0052814	\N	\N
48292	48260	f	Brocklyn Vate	Johnson	\N	t0042704	t0042704	\N	\N
47314	47286	f	Christine	Deschamps	\N	t0042306	t0042306	\N	\N
113528	113011	f	Caitlyn	Hanterman	\N	t0057763	r0035105	\N	\N
47827	47797	f	Mandy	Craigg	\N	t0042563	t0042563	\N	\N
113538	113021	f	Jackson	Clements	\N	t0057783	r0035122	\N	\N
113454	112937	f	Justin	Thomas	\N	t0057675	r0035042	\N	\N
104853	104615	f	Susan	Ward	\N	t0054504	r0032098	\N	\N
47252	47224	f	Amy	Ernest	\N	t0042231	t0042231	\N	\N
103197	47170	f	Jeremy	Brule	\N	t0052806	t0052806	\N	\N
103400	79740	f	Kate	Givv	\N	t0053030	r0031487	\N	\N
47198	47170	f	Jeremy	Brule	\N	t0042187	t0042187	\N	\N
79801	79740	f	Kate	Givv	\N	t0044265	r0025547	\N	\N
47488	47459	f	Adrian	Wojcik	\N	t0042436	t0042436	\N	\N
46282	46254	f	Karla P.	Palacios Santana	\N	t0042048	t0042048	\N	\N
48288	48256	f	Nicole E.	Balliston	\N	t0042712	t0042712	\N	\N
113489	112972	f	Tom	Bardon	\N	t0057685	r0035055	\N	\N
47265	47237	f	Josslain	Dune	\N	t0042251	t0042251	\N	\N
46212	46184	f	Jennifer	Sutton	\N	t0041979	t0041979	\N	\N
103316	46184	f	Jennifer	Sutton	\N	t0052802	t0052802	\N	\N
78203	78147	f	Daniel	Mazic	\N	t0041914	t0041914	\N	\N
47699	47669	f	Scott	Crowley	\N	t0042465	t0042465	\N	\N
47948	47916	f	Nina	Rakie	\N	t0042628	t0042628	\N	\N
47744	47714	f	Belarmino	Jimenez	\N	t0042488	t0042488	\N	\N
45891	45867	f	Thuy	Mai	\N	t0041565	t0041565	\N	\N
102720	14864	f	Beverley	Martin	\N	t0051591	r0030566	\N	\N
14863	14864	f	Beverley	Martin	\N	t0010959	r0006830	\N	\N
46271	46243	f	Amanda	Lalonde-Koehler	\N	t0042031	t0042031	\N	\N
47748	47718	f	Courtney	Brewster	\N	t0042510	t0042510	\N	\N
48320	48288	f	Jordan	Wallace	jordanwallace_90@hotmail.com	t0042702	t0042702	\N	\N
103207	46243	f	Amanda	Lalonde-Koehler	\N	t0052803	t0052803	\N	\N
45516	45494	f	Churence (Superintendent)	Hunt	\N	t0041279	t0041279	\N	\N
46243	46215	f	Gene	Scarpelli (BR)	\N	t0042006	t0042006	\N	\N
47277	47249	f	Mandy	Pugh	\N	t0042256	t0042256	\N	\N
78197	78141	f	Christopher	Puttok	\N	t0041908	t0041908	\N	\N
45994	45970	f	Yun-Seok	Jun	jun.yunseok@gmail.com	t0041655	t0041655	\N	\N
47254	47226	f	Ali	Niknafs	\N	t0042227	t0042227	\N	\N
113385	112868	f	Eric	Denovan	\N	t0057609	r0034976	\N	\N
47188	47160	f	Sunil	Raval	sun_raval@hotmail.com	t0042165	t0042165	\N	\N
78184	78128	f	Robert	Tremble	\N	t0041896	t0041896	\N	\N
48325	48293	f	Jaqueline	Lambert	\N	t0042713	t0042713	\N	\N
47740	47710	f	Joshua (SC)	Piddington	\N	t0042494	t0042494	\N	\N
47260	47232	f	Stacy	Sansoucy	\N	t0042236	t0042236	\N	\N
103102	47232	f	Stacy	Sansoucy	\N	t0052807	t0052807	\N	\N
46239	46211	f	Michael	Mallette	\N	t0042008	t0042008	\N	\N
45772	45749	f	Mike	Walker-Dolar	\N	t0041479	t0041479	\N	\N
103364	46032	f	Shenleighanne	Devereux	\N	t0052912	r0031450	\N	\N
46060	46032	f	Shenleighanne	Devereux	\N	t0041847	r0023178	\N	\N
106630	106137	f	Deborah	Odia	\N	t0055362	r0032760	\N	\N
47712	47682	f	Jeannie	Lee	\N	t0042456	t0042456	\N	\N
47799	47769	f	Donatasroyan	Antonkunathas	\N	t0042536	t0042536	\N	\N
46133	46105	f	Ashley	Kadey	\N	t0041885	t0041885	\N	\N
78196	78140	f	Michael	Macdonnel	\N	t0041907	t0041907	\N	\N
47288	47260	f	Alyssa (SUITE COLLECTIONS)	St.John	\N	t0042270	t0042270	\N	\N
47226	47198	f	Alden	Walton	\N	t0042196	t0042196	\N	\N
45853	45829	f	Joseph	Hunter	\N	t0041553	t0041553	\N	\N
47845	47815	f	Sara	O'Brien	\N	t0042580	t0042580	\N	\N
112794	112278	f	Robert	MacPherson	\N	t0056829	r0034641	\N	\N
47839	47809	f	Mamode Izam	Chowtee	\N	t0042584	t0042584	\N	\N
28615	28616	f	Christopher	Butler	\N	t0010965	r0015436	\N	\N
102730	28616	f	Christopher	Butler	\N	t0051597	r0030568	\N	\N
28617	28618	f	Sushma	Ghimire	\N	t0010968	r0015438	\N	\N
102757	102583	f	Sheila	Burgess	\N	t0052189	r0031096	\N	\N
102735	28618	f	Sushma	Ghimire	\N	t0051600	r0030570	\N	\N
45897	45873	f	Patricia	Wheelhouse	\N	t0041584	t0041584	\N	\N
103363	46031	f	Daniel	Dreer	\N	t0052912	t0052912	\N	\N
46059	46031	f	Daniel	Dreer	\N	t0041847	t0041847	\N	\N
45925	45901	f	Tanner (SC)	Leeb	\N	t0041609	t0041609	\N	\N
99242	99168	f	Alex	Carreno	\N	t0042274	t0042274	\N	\N
47289	47261	f	Ben	Tegs	\N	t0042275	t0042275	\N	\N
47181	47153	f	Melissa	Chung	chung.melissa@hotmail.com	t0042154	t0042154	\N	\N
47745	47715	f	Saher	Alyonani	\N	t0042507	t0042507	\N	\N
47328	47300	f	Peter	Gamer (Super)	\N	t0042365	t0042365	\N	\N
46151	46123	f	Michael	Lambert	\N	t0041941	t0041941	\N	\N
47329	47301	f	Azeez Hussama	Azez	\N	t0042363	t0042363	\N	\N
45752	45729	f	Jonathan	Melancon	mom_dad_kids@outlook.com	t0041463	t0041463	\N	\N
103096	47301	f	Azeez Hussama	Azez	\N	t0052810	t0052810	\N	\N
103495	45962	t	Ying	Li	lyniuniu@hotmail.com	t0053025	t0053025	\N	\N
45986	45962	t	Ying	Li	lyniuniu@hotmail.com	t0041654	t0041654	\N	\N
103694	103460	f	Heather	Buckingham	\N	t0054454	r0032043	\N	\N
47463	47434	f	Nikhil	Bhatia	\N	t0042431	t0042431	\N	\N
41008	40994	f	Robert Alex Garvagh and	Josh Maidment and Christian Freure	\N	t0037166	t0037166	\N	\N
104944	104705	f	Meng Yan	Zhang	\N	t0034148	r0032124	\N	\N
102733	28617	f	Joel	Villacorta	\N	t0051599	r0030569	\N	\N
28616	28617	f	Joel	Villacorta	\N	t0010967	r0015437	\N	\N
46000	45976	f	Thai Tan	Truong	johntruong12@hotmail.com	t0041650	t0041650	\N	\N
112786	112270	f	Hyun Jeong	Yi	\N	t0056830	r0034642	\N	\N
47476	47447	f	Elizabeth	Koenig	\N	t0042429	t0042429	\N	\N
45748	45725	f	Yi Ru	Huang	evehuangyiru0218@gmail.com	t0041464	t0041464	\N	\N
113529	113012	f	Kai	Hanterman	\N	t0057763	r0035106	\N	\N
46322	46294	f	Jose (SC)	Interiano	\N	t0042100	t0042100	\N	\N
47333	47305	f	Omar Abdulaziz	Alofi	\N	t0042371	t0042371	\N	\N
47380	47352	f	Scott	Pate	\N	t0042402	t0042402	\N	\N
78211	78155	f	Deborah	Brockway	\N	t0041922	t0041922	\N	\N
45825	45801	f	Mary	Haddock	\N	t0041526	t0041526	\N	\N
46002	45978	f	Karen	Wannamaker	\N	t0041658	t0041658	\N	\N
46298	46270	f	Zachary	Lalonde	\N	t0042087	t0042087	\N	\N
102707	28606	f	Sujatha	Vaddi	\N	t0051585	r0030560	\N	\N
28605	28606	f	Sujatha	Vaddi	\N	t0010953	r0015429	\N	\N
46179	46151	f	David Da	Silva	\N	t0041962	t0041962	\N	\N
47741	47711	f	Lacey	Hogan	\N	t0042492	t0042492	\N	\N
47339	47311	f	Ingram	Carney	ingramcarney@yahoo.ca	t0042359	t0042359	\N	\N
47829	47799	f	Jeremy	Sears	\N	t0042569	t0042569	\N	\N
104852	104614	f	John	Ward	\N	t0054504	r0032097	\N	\N
103641	103407	f	Laura	Wood	\N	t0054408	r0032032	\N	\N
39140	39128	f	Ruth	Ymalay	\N	t0036644	t0036644	\N	\N
78181	78125	f	Hossien	Ekmail	\N	t0041893	t0041893	\N	\N
47227	47199	f	Stephanie T	Whalen	\N	t0042208	t0042208	\N	\N
47801	47771	f	Victoria	Cram	\N	t0042544	t0042544	\N	\N
47485	47456	f	Luis	Andrade	\N	t0042424	t0042424	\N	\N
78189	78133	f	Melanie	Watts	\N	t0041901	t0041901	\N	\N
47310	47282	f	Erin	Leece	\N	t0042308	t0042308	\N	\N
45944	45920	f	Melanie	Clow	\N	t0041628	t0041628	\N	\N
47224	47196	f	Mary-Katherine	Jay	markatherin.jay@gmail.com	t0042213	t0042213	\N	\N
47375	47347	f	Dylan	Hammond	\N	t0042387	t0042387	\N	\N
45832	45808	f	BELL	ANTENNA	\N	t0041532	t0041532	\N	\N
45717	45694	f	Slavka Sylvia	Spaseski	\N	t0041437	t0041437	\N	\N
78209	78153	f	Fred	Sheppard	\N	t0041920	t0041920	\N	\N
45842	45818	f	Holli	Wagenknecht	\N	t0041523	t0041523	\N	\N
45207	45181	f	Andrew	Trivett	\N	t0040735	t0040735	\N	\N
46222	46194	f	Amber	Haffner	\N	t0041980	t0041980	\N	\N
45851	45827	t	Britt	Jessop	bjessop89@gmail.com	t0041539	t0041539	\N	\N
43738	43719	f	Peihai	Li	\N	t0039371	t0039371	\N	\N
45784	45761	f	Sean	Curran	\N	t0041491	t0041491	\N	\N
48306	48274	f	Paul William	Mossman	pbmossman@bell.net	t0042707	t0042707	\N	\N
78208	78152	f	Karen	Geler	\N	t0041919	t0041919	\N	\N
45770	45747	t	Riasat Mahbub	Rakin	riasatmahbub@gmail.com	t0041497	t0041497	\N	\N
78185	78129	f	Feng	Xiang	\N	t0041897	t0041897	\N	\N
112799	112283	f	Daffodil	Arrubio	\N	t0056825	r0034638	\N	\N
45765	45742	f	Diane	Worden	\N	t0041471	t0041471	\N	\N
45911	45887	f	Erin	Madden	\N	t0041575	t0041575	\N	\N
46138	46110	f	Patrick	Bender	\N	t0041926	t0041926	\N	\N
78207	78151	f	Ricardo	Sampath	\N	t0041918	t0041918	\N	\N
47275	47247	f	Marlon Bejarano	Gaviria	\N	t0042258	t0042258	\N	\N
45863	45839	f	Kayla	Dent	\N	t0041546	t0041546	\N	\N
47487	47458	f	Lilia	Kornienko	\N	t0042416	t0042416	\N	\N
47183	47155	f	Mohamed	Mohamed	\N	t0042158	t0042158	\N	\N
45083	45058	f	David	Wolf	\N	t0040671	t0040671	\N	\N
47205	47177	f	Michael Alain	Heroux	\N	t0042185	t0042185	\N	\N
102722	46107	f	Lisa	Summers	lisa-summers@hotmail.com	t0051619	t0051619	\N	\N
46135	46107	f	Lisa	Summers	lisa-summers@hotmail.com	t0041924	t0041924	\N	\N
78198	78142	f	Stanislaw	Muszel	\N	t0041909	t0041909	\N	\N
47320	47292	f	Sara	Mirzaei	\N	t0042318	t0042318	\N	\N
78200	78144	f	Jessica	Redwood	\N	t0041911	t0041911	\N	\N
112439	111924	f	Jaqueline	Cunha	\N	t0056600	r0034312	\N	\N
46055	46027	t	Wali	Ahmed	waliwaaq@gmail.com	t0041840	t0041840	\N	\N
47280	47252	f	Xiu Feng	Liu	\N	t0042255	t0042255	\N	\N
113465	112948	f	Ali	Alsafar	\N	t0057664	r0035037	\N	\N
113466	112949	f	Isam	Alsafar	\N	t0057664	r0035038	\N	\N
46162	46134	f	Francesca	Brundisini	\N	t0041943	t0041943	\N	\N
78199	78143	f	Shayam	Sankarsingh	\N	t0041910	t0041910	\N	\N
47825	47795	t	Jason	Harris	jharris-27@hotmail.com	t0042571	t0042571	\N	\N
47475	47446	f	Wendy	Lo	wendy_lo_99@yahoo.com	t0042435	t0042435	\N	\N
46043	46015	f	Eqbal	Al-Gburi	\N	t0041837	t0041837	\N	\N
47490	47461	f	Alison	Trowbridge	alison.trowbridge@hotmail.com	t0042421	t0042421	\N	\N
45886	45862	f	Ahmad Wali	Nasir	\N	t0041583	t0041583	\N	\N
47767	47737	f	Logan	Konarek	\N	t0042496	t0042496	\N	\N
46166	46138	f	Gaelle	Nkuipou	\N	t0041951	t0041951	\N	\N
47795	47765	f	Stephanie	Jack	\N	t0042543	t0042543	\N	\N
46323	46295	f	Victoria	Mutschler	\N	t0042103	t0042103	\N	\N
47761	47731	f	Aaron	Fisch	\N	t0042495	t0042495	\N	\N
42850	42834	f	Tyler (SC)	Westman	\N	t0039209	t0039209	\N	\N
45992	45968	f	Daryna	Molnar	\N	t0041653	t0041653	\N	\N
47742	47712	f	Amanda	Pedersen	\N	t0042487	t0042487	\N	\N
46156	46128	f	Dimitra	Nikolopoulos	\N	t0041944	t0041944	\N	\N
113491	112974	f	Evan	White	nightc123@gmail.com	t0057686	r0035056	\N	\N
47318	47290	f	Meena	Gunpeth	\N	t0042317	t0042317	\N	\N
103079	45752	f	Sahra	Guled	\N	t0052798	t0052798	\N	\N
45775	45752	f	Sahra	Guled	\N	t0041506	t0041506	\N	\N
46327	46299	f	Shaneice	Richardson	\N	t0042102	t0042102	\N	\N
78188	78132	f	Chris	Tangelis	\N	t0041900	t0041900	\N	\N
47846	47816	f	Maury	Drutz	\N	t0042566	t0042566	\N	\N
48321	48289	f	Paulina	Kursa	\N	t0042703	t0042703	\N	\N
38821	38809	f	Sarfaraz	Khan	sarfaraz79k@yahoo.com	t0037172	t0037172	\N	\N
47293	47265	f	Abdurhman	Albasir	abdu_85e@hotmail.com	t0042268	t0042268	\N	\N
46304	46276	f	Avery	Trice	\N	t0042084	t0042084	\N	\N
47735	47705	f	Joleen	Mountain	\N	t0042486	t0042486	\N	\N
39290	39278	f	Jehad	El-Shrouf	\N	t0036702	t0036702	\N	\N
46048	46020	f	Aries	Romero	\N	t0041846	t0041846	\N	\N
112087	111572	f	Josh	Mactby	\N	t0056576	r0034128	\N	\N
78193	78137	f	Mark	Lewandowski	\N	t0041904	t0041904	\N	\N
78195	78139	f	Jerome	Bertrand	\N	t0041906	t0041906	\N	\N
103358	87052	f	Phiri	Philip	\N	t0052879	r0031448	\N	\N
87123	87052	f	Phiri	Philip	\N	t0035496	r0028138	\N	\N
39134	39122	f	Teresita	Butao	\N	t0036638	t0036638	\N	\N
47454	47425	f	Kelkile	Shibre Teshome	\N	t0042432	t0042432	\N	\N
45913	45889	f	Morteza Mohammadadeh (Manni)	Shamsabad (Building Rep)	\N	t0041561	t0041561	\N	\N
45855	45831	f	Justin	Weil	\N	t0041545	t0041545	\N	\N
44983	44958	f	Kun	Luo	\N	t0040584	t0040584	\N	\N
47337	47309	f	Vacant	Peel Housing	\N	t0042357	t0042357	\N	\N
47291	47263	f	Nicholas	Riedler	\N	t0042267	t0042267	\N	\N
46089	46061	f	Yang	Ba	bayang06330@hotmail.com	t0041877	t0041877	\N	\N
78210	78154	f	Madina Maria	Medina	\N	t0041921	t0041921	\N	\N
39293	39281	f	Kingsley	Larbi	\N	t0036705	t0036705	\N	\N
103703	103469	f	Robert	MacPherson	bnay@kingsleydev.com	t0054457	r0032044	\N	\N
113480	112963	f	Orans	Ampofo	\N	t0057682	r0035053	\N	\N
47258	47230	t	Sijo	Cheeran	chrsfo@gmail.com	t0042228	t0042228	\N	\N
45996	45972	f	Zhaolun	Ji	zhaolun-ji@wau.com	t0041656	t0041656	\N	\N
45538	45516	f	Josh	Whelan-McKay	joshing40@hotmail.com	t0041280	t0041280	\N	\N
47343	47315	f	Marat	Rafikov	\N	t0042369	t0042369	\N	\N
78201	78145	f	Williamina	Hendershot	\N	t0041912	t0041912	\N	\N
112871	112355	f	Taylor	Kuntz	\N	t0056860	r0034673	\N	\N
46081	46053	f	Kamel	Salmi	\N	t0041873	t0041873	\N	\N
46037	46009	f	Patricia	Shelly	\N	t0041841	t0041841	\N	\N
47703	47673	f	Karen	Sladden	\N	t0042484	t0042484	\N	\N
112424	111909	f	Jeffrey	Levine	\N	t0035989	r0034370	\N	\N
46198	46170	f	Mandeep	Rai	\N	t0041982	t0041982	\N	\N
45657	45634	f	Leslie	Lancey (Super)	\N	t0041354	t0041354	\N	\N
45768	45745	f	Luchan	Lin	lluchan@uwaterloo.ca	t0041496	t0041496	\N	\N
78183	78127	f	Darren	MacDonald	\N	t0041895	t0041895	\N	\N
45952	45928	f	Aashish	Gauran	\N	t0041614	t0041614	\N	\N
47220	47192	f	Wenxiao	Xie	\N	t0042195	t0042195	\N	\N
48317	48285	f	Brad	Cator	\N	t0042705	t0042705	\N	\N
47304	47276	f	Bruce	Parkinson (Suite Collections)	\N	t0042316	t0042316	\N	\N
103212	47276	f	Bruce	Parkinson (Suite Collections)	\N	t0052809	t0052809	\N	\N
28563	28564	f	Donavin	Dabrowski	\N	t0010910	r0015518	\N	\N
102611	28564	f	Donavin	Dabrowski	\N	t0051542	r0030517	\N	\N
47481	47452	f	Jessica	Martin	\N	t0042418	t0042418	\N	\N
48238	48206	f	Charles	Lang	\N	t0042675	t0042675	\N	\N
78190	78134	f	Nalini	Sankarsingh	\N	t0041902	t0041902	\N	\N
102726	28615	f	Denitha	Chelvaratnam	\N	t0051594	r0030567	\N	\N
47832	47802	f	Pawel	Wiecki	\N	t0042581	t0042581	\N	\N
46306	46278	f	Xi	Chen	\N	t0042079	t0042079	\N	\N
28614	28615	f	Denitha	Chelvaratnam	\N	t0010962	r0015435	\N	\N
78182	78126	f	Zhang	Ran	\N	t0041894	t0041894	\N	\N
47322	47294	f	Tammy	Benoit	\N	t0042352	t0042352	\N	\N
41822	41808	f	Karen	Boucher	\N	t0039153	t0039153	\N	\N
46132	46104	f	Candance	Ranger	\N	t0041923	t0041923	\N	\N
103174	45759	f	Reinaldo Andres	Sepulveda	\N	t0052797	t0052797	\N	\N
45782	45759	f	Reinaldo Andres	Sepulveda	\N	t0041481	t0041481	\N	\N
43786	43767	f	Laszlone	Magyar	\N	t0039424	t0039424	\N	\N
47750	47720	f	Martha Ambar	Flores	\N	t0042504	t0042504	\N	\N
102693	28603	f	Tre	Dempsey	\N	t0051579	r0030557	\N	\N
28602	28603	f	Tre	Dempsey	\N	t0010947	r0015427	\N	\N
47379	47351	f	Sam	Barber	\N	t0042385	t0042385	\N	\N
113476	112959	f	Mary-Lynn	McFarlane	\N	t0057683	r0035054	\N	\N
103161	47168	f	Hong	Jiang	\N	t0052805	t0052805	\N	\N
46269	46241	f	Candace Marie (ODSP)	Rayner	\N	t0042042	t0042042	\N	\N
47196	47168	f	Hong	Jiang	\N	t0042183	t0042183	\N	\N
47186	47158	f	Jessica	Sinclair	\N	t0042169	t0042169	\N	\N
47706	47676	f	Trepora	Melissa	\N	t0042451	t0042451	\N	\N
45923	45899	f	Codi  (SC)	Hayes	\N	t0041596	t0041596	\N	\N
104854	104616	f	Dave	Murie	\N	t0054293	r0032103	\N	\N
45908	45884	f	Ariana	Blakney	\N	t0041562	t0041562	\N	\N
47245	47217	f	Lefter	Kristo	\N	t0042221	t0042221	\N	\N
47812	47782	f	Janeen	Nippard	janeennippard@gmail.com	t0042534	t0042534	\N	\N
45997	45973	f	Charlotte	McLaughlin	\N	t0041639	t0041639	\N	\N
45791	45768	f	Jeesang	Yoo	\N	t0041487	t0041487	\N	\N
47222	47194	f	Reza	Atashrazm	arashatashmj@yahoo.com	t0042214	t0042214	\N	\N
44787	44765	f	Jeff	Edward	\N	t0040353	t0040353	\N	\N
78202	78146	f	Richard	Foster	\N	t0041913	t0041913	\N	\N
45898	45874	f	Robert	Vansckle	\N	t0041586	t0041586	\N	\N
46280	46252	f	David Da	Silva	\N	t0042052	t0042052	\N	\N
103372	47924	f	Cassandra	Martinez	\N	t0052913	t0052913	\N	\N
47956	47924	f	Cassandra	Martinez	\N	t0042646	t0042646	\N	\N
46168	46140	f	Leslie	Atkinson	\N	t0041956	t0041956	\N	\N
47317	47289	t	Donald	Mann	chefdam@hotmail.com	t0042309	t0042309	\N	\N
46325	46297	f	Dadilo Shiferaw	Sugebo	\N	t0042109	t0042109	\N	\N
39141	39129	f	Alemu-Amare	Fikre	aleyeha@yahoo.com	t0036645	t0036645	\N	\N
102767	102593	f	Jesse	Smith	\N	t0052187	r0031094	\N	\N
46114	46086	f	Luke	Roman	\N	t0041868	t0041868	\N	\N
99310	99236	f	John	Lingard	\N	t0041250	t0041250	\N	\N
46266	46238	f	Roger	Smith	\N	t0042029	t0042029	\N	\N
113492	112975	f	Janice	White	\N	t0057686	r0035057	\N	\N
47460	47431	f	Ashley	Colden	\N	t0042415	t0042415	\N	\N
47835	47805	f	Chris	MacDonald	\N	t0042570	t0042570	\N	\N
47217	47189	f	Kevin	Vandenberg	kevin_vandenberg1936@hotmail.com	t0042194	t0042194	\N	\N
47334	47306	f	Hayes	McDonald	\N	t0042366	t0042366	\N	\N
45761	45738	f	Ming (Cynthia)	Liu	lmcynthia@eoutlook.com	t0041469	t0041469	\N	\N
78187	78131	f	Mary Ann	Kyle	\N	t0041899	t0041899	\N	\N
102601	28561	f	Margaret	Kell	\N	t0051538	r0030514	\N	\N
28560	28561	f	Margaret	Kell	\N	t0010906	r0015382	\N	\N
47382	47354	f	Lauren	Hyssen	\N	t0042400	t0042400	\N	\N
\.


--
-- PostgreSQL database dump complete
--

