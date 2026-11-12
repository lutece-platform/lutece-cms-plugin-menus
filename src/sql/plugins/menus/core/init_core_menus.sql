-- liquibase formatted sql
-- changeset menus:init_core_menus.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'CUSTOM_MENUS_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES 
('CUSTOM_MENUS_MANAGEMENT','menus.adminFeature.custom_menus_management.name',0,'jsp/admin/plugins/menus/ManageCustomMenus.jsp','menus.adminFeature.custom_menus_management.description',0,'menus','SITE','ti ti-menu-2',NULL);

--
-- Dumping data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'CUSTOM_MENUS_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('CUSTOM_MENUS_MANAGEMENT',1);
