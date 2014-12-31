
CREATE TABLE BPM_CONF_USER(
	ID BIGINT auto_increment,
	VALUE VARCHAR(200),
	TYPE INT,
	STATUS INT,
	PRIORITY INT,
	NODE_ID BIGINT,
        CONSTRAINT PK_BPM_CONF_USER PRIMARY KEY(ID),
        CONSTRAINT FK_BPM_CONF_USER_NODE FOREIGN KEY(NODE_ID) REFERENCES BPM_CONF_NODE(ID)
) ENGINE=INNODB CHARSET=UTF8;

