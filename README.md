**Android WebAppBase**
==================
This project help you enable to mount your webapp to WebView-based-app conveniently.

You can make own app by just change app's xml values and change images.

　

You should to change this images.
----------------------
**mipmap\ic_launcher.png** : *App icon*

**drawable\logo.png** : *App splash logo*

　

xml doc
--------
**bool.xml**

> *is_develop*

> 　Set this value true if your app is still in development.

> *is_contain_fingerpush*

> 　This app contains fingerpush basically. Set true to use.

> *is_preloader_on*

> 　Set this value true if you want to show loading-screen while webview is loading page.

> *is_spinner_on*

> 　Set this value to show spinner while loading.

> *is_splash_on*

> 　Set this value to show splash screen when webview first loaded.

　


**strings.xml**


> *app_name*

> 　Your app's name. This value is used on Alert, Toast, Logs and App naming.

> *app_prefix*

> 　Your app's prefix. This value is used on javascript bridge and app scheme.

> *app_host*

> 　Your app's host name when app is called by url scheme.

> *root_url*

> 　Your web root url.

> *root_url_test*

> 　Your test web root url.

　

Finger Push assets
--------
　FingerPush.properties

　

Thank you.

　

Copyright 2016 HansolLim
 
Released under the MIT license

http://hsol.github.io/
