package yore.nextdate;

import java.util.ArrayList;

/**
 * NextDate逻辑类
 * 不要实例化，修改入口函数以测试
 * 请使用判定标志常量
 */
public class NextDate {
    // 返回结果判定标志
    public static final String NO_LUNAR_INFO = "2";     // 农历越界
    public static final String SUCCESS = "1";           // 计算成功
    public static final String FAIL = "0";              // 计算失败
    // 公历用计算常量
    private static final int weekBase = 1;
    private static final int yearBase = 1900;
    private static final int yearUpper = 2100;
    private static final int addInLeapYear = 366 % 7;
    private static final int addInNormalYear = 365 % 7;
    private static final int[][] calendarMonth = {
            {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},
            {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}
    };
    // 错误码
    private static final int validSuccess = 100;
    private static final int errDateOutOfRange = 101;
    private static final int errNoSuchDate = 102;
    // 错误信息
    private static final String errDateOutOfRangeMsg = "输入的日期超出应用可用范围";
    private static final String errNoSuchDateMsg = "输入的日期不存在";
    private static final String unknownErrorMsg = "未知的错误";

    /**
     * 获取输入日期的后一天的公历和农历信息入口方法
     *
     * @param yearNow  输入年
     * @param monthNow 输入月
     * @param dayNow   输入日
     * @return 后一天的日期信息或者错误信息列表
     */
    public static ArrayList<String> getNextDateInfo(int yearNow, int monthNow, int dayNow) {
        ArrayList<String> result = new ArrayList<>();
        switch (validDate(yearNow, monthNow, dayNow)) {
            case errDateOutOfRange:
                result.add(FAIL);
                result.add(errDateOutOfRangeMsg);
                break;
            case errNoSuchDate:
                result.add(FAIL);
                result.add(errNoSuchDateMsg);
                break;
            case validSuccess:
                if (dayNow == 31 && monthNow == 12 && yearNow == yearUpper) result.add(NO_LUNAR_INFO);
                else result.add(SUCCESS);
                String[] info = _getNextDateInfo(yearNow, monthNow, dayNow);
                for (String s : info) {
                    result.add(s);
                }
                break;
            default:
                result.add(FAIL);
                result.add(unknownErrorMsg);
                break;
        }
        return result;
    }

    /**
     * 获取日期后一天信息主逻辑
     *
     * @return 后一天信息字符串数组
     */
    private static String[] _getNextDateInfo(int year, int month, int day) {
        String[] info;
        int isLeapYear = isLeapYear(year) ? 1 : 0;
        int yearNext = year;
        int monthNext = month;
        int dayNext = day + 1;
        int week = weekBase;
        int total = 0;          // 与1900-1-1的时间差,用于计算农历

        // 计算下一天的公历日期
        if (dayNext > calendarMonth[isLeapYear][monthNext-1]) {
            dayNext = 1;
            monthNext++;
            if (monthNext > 12) {
                monthNext = 1;
                yearNext++;
            }
        }

        // 计算下一天星期
        for (int y = yearBase; y < yearNext; y++) {
            if (isLeapYear(y)) {
                week = (week + addInLeapYear) % 7;
                total+=366;
            }
            else {
                week = (week + addInNormalYear) % 7;
                total+=365;
            }
        }
        for (int m = 1; m < monthNext; m++) {
            week = (week + calendarMonth[isLeapYear][m-1]) % 7;
            total += calendarMonth[isLeapYear][m-1];
        }
        week = (week + dayNext - 1) % 7;
        total += dayNext - 1;

        // 根据公历日期获取农历日期
        String[] lunarDateInfo = LunarUtil.getLunarDateInfo(yearNext, monthNext, dayNext, total);

        // 更新后一天信息字符串数组
        info = new String[]{
                yearNext + "",
                monthNext + "",
                dayNext + "",
                week + "",
                lunarDateInfo[0],
                lunarDateInfo[1],
                lunarDateInfo[2],
                lunarDateInfo[3]
        };

        return info;
    }

    /**
     * 判断某年是否是闰年
     *
     * @return 是否是闰年的结果
     */
    private static Boolean isLeapYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
    }

    /**
     * 判断日期是否合法
     *
     * @return 错误码
     */
    private static int validDate(int year, int month, int day) {
        if (year < 1900 || year > 2100) {
            return errDateOutOfRange;
        } else if (month > 12 || month < 1) {
            System.out.println(year+"-"+month+"-"+day);
            return errNoSuchDate;
        } else {
            int isLeapYear = isLeapYear(year) ? 1 : 0;
            if (day < 1 || day > calendarMonth[isLeapYear][month-1]) {
                System.out.println(year+"-"+month+"-"+day);
                return errNoSuchDate;
            }
        }
        return validSuccess;
    }


    public static void main(String[] args) {
        int year = 2099;
        for (int i=1; i<=12; i++) {
            for (int j=1; j<=calendarMonth[isLeapYear(year)?1:0][i-1]; j++){
                for (String s : getNextDateInfo(year, i, j)) {
                    System.out.print(s + ",");
                }
                System.out.println();
                System.out.println();
            }
        }
    }
}
