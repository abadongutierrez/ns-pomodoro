-- This script didnt run with the version 1.5
INSERT INTO public.nsp_user(username, email, password)
    VALUES('trashuser', 'default@mail.com', crypt('password', gen_salt('bf')));