/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.kpi.service;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.kpi.config.metadata.SbiKpiComments;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManageCommentsAction extends AbstractSpagoBIAction {
	
	private static Logger logger = Logger.getLogger(ManageCommentsAction.class);
	private final String MESSAGE_DET = "MESSAGE_DET";
	// type of service
	private final String COMMENTS_LIST = "COMMENTS_LIST";
	private final String SAVE_COMMENT= "SAVE_COMMENT";
	private final String KPI_INST_ID = "kpiInstId";
	private final String COMMENT = "comment";
	@Override
	public void doService() {
		// TODO Auto-generated method stub
		logger.debug("IN");
		Locale locale = getLocale();

		String serviceType = this.getAttributeAsString(MESSAGE_DET);
		logger.debug("Service type "+serviceType);
		if (serviceType != null && serviceType.equalsIgnoreCase(COMMENTS_LIST)) {
			try {	
				Integer kpiInstId = this.getAttributeAsInteger(KPI_INST_ID);
				List<SbiKpiComments> comments = DAOFactory.getKpiInstanceDAO().loadCommentsByKpiInstanceId(kpiInstId);
				logger.debug("Loaded comments list");
				JSONArray commentsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(comments, locale);
				JSONObject commentsRootJSON = new JSONObject();
				commentsRootJSON.put("comments", commentsJSON);
				writeBackToClient(new JSONSuccess(commentsRootJSON));

			} catch (Throwable e) {
				logger.error("Exception occurred while retrieving comments", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while retrieving comments", e);                                                                                                                                                                                                                                                        
			}
		}else if(serviceType != null && serviceType.equalsIgnoreCase(SAVE_COMMENT)){
			try {	
				Integer kpiInstId = this.getAttributeAsInteger(KPI_INST_ID);
				String htmlComment = this.getAttributeAsString(COMMENT);

				IEngUserProfile profile = getUserProfile();
				String owner = ((UserProfile)profile).getUserId().toString();
				Integer idComment = DAOFactory.getKpiInstanceDAO().saveKpiComment(kpiInstId, htmlComment, owner);
				logger.debug("Saved comment");

				writeBackToClient(new JSONSuccess(idComment+""));

			} catch (Throwable e) {
				logger.error("Exception occurred while saving comment", e);
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Exception occurred while saving comment", e);                                                                                                                                                                                                                                                        
			}
		}else if(serviceType == null){
			logger.debug("No service");
		}
		logger.debug("OUT");
	}

}
