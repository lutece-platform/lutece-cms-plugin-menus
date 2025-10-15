--
-- Structure for table menus
--

DROP TABLE IF EXISTS menus;
CREATE TABLE menus (
  id_menu INT DEFAULT 0 NOT NULL,
  menu_name varchar(255) DEFAULT '' NOT NULL,
  type_menu varchar(255) DEFAULT '' NOT NULL,
  id_page_root INT DEFAULT 0 NOT NULL,
  menu_marker varchar(50) DEFAULT '' NOT NULL,    
  PRIMARY KEY (id_menu)
);

--
-- Structure for table custom_menus
--

DROP TABLE IF EXISTS custom_menu_items;
DROP TABLE IF EXISTS custom_menus;

CREATE TABLE menus_custom_menu (
  id_menu INT AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  bookmark varchar(100) NOT NULL,
  type varchar(50) NOT NULL,
  description varchar(255) DEFAULT '',
  PRIMARY KEY (id_menu)
);

--
-- Structure for table custom_menu_items
--

CREATE TABLE menus_custom_menu_items (
  id_item INT AUTO_INCREMENT,
  id_parent_menu INT NOT NULL,
  id_source_item varchar(255) default '',
  is_label_dynamic BOOLEAN DEFAULT FALSE,
  is_blank BOOLEAN DEFAULT FALSE,
  label varchar(255) default '',
  type varchar(50) NOT NULL,
  url varchar(500) default '',
  item_order INT DEFAULT 0,
  PRIMARY KEY (id_item),
  FOREIGN KEY (id_parent_menu) REFERENCES menus_custom_menu(id_menu) ON DELETE CASCADE
);
