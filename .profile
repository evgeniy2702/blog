web: bundle exec rails server -p $PORT -e $RAILS_ENV -b 0.0.0.0
worker: bundle exec sidekiq -e $RAILS_ENV
$ echo unset JAVA_TOOL_OPTIONS