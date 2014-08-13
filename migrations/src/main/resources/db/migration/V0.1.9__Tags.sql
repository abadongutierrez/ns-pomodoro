CREATE TABLE public.tag (
    tag_id serial PRIMARY KEY,
    name TEXT,
    created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() 
);

CREATE TABLE public.task_tag (
    task_id integer NOT NULL,
    tag_id integer NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT task_tag_fk FOREIGN KEY (task_id)
    REFERENCES public.task (task_id)
    ON UPDATE NO ACTION ON DELETE CASCADE,

    CONSTRAINT tag_task_fk FOREIGN KEY (tag_id)
    REFERENCES public.tag (tag_id)
    ON UPDATE NO ACTION ON DELETE CASCADE 
);