


INSERT INTO ORG_POSITION_TYPE(ID,NAME,SCOPE_ID) VALUES(3,'管理','1');



UPDATE ORG_POSITION SET NAME='总经理',TYPE_ID=3 where id=1;
UPDATE ORG_POSITION SET NAME='部门经理',TYPE_ID=3 where id=2;
INSERT INTO ORG_POSITION(ID,NAME,TYPE_ID,SCOPE_ID) VALUES(3,'研发工程师',1,'1');
INSERT INTO ORG_POSITION(ID,NAME,TYPE_ID,SCOPE_ID) VALUES(4,'商务主管',2,'1');


