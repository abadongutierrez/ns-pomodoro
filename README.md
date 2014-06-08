# ns-pomodoro

FIXME

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Migrate DB

```
gradle flywayMigrate -i
```

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

### Referencies

* [Noir Documentation](http://www.webnoir.org/autodoc/1.0.0/index.html)
* [Bootstrap](http://getbootstrap.com/)
* [Clostache](https://github.com/fhd/clostache) {{ mustache }} for Clojure
* [Clojure Docs](http://clojuredocs.org/)
* [PostgreSQL](http://www.postgresql.org/docs/9.1/static/index.html) 9.1 Documentation

## License

Copyright © 2014 FIXME
