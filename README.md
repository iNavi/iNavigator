# iNavigator
A recommender system based on Google Map and Activity Recognition

The process of iNavigator system as below diagram:
```
          .----------.               .------------.                           .------------.        .------------.
          |          |               |            |activity                   | Google Map |        |            |
  Sensor  | HARLib   |Processed Data | AR System  |(walking, sitting...etc)   |   and      |        |  GeoJSON   |
 -------> |          |-------------->|            |-------------------------> |  Google    |<-----> |            |
          |          |               |            |                           |  Service   |        |            |
          '----------'               '------------'                           '------------'        '------------'
```

## Google Maps Android API utility library

This open-source library contains utilities that are useful for a wide
range of applications using the Google Maps Android API.

## HARLib

A Human Activity Recognition Library on Android Platform.

## Reference
- https://github.com/yhcvb/HARAndroid
- https://github.com/googlemaps/android-maps-utils

