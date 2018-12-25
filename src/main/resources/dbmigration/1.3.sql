-- apply changes
alter table pinterest_settings alter column pinterest_username rename to username;
alter table pinterest_settings alter column pinterest_password rename to password;

