/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * TODO anto: aggiornare documentazione!!!
 * 
 * Data view for a browser style. It define a layout and provides the stubs
 * methods for the icons browser. This methods should be overridden to define
 * the logic. Only the methods onAddNewRow, onGridSelect and onCloneRow are
 * implemented. The configuration dataView must be passed to the object. It
 * should contains the method setFormState (it is used by onGridSelect)
 * 
 * 
 * @example Ext.define('Sbi.tools.datasource.DataSourceListDetailPanel', {
 *          extend: 'Sbi.widgets.compositepannel.ListDetailPanel' , constructor:
 *          function(config) { //init services this.initServices(); //init form
 *          this.form =
 *          Ext.create('Sbi.tools.datasource.DataSourceDetailPanel',{services:
 *          this.services}); this.columns = [{dataIndex:"DATASOURCE_LABEL",
 *          header:"Name"}, {dataIndex:"DESCRIPTION", header:"description"}];
 *          this.fields =
 *          ["DATASOURCE_ID","DATASOURCE_LABEL","DESCRIPTION","DRIVER","DIALECT_ID","DIALECT_CLASS","DIALECT_NAME","JNDI_URL","USER","PASSWORD","SCHEMA","MULTISCHEMA","CONNECTION_URL"];
 *          this.form.on("save",this.onFormSave,this);
 *          this.callParent(arguments); } , initServices: function(baseParams){
 *          this.services["getAllValues"]=
 *          Sbi.config.serviceRegistry.getRestServiceUrl({ serviceName:
 *          'datasources/listall' , baseParams: baseParams }); ... } ,
 *          onDeleteRow: function(record){ Ext.Ajax.request({ url:
 *          this.services["delete"], params: {DATASOURCE_ID:
 *          record.get('DATASOURCE_ID')}, ... } , onFormSave: function(record){
 *          Ext.Ajax.request({ url: this.services["save"], params: record, ... }
 *          }); ...
 * 
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */

Ext.define('Sbi.tools.dataset.DataSetsView', {
	extend : 'Ext.DataView'

	,
	config : {
		/**
		 * The Ext.data.Store to bind this DataView to.
		 */
		store : null,

		/**
		 * A simple CSS selector that will be used to determine what nodes this
		 * DataView will be working with.
		 */
		itemSelector : null,

		/**
		 * The definition of the columns of the grid.
		 * {@link Sbi.widgets.store.InMemoryFilteredStore#InMemoryFilteredStore}
		 */
		columns : [],
		/**
		 * The list of the properties that should be filtered
		 */
		filteredProperties : new Array(),

		frame : true

	}

	/**
	 * In this constructor you must pass configuration
	 */
	,
	constructor : function(config) {
				
		this.initConfig(config);
		this.initTemplate();
		
		Ext.apply(this, config || {});
		
		this.itemSelector = 'dd';
		this.overClass = 'dd.over', this.trackOver = true,
				this.overItemCls = 'x-item-over', this.frame = true,
				this.emptyText = 'No datasets available', this.inline = {
					wrap : false
				}, this.scrollable = 'horizontal', this.renderTo = Ext
						.getBody()

		this.callParent(arguments);

		this.addListener('itemclick', this.onClick, this);
		this.addListener('itemmouseenter', this.onMouseOver, this);
		this.addListener('itemmouseleave', this.onMouseOut, this);

	}
	
	,
	initTemplate : function() {
		// BUILD THE TPL
		Sbi.debug('DataViewPanel bulding the tpl...');

		var noItem = LN('sbi.browser.folderdetailpanel.emptytext');
		this.tpl = new Ext.XTemplate(
				'<div id="sample-ct">', 	            
	 	           '<div class="group-view">',
	 	            '<h2><div class="group-header">Elenco datasets</div></h2>',
	 	            '<ul>',
	 	            	'<tpl if="root.length == 0">',
	 	            		'<div id="empty-group-message">',
	 	            		noItem,
	 	            		'</div>',
	 	            	'</tpl>',        
	 	            	'<tpl for=".">',
		                    '<dd class="group-item">',
//				                '<div class="item-control-panel">',	 
//			                    	'<tpl for="actions">',   
//			                        	'<div class="button"><img class="action-{name}" title="{description}" src="' + Ext.BLANK_IMAGE_URL + '"/></div>',
//			                        '</tpl>',
//			                    '</div>',
		                    	'<p>{name}</p>',
								'<img src=\"'+ Ext.LEAF_IMAGE_URL  +'\" width="50">' ,
								'<br/>', 
		                    '</dd>',
		                '</tpl>',	              
	 	            '<div style="clear:left"></div>',
		          '</ul>',
	 	          '</div>',
	 	        '</div>');
		
		
		Sbi.debug('DataViewPanel tpl built.');

		return this.tpl;
	}
	
	,
	onRender : function(obj, opt) {
		Ext.DataView.superclass.onRender.call(this, opt);
	}

	,
	onClick : function(obj, rec, item, idx, e, opt) {
		/*
		 * // is of type Ext.EventObject var group =
		 * e.getTarget('div[class=group-header]', 10, true); if(group){
		 * group.up('div[class*=group]').toggleClass('collapsed'); }
		 * 
		 * return Sbi.browser.FolderView.superclass.onClick.apply(this,
		 * arguments);
		 */
		alert("click of id: " + idx);
	}

	,
	onMouseOver : function(obj, e, opt) {

		var group = e.getTarget('div[class=group-header]', 10, true);
		if (!group) {
			var d = e.getTarget('[class*=group-item]', 5, true);
			if (d) {
				var t = d.first('div[class*=item-control-panel]', false);
				if (t) {
					t.applyStyles('visibility:visible');
				}
			}
		}
		// return Sbi.browser.FolderView.superclass.onMouseOver.apply(this,
		// arguments);
		// return this.callParent.onMouseOver(arguments);

	}

	,
	onMouseOut : function(e) {
		/*
		 * var group = e.getTarget('div[class=group-header]', 10, true);
		 * if(!group){ var d = e.getTarget('[class*=group-item]', 5, true);
		 * if(d){ var t = d.first('div[class*=item-control-panel]', false);
		 * if(t){ t.applyStyles('visibility:hidden'); } } } return
		 * Sbi.browser.FolderView.superclass.onMouseOut.apply(this, arguments);
		 */
	}
});