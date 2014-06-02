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
    -- Pomodoro created is a pomodoro started
    started_date NOT NULL TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    ended_date TIMESTAMP WITHOUT TIME ZONE,
    
    CONSTRAINT pomodoro_task_fk FOREIGN KEY (task_id)
    REFERENCES public.task (task_id)
    ON UPDATE NO ACTION ON DELETE CASCADE
);