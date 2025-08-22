/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.menus.web;

import fr.paris.lutece.plugins.menus.business.Menus;
import fr.paris.lutece.plugins.menus.business.MenusHome;
import fr.paris.lutece.plugins.menus.service.MenusPlugin;
import fr.paris.lutece.portal.business.XmlContent;
import fr.paris.lutece.portal.business.page.Page;
import fr.paris.lutece.portal.business.page.PageHome;
import fr.paris.lutece.portal.business.portalcomponent.PortalComponentHome;
import fr.paris.lutece.portal.business.style.ModeHome;
import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.PortalMenuService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.xml.XmlUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the list of the page associated by the main menu of the site
 */
public class TreeMenuPageInclude implements PageInclude
{
    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final int PORTAL_COMPONENT_MENU_TREE = 7;
    private static final String MENU_MARKER = "page_tree_menu";

    /**
     * Substitue specific Freemarker markers in the page template.
     * 
     * @param rootModel
     *            the HashMap containing markers to substitute
     * @param data
     *            A PageData object containing applications data
     * @param nMode
     *            The current mode
     * @param request
     *            The HTTP request
     */
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        if ( request != null )
        {
            int nCurrentPageId;

            try
            {
                nCurrentPageId = ( request.getParameter( Parameters.PAGE_ID ) == null ) ? 0 : Integer.parseInt( request.getParameter( Parameters.PAGE_ID ) );
            }
            catch( NumberFormatException nfe )
            {
                AppLogService.info( "MainMenuInclude.fillTemplate() : " + nfe.getLocalizedMessage( ) );
                nCurrentPageId = 0;
            }

            /*
             * int nCurrentPageId = ( request.getParameter( Parameters.PAGE_ID ) == null ) ? 0 : Integer.parseInt( request.getParameter( Parameters.PAGE_ID ) );
             */
            Plugin plugin = PluginService.getPlugin( MenusPlugin.PLUGIN_NAME );

            for ( Menus menus : MenusHome.findAll( plugin ) )
            {
                if ( menus.getMenuType( ).startsWith( MENU_MARKER ) )
                {
                    String strTreeMenuPage = ( ( getTreeMenuPage( nCurrentPageId, nMode, menus, request ) == null ) ? "" : getTreeMenuPage( nCurrentPageId,
                            nMode, menus, request ) );
                    String strMarkerTreeMenuPage = menus.getMenuMarker( );
                    rootModel.put( strMarkerTreeMenuPage, strTreeMenuPage );
                }
            }
        }
    }

    /**
     * Builds the tree menu bar
     *
     * @param nIdPage
     *            The page id
     * @param nMode
     *            the mode id
     * @param menus
     *            the current menu
     * @param request
     *            The HttpServletRequest
     * @return The list of the tree menus layed out with the stylesheet of the mode
     */
    public String getTreeMenuPage( int nIdPage, int nMode, Menus menus, HttpServletRequest request )
    {
        StringBuffer strXml = new StringBuffer( );

        String strCurrentPageId = Integer.toString( nIdPage );

        Collection<Page> listPagesMenu;

        listPagesMenu = PageHome.getChildPagesMinimalData( menus.getIdPageRoot( ) );
        strXml.append( XmlUtil.getXmlHeader( ) );
        XmlUtil.beginElement( strXml, XmlContent.TAG_MENU_LIST );

        int nMenuIndex = 1;

        for ( Page menuPage : listPagesMenu )
        {
            if ( ( menuPage.isVisible( request ) ) || ( nMode == PortalMenuService.MODE_ADMIN ) )
            {
                XmlUtil.beginElement( strXml, XmlContent.TAG_MENU );
                XmlUtil.addElement( strXml, XmlContent.TAG_MENU_INDEX, nMenuIndex );
                XmlUtil.addElement( strXml, XmlContent.TAG_PAGE_ID, menuPage.getId( ) );
                XmlUtil.addElementHtml( strXml, XmlContent.TAG_PAGE_NAME, menuPage.getName( ) );
                XmlUtil.addElementHtml( strXml, XmlContent.TAG_PAGE_DESCRIPTION, menuPage.getDescription( ) );
                XmlUtil.addElementHtml( strXml, XmlContent.TAG_CURRENT_PAGE_ID, strCurrentPageId );

                // Seek of the sub-menus
                XmlUtil.beginElement( strXml, XmlContent.TAG_SUBLEVEL_MENU_LIST );

                Collection<Page> listSubLevelMenuPages = PageHome.getChildPagesMinimalData( menuPage.getId( ) );
                int nSubLevelMenuIndex = 1;

                for ( Page subLevelMenuPage : listSubLevelMenuPages )
                {
                    if ( ( subLevelMenuPage.isVisible( request ) ) || ( nMode == PortalMenuService.MODE_ADMIN ) )
                    {
                        XmlUtil.beginElement( strXml, XmlContent.TAG_SUBLEVEL_MENU );
                        XmlUtil.addElement( strXml, XmlContent.TAG_MENU_INDEX, nMenuIndex );
                        XmlUtil.addElement( strXml, XmlContent.TAG_SUBLEVEL_INDEX, nSubLevelMenuIndex );
                        XmlUtil.addElement( strXml, XmlContent.TAG_PAGE_ID, subLevelMenuPage.getId( ) );
                        XmlUtil.addElementHtml( strXml, XmlContent.TAG_PAGE_NAME, subLevelMenuPage.getName( ) );
                        XmlUtil.addElementHtml( strXml, XmlContent.TAG_PAGE_DESCRIPTION, subLevelMenuPage.getDescription( ) );
                        XmlUtil.addElementHtml( strXml, XmlContent.TAG_CURRENT_PAGE_ID, strCurrentPageId );
                        XmlUtil.endElement( strXml, XmlContent.TAG_SUBLEVEL_MENU );
                    }
                }

                XmlUtil.endElement( strXml, XmlContent.TAG_SUBLEVEL_MENU_LIST );
                XmlUtil.endElement( strXml, XmlContent.TAG_MENU );
                nMenuIndex++;
            }
        }

        XmlUtil.endElement( strXml, XmlContent.TAG_MENU_LIST );

        StyleSheet xslSource;

        // Selection of the XSL stylesheet
        switch( nMode )
        {
            case PortalMenuService.MODE_NORMAL:
            case PortalMenuService.MODE_ADMIN:
                xslSource = PortalComponentHome.getXsl( PORTAL_COMPONENT_MENU_TREE, PortalMenuService.MODE_NORMAL );

                break;

            default:
                xslSource = PortalComponentHome.getXsl( PORTAL_COMPONENT_MENU_TREE, nMode );

                break;
        }

        Properties outputProperties = ModeHome.getOuputXslProperties( nMode );

        Map<String, String> mapParamRequest = new HashMap<String, String>( );
        PortalService.setXslPortalPath( mapParamRequest, nMode );

        XmlTransformerService xmlTransformerService = new XmlTransformerService( );

        return xmlTransformerService.transformBySourceWithXslCache( strXml.toString( ), xslSource, mapParamRequest, outputProperties );
    }
}
