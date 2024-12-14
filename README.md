# react-native-usage-stats-for-java

React Native 모듈로 안드로이드에서만 사용 가능합니다.

이 모듈은 사용자가 사용한 앱의 이름, 총 사용 시간, 처음 사용 시간, 마지막 구동 시간, 마지막 사용 시간, 패키지 이름, 시스템 앱 여부를 가져옵니다. Kotlin 1.8을 사용할 수 없는 경우에도 사용할 수 있도록 설계된 라이브러리입니다.

## 설치

```bash
npm install react-native-usage-stats-for-java
yarn add react-native-usage-stats-for-java
```

## 권한 추가
AndroidManifest.xml

```xml
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
```

## Usage

권한 확인 및 요청
사용 통계에 접근하기 위해서는 먼저 권한을 확인하고 요청해야 합니다.

```js
import { UsageStatsModule } from 'react-native-usage-stats-for-java';

const getUsagePermission = () => {
  UsageStatsModule.hasUsageStatsPermission()
    .then((hasPermission) => {
      if (hasPermission) {
        console.log('Usage stats permission is granted');
      } else {
        console.log('Usage stats permission is not granted');
        // 권한 요청
        UsageStatsModule.requestUsageStatsPermission();
      }
    })
    .catch((error) => console.error(error));
};
```

사용 통계 조회
사용 통계를 조회하는 방법은 다음과 같습니다.

```js
const getUsageStats = () => {
  const tensecond = 1000 * 10; // 10초
  const now = new Date().getTime();
  const startMilliseconds = now - tensecond;
  const endMilliseconds = now;

  UsageStatsManagerModule.queryUsageStats(
    4, // 기간: 4일
    startMilliseconds,
    endMilliseconds
  ).then((res) => {
    if (res) {
      const jsonData = res;
      for (const key in jsonData) {
        if (jsonData.hasOwnProperty(key)) {
          const element = jsonData[key];
          if (element.lastTimeUsed > now - tensecond && !element.isSystem) {
            console.log(element);
          }
        }
      }
    } else {
      console.log('fail to getUsageStats');
    }
  });
};
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
