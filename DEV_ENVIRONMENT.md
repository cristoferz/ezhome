# Developer guide: getting your environment set up

## Web Development
1. Install node, with at least version 4.6.1
1. Run `npm install -g angular-cli` to install angular-cli
1. On folder `local-site/frontend` run `npm install`

To bring up a local server, run `ng serve`. This automatically watch for changes and rebuild. The browser should refresh automatically when changes are made.

To build the components in dev mode, run `ng build`. To build the components in release mode, run `ng build --prod`.

## Controller Development
Controller is developed is Netbeans. Install at least Netbeans 8.

Is recommended to have ant installed and on path to allow command line compilation. This is mainly necessary for a release build, for development Netbeans 
compilation will serve well.

## Database Development
EzHome database is a PostgreSQL. 

There is no templates already defined for database.  

## Devices Development
Devices are developed with Arduino IDE. Install at least version 1.6.9. 

## Build a release
Is necessary to have all environments configured. This is needed because of make commands that rebuild all structures before packaging.

```

