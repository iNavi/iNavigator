# HARAndroid
A Human Activity Recognition Library and Demo on Android Platform.

## HARLib

### The Process of HAR
The process of HAR as below diagram:
```
          .------------.        .--------------.        .------------.
  Sensor  |            |        |              |        |            |activity
  Event   | SensorData |RawData | Data         |Instance| Classifier |(walking, sitting...etc)
 -------> | Collector  |------->| Preprocessor |------->|            |------->
          |            |        |              |        |            |
          '------------'        '--------------'        '------------'
```
#### Sensor Data Collect
Collect a period of time of accelerometer sensor data on android phone. Then input an array of RawData into DataPreprocessor.

#### Sensor Data PreProcess
This process will extract features from the array of RawData, and then trans these feature into Weka's class - Instance, which can pass to Classifier.

#### Classify
Classifier will process the Instance, then give an classify result. The Classifier will import the mode file on initial phase, which was trained and created on Weka desktop version.
The default Classifier use Decision Tree J48 algorithm, it has pretty good performance. If you need to try another algorithm, you can pass the path of your classifier model file as parameter
into the constructor of HumanActivityRecognizer.

### Usage of HARLib

#### Library Dependencies

you can download lastest library at [release page](https://github.com/yhcvb/HARAndroid/releases)
And then add these to you build.gradle

```
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
	compile(name: 'HARLib-release', ext: 'aar')
    compile(name: 'WekaAndroid-release', ext: 'aar')
}
```

#### Sample Code

The Usage of HARLib is easy, You can simply create instance of this class and call start() method, then will get recognition result in listener.
```
	HumanActivityRecognizer mHAR;

	private void initHAR() {
		mHAR = new HumanActivityRecognizer(context, true, HarMode.CLASSIFY, mHarDataListener);
		mHAR.start();
	}

	private HarDataListener mHarDataListener = new HarDataListener() {
		@Override
		public void onHarDataChange(HumanActivity ha) {
			// recognition result
		}

		@Override
		public void onHarRawDataChange(List<RawData> rawDataList) {
			// raw sensor data
		}
	};
```

## HARDemo

This is an demo project for HARLib, you can down app [here](https://fir.im/hardemo)

## Reference
- http://www.cis.fordham.edu/wisdm/
- http://www.cs.waikato.ac.nz/ml/weka/
- https://github.com/Shookit/android-ml-weka

# iNavigator
