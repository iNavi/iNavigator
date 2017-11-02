# iNavigator
A recommender system based on Google Map and Activity Recognition

The process of iNavigator system as below diagram:
```
          .------------.               .------------.                            .------------.            .------------.
  Sensor  |            |               |            |activity                    |            |            |            |
  Event   | HARLib     |Processed Data | AR System  |(walking, sitting...etc)    |   Google   |            |  GeoJSON   |
 -------> |            |-------------->|            |--------------------------> |    Map     |<---------> |            |
          |            |               |            |                            |            |            |            |
          '------------'               '------------'                            '------------'            '------------'


```

## Google Maps Android API utility library

This open-source library contains utilities that are useful for a wide
range of applications using the Google Maps Android API.

## HARLib

#### Sensor Data Collect
Collect a period of time of accelerometer sensor data on android phone. Then input an array of RawData into DataPreprocessor.

#### Sensor Data PreProcess
This process will extract features from the array of RawData, and then trans these feature into Weka's class - Instance, which can pass to Classifier.

#### Classify
Classifier will process the Instance, then give an classify result. The Classifier will import the mode file on initial phase, which was trained and created on Weka desktop version.
The default Classifier use Decision Tree J48 algorithm, it has pretty good performance. If you need to try another algorithm, you can pass the path of your classifier model file as parameter
into the constructor of HumanActivityRecognizer.

## Android GeoJSON
A complete GeoJSON implementation for Android.

## Reference
- https://github.com/yhcvb/HARAndroid
- https://github.com/googlemaps/android-maps-utils
- https://github.com/cocoahero/android-geojson

