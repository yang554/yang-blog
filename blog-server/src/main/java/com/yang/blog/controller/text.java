import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class text {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入上次月经的开始日期（格式：yyyy-MM-dd）：");
        String lastPeriodStartDateStr = scanner.nextLine();
        LocalDate lastPeriodStartDate = LocalDate.parse(lastPeriodStartDateStr);

        System.out.print("请输入月经周期的天数：");
        int cycleLength = scanner.nextInt();

        System.out.print("请输入月经持续的天数：");
        int periodLength = scanner.nextInt();

        LocalDate nextPeriodStartDate = lastPeriodStartDate.plusDays(cycleLength);
        LocalDate nextPeriodEndDate = nextPeriodStartDate.plusDays(periodLength);

        System.out.println("下次月经开始日期：" + nextPeriodStartDate);
        System.out.println("下次月经结束日期：" + nextPeriodEndDate);

        long daysUntilNextPeriod = LocalDate.now().until(nextPeriodStartDate, ChronoUnit.DAYS);
        System.out.println("距离下次月经开始还有：" + daysUntilNextPeriod + "天");
    }
}