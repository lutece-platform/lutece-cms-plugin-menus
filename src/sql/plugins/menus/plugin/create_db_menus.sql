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