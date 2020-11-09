
# react-native-react-native-package-manager

## Getting started

`$ npm install react-native-react-native-package-manager --save`

### Mostly automatic installation

`$ react-native link react-native-react-native-package-manager`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNReactNativePackageManagerPackage;` to the imports at the top of the file
  - Add `new RNReactNativePackageManagerPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-react-native-package-manager'
  	project(':react-native-react-native-package-manager').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-react-native-package-manager/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-react-native-package-manager')
  	```


## Usage
```javascript
import RNReactNativePackageManager from 'react-native-react-native-package-manager';

// TODO: What to do with the module?
RNReactNativePackageManager;
```
  