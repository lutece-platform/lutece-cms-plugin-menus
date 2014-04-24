/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.plugins.menus.service.MenusService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * @author lenaini
 */
public class AccessibilityAnchorMenuInclude implements PageInclude
{
    /////////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String TEMPLATE_MENU_ACCESSIBILITY_ANCHOR = "skin/plugins/menus/accessibility_anchor_page.html";
    private static final String MARK_PAGE_PARAMETER = "page_parameter";
    private static final String MARK_PAGE_PARAMETER_VALUE = "page_parameter_value";
    private static final String MENU_MARKER = "accessibility_anchor_menu";
    private static final String PARAMETER_XPAGE_NAME = "page";
    private static final String PARAMETER_PAGE_ID = "page_id";
    private static final String PARAMETER_PAGE_OPERATOR = "=";

    /**
     * Substitue specific Freemarker markers in the page template.
     * @param rootModel the HashMap containing markers to substitute
     * @param data A PageData object containing applications data
     * @param nMode The current mode
     * @param request The HTTP request
     */
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        if ( request != null )
        {
            Plugin plugin = PluginService.getPlugin( MenusPlugin.PLUGIN_NAME );

            for ( Menus menus : MenusHome.findAll( plugin ) )
            {
                if ( menus.getMenuType(  ).equals( MENU_MARKER ) )
                {
                    String strMarkerMenuXPage = menus.getMenuMarker(  );
                    rootModel.put( strMarkerMenuXPage, getAccessibilityTemplate( nMode, request ) );
                }
            }
        }
    }

    /**
     * Display the accessibility anchor app
     * @param request The HTTP request
     * @return the page
     */
    public String getAccessibilityTemplate( int nMode, HttpServletRequest request )
    {
        HashMap<String, Object> modelList = new HashMap<String, Object>(  );
        Locale locale = ( request == null ) ? null : request.getLocale(  );

        String strXPageParameter = request.getParameter( PARAMETER_XPAGE_NAME );
        String strPageParameter = PARAMETER_XPAGE_NAME + PARAMETER_PAGE_OPERATOR;
        String strPageParameterValue = strXPageParameter;

        if ( strXPageParameter == null )
        {
            strPageParameter = "";
            strPageParameterValue = "";
        }

        int nCurrentPageId;

        try
        {
            nCurrentPageId = ( request.getParameter( Parameters.PAGE_ID ) == null ) ? 0
                                                                                    : Integer.parseInt( request.getParameter( 
                        Parameters.PAGE_ID ) );
        }
        catch ( NumberFormatException nfe )
        {
            AppLogService.info( "MainMenuInclude.fillTemplate() : " + nfe.getLocalizedMessage(  ) );
            nCurrentPageId = 0;
        }

        String strCurrentPageId = Integer.toString( nCurrentPageId );

        if ( ( strCurrentPageId != null ) && ( !strCurrentPageId.equals( "0" ) ) )
        {
            strPageParameter = PARAMETER_PAGE_ID + PARAMETER_PAGE_OPERATOR;
            strPageParameterValue = strCurrentPageId;
        }

        // Insert the rows in the list
        modelList.put( MARK_PAGE_PARAMETER, strPageParameter );
        modelList.put( MARK_PAGE_PARAMETER_VALUE, strPageParameterValue );

        // Define the site path from url, by mode
        modelList.put( MenusService.MARKER_SITE_PATH, MenusService.getInstance(  ).getSitePath( nMode ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MENU_ACCESSIBILITY_ANCHOR, locale,
                modelList );

        return templateList.getHtml(  );
    }
}
