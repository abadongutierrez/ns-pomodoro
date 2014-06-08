INSERT INTO public.user(username, email, password)
    VALUES('default', 'default@mail.com', crypt('password', gen_salt('bf')));

UPDATE public.task
    SET user_id = (SELECT user_id FROM public.user WHERE username = 'default');

ALTER TABLE public.task
    ALTER COLUMN task_id SET NOT NULL;