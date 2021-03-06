/* to check:
CREATE TABLE  SBI_COMMUNITY(
  COMMUNITY_ID int(11) NOT NULL AUTO_INCREMENT,
  NAME varchar(200) NOT NULL,
  DESCRIPTION varchar(350) DEFAULT NULL,
  OWNER int(11) NOT NULL,
  FUNCT_CODE varchar(40) DEFAULT NULL,
  CREATION_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  LAST_CHANGE_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  USER_IN varchar(100) NOT NULL,
  USER_UP varchar(100) DEFAULT NULL,
  USER_DE varchar(100) DEFAULT NULL,
  TIME_IN timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  TIME_UP timestamp NULL DEFAULT NULL,
  TIME_DE timestamp NULL DEFAULT NULL,
  SBI_VERSION_IN varchar(10) DEFAULT NULL,
  SBI_VERSION_UP varchar(10) DEFAULT NULL,
  SBI_VERSION_DE varchar(10) DEFAULT NULL,
  META_VERSION varchar(100) DEFAULT NULL,
  ORGANIZATION varchar(20) DEFAULT NULL,
  PRIMARY KEY (COMMUNITY_ID)
);

CREATE TABLE SBI_COMMUNITY_USERS (
  `COMMUNITY_ID` int(11) NOT NULL,
  `USER_ID` int(11) NOT NULL,
  CREATION_DATE timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  LAST_CHANGE_DATE timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  USER_IN varchar(100) NOT NULL,
  USER_UP varchar(100) DEFAULT NULL,
  USER_DE varchar(100) DEFAULT NULL,
  TIME_IN timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  TIME_UP timestamp NULL DEFAULT NULL,
  TIME_DE timestamp NULL DEFAULT NULL,
  SBI_VERSION_IN varchar(10) DEFAULT NULL,
  SBI_VERSION_UP varchar(10) DEFAULT NULL,
  SBI_VERSION_DE varchar(10) DEFAULT NULL,
  META_VERSION varchar(100) DEFAULT NULL,
  ORGANIZATION varchar(20) DEFAULT NULL,
  PRIMARY KEY (`COMMUNITY_ID`,`USER_ID`),
  CONSTRAINT `FK_COMMUNITY` FOREIGN KEY (`COMMUNITY_ID`) REFERENCES `SBI_COMMUNITY` (`COMMUNITY_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_USER` FOREIGN KEY (`USER_ID`) REFERENCES `SBI_USER` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ;

*/


ALTER TABLE SBI_OBJECTS ADD COLUMN PREVIEW_FILE VARCHAR(100);\p\g

INSERT INTO SBI_USER_FUNC (USER_FUNCT_ID, NAME, DESCRIPTION, USER_IN, TIME_IN)
    VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_USER_FUNC'), 
    'MeasuresCatalogueManagement','MeasuresCatalogueManagement', 'server', current_timestamp);\p\g
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_USER_FUNC';\p\g

INSERT INTO SBI_ROLE_TYPE_USER_FUNC (ROLE_TYPE_ID, USER_FUNCT_ID)
    VALUES ((SELECT VALUE_ID FROM SBI_DOMAINS WHERE VALUE_CD = 'ADMIN' AND DOMAIN_CD = 'ROLE_TYPE'), 
    (SELECT USER_FUNCT_ID FROM SBI_USER_FUNC WHERE NAME = 'MeasuresCatalogueManagement'));\p\g
commit;\p\g

INSERT INTO SBI_ROLE_TYPE_USER_FUNC (ROLE_TYPE_ID, USER_FUNCT_ID)
    VALUES ((SELECT VALUE_ID FROM SBI_DOMAINS WHERE VALUE_CD = 'ADMIN' AND DOMAIN_CD = 'ROLE_TYPE'), 
    (SELECT USER_FUNCT_ID FROM SBI_USER_FUNC WHERE NAME = 'CreateWorksheetFromDatasetUserFunctionality'));\p\g
INSERT INTO SBI_ROLE_TYPE_USER_FUNC (ROLE_TYPE_ID, USER_FUNCT_ID)
    VALUES ((SELECT VALUE_ID FROM SBI_DOMAINS WHERE VALUE_CD = 'DEV_ROLE' AND DOMAIN_CD = 'ROLE_TYPE'), 
    (SELECT USER_FUNCT_ID FROM SBI_USER_FUNC WHERE NAME = 'CreateWorksheetFromDatasetUserFunctionality'));\p\g
INSERT INTO SBI_ROLE_TYPE_USER_FUNC (ROLE_TYPE_ID, USER_FUNCT_ID)
    VALUES ((SELECT VALUE_ID FROM SBI_DOMAINS WHERE VALUE_CD = 'TEST_ROLE' AND DOMAIN_CD = 'ROLE_TYPE'), 
    (SELECT USER_FUNCT_ID FROM SBI_USER_FUNC WHERE NAME = 'CreateWorksheetFromDatasetUserFunctionality'));\p\g
INSERT INTO SBI_ROLE_TYPE_USER_FUNC (ROLE_TYPE_ID, USER_FUNCT_ID)
    VALUES ((SELECT VALUE_ID FROM SBI_DOMAINS WHERE VALUE_CD = 'MODEL_ADMIN' AND DOMAIN_CD = 'ROLE_TYPE'), 
    (SELECT USER_FUNCT_ID FROM SBI_USER_FUNC WHERE NAME = 'CreateWorksheetFromDatasetUserFunctionality'));\p\g
commit;\p\g

CREATE TABLE SBI_GEO_LAYERS (
  LAYER_ID INTEGER NOT NULL,
  LABEL varchar(100) NOT NULL,
  NAME varchar(100),
  DESCR varchar(100),
  LAYER_DEFINITION LONG BYTE NOT NULL,
  TYPE varchar(40),
  IS_BASE_LAYER SMALLINT NOT NULL DEFAULT 0,
        USER_IN              VARCHAR(100) NOT NULL,
        USER_UP              VARCHAR(100),
        USER_DE              VARCHAR(100),
        TIME_IN              TIMESTAMP NOT NULL,
        TIME_UP              TIMESTAMP NULL DEFAULT NULL,
        TIME_DE              TIMESTAMP NULL DEFAULT NULL,
        SBI_VERSION_IN       VARCHAR(10),
        SBI_VERSION_UP       VARCHAR(10),
        SBI_VERSION_DE       VARCHAR(10),
        META_VERSION         VARCHAR(100),
        ORGANIZATION         VARCHAR(20), 
  CONSTRAINT SBI_GEO_LAYERS UNIQUE (LABEL, ORGANIZATION),
  PRIMARY KEY (LAYER_ID)
) ;\p\g


INSERT into SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN) 
values ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),'FILE','FILE','LAYER_TYPE','Layer Type','Layer Type','');\p\g
UPDATE hibernate_sequences SET next_val = (SELECT MAX(VALUE_ID) + 1 FROM SBI_DOMAINS) WHERE sequence_name = 'SBI_DOMAINS';\p\g  

INSERT into SBI_DOMAINS (VALUE_ID, VALUE_CD,VALUE_NM,DOMAIN_CD,DOMAIN_NM,VALUE_DS, USER_IN) 
values ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_DOMAINS'),'WFS','WFS','LAYER_TYPE','Layer Type','Layer Type','');\p\g
UPDATE hibernate_sequences SET next_val = (SELECT MAX(VALUE_ID) + 1 FROM SBI_DOMAINS) WHERE sequence_name = 'SBI_DOMAINS';\p\g  

INSERT INTO SBI_USER_FUNC (USER_FUNCT_ID, NAME, DESCRIPTION, USER_IN, TIME_IN)
    VALUES ((SELECT next_val FROM hibernate_sequences WHERE sequence_name = 'SBI_USER_FUNC'), 
    'GeoLayersManagement','GeoLayersManagement', 'server', current_timestamp);\p\g
update hibernate_sequences set next_val = next_val+1 where sequence_name = 'SBI_USER_FUNC';\p\g

INSERT INTO SBI_ROLE_TYPE_USER_FUNC (ROLE_TYPE_ID, USER_FUNCT_ID)
    VALUES ((SELECT VALUE_ID FROM SBI_DOMAINS WHERE VALUE_CD = 'ADMIN' AND DOMAIN_CD = 'ROLE_TYPE'), 
    (SELECT USER_FUNCT_ID FROM SBI_USER_FUNC WHERE NAME = 'GeoLayersManagement'));\p\g
commit;\p\g

ALTER TABLE SBI_COMMUNITY ADD UNIQUE INDEX NAME_UNIQUE (ORGANIZATION, NAME ASC) ;\p\g 

ALTER TABLE SBI_ENGINES DROP FOREIGN KEY FK_SBI_ENGINE_3;
ALTER TABLE SBI_ENGINES DROP COLUMN DEFAULT_DS_ID;
commit;

ALTER TABLE SBI_DATA_SOURCE ADD COLUMN READ_ONLY TINYINT DEFAULT 0;\p\g
ALTER TABLE SBI_DATA_SOURCE ADD COLUMN WRITE_DEFAULT TINYINT DEFAULT 0;\p\g
commit;\p\g

ALTER TABLE SBI_DATA_SET DROP COLUMN DATA_SOURCE_PERSIST_ID;\p\g
commit;\p\g

UPDATE SBI_CONFIG SET VALUE_CHECK = '' WHERE VALUE_CHECK = 'spagobi@eng.it';\p\g
commit;\p\g