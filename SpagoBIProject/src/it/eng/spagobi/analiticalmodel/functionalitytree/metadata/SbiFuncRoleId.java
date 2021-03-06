/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.functionalitytree.metadata;

import it.eng.spagobi.commons.metadata.SbiDomains;
import it.eng.spagobi.commons.metadata.SbiExtRoles;





/**
 * SbiFuncRoleId generated by hbm2java
 */
public class SbiFuncRoleId  implements java.io.Serializable {

    // Fields    

     private SbiDomains state;
     private SbiExtRoles role;
     private SbiFunctions function;


    // Constructors

    /**
     * default constructor.
     */
    public SbiFuncRoleId() {
    }
    
   
    
    

    // Property accessors

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof SbiFuncRoleId) ) return false;
		 SbiFuncRoleId castOther = ( SbiFuncRoleId ) other; 
         
		 return (this.getState()==castOther.getState()) || ( this.getState()!=null && castOther.getState()!=null && this.getState().equals(castOther.getState()) )
 && (this.getRole()==castOther.getRole()) || ( this.getRole()!=null && castOther.getRole()!=null && this.getRole().equals(castOther.getRole()) )
 && (this.getFunction()==castOther.getFunction()) || ( this.getFunction()!=null && castOther.getFunction()!=null && this.getFunction().equals(castOther.getFunction()) );
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getState().hashCode();
         result = 37 * result + this.getRole().hashCode();
         result = 37 * result + this.getFunction().hashCode();
         return result;
   }   


	/**
	 * Gets the function.
	 * 
	 * @return the function
	 */
	public SbiFunctions getFunction() {
		return function;
	}
	
	/**
	 * Sets the function.
	 * 
	 * @param function the new function
	 */
	public void setFunction(SbiFunctions function) {
		this.function = function;
	}
	
	/**
	 * Gets the role.
	 * 
	 * @return the role
	 */
	public SbiExtRoles getRole() {
		return role;
	}
	
	/**
	 * Sets the role.
	 * 
	 * @param role the new role
	 */
	public void setRole(SbiExtRoles role) {
		this.role = role;
	}
	
	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public SbiDomains getState() {
		return state;
	}
	
	/**
	 * Sets the state.
	 * 
	 * @param state the new state
	 */
	public void setState(SbiDomains state) {
		this.state = state;
	}
}