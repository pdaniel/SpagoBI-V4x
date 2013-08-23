/*
*
* @file LeafListener.java
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
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: LeafListener.java,v 1.3 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.widgets.client.palotable;

import com.tensegrity.palo.gwt.widgets.client.palotable.header.Header;
import com.tensegrity.palo.gwt.widgets.client.palotable.header.HeaderItem;

public interface LeafListener {

	public void reset(Header header);
//	public void visitedLastLeaf(HeaderItem item);
	public void visitedLeaf(HeaderItem item);
//	public void visitedLastLeaf(HeaderItem item);
//	public void visitRowLeaf(HeaderItem item);
//	public void visitColumnLeaf(HeaderItem item);
	
//	public boolean isEmptyRow(int rowIndex);
//	public boolean isEmptyColumn(int colIndex);
}
