package it.eng.spagobi.signup.service.rest;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiCommonInfo;
import it.eng.spagobi.commons.metadata.SbiExtRoles;
import it.eng.spagobi.commons.utilities.ChannelUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.profiling.bean.SbiUser;
import it.eng.spagobi.profiling.bean.SbiUserAttributes;
import it.eng.spagobi.profiling.bean.SbiUserAttributesId;
import it.eng.spagobi.profiling.dao.ISbiAttributeDAO;
import it.eng.spagobi.profiling.dao.ISbiUserDAO;
import it.eng.spagobi.rest.annotations.ToValidate;
import it.eng.spagobi.rest.publishers.PublisherService;
import it.eng.spagobi.security.Password;
import it.eng.spagobi.tools.dataset.validation.FieldsValidatorFactory;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;

import java.io.IOException;
import java.net.URL;
import java.security.Security;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import nl.captcha.Captcha;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/signup")
public class Signup {

	
	@Context
	private HttpServletResponse servletResponse;

	private static Logger logger = Logger.getLogger(PublisherService.class);

	@GET
	@Path("/prepareUpdate")
	public void prepareUpdate(@Context HttpServletRequest req) {
		
	  try{
		  UserProfile profile = (UserProfile)req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		  ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
		  SbiUser user = userDao.loadSbiUserByUserId((String)profile.getUserId());
		  int i = user.getFullName().indexOf(" ");
		  Map<String,String> data = profile.getUserAttributes(); 
		  data.put("name", user.getFullName().substring(0, i));
		  data.put("surname", user.getFullName().substring(i+1));
		  req.setAttribute("data", data );
		  
	  }catch (Throwable t) {
	    throw new SpagoBIServiceException(
					"An unexpected error occured while executing the subscribe action", t);
	  }
	  try {
		    req.getRequestDispatcher("/WEB-INF/jsp/signup/modify.jsp").forward(req, servletResponse);
		  } catch (ServletException e) {
				logger.error("Error dispatching request");
		  } catch (IOException e) {
				logger.error("Error writing content");
		  }
    }
	@GET
	@Path("/delete")
    public void delete(@Context HttpServletRequest req) {
		
	  try{	
		UserProfile profile = (UserProfile)req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
	    SbiUser user = userDao.loadSbiUserByUserId((String)profile.getUserId());
	    
	    userDao.deleteSbiUserById( user.getId() );
	    
	    String host = req.getHeader("Host");
		int index = host.indexOf(":");
		URL url = new URL(req.getScheme(), host.substring(0, index), Integer.parseInt(host.substring(index+1)), 
				req.getContextPath() + "/servlet/AdapterHTTP?ACTION_NAME=LOGOUT_ACTION&LIGHT_NAVIGATOR_DISABLED=TRUE" );
		
		servletResponse.sendRedirect(url.toString());
	  
	  }
	  catch (Throwable t) {
	    throw new SpagoBIServiceException(
					"An unexpected error occured while executing the subscribe action", t);
	  }
	  
		
	}
	private void updAttribute(ISbiUserDAO userDao, ISbiAttributeDAO dao, String attributeValue, String userId, int id, int attributeId ) throws EMFUserError{
		
	  if( attributeValue != null ){	
        SbiUserAttributes	userAttribute = dao.loadSbiAttributesByUserAndId(id, attributeId);
	    if( userAttribute != null ){
          userAttribute.getCommonInfo().setTimeUp(new Date(System.currentTimeMillis()));
          userAttribute.getCommonInfo().setUserUp(userId);
          userAttribute.getCommonInfo().setSbiVersionUp(SbiCommonInfo.SBI_VERSION);
	    }
	    else{
		  userAttribute = new SbiUserAttributes();
		  userAttribute.getCommonInfo().setOrganization("SPAGOBI");
		  userAttribute.getCommonInfo().setTimeIn(new Date(System.currentTimeMillis()));
		  userAttribute.getCommonInfo().setUserIn(userId);
		  userAttribute.getCommonInfo().setSbiVersionIn(SbiCommonInfo.SBI_VERSION);
	    
	      SbiUserAttributesId pk = new SbiUserAttributesId();
	      pk.setAttributeId(attributeId);
	      pk.setId(id);
	      userAttribute.setId(pk);
	    }
	    userAttribute.setAttributeValue(attributeValue);
	    userDao.updateSbiUserAttributes(userAttribute);
	  }else{
	     try{ userDao.deleteSbiUserAttributeById(id, attributeId); }catch(EMFUserError err){}  
	  }
    }
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@ToValidate(typeName=FieldsValidatorFactory.SIGNUP)
	public String update(@Context HttpServletRequest req) {
		
		String nome     =  GeneralUtilities.trim(req.getParameter("nome"));
		String cognome  =  GeneralUtilities.trim(req.getParameter("cognome"));
		String password =  GeneralUtilities.trim(req.getParameter("password"));
		String email    =  GeneralUtilities.trim(req.getParameter("email"));
		String dataNascita    
		                =  GeneralUtilities.trim(req.getParameter("dataNascita"));
		String indirizzo=  GeneralUtilities.trim(req.getParameter("indirizzo"));
		String azienda  =  GeneralUtilities.trim(req.getParameter("azienda"));
		String biografia=  GeneralUtilities.trim(req.getParameter("biografia"));
		String lingua   =  GeneralUtilities.trim(req.getParameter("lingua"));
		
		
		try {
		  
		  UserProfile profile = (UserProfile)req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		  ISbiUserDAO userDao      = DAOFactory.getSbiUserDAO();
		  ISbiAttributeDAO attrDao = DAOFactory.getSbiAttributeDAO();
		  
		  SbiUser user = userDao.loadSbiUserByUserId((String)profile.getUserId());
		  int userId = user.getId();
		  
		  user.setFullName(nome + " " + cognome);
		  if( password != null ) user.setPassword( Password.encriptPassword( password ));
		  userDao.updateSbiUser( user, userId );
		  
		  updAttribute( userDao, attrDao, email, user.getUserId(), userId, attrDao.loadSbiAttributeByName("email").getAttributeId() );
		  updAttribute( userDao, attrDao, dataNascita, user.getUserId(), userId, attrDao.loadSbiAttributeByName("birth_date").getAttributeId() );
		  updAttribute( userDao, attrDao, indirizzo, user.getUserId(), userId, attrDao.loadSbiAttributeByName("location").getAttributeId() );
		  updAttribute( userDao, attrDao, azienda, user.getUserId(), userId, attrDao.loadSbiAttributeByName("company").getAttributeId() );
		  updAttribute( userDao, attrDao, biografia, user.getUserId(), userId, attrDao.loadSbiAttributeByName("short_bio").getAttributeId() );
		  updAttribute( userDao, attrDao, lingua, user.getUserId(), userId, attrDao.loadSbiAttributeByName("language").getAttributeId() );
		  
		  profile.setAttributeValue("name",      nome);
		  profile.setAttributeValue("surname",   cognome);
		  profile.setAttributeValue("language",  lingua); 
		  profile.setAttributeValue("short_bio", biografia);
		  profile.setAttributeValue("company",   azienda);
		  profile.setAttributeValue("location",  indirizzo);
		  profile.setAttributeValue("birth_date",dataNascita);
		  profile.setAttributeValue("email",     email);
		  
		} catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while executing the subscribe action", t);
		}
        return new JSONObject().toString();
	}
	
	@GET
	@Path("/prepareActive")
	public void prepareActive(@Context HttpServletRequest req) {
		
	  try {
		    req.getRequestDispatcher("/WEB-INF/jsp/signup/active.jsp").forward(req, servletResponse);
		  } catch (ServletException e) {
				logger.error("Error dispatching request");
		  } catch (IOException e) {
				logger.error("Error writing content");
		  }
    }
    
	@POST
	@Path("/active")
	public String active(@Context HttpServletRequest req) {
		
	  String id = req.getParameter("accountId");
	  String expired_time = SingletonConfig.getInstance().getConfigValue("MAIL.SIGNUP.expired_time");
	  
	  try {
		  ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
		  SbiUser user = null;
		  try{
		    user = userDao.loadSbiUserById( Integer.parseInt( id ));
		  }catch(EMFUserError emferr){}
		  if( user == null ) 
		    return new JSONObject("{message: 'Unknow user'}").toString();
		  
		  if( !user.getFlgPwdBlocked() )
		    return new JSONObject("{message: 'User activated'}").toString();
			  
		  long now = System.currentTimeMillis();
		  if( now > user.getCommonInfo().getTimeIn().getTime() + Long.parseLong(expired_time) * 24 * 60 * 60 * 1000 )
		    return new JSONObject("{message: 'User activation expired time'}").toString();
		  
		  user.setFlgPwdBlocked(false);
		  userDao.updateSbiUser(user, null );
		  
		  return new JSONObject("{message: 'User activated'}").toString();
	  } catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while executing the subscribe action", t);
	  }
	}
	
	@POST
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON)
	@ToValidate(typeName=FieldsValidatorFactory.SIGNUP)
	public String create(@Context HttpServletRequest req) {
		
		String nome     =  GeneralUtilities.trim(req.getParameter("nome"));
		String cognome  =  GeneralUtilities.trim(req.getParameter("cognome"));
		String username =  GeneralUtilities.trim(req.getParameter("username"));
		String password =  GeneralUtilities.trim(req.getParameter("password"));
		String email    =  GeneralUtilities.trim(req.getParameter("email"));
		String sesso    =  GeneralUtilities.trim(req.getParameter("sesso"));
		String dataNascita    
		                =  GeneralUtilities.trim(req.getParameter("dataNascita"));
		String indirizzo=  GeneralUtilities.trim(req.getParameter("indirizzo"));
		String azienda  =  GeneralUtilities.trim(req.getParameter("azienda"));
		String biografia=  GeneralUtilities.trim(req.getParameter("biografia"));
		String lingua   =  GeneralUtilities.trim(req.getParameter("lingua"));
		String captcha  =  GeneralUtilities.trim(req.getParameter("captcha"));
		
		try {
		  Captcha c = (Captcha) req.getSession().getAttribute(Captcha.NAME);
		  if( !c.isCorrect(captcha) ){
		    JSONObject errorMsg = new JSONObject();
			JSONArray errors = new JSONArray();
			errors.put(new JSONObject("{message: 'Campo Captcha non verificato'}"));
			errorMsg.put("errors", errors);
			errorMsg.put("message", "validation-error");
			return errorMsg.toString(); 	  
				  
		  }	
		  
		  ISbiUserDAO userDao = DAOFactory.getSbiUserDAO();
		  if( userDao.isUserIdAlreadyInUse( username ) != null ){
		    JSONObject errorMsg = new JSONObject();
		    JSONArray errors = new JSONArray();
		    errors.put(new JSONObject("{message: 'Username in uso'}"));
		    errorMsg.put("errors", errors);
			errorMsg.put("message", "validation-error");
			return errorMsg.toString(); 
		  }
		  
		  SbiUser user = new SbiUser();
		  user.setUserId(username);
		  user.setPassword( Password.encriptPassword( password ));
		  user.setFullName( nome + " " + cognome );
		  user.getCommonInfo().setOrganization("SPAGOBI");
		  user.setFlgPwdBlocked(true);
		  
		  Set<SbiExtRoles> roles = new HashSet<SbiExtRoles>();
		  SbiExtRoles r = new SbiExtRoles();
		  r.setExtRoleId(3);
		  r.getCommonInfo().setOrganization("SPAGOBI");
		  roles.add(r);
		  user.setSbiExtUserRoleses(roles);
		  
		  Set<SbiUserAttributes> attributes = new HashSet<SbiUserAttributes>();
		  
		  ISbiAttributeDAO attrDao = DAOFactory.getSbiAttributeDAO();
		  
		  addAttribute(attributes, attrDao.loadSbiAttributeByName("email").getAttributeId(),  email);
		  addAttribute(attributes, attrDao.loadSbiAttributeByName("gender").getAttributeId(),  sesso);
		  addAttribute(attributes, attrDao.loadSbiAttributeByName("birth_date").getAttributeId(),  dataNascita);
		  addAttribute(attributes, attrDao.loadSbiAttributeByName("location").getAttributeId(),  indirizzo);
		  addAttribute(attributes, attrDao.loadSbiAttributeByName("company").getAttributeId(),  azienda);
		  addAttribute(attributes, attrDao.loadSbiAttributeByName("short_bio").getAttributeId(), biografia);
		  addAttribute(attributes, attrDao.loadSbiAttributeByName("language").getAttributeId(), lingua);
		 
		  user.setSbiUserAttributeses(attributes);
		  int id = userDao.fullSaveOrUpdateSbiUser(user);
		  
		  String subject = SingletonConfig.getInstance().getConfigValue("MAIL.SIGNUP.subject");
	      String body    = SingletonConfig.getInstance().getConfigValue("MAIL.SIGNUP.body");
		  String host = req.getHeader("Host");
		  int index = host.indexOf(":");
			  
		  URL url = new URL(req.getScheme(), host.substring(0, index), Integer.parseInt(host.substring(index+1)), req.getContextPath() + "/restful-services/signup/prepareActive?accountId=" + id );
			  
	      sendMail(email, subject, body + " \r\n \r\n " + url.toExternalForm() );
		  
		  
		} catch (Throwable t) {
			throw new SpagoBIServiceException(
					"An unexpected error occured while executing the subscribe action", t);
		}
        return new JSONObject().toString();
	}
	
	private void addAttribute( Set<SbiUserAttributes> attributes, int attrId, String attrValue ){
		
	  if( attrValue != null ){	
	    SbiUserAttributes a = new SbiUserAttributes();
	    a.getCommonInfo().setOrganization("SPAGOBI");
	    SbiUserAttributesId id = new SbiUserAttributesId();
	    id.setAttributeId(attrId);
	    a.setId(id);
	    a.setAttributeValue(attrValue);
	    attributes.add(a);
	  }  
	}
    @POST
	@Path("/prepare")
	public void prepare(@Context HttpServletRequest req) {
		
		
	  try {
	    req.getRequestDispatcher("/WEB-INF/jsp/signup/signup.jsp").forward(req, servletResponse);
	  } catch (ServletException e) {
			logger.error("Error dispatching request");
	  } catch (IOException e) {
			logger.error("Error writing content");
	  }
	}
    
    private void sendMail(String emailAddress, String subject, String emailContent) throws Exception{
		
	    final String DEFAULT_SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	    final String CUSTOM_SSL_FACTORY = "it.eng.spagobi.commons.services.DummySSLSocketFactory";
	    
		String smtphost = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.smtphost");
	    String smtpport = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.smtpport");
	    String smtpssl  = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.useSSL"); 
	    logger.debug(smtphost+" "+smtpport+" use SSL: "+smtpssl);
	    
	    //Custom Trusted Store Certificate Options
	    String trustedStorePath = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.trustedStore.file"); 
	    String trustedStorePassword = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.trustedStore.password"); 
	    
	    int smptPort=25;
	    
		if( (smtphost==null) || smtphost.trim().equals(""))
			throw new Exception("Smtp host not configured");
		if( (smtpport==null) || smtpport.trim().equals("")){
			throw new Exception("Smtp host not configured");
		}else{
			smptPort=Integer.parseInt(smtpport);
		}
		
		String from = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.from");
		if( (from==null) || from.trim().equals(""))
			from = "spagobi@eng.it";
		String user = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.user");
		if( (user==null) || user.trim().equals("")){
			logger.debug("Smtp user not configured");	
			user=null;
		}
		String pass = SingletonConfig.getInstance().getConfigValue("MAIL.PROFILES.user.password");
		if( (pass==null) || pass.trim().equals("")){
		logger.debug("Smtp password not configured");	
		}
		
		//Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", smtphost);
		props.put("mail.smtp.port", Integer.toString(smptPort));
		//Set timeout limit for mail server to respond
		props.put("mail.smtp.timeout", "5000");             
        props.put("mail.smtp.connectiontimeout", "5000"); 
		
		// open session
		Session session=null;
		// create autheticator object
		Authenticator auth = null;
		if (user!=null) {
			auth = new SMTPAuthenticator(user, pass);
			props.put("mail.smtp.auth", "true");
	 	    //SSL Connection
	    	if (smtpssl.equals("true")){
	            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());	            
			    props.put("mail.smtps.auth", "true");
		        props.put("mail.smtps.socketFactory.port", Integer.toString(smptPort));
	            if ((!StringUtilities.isEmpty(trustedStorePath)) ) {            	
					/* Dynamic configuration of trustedstore for CA
					 * Using Custom SSLSocketFactory to inject certificates directly from specified files
					 */
	            	
			        props.put("mail.smtps.socketFactory.class", CUSTOM_SSL_FACTORY);

	            } else {
	            
			        props.put("mail.smtps.socketFactory.class", DEFAULT_SSL_FACTORY);
	            }
		        props.put("mail.smtp.socketFactory.fallback", "false"); 
	    	}
			
			session = Session.getInstance(props, auth);
			logger.info("Session.getInstance(props, auth)");
			
		}else{
			session = Session.getInstance(props);
			logger.info("Session.getInstance(props)");
		}
		
		// create a message
		Message msg = new MimeMessage(session);
		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);
		InternetAddress addressTo = new InternetAddress(emailAddress);

		msg.setRecipient(Message.RecipientType.TO, addressTo);
		
		// Setting the Subject and Content Type
		msg.setSubject(subject);
		// create and fill the first message part
		MimeBodyPart mbp1 = new MimeBodyPart();
		mbp1.setText(emailContent);
		// create the Multipart and add its parts to it
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);
		// add the Multipart to the message
		msg.setContent(mp);
		// send message
    	if ((smtpssl.equals("true")) && (!StringUtilities.isEmpty(user)) &&  (!StringUtilities.isEmpty(pass))){
    		//USE SSL Transport comunication with SMTPS
	    	Transport transport = session.getTransport("smtps");
	    	transport.connect(smtphost,smptPort,user,pass);
	    	transport.sendMessage(msg, msg.getAllRecipients());
	    	transport.close(); 
    	}
    	else {
    		//Use normal SMTP
	    	Transport.send(msg);
    	}
		
		
	}
    private class SMTPAuthenticator extends javax.mail.Authenticator {
	  private String username = "";
	  private String password = "";

	  public PasswordAuthentication getPasswordAuthentication()
	  {
		return new PasswordAuthentication(username, password);
	  }

	  public SMTPAuthenticator(String user, String pass) {
		this.username = user;
		this.password = pass;
	  }
   }

}
