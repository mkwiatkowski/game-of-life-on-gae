Game of Life on Google App Engine written in Clojure

## Live demo

http://game-of-life-on-gae.appspot.com/

## Development

M-x clojure-jack-in
(require 'game-of-life-on-gae.core)
(in-ns 'game-of-life-on-gae.core)
(ae/serve game-of-life-on-gae-app)

## Testing

$ lein appengine-prepare
$ dev_appserver.sh war/

## Deployment

$ lein appengine-prepare
$ appcfg.sh update war/

## License

Copyright (C) 2011 Michal Kwiatkowski

Distributed under the Eclipse Public License, the same as Clojure.
