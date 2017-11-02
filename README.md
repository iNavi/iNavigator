# iNavigator
A recommender system based on Google Map and Activity Recognition

The process of iNavigator system as below diagram:
```
          .----------.               .------------.                           .------------.        .------------.
  Sensor  |          |               |            |activity                   | Google Map |        |            |
  Event   | HARLib   |Processed Data | AR System  |(walking, sitting...etc)   |   and      |        |  GeoJSON   |
 -------> |          |-------------->|            |-------------------------> |  Google    |<-----> |            |
          |          |               |            |                           |  Service   |        |            |
          '----------'               '------------'                           '------------'        '------------'
```

## Google Maps Android API utility library

This open-source library contains utilities that are useful for a wide
range of applications using the Google Maps Android API.

## HARLib

A Human Activity Recognition Library and Demo on Android Platform.

## Android GeoJSON
A complete GeoJSON implementation for Android.

## Reference
- https://github.com/yhcvb/HARAndroid
- https://github.com/googlemaps/android-maps-utils
- https://github.com/cocoahero/android-geojson

