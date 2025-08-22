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
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lenaini
 */
public class MenusAdminJspBean extends PluginAdminPageJspBean
{
    // Right
    public static final String RIGHT_MANAGE_MENUS = "MENUS_MANAGEMENT";

    // properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MENUS_LIST = "menus.manage_menus.pageTitle";

    // Markers
    private static final String MARK_MENU_LIST = "menus_list";

    // templates
    private static final String TEMPLATE_MANAGE_MENUS = "/admin/plugins/menus/manage_menus.html";

    /**
     * 
     */
    private static final long serialVersionUID = -4105692805533343351L;

    /**
     * returns the template of the MenusLists management
     * 
     * @param request
     *            The HttpRequest
     * @return template of lists management
     */
    public String getManageMenus( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MENUS_LIST );

        Map<String, Object> model = new HashMap<String, Object>( );

        Collection<Menus> listMenusList = MenusHome.findAll( getPlugin( ) );
        model.put( MARK_MENU_LIST, listMenusList );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_MENUS, getLocale( ), model );

        return getAdminPage( templateList.getHtml( ) );
    }

//    /**
//     * Returns the list of the code_theme of the page
//     *
//     * @return the list of the page Code_theme in form of ReferenceList
//     */
//    public static ArrayList getMenusList( Menus menus )
//    {
//        // recovers themes list from the includes.list entry in the properties download file
//        String strMenusList = AppPropertiesService.getProperty( PROPERTY_MENUS_LIST );
//   
//        StringTokenizer strTokens = new StringTokenizer( strMenusList, "," );
//        ArrayList<Menus> list = new ArrayList<Menus>(  );
//   
//        while ( strTokens.hasMoreElements(  ) )
//        {
//            String strMenuName = strTokens.nextToken(  ).trim(  );
//            String strName = AppPropertiesService.getProperty( PROPERTY_PREFIX + strMenuName + PROPERTY_SUFFIX_NAME );
//            menus.setName( strName  );
//            String strTypeMenu = AppPropertiesService.getProperty( PROPERTY_PREFIX + strMenuName + PROPERTY_SUFFIX_TYPE_MENU ) ;
//            menus.setTypeMenu ( strTypeMenu );
//            menus.setIdRoot(Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_PREFIX + strMenuName + PROPERTY_SUFFIX_ID_ROOT ) ) );
//            menus.setMarker(  AppPropertiesService.getProperty( PROPERTY_PREFIX + strMenuName + PROPERTY_SUFFIX_MARKER ) );
//            list.add( menus );
//        }
//   
//        return list;
//    }
}
