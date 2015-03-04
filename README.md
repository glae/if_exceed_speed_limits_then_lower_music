# Dépasse la vitesse = baisse la musique

## Objectif 

Application, site ou plugin dont le but est d'encourager le conducteur à respecter les limitations de vitesse, en réduisant le volume musical dès que la limitation de vitesse (ou au seuil au-delà) en cours est dépassée. 

## Applications utilisables telles quelles 

Rien trouvé, ce qui s'en rapproche le plus est [Mappy GPS free](https://play.google.com/store/apps/details?id=com.mappy.androidpagesjaunes&hl=fr_FR) (alerte sonore)

Soutenir http://www.apivir.org/index.html

Un peu à côté, un HUD sur la vitre : https://www.youtube.com/watch?v=rzda7CQ-ZAU 

## API exploitables

Voici les trois données nécessaires pour envisager une réalisation : 

### 1. Vitesse véhicule

- il existe une norme d'API http://www.w3.org/2014/automotive/vehicle_spec.html

- http://developer.android.com/guide/topics/location/strategies.html et http://developer.android.com/reference/android/location/Location.html#getSpeed() ou encore http://www.devlper.com/2010/07/getting-speed-of-the-device-using-gps-in-android/

### 2. Limites de vitesse

- Openstreetmap : http://www.itoworld.com/map/124 (API légère à consommer : http://wiki.openstreetmap.org/wiki/Overpass_API et http://wiki.openstreetmap.org/wiki/Key%3amaxspeed) ou encore https://www.data.gouv.fr/fr/datasets/kilometrage-des-types-de-routes-repartis-par-communes/

- [**choix pour test**] HERE (ex Navteq) : https://developer.here.com/rest-apis/documentation/routing/topics/resource-get-link-info.html (http://stackoverflow.com/questions/22821101/here-maps-rest-api-getlinkinfo-returns-incorrect-speed-limit) http://heremaps.github.io/examples/explorer.html#speed-limit-on-click__index Le problème est que toutes les données ne semblent pas disponibles (le "/", à voir) http://route.st.nlp.nokia.com/routing/6.2/getlinkinfo.json?app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg&waypoint=47.40313352435497,0.8754457378382199
 
D'après les https://developer.here.com/terms-conditions nous avons droit à une requête par seconde gratuite.

https://developer.android.com/training/basics/firstapp/index.html

- Wikispeedia

### 3. Accès à la gestion du volume
- [AudioManager](http://developer.android.com/reference/android/media/AudioManager.html)

## Solutions techniques

TODO