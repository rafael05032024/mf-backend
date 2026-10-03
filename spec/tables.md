USER
id            | number       | not null      | PK | AUTOGENERATE
name          | varchar(255) | not null
document      | varchar(50)  | default null
birthdate     | date         | default null
profile       | varchar(50)  | not null
thumb         | varchar(50)  | null
email         | varchar(50)  | not null
verified      | boolean      | default false
password      | varchar(50)  | not null
created_at    | timestamp    | default now()
updated_at    | timestamp    | default now()
deleted_at    | timestamp    | default null

USER_POST
owner         | number       | not null      | FK(USER)
type          | number       | not null        
link          | varchar(50)  | not null
is_private    | boolean      | not null
created_at    | timestamp    | default now()
updated_at    | timestamp    | default now()

SIGNATURE 
subscriber    | number       | not null      | FK(USER)
producer      | number       | not null      | FK(USER)
created_at    | timestamp    | default now()
updated_at    | timestamp    | default now()
expire_at     | timestamp    | not null

WALLET
id            | number       | not null      | PK         | AUTOGENERATE    
owner         | number       | not null      | FK(USER)
balance       | number       | not null      | default 0

TRANSACTION
wallet_id     | number       | not null      | FK(WALLET)
type          | number       | not null
value         | number       | not null
created_at    | timestamp    | default now()
updated_at    | timestamp    | default now()

NOTIFICATION
id            | number       | not null      | PK         | AUTOGENERATE  
text          | varchar(50)  | not null
created_at    | timestamp    | default now()
readed_at     | timestamp    | default now()

PLAN
producer      | number       | not null      | FK(USER)
value         | number       | not null
created_at    | timestamp    | default now()
updated_at    | timestamp    | default now()