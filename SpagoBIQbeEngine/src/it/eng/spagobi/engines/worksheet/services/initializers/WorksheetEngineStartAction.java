/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.worksheet.services.initializers;

import it.eng.qbe.datasource.AbstractDataSource;
import it.eng.qbe.statement.sql.SQLDataSet;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.worksheet.WorksheetEngine;
import it.eng.spagobi.engines.worksheet.WorksheetEngineAnalysisState;
import it.eng.spagobi.engines.worksheet.WorksheetEngineException;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.template.WorksheetTemplateParser;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineStartupException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.temporarytable.TemporaryTable;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableManager;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableRecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *          Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class WorksheetEngineStartAction extends AbstractEngineStartAction {	

	private static final long serialVersionUID = 4631203497610373565L;

	// INPUT PARAMETERS
	
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	public static final String IS_A_DATASET_LINKED_TO_DOC = "IS_A_DATASET_LINKED_TO_DOC";
	
	protected WorksheetEngineInstance worksheetEngineInstance = null;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(WorksheetEngineStartAction.class);
    
    public static final String ENGINE_NAME = "SpagoBIWorksheetEngine";
		
    public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
    	worksheetEngineInstance = null;
    	WorksheetEngineAnalysisState analysisState;
    	Locale locale;
    	
    	logger.debug("IN");
       
    	try {
    		setEngineName(ENGINE_NAME);
			super.service(serviceRequest, serviceResponse);
			SourceBean templateBean = getTemplateAsSourceBean();
			logger.debug("User Id: " + getUserId());
			logger.debug("Audit Id: " + getAuditId());
			//logger.debug("Document Id: " + getDocumentId());
			logger.debug("Template: " + templateBean);
						
			if(getAuditServiceProxy() != null) {
				logger.debug("Audit enabled: [TRUE]");
				getAuditServiceProxy().notifyServiceStartEvent();
			} else {
				logger.debug("Audit enabled: [FALSE]");
			}
			
			logger.debug("Creating engine instance ...");
			try {
				
				Map env = this.getEnv();
				this.checkPersistence( env , templateBean );
				
				worksheetEngineInstance = WorksheetEngine.createInstance(templateBean, env);
				QbeEngineInstance qbeEngineInstance = this.getQbeEngineInstance(worksheetEngineInstance);
				if (qbeEngineInstance != null) {
					worksheetEngineInstance.setQbeEngineInstance(qbeEngineInstance);
					setAttribute(EngineConstants.ENGINE_INSTANCE, qbeEngineInstance);
					setAttributeInSession(EngineConstants.ENGINE_INSTANCE, qbeEngineInstance);
				}
				
				this.initDataSet(worksheetEngineInstance);
				this.initDataSource(worksheetEngineInstance);
				
			} catch(Throwable t) {
				SpagoBIEngineStartupException serviceException;
				String msg = "Impossible to create engine instance for document [" + getDocumentId() + "].";
				Throwable rootException = t;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				msg += "\nThe root cause of the error is: " + str;
				serviceException = new SpagoBIEngineStartupException(ENGINE_NAME, msg, t);
				
				if(rootException instanceof WorksheetEngineException) {
					WorksheetEngineException e = (WorksheetEngineException)rootException;
					serviceException.setDescription( e.getDescription());
					serviceException.setHints( e.getHints() );
				} 
				throw serviceException;
			}
			logger.debug("Engine instance succesfully created");
			
			worksheetEngineInstance.setAnalysisMetadata( getAnalysisMetadata() );
			if( getAnalysisStateRowData() != null ) {
				logger.debug("Loading subobject [" + worksheetEngineInstance.getAnalysisMetadata().getName() + "] ...");
				try {
					analysisState = new WorksheetEngineAnalysisState();
					analysisState.load( getAnalysisStateRowData() );
					worksheetEngineInstance.setAnalysisState( analysisState );
				} catch(Throwable t) {
					SpagoBIEngineStartupException serviceException;
					String msg = "Impossible load subobject [" + worksheetEngineInstance.getAnalysisMetadata().getName() + "].";
					Throwable rootException = t;
					while(rootException.getCause() != null) {
						rootException = rootException.getCause();
					}
					String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
					msg += "\nThe root cause of the error is: " + str;
					serviceException = new SpagoBIEngineStartupException(ENGINE_NAME, msg, t);
					
					throw serviceException;
				}
				logger.debug("Subobject [" + worksheetEngineInstance.getAnalysisMetadata().getName() + "] succesfully loaded");
			}
			
			locale = (Locale)worksheetEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
			
			setAttributeInSession( WorksheetEngineInstance.class.getName() , worksheetEngineInstance );	
			setAttribute(WorksheetEngineInstance.class.getName(), worksheetEngineInstance);
			
			setAttribute(LANGUAGE, locale.getLanguage());
			setAttribute(COUNTRY, locale.getCountry());
			
			if(!this.goToWorksheetPreentation()){
				writeBackToClient(new JSONAcknowledge());
			}
			
		} catch (Throwable e) {
			SpagoBIEngineStartupException serviceException = null;
			
			if(e instanceof SpagoBIEngineStartupException) {
				serviceException = (SpagoBIEngineStartupException)e;
			} else {
				Throwable rootException = e;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				String message = "An unpredicted error occurred while executing " + getEngineName() + " service."
								 + "\nThe root cause of the error is: " + str;
				
				serviceException = new SpagoBIEngineStartupException(getEngineName(), message, e);
			}
			
			throw serviceException;
		} finally {
			logger.debug("OUT");
		}		
	}


	/*
     * This is useful when starting a worksheet document created starting from a non-persisted dataset and using also QBE.
     * In this case the starting dataset must be persisted immediately.
     */
    private void checkPersistence(Map env, SourceBean template) {
    	String documentId = null;
    	try {
    		documentId = this.getDocumentId();
    	} catch (Exception e) {
    		logger.debug("Error while getting document id, may be it is not defined since we are creating a new document", e);
    	}
    	// we check the document id because, if it is not null, the document may have a dataset
    	if (documentId != null) {
    		IDataSet dataset = this.getDataSet();
    		boolean hasInnerQbe = WorksheetTemplateParser.getInstance().hasInnerQbeQuery(template);
    		if (hasInnerQbe && dataset != null) {
    			this.checkPersistence(dataset, env);
    		}
    	}
		
	}

	protected boolean goToWorksheetPreentation() {
		return true;
	}

	protected QbeEngineInstance getQbeEngineInstance(WorksheetEngineInstance worksheetEngineInstance) {
    	QbeEngineInstance qbeEngineInstance = worksheetEngineInstance.getQbeEngineInstance();
		return qbeEngineInstance;
	}

	public void initDataSet(WorksheetEngineInstance worksheetEngineInstance) {
		IDataSet dataset = null;
		QbeEngineInstance qbeEngineInstance = worksheetEngineInstance.getQbeEngineInstance();
		if (qbeEngineInstance != null) {
			// retrieves dataset as the Qbe active query
			logger.debug("Qbe engine instance found. Retrieving Qbe query as dataset...");
			dataset = qbeEngineInstance.getActiveQueryAsDataSet();
		} else {
			// retrieves dataset from document configuration (i.e. the dataset associated to the document)
			logger.debug("Qbe engine instance not found. Retrieving dataset from document configuration...");
			dataset = getDataSet();
		}
		
		// update parameters into the dataset
		logger.debug("Setting parameters into dataset...");
		logger.debug( worksheetEngineInstance.getEnv() );
		// putting all env patameters into dataset's parameters, also the SpagoBIConstants.TEMPORARY_TABLE_NAME
		dataset.setParamsMap( worksheetEngineInstance.getEnv() );
		
		// update profile attributes into dataset
		Map<String, Object> userAttributes = new HashMap<String, Object>();
		UserProfile profile = (UserProfile) this.getEnv().get(EngineConstants.ENV_USER_PROFILE);
		userAttributes.putAll(profile.getUserAttributes());
		userAttributes.put(SsoServiceInterface.USER_ID, profile.getUserId().toString());
		logger.debug("Setting user profile attributes into dataset...");
		logger.debug( userAttributes );
		dataset.setUserProfileAttributes(userAttributes);
		
		worksheetEngineInstance.setDataSet(dataset);
    }
    
    public void initDataSource(WorksheetEngineInstance worksheetEngineInstance) {
		IDataSource datasource = null;
		QbeEngineInstance qbeEngineInstance = worksheetEngineInstance.getQbeEngineInstance();
		if (qbeEngineInstance != null) {
			// retrieves datasource as the Qbe datasource
			logger.debug("Qbe engine instance found. Retrieving Qbe datasource...");
			datasource = ((AbstractDataSource) qbeEngineInstance
							.getDataSource()).getToolsDataSource();
		} else {
			// retrieves datasource from document configuration (i.e. the datasource associated to the document)
			logger.debug("Qbe engine instance not found. Retrieving datasource from document configuration...");
			datasource = getDataSource();
		}
		worksheetEngineInstance.setDataSource(datasource);
    }

	public WorksheetEngineInstance getWorksheetEngineInstance() {
		return worksheetEngineInstance;
	}

	public void setWorksheetEngineInstance(
			WorksheetEngineInstance worksheetEngineInstance) {
		this.worksheetEngineInstance = worksheetEngineInstance;
	}
	
	 public String getDocumentId() {
		 String documentId = null;
		 try {
			 documentId = super.getDocumentId();
		} catch (SpagoBIEngineStartupException e) {
			logger.debug("Impossible tol load the document id ",e);
		}

		return documentId;   	
	 }
    
	public Map getEnv() {
		Map env = super.getEnv();
		String schema = null;
		String attrname = null;

		String datasourceLabel = this
				.getAttributeAsString(EngineConstants.ENGINE_DATASOURCE_LABEL);

		if (datasourceLabel != null) {
			IDataSource dataSource = getDataSourceServiceProxy()
					.getDataSourceByLabel(datasourceLabel);
			if (dataSource.checkIsMultiSchema()) {
				logger.debug("Datasource [" + dataSource.getLabel()
						+ "] is defined on multi schema");
				try {
					logger.debug("Retriving target schema for datasource ["
							+ dataSource.getLabel() + "]");
					attrname = dataSource.getSchemaAttribute();
					logger.debug("Datasource's schema attribute name is equals to ["
							+ attrname + "]");
					Assert.assertNotNull(
							attrname,
							"Datasource's schema attribute name cannot be null in order to retrive the target schema");
					schema = (String) getUserProfile().getUserAttribute(
							attrname);
					Assert.assertNotNull(schema,
							"Impossible to retrive the value of attribute ["
									+ attrname + "] form user profile");
					dataSource.setJndi(dataSource.getJndi() + schema);
					logger.debug("Target schema for datasource  ["
							+ dataSource.getLabel() + "] is ["
							+ dataSource.getJndi() + "]");
				} catch (Throwable t) {
					throw new SpagoBIEngineRuntimeException(
							"Impossible to retrive target schema for datasource ["
									+ dataSource.getLabel() + "]", t);
				}
				logger.debug("Target schema for datasource  ["
						+ dataSource.getLabel() + "] retrieved succesfully");
			}
			env.put(EngineConstants.ENGINE_DATASOURCE, dataSource);
		}

		IDataSet dataSetLinkedToDoc = getDataSet();
		if (dataSetLinkedToDoc != null) {
			List<IDataSet> dataSets = new ArrayList<IDataSet>();
			dataSets.add(dataSetLinkedToDoc);
			env.put(EngineConstants.ENV_DATASETS, dataSets);
		}
		return env;
	}
	

        
}
