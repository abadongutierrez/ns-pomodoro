# ns-pomodoro

FIXME

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

Or start REPL:

```
lein repl
user=> (use 'ns-pomodoro.repl)
user=> (start-server)
```

To reload when something has changed:

```
user=> (use 'ns-pomodoro.repl :reload)
```

## License

Copyright © 2014 FIXME
