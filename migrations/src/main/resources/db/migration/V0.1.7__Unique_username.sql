ALTER TABLE public.nsp_user
    ADD CONSTRAINT unique_username UNIQUE (username);