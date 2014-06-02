CREATE TABLE public.task (
    task_id serial PRIMARY KEY,
    name TEXT,
    is_done BOOLEAN DEFAULT FALSE,
    entered_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    done_date TIMESTAMP WITHOUT TIME ZONE 
);

CREATE TABLE public.pomodoro (
    pomodoro_id serial PRIMARY KEY,
    task_id integer NOT NULL,
    entered_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    started_date TIMESTAMP WITHOUT TIME ZONE,
    ended_date TIMESTAMP WITHOUT TIME ZONE,
    
    CONSTRAINT pomodoro_task_fk FOREIGN KEY (task_id)
    REFERENCES public.task (task_id)
    ON UPDATE NO ACTION ON DELETE CASCADE
);