/*
*
* @file DefaultTableFormat.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author ArndHouben
*
* @version $Id: DefaultTableFormat.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2007
 * All rights reserved
 */
package org.palo.api.ext.ui.table.impl;

import org.palo.api.ext.ui.Format;
import org.palo.api.ext.ui.table.TableFormat;

/**
 * <code>DefaultTableFormat</code>
 * A default implementation of the <code>{@link TableFormat}</code> interface.
 *
 * @author ArndHouben
 * @version $Id: DefaultTableFormat.java,v 1.2 2009/04/29 10:21:57 PhilippBouillon Exp $
 **/
class DefaultTableFormat implements TableFormat {

	private Format cellFormat;
	private Format headerFormat;
	
	DefaultTableFormat() {		
	}

	
	final void setCellFormat(Format cellFormat) {
		this.cellFormat = cellFormat;
	}


	final void setHeaderFormat(Format headerFormat) {
		this.headerFormat = headerFormat;
	}


//	final void setPriority(int priority) {
//		this.priority = priority;
//	}
//

	public Format getCellFormat() {
		return cellFormat;
	}

	public Format getHeaderFormat() {
		return headerFormat;
	}

//	public int getPriority() {
//		return priority;
//	}
}
