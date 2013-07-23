/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.tools.dataset.validation;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import it.eng.spagobi.meta.model.olap.Level;
import it.eng.spagobi.metamodel.HierarchyWrapper;
import it.eng.spagobi.metamodel.MetaModelWrapper;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.measurecatalogue.MeasureCatalogue;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */
public class GeoSpatialDimensionDatasetValidator  extends AbstractDatasetValidator {

	public static transient Logger logger = Logger.getLogger(GeoSpatialDimensionDatasetValidator.class);
	public final String GEO_HIERARCHY_NAME = "geo"; //this validator check only hierarchies with this name


	public GeoSpatialDimensionDatasetValidator(IDatasetValidator child){
		childValidator = child;
	}

	/* (non-Javadoc)
	 * @see it.eng.spagobi.tools.dataset.validation.AbstractDatasetValidator#doValidateDataset(it.eng.spagobi.tools.dataset.common.datastore.IDataStore)
	 */
	@Override
	public ValidationErrors doValidateDataset(IDataStore dataStore,Map<String, HierarchyLevel> hierarchiesColumnsToCheck ) {
		ValidationErrors validationErrors = new ValidationErrors();
		MeasureCatalogue measureCatalogue = new MeasureCatalogue();
		
		MetaModelWrapper metamodelWrapper = measureCatalogue.getMetamodelWrapper();
		//List<HierarchyWrapper> hierarchyWrappers = metamodelWrapper.getHierarchies();
		
		for (Map.Entry<String, HierarchyLevel> entry : hierarchiesColumnsToCheck.entrySet())
		{
		    logger.debug("Column Name= "+entry.getKey() + " / HierarchyLevel" + entry.getValue());
		    String columnName = entry.getKey();
		    HierarchyLevel hierarchyLevel = entry.getValue();
		    if (hierarchyLevel.isValidEntry()){
		    	String hierarchyName = hierarchyLevel.getHierarchy_name();
		    	String hierarchyLevelName = hierarchyLevel.getLevel_name();
		    	if (hierarchyName.equalsIgnoreCase(GEO_HIERARCHY_NAME)){
		    		HierarchyWrapper hierarchy = metamodelWrapper.getHierarchy(GEO_HIERARCHY_NAME);
		    		if (hierarchy != null){
		    			if (hierarchy.getName().equalsIgnoreCase(hierarchyName)){
			    			List<Level> levels = hierarchy.getLevels();
			    			Level level = hierarchy.getLevel(hierarchyLevelName);
			    			if (level != null){
			    				String levelName = level.getName();
				    			IDataStore dataStoreLevel = hierarchy.getMembers(levelName); //return a dataStore with one column only

				    			Set admissibleValues = dataStoreLevel.getFieldDistinctValuesAsString(0);

				    			//Iterate the datastore (of the dataset) and check if values are ammissible
				    			Iterator it = dataStore.iterator();
				    			int columnIndex = dataStore.getMetaData().getFieldIndex(columnName); //TODO: check se pu� dar problemi usare questo metodo deprecato
				    			int rowNumber = 0;
				    			while( it.hasNext() ) {
				    	    		IRecord record = (IRecord)it.next();
				    	    		IField field = record.getFieldAt(columnIndex);
				    	    		Object fieldValue = field.getValue(); 
				    	    		if(fieldValue != null)  {
				    	    			if (!admissibleValues.contains(fieldValue))
				    	    			{
				    	    				//add error TODO: check if index are 0-based o 1-based
				    	    				validationErrors.addError(rowNumber, columnIndex+1, field);
				    	    			}
				    	    		} else {
			    	    				//add error TODO: check if index are 0-based o 1-based
			    	    				validationErrors.addError(rowNumber, columnIndex+1, field);
				    	    		}
				    	    		rowNumber++;
				    	    	}
			    			} else {
				    			logger.warn("Attention: the hierarchy "+GEO_HIERARCHY_NAME+" doesn't contain a level "+hierarchyLevelName);
			    			}
			    			

		    			}		    			
		    		} else {
		    			logger.warn("Attention: the validation model doesn't contain a hierarchy with name "+GEO_HIERARCHY_NAME+". Validation will not be performed.");
		    		}

		    	}
		    }
		    
		    
		    
		  
		}
		
		

		
		return validationErrors;
	}
	
	public boolean checkValue(Set admissibleValues, Object fieldValue){
		boolean findString = false;
		boolean findNumber = false;
		
		if (fieldValue instanceof String){
			findString = true;
		} else if (fieldValue instanceof Number){
			findNumber = true;
		}
		
		for (Object admissibleValue : admissibleValues) {
			if (admissibleValue instanceof String){
				if (findString) {
					if (admissibleValue.equals(fieldValue)){
						return true;
					}
				}
			} else if (admissibleValue instanceof Number){
				if (findNumber){
					double admissibleValueDouble = ((Number)admissibleValue).doubleValue();
					double fieldValueDouble = ((Number)fieldValue).doubleValue();
					if (admissibleValueDouble == fieldValueDouble){
						return true;
					}

				}
			}
		}
		
		return false;
	}
	


}