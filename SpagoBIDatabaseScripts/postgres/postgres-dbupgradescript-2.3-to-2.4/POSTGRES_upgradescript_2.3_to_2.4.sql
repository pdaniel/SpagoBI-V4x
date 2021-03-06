/* Modifies for add menagement of notes linked to user */
ALTER TABLE SBI_OBJECT_NOTES ADD  OWNER VARCHAR(50);
ALTER TABLE SBI_OBJECT_NOTES ADD  ISPUBLIC SMALLINT;
ALTER TABLE SBI_OBJECT_NOTES ADD  CREATION_DATE TIMESTAMP DEFAULT CURRENT_DATE NOT NULL ;
ALTER TABLE SBI_OBJECT_NOTES ADD  LAST_CHANGE_DATE TIMESTAMP DEFAULT CURRENT_DATE NOT NULL ;

ALTER TABLE SBI_SUBOBJECTS ALTER COLUMN DESCRIPTION SET NOT NULL;
ALTER TABLE SBI_SUBOBJECTS ALTER COLUMN DESCRIPTION TYPE VARCHAR(1000);

ALTER TABLE SBI_DATA_SET ADD DS_METADATA VARCHAR(2000) DEFAULT NULL;
/* force a valid value for date fields in existing records: */
UPDATE SBI_OBJECT_NOTES SET LAST_CHANGE_DATE = CURRENT_TIMESTAMP,CREATION_DATE = CURRENT_TIMESTAMP;
/* force a valid value for owner field in existing records: 
***************************** ATTENTION **********************************
* The OWNER value depends from your context... 
we suggest 'biadmin' because is the classic admin user in SpaogoBI demo: 
you should change this value with a valid user in your platfrom, in this way 
he may change or delete the EXISTING notes!!*/
UPDATE SBI_OBJECT_NOTES SET OWNER = 'biadmin';
/*************************************************************************/
COMMIT;

/* Modifies for add possibility to update subobjects. It's necessary delete all subobjects where 
name is null because we add not null constraint to name column. */
DELETE FROM SBI_REMEMBER_ME WHERE SUBOBJ_ID IN (SELECT SUBOBJ_ID FROM SBI_SUBOBJECTS WHERE NAME IS NULL OR NAME ='');
DELETE FROM SBI_AUDIT WHERE SUBOBJ_ID IN (SELECT SUBOBJ_ID FROM SBI_SUBOBJECTS WHERE NAME IS NULL OR NAME ='');
DELETE FROM SBI_MENU WHERE SUBOBJ_NAME IN (SELECT NAME FROM SBI_SUBOBJECTS WHERE NAME IS NULL OR NAME ='');
DELETE FROM SBI_BINARY_CONTENTS WHERE BIN_ID IN (SELECT BIN_ID FROM SBI_SUBOBJECTS WHERE NAME IS NULL OR NAME ='');
DELETE FROM SBI_SUBOBJECTS WHERE NAME IS NULL OR NAME =''; 
COMMIT;
ALTER TABLE SBI_SUBOBJECTS ALTER COLUMN NAME TYPE VARCHAR(50);

/* Modified for metadata management */

ALTER TABLE SBI_OBJECTS DROP COLUMN DESCR_EXT;
ALTER TABLE SBI_OBJECTS DROP COLUMN OBJECTIVE;
ALTER TABLE SBI_OBJECTS DROP COLUMN LANGUAGE;
ALTER TABLE SBI_OBJECTS DROP COLUMN KEYWORDS;

ALTER TABLE SBI_EXT_ROLES ADD COLUMN SAVE_METADATA BOOLEAN DEFAULT TRUE;

CREATE SEQUENCE SBI_OBJ_METADATA_SEQ INCREMENT 1 START  1 ;
CREATE TABLE SBI_OBJ_METADATA (
	OBJ_META_ID 		INTEGER DEFAULT nextval('SBI_OBJ_METADATA_SEQ') NOT NULL,
    LABEL	 	        VARCHAR(20) NOT NULL,
    NAME 	            VARCHAR(40) NOT NULL,
    DESCRIPTION	        VARCHAR(100),  
    DATA_TYPE_ID		INTEGER NOT NULL,
    CREATION_DATE 	    DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,  
    PRIMARY KEY (OBJ_META_ID)
)WITHOUT OIDS;

CREATE UNIQUE INDEX XAK1SBI_OBJ_METADATA ON SBI_OBJ_METADATA
(
       OBJ_META_ID
);

ALTER TABLE SBI_OBJ_METADATA ADD  FOREIGN KEY (DATA_TYPE_ID) REFERENCES SBI_DOMAINS (VALUE_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;

CREATE SEQUENCE SBI_OBJ_METACONTENTS_SEQ INCREMENT 1 START  1 ;
CREATE TABLE SBI_OBJ_METACONTENTS (
  OBJ_METACONTENT_ID INTEGER DEFAULT nextval('SBI_OBJ_METACONTENTS_SEQ') NOT NULL,
  OBJMETA_ID 		 INTEGER  NOT NULL ,
  BIOBJ_ID 			 INTEGER  NOT NULL,
  SUBOBJ_ID 		 INTEGER,
  BIN_ID 			 INTEGER,
  CREATION_DATE 	 DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,  
  LAST_CHANGE_DATE   DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,  
    PRIMARY KEY (OBJ_METACONTENT_ID)
)WITHOUT OIDS;


CREATE UNIQUE INDEX XAK1SBI_OBJ_METACONTENTS ON SBI_OBJ_METACONTENTS
(
        OBJMETA_ID,
        BIOBJ_ID,
        SUBOBJ_ID
);

ALTER TABLE SBI_OBJ_METACONTENTS ADD  FOREIGN KEY ( OBJMETA_ID ) REFERENCES SBI_OBJ_METADATA (  OBJ_META_ID ) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE SBI_OBJ_METACONTENTS ADD  FOREIGN KEY ( BIOBJ_ID )   REFERENCES SBI_OBJECTS (  BIOBJ_ID ) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE SBI_OBJ_METACONTENTS ADD  FOREIGN KEY ( SUBOBJ_ID )  REFERENCES SBI_SUBOBJECTS (  SUBOBJ_ID ) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE SBI_OBJ_METACONTENTS ADD  FOREIGN KEY ( BIN_ID )     REFERENCES SBI_BINARY_CONTENTS(BIN_ID) ON UPDATE RESTRICT ON DELETE RESTRICT;

--adds new funcionality for metadata management
INSERT INTO SBI_USER_FUNC (NAME, DESCRIPTION) VALUES ('ObjMetadataManagement', 'ObjMetadataManagement');
INSERT INTO  SBI_ROLE_TYPE_USER_FUNC values((SELECT VALUE_ID FROM SBI_DOMAINS WHERE DOMAIN_CD = 'ROLE_TYPE' AND VALUE_CD = 'ADMIN'), (SELECT USER_FUNCT_ID FROM SBI_USER_FUNC WHERE NAME='ObjMetadataManagement'));
COMMIT;
