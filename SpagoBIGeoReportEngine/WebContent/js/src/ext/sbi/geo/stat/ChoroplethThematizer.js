/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
 
 **/
 

Ext.ns("Sbi.geo.stat");

/**
 * @requires core/GeoStat.js
 */

Sbi.geo.stat.ChoroplethThematizer = function(map, config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	// constructor
	Sbi.geo.stat.ChoroplethThematizer.superclass.constructor.call(this, map, config);
};

/**
 * @class Sbi.geo.stat.ChoroplethThematizer
 * @extends Sbi.geo.stat.Thematizer
 * 
 * Use this class to create choropleths on a map.
 */

/**
 * @cfg {Object} config
 * ...
 */

Ext.extend(Sbi.geo.stat.ChoroplethThematizer, Sbi.geo.stat.Thematizer, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	
	/**
	 * @property {Sbi.geo.stat.Classification} classification
	 * Defines the different classification to use
	 */
    classification: null
	
    
    
    /**
     * APIProperty: colors
     * {Array(<mapfish.Color>}} Array of 2 colors to be applied to features
     *     We should use styles instead
     */
    , colors: [
        new mapfish.ColorRgb(120, 120, 0),
        new mapfish.ColorRgb(255, 0, 0)
    ]

    /**
     * APIProperty: method
     * {Integer} Specifies the distribution method to use. Possible
     *      values are:
     *      Sbi.geo.stat.Classifier.CLASSIFY_BY_QUANTILS and
     *      Sbi.geo.stat.Classifier.CLASSIFY_BY_EQUAL_INTERVALS
     */
    , method: Sbi.geo.stat.Classifier.CLASSIFY_BY_QUANTILS

    /**
     * APIProperty: numClasses
     * {Integer} Number of classes
     */
    , numClasses: 5

    /**
     * Property: defaultSymbolizer
     * {Object} Overrides defaultSymbolizer in the parent class
     */
    , defaultSymbolizer: {'fillOpacity': 1}

    /**
     * Property: colorInterpolation
     * {Array({<mapfish.Color>})} Array of {<mapfish.Color} resulting from the
     *      RGB color interpolation
     */
    , colorInterpolation: null

    // =================================================================================================================
	// METHODS
	// =================================================================================================================
    
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
    
    /**
     * Constructor: Sbi.geo.stat.ChoroplethThematizer
     *
     * Parameters:
     * map - {<OpenLayers.Map>} OpenLayers map object
     * options - {Object} Hashtable of extra options
     */
    , initialize: function(map, options) {
    	Sbi.geo.stat.ChoroplethThematizer.superclass.initialize.call(this, map, options);
    }
    
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    
  
    /**
     * @method 
     * Thematize the map using the classification
     *
     * @param {Object} options object with a single {Boolean} property: resetClassification.
     */
    , thematize: function(options) {
           
    	Sbi.trace("[ChoroplethThematizer.thematize] : IN");
    	   
    	if (options) {
    		if(options.resetClassification) {
    			this.setClassification();
    		} else {
    			this.updateOptions(options);
    		}
        }
    	  
    	var bins = this.classification.getBins(); 
    	var filters = new Array(bins.length);
    	var rules = new Array(bins.length);
        for (var i = 0; i < bins.length; i++) {
        	
        	filters[i] = this.createClassFilter(bins[i], i);
            var rule = new OpenLayers.Rule({
                symbolizer: {fillColor: this.colorInterpolation[i].toHexString()},
                filter: filters[i]
            });
            rules[i] = rule;
        }
	    	
	
        this.extendStyle(rules);
        
        Sbi.geo.stat.ChoroplethThematizer.superclass.thematize.call(this, arguments);
        
        for (var i = 0; i < rules.length; i++) {
        	Sbi.trace("Features thematized succesfully for class [" + i + "] are [" + filters[i].filteredFeatures + "] on [" + filters[i].dataPoints.length +"] expected");
        }
        
        Sbi.trace("[ChoroplethThematizer.thematize] : OUT");
    }
    
    /**
     * @method
     * Create a feature filter that returns only features belonging to the specified class
     * 
     * @param {Sbi.geo.stat.Bin} bin the class's bin
     * @param {Integer} the class index
     */
    , createClassFilter: function(bin, binIndex) {
    	var filter = new OpenLayers.Filter.Function({
        	evaluate: function(attributes) { 
        		this.invoked = true;
    	        for(var j = 0; j < this.dataPoints.length; j++) {
    	        	if(this.dataPoints[j].coordinatesAreEqualTo([attributes[this.layerId]])) {
    	        		Sbi.trace("Feature [" + attributes[this.layerId]+ "] belong to class [" + binIndex + "]");
    	        		this.filteredFeatures++;
    	        		return true;
    	        	}
    	        }
    	        return false;
    	    }
    	});
    	filter.filteredFeatures = 0;
    	filter.layerId = this.layerId;
    	filter.binIndex = binIndex;
    	filter.dataPoints = bin.dataPoints;
    	filter.invoked = false;
    	
    	return filter;
    }
    
    /**
     * @method
     * Creates the classification that will be used for map thematization
     */
    , classify: function() {
    	Sbi.trace("[ChoroplethThematizer.setClassification] : IN");
        
    	var distribution = this.getDistribution(this.indicator);
    	Sbi.debug("[ChoroplethThematizer.setClassification] : Extracted [" + distribution.getSize() + "] values for indicator [" + this.indicator + "]");
        
        var classificationOptions = {
            'labelGenerator' : this.options.labelGenerator
        };
        
        var classifier = new Sbi.geo.stat.Classifier({distribution: distribution, classificationOptions: classificationOptions});
        this.classification = classifier.classify(
            this.method,
            this.numClasses,
            null
        );
        this.createColorInterpolation();
        
        Sbi.trace("[ChoroplethThematizer.setClassification] : OUT");
    }
    
    /**
     * @method
     * @deperecated use #classify instead
     */
    , setClassification: function() {
    	this.classify();
    }
    
    
    
    /**
     * @method
     * Method used to update the properties 
     * 	- indicator, 
     *  - method, 
     *  - numClasses,
     *  - colors
     *
     * @param {Object} options object
     */
    , updateOptions: function(newOptions) {
    	Sbi.trace("[ChoroplethThematizer.updateOptions] : IN");
        var oldOptions = Ext.apply({}, this.options);
        this.setOptions(newOptions);
        if (newOptions) {
            if (newOptions.method != oldOptions.method ||
                newOptions.indicator != oldOptions.indicator ||
                newOptions.numClasses != oldOptions.numClasses) {
                this.setClassification();
            } else if (newOptions.colors && (
                       !newOptions.colors[0].equals(oldOptions.colors[0]) ||
                       !newOptions.colors[1].equals(oldOptions.colors[1]))) {
                this.createColorInterpolation();
            }
        }
        Sbi.trace("[ChoroplethThematizer.updateOptions] : OUT");
    }  

    /**
     * Method: createColorInterpolation
     *      Generates color interpolation in regard to classification.
     */
    , createColorInterpolation: function() {
        var initialColors = this.colors;
        var numColors = this.classification.bins.length;
        this.colorInterpolation =
            mapfish.ColorRgb.getColorsArrayByRgbInterpolation(
                initialColors[0], initialColors[1], numColors
            );
    }
    
    



    
    
    

  

    /**
     * Method: updateLegend
     *    Update the legendDiv content with new bins label
     */
    , updateLegend: function() {
        if (!this.legendDiv) {
            return;
        }

        // TODO use css classes instead
        this.legendDiv.update("");
        for (var i = 0; i < this.classification.bins.length; i++) {
            var element = document.createElement("div");
            element.style.backgroundColor = this.colorInterpolation[i].toHexString();
            element.style.width = "30px";
            element.style.height = "15px";
            element.style.cssFloat = "left";
            element.style.marginRight = "10px";
            this.legendDiv.appendChild(element);

            var element = document.createElement("div");
            element.innerHTML = this.classification.bins[i].label;
            this.legendDiv.appendChild(element);

            var element = document.createElement("div");
            element.style.clear = "left";
            this.legendDiv.appendChild(element);
        }
    }
});
