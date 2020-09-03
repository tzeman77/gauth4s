
Implementation of Scala wrapper over Sign-In for web sites:
<https://developers.google.com/identity/sign-in/web/sign-in>

## Configuration: ##

1. Follow the instruction to create authorization credentials: <https://developers.google.com/identity/sign-in/web/sign-in#create_authorization_credentials>
2. Put the client ID into `example/jvm/resources/application.conf` (never commit this conf file; it is ignored by default):

```
config.gauth.clientId = ...
```

## Build & Run: ##

Development/interactive build/REPL:

```
./mill --repl -w

example.jvm.runBackground()()

```

### Generating IdeaJ project: ###

```
./mill mill.scalalib.GenIdea/idea
```

