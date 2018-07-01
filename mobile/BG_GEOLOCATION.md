

ionic g provider LocationTracker
```
ionic plugin add cordova-plugin-mauron85-background-geolocation
```

Whitelist to allow internet connections:

```
cordova plugin add cordova-plugin-whitelist
```

On index.html:

``` 
<meta http-equiv="Content-Security-Policy" content="default-src *; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline' 'unsafe-eval'">
```


Crosswalk for old android versions:

```
ionic plugin add cordova-plugin-crosswalk-webview
```