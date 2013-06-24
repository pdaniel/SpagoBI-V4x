/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
Ext.ns("Sbi.settings.top.toolbar");
Ext.ns("Sbi.settings.bottom.toolbar");
Ext.ns("Sbi.settings.toolbar.html");

Sbi.settings.navigationToolbar = {
		label: "executionInstance.OBJECT_NAME"
};

/**
 * Top custom toolbar settings
 */
Sbi.settings.top.toolbar = {
		buttons: ['documentbrowser',
		          'back',
		          'home', 	
		          'prec',
		          'params',
		          'refresh',
		          'spacer',
		          'title',
		          'spacer',
		          'logout'
		          ],
		          execution:['home',
		                     'prec',
		                     'params',
		                     'refresh',
		                     'spacer',
		                     'title',
		   		          	 'spacer',
		                     'logout'
		                     ],
		                     main:['documentbrowser',
		                           'spacer',
		                           'logout'
		                           ],
		                           login:[],
		                           parameters:[]

};

Sbi.settings.bottom.toolbar = {
		buttons: [
		         'html_telecom_img',  
		         'spacer',
		         'navigation',
		         'logout'
		          ],
		          execution:[
		                     'html_telecom_img',  
		                     'spacer',
		                     'navigation'
		                     ],
		                     main:['html_telecom_img', 'spacer'],
		                     login:[],
		                     parameters:[]

};

Sbi.settings.toolbar.html ={
		//code: '<div style="color: violet; border: 1px solid red; background-color: #fff;">Questo &egrave; un html di esempio</div>'
		telecom_img: '<img height="33"  src="data:image/png;base64,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"/>',
		telecom: 'SpagoBI Mobile'
};


