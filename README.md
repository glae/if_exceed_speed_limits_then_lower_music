# Excess speed limits = lower music volume

## Goal

Application, site or plugin aiming to encourage a driver to respect speed limits, by reducing music volume when exceeding speed limit.

## Existing stuff 

- [Mappy GPS free](https://play.google.com/store/apps/details?id=com.mappy.androidpagesjaunes&hl=fr_FR) (beep alarm when over speed limit)
- http://www.apivir.org/index.html
- related, HUD in car: https://www.youtube.com/watch?v=rzda7CQ-ZAU 

## Usable APIs

Here are the three needed data to consider an implementation:

### 1. Vehicle speed

- there is a API norm: http://www.w3.org/2014/automotive/vehicle_spec.html

- http://developer.android.com/guide/topics/location/strategies.html and http://developer.android.com/reference/android/location/Location.html#getSpeed() or http://www.devlper.com/2010/07/getting-speed-of-the-device-using-gps-in-android/

### 2. Speed limits

- Openstreetmap : http://www.itoworld.com/map/124 (light API: http://wiki.openstreetmap.org/wiki/Overpass_API et http://wiki.openstreetmap.org/wiki/Key%3amaxspeed) or https://www.data.gouv.fr/fr/datasets/kilometrage-des-types-de-routes-repartis-par-communes/

- [**chosen for test**] HERE (Nokia/ex Navteq) : https://developer.here.com/rest-apis/documentation/routing/topics/resource-get-link-info.html (http://stackoverflow.com/questions/22821101/here-maps-rest-api-getlinkinfo-returns-incorrect-speed-limit) http://heremaps.github.io/examples/explorer.html#speed-limit-on-click__index   

API call example: http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=47.40313352435497,0.8754457378382199
 
https://developer.here.com/terms-conditions says we have 1 free request per second.

- Wikispeedia

### 3. Volume control
- [AudioManager](http://developer.android.com/reference/android/media/AudioManager.html)
