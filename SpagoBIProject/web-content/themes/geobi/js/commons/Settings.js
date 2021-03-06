/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * Document browser settings
 */
Ext.ns("Sbi.settings.geobi");

Sbi.settings.geobi.browser = {
		mexport: {
			massiveExportWizard: {
				resizable: true
			}
			, massiveExportWizardOptionsPage: {
				
			}, massiveExportWizardParametersPage: {
				
			}
			, massiveExportWizardTriggerPage: {
				showJobDetails: false
			}
		}
	  , showLeftPanels: true
	  , showBreadCrumbs: true
//	  , maxNumberOfExecutionTabs: 5 //the maximum number of tabs to open on execution of documents if valorized
	  , typeLayout: 'card' //possible values: 'tab' or 'card'
	  , showTitle: false 
} 

//Sbi.settings.browser = Ext.apply(Sbi.settings.browser,Sbi.settings.geobi.browser);

Sbi.settings = Ext.apply(Sbi.settings,Sbi.settings.geobi);


