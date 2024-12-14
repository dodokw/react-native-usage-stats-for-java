import { NativeModules } from 'react-native';
import { UsageStats } from '../src/index';

jest.mock('react-native', () => ({
  NativeModules: {
    Usagestats: {
      hasUsageStatsPermission: jest.fn(),
      requestUsageStatsPermission: jest.fn(),
    },
  },
}));

describe('UsageStats', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should check for usage stats permission', async () => {
    NativeModules.Usagestats.hasUsageStatsPermission.mockResolvedValue(true);
    const result = await UsageStats.hasUsageStatsPermission();
    expect(result).toBe(true);
    expect(NativeModules.Usagestats.hasUsageStatsPermission).toHaveBeenCalled();
  });

  it('should request usage stats permission', async () => {
    await UsageStats.requestUsageStatsPermission();
    expect(NativeModules.Usagestats.requestUsageStatsPermission).toHaveBeenCalled();
  });
});
