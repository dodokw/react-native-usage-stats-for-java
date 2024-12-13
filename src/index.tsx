import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-usage-stats' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const UsageStats = NativeModules.UsageStats
  ? NativeModules.UsageStats
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export type UsageStatsModuleProps = {
  requestUsageStatsPermission: () => Promise<void>; // 권한 요청
  hasUsageStatsPermission: () => Promise<boolean>; // 권한 확인
};

export type usageStatsManagerProps = {
  queryUsageStats: (
    intervalType: 0 | 1 | 2 | 3 | 4, // Best is 4
    beginTime: number, // 시작 시간
    endTime: number // 끝 시간
  ) => Promise<queryUsageStatsProps[]>;
};

export type queryUsageStatsProps = {
  appName: string; // 앱 이름
  totalTimeInForeground: number; // 총 사용 시간
  firstTimeStamp: number; // 처음 사용 시간
  lastTimeStamp: number; // 마지막 구동 시간
  lastTimeUsed: number; // 마지막 사용 시간
  packageName: string; // 패키지 이름
  isSystem: boolean; // 시스템 앱 여부 (true: 시스템 앱, false: 사용자 앱)
};

export const UsageStatsModule: UsageStatsModuleProps = {
  requestUsageStatsPermission: async () => {
    return UsageStats.requestUsageStatsPermission();
  },
  hasUsageStatsPermission: async () => {
    return UsageStats.hasUsageStatsPermission();
  },
};

export const UsageStatsManager: usageStatsManagerProps = {
  queryUsageStats: async (intervalType, beginTime, endTime) => {
    return UsageStats.queryUsageStats(intervalType, beginTime, endTime);
  },
};
