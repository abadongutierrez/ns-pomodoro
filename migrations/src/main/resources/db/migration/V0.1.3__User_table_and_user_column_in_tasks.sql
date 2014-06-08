CREATE TABLE public.user (
    user_id serial PRIMARY KEY,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    entered_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() 
);

ALTER TABLE public.task
    ADD COLUMN user_id INTEGER,
    ADD CONSTRAINT user_task_fk FOREIGN KEY (user_id)
    REFERENCES public.user (user_id)
    ON UPDATE NO ACTION ON DELETE CASCADE;