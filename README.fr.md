![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=cms-plugin-menus-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-menus&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-menus)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-menus&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-menus)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-menus&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-menus)

# Plugin menus

## Introduction

Ce plugin permet de créer et d'afficher des menus insérables dans le front de vos sites lutèces. Il a pour objectif de faciliter la création et la manipulation des menus au sein des sites lutèce.

Pour rappel, un site lutèce se compose de
 
* **pages**, dont la gestion (création, modification, suppression) se fait dans le back office, dans la section Administration du site. Ces pages correspondent au contenu éditable du site et sont organisées les unes par rapport aux autres, sous forme d'une arboresence à plusieurs niveaux de profondeur. La page home correspond à la racine. Les pages filles (profondeur 1) peuvent avoir également des pages filles (profondeur 2) et ainsi de suite.
* **xpages**, qui correspond à des pages associées à des fonctionnalités de plugins. Lors de l'ajout d'un plugin, (par exemple le plugin forms) au pom du site, les pages destinées aux utilisateurs du site (ex : les formulaires de forms), rattachées au plugin, sont appelées des xpages. Pour apparaitre dans le site, il faut activer le plugin associé. Les xpages, sont indépendantes de l'arboresence principale du site.


Le plugin menus permet de gérer deux types de menus :
 
* Les menus non personnalisables (NCM)
* Les menus personnalisables (Custom Menu CM)

I. Les menus non personnalisables (NCM)
Les menus non personnalisables sont des menus permettants d'afficher seulement les pages du site. Ils sont non modifiables, et se découpent en deux types :
 
* un **menu principal** pour accéder aux **pages d'un niveau spécifique**. Le premier niveau de menu est affiché par défaut comme menu principal.
* un **menu complet** sous forme d'**arbre** permettant d'accéder à **l'ensemble des pages du site** quelque soit le niveau.


Ces différents menus peuvent être affichés dans les pages d'un site grâce à des markers spécifiques à insérer dans le template de page du site `page_frameset.html` 

Les différents markers sont les suivants :
 
*  `${page_tree_menu_main}` : permet d'afficher sous forme de menu les pages d'un niveau spécifique. Par défaut, le premier niveau est affiché
*  `${page_tree_menu_tree}` : permet d'afficher sous forme de menu les pages filles de la page courante affichée
*  `${page_tree_menu_tree_all_pages}` : permet d'afficher le menu sous forme d'arbre de l'ensemble des pages du site

II. Les menus personnalisés (CM)
Les menus personnalisables sont éditables (création, modification, suppression) à partir du back office du site. Ils peuvent contenir des liens vers des pages, xpages, url externes et sous menus (à 1 ou 2 de profondeur).

Ces différents menus peuvent être affichés dans les pages d'un site grâce à des markers spécifiques à créer dans le back office et à insérer dans le template de page du site `page_frameset.html` 

Il existe trois types de menu personnalisable :
 
* **Menu personnalisé page d'accueil** : Ce type de menu peut être affiché automatiquement dans la barre de navigation Lutèce par défaut de la page home. La profondeur maximale du menu d'accueil est de 1. Il peut également être affiché par son marker (appelé bookmark dans le back office).
* **Menu personnalisé pages internes** : Ce type de menu peut être affiché automatiquement dans la barre de navigation Lutèce par défaut de toutes les pages internes. La profondeur maximale du menu interne est de 1. Il peut également être affiché avec son marker. Si aucun menu interne n'existe, le menu de la page d'accueil est utilisé par défaut pour toutes les pages.
* **Menu personnalisé sideBar** : Ce type de menu peut être affiché automatiquement dans la sideBar gauche par défaut des sites Lutèces, présente dans toutes les pages du site. La profondeur maximale du menu sideBar est de 1. Il peut également être affiché avec son marker.
* **Menu personnalisé** : Ce type de menu peut être affiché partout sur le site, en appelant son marker dans le template html page_frameset. Par exemple : Le menu 'subMenu1' avec le marker subMenuExample peut être affiché dans page_frameset avec le marqueur `${subMenu1}` . Le choix du marker se fait dans le backoffice, lors de la création ou de la modification du menu. Le sous-menu a une profondeur maximale de 2.


Attention, lorsque vous modifiez le page_frameset, vous devez réinitialiser le cache du service de page.
III. Fonction Rest
Ce plugin fournit un point d'accès JSON permettant d'obtenir le menu complet du site sous forme d'arborescence. La réponse contient, pour chaque élément de menu, les informations de la page, notamment son URL.

## Configuration

L'affichage des menus non personnalisables est configurable à partir de paramètres présents dans le fichier de properties du plugin

Les principaux paramètres sont les suivants :
 
*  `menus.mainTreeMenu.depth.main` : définit le niveau des pages à afficher via le marker `${page_tree_menu_main}` 
*  `menus.mainTreeMenu.depth.tree` : définit le niveau de la page courante pour l'affichage du menu des pages filles via le marker `${page_tree_menu_tree}` 
*  `menus.mainTreeMenu.depth.tree.allpages` : définit le nombre de niveau à afficher lors de l'affichage du menu complet via le marker `${page_tree_menu_tree_all_pages}` 


## Usage

URL d'accès au service REST permettant de récupérer le menu complet sous forme d'arbre au format JSON : `${base_url}/jsp/site/Portal.jsp?page=treemenupages&action=menutree` 


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-menus/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*