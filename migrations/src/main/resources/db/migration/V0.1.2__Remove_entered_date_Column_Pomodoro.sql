ALTER TABLE public.pomodoro
    DROP COLUMN entered_date,
    ALTER COLUMN started_date SET DEFAULT NOW();