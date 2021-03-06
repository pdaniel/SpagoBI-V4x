/*
*
* @file BorderedWidget.java
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
* @version $Id: BorderedWidget.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.widgets.client;

import com.google.gwt.user.client.ui.Widget;
import com.tensegrity.palo.gwt.widgets.client.util.Point;

/**
 * <code>BorderedWidget</code>
 * TODO DOCUMENT ME
 *
 * Should be used if the widget has a bordered style
 * @version $Id: BorderedWidget.java,v 1.2 2009/12/17 16:14:15 PhilippBouillon Exp $
 **/
public abstract class BorderedWidget extends Widget {

	private int BORDER_PIXEL_WIDTH = 0;
	private int BORDER_PIXEL_HEIGHT = 0;
	
	protected void onLoad() {
		super.onLoad();
		determineBorderPixel();
	}

	public int getOffsetHeight() {
		return super.getOffsetHeight() - BORDER_PIXEL_HEIGHT;
	}

	public int getOffsetWidth() {
		return super.getOffsetWidth() - BORDER_PIXEL_WIDTH;
	}

	public Point getOffsets() {
		return new Point(getOffsetWidth(), getOffsetHeight());
	}
	
	protected void determineBorderPixel() {
		setWidth("10px");
		setHeight("10px");
		BORDER_PIXEL_WIDTH = super.getOffsetWidth() - 10;
		BORDER_PIXEL_HEIGHT = super.getOffsetHeight() - 10;
	}
	
}
