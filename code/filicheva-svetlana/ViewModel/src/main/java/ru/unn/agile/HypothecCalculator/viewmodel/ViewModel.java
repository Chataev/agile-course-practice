package ru.unn.agile.HypothecCalculator.viewmodel;

import ru.unn.agile.HypothecCalculator.model.*;
import javax.swing.table.DefaultTableModel;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ViewModel {

    private String status;
    private boolean isButtonEnabled;

    private String houseCost;
    private String downPayment;
    private String countOfPeriods;
    private String interestRate;
    private String flatFee;
    private String monthlyFee;
    private String startMonth;
    private String startYear;

    private Hypothec.CurrencyType currencyType;
    private Hypothec.PeriodType periodType;
    private Hypothec.InterestRateType interestRateType;
    private Hypothec.FlatFeeType flatFeeType;
    private Hypothec.MonthlyFeeType monthlyFeeType;
    private Hypothec.CreditType creditType;

    private String monthlyPayment;
    private String overpaymentWithFees;
    private String overpayment;
    private DefaultTableModel graphicOfPayments;

    private Hypothec.Builder hypothecForParsing;
    private CreditCalculator creditCalculator;
    private GraphicOfPaymentsMaker graphicOfPaymentsMaker;
    private final ILogger logger;

    private static final double DOUBLE_DELTA = 0.001;

    public ViewModel(final ILogger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger is null");
        }
        this.logger = logger;

        status = Status.WAITING;
        isButtonEnabled = false;

        houseCost = "";
        downPayment = "0";
        countOfPeriods = "";
        interestRate = "";
        flatFee = "0";
        monthlyFee = "0";

        currencyType = Hypothec.CurrencyType.DOLLAR;
        periodType = Hypothec.PeriodType.MONTH;
        interestRateType = Hypothec.InterestRateType.MONTHLY;
        flatFeeType = Hypothec.FlatFeeType.CONSTANT_SUM;
        monthlyFeeType = Hypothec.MonthlyFeeType.CREDIT_SUM_PERCENT;
        creditType = Hypothec.CreditType.DIFFERENTIATED;

        startMonth = "11";
        startYear = "2015";

        graphicOfPayments = new DefaultTableModel();
    }

    public boolean checkInput() {
        cleanOutput();
        status = Status.READY;
        isButtonEnabled = false;

        hypothecForParsing = new Hypothec.Builder();

        if (!houseCostAndCountOfPeriodsIsOK()) {
            return false;
        }
        if (!downPaymentIsOK()) {
            return false;
        }
        if (!interestRateIsOK()) {
            return false;
        }
        if (!flatFeeIsOK()) {
            return false;
        }
        if (!monthlyFeeIsOK()) {
            return false;
        }
        if (!startDateIsOK()) {
            return false;
        }

        if (status == Status.READY) {
            isButtonEnabled = true;
            return true;
        }
        return false;
    }

    public void compute() {

        if (checkInput()) {
            logComputationResult();
            Hypothec hypothec = createHypothec();

            creditCalculator = new CreditCalculator(hypothec);
            graphicOfPaymentsMaker = new GraphicOfPaymentsMaker(hypothec);

            computeMonthlyPayment();
            computeOverpayment();
            computeOverpaymentWithFees();
            createTable();

            status = Status.SUCCESS;
        }
    }

    public List<String> getLog() {
        return logger.getLog();
    }

    private boolean houseCostAndCountOfPeriodsIsOK() {
        double houseCostDouble;
        int countOfPeriodsDouble;

        if (houseCost.isEmpty()) {
            status = Status.WAITING;
            houseCostDouble = 0.0;
        } else {
            try {
                houseCostDouble = Double.parseDouble(houseCost);
            }  catch (Exception e) {
                status = Status.BAD_FORMAT;
                return false;
            }
        }

        if (countOfPeriods.isEmpty()) {
            status = Status.WAITING;
            countOfPeriodsDouble = 1;
        } else {
            try {
                countOfPeriodsDouble = Integer.parseInt(countOfPeriods);
            } catch (Exception e) {
                status = Status.BAD_FORMAT;
                return false;
            }
        }

        try {
            hypothecForParsing = new Hypothec.Builder(houseCostDouble, countOfPeriodsDouble);
        } catch (HypothecInputException e) {
            status = e.getMessage();
            return false;
        }
        return true;
    }

    private boolean downPaymentIsOK() {
        if (downPayment.isEmpty()) {
            status = Status.WAITING;
        } else {
            try {
                double downPaymentDouble = Double.parseDouble(downPayment);
                hypothecForParsing.downPayment(downPaymentDouble);
            } catch (HypothecInputException e) {
                status = e.getMessage();
                return false;
            } catch (Exception e) {
                status = Status.BAD_FORMAT;
                return false;
            }
        }
        return true;
    }

    private boolean interestRateIsOK() {
        if (interestRate.isEmpty()) {
            status = Status.WAITING;
        } else {
            try {
                double interestRateDouble = Double.parseDouble(interestRate);
                hypothecForParsing.interestRate(interestRateDouble);
            } catch (HypothecInputException e) {
                status = e.getMessage();
                return false;
            } catch (Exception e) {
                status = Status.BAD_FORMAT;
                return false;
            }
        }
        return true;
    }

    private boolean flatFeeIsOK() {
        if (flatFee.isEmpty()) {
            status = Status.WAITING;
        } else {
            try {
                double flatFeeDouble = Double.parseDouble(flatFee);
                hypothecForParsing.flatFee(flatFeeDouble);
            } catch (HypothecInputException e) {
                status = e.getMessage();
                return false;
            } catch (Exception e) {
                status = Status.BAD_FORMAT;
                return false;
            }
        }
        return true;
    }

    private boolean monthlyFeeIsOK() {
        if (monthlyFee.isEmpty()) {
            status = Status.WAITING;
        } else {
            try {
                double monthlyFeeDouble = Double.parseDouble(monthlyFee);
                hypothecForParsing.monthlyFee(monthlyFeeDouble);
            } catch (HypothecInputException e) {
                status = e.getMessage();
                return false;
            } catch (Exception e) {
                status = Status.BAD_FORMAT;
                return false;
            }
        }
        return true;
    }

    private boolean startDateIsOK() {
        int month;
        int year;

        if (startMonth.isEmpty() || startYear.isEmpty()) {
            status = Status.WAITING;
        } else {
            try {
                month = Integer.parseInt(startMonth) - 1;
                year = Integer.parseInt(startYear);
            } catch (Exception e) {
                status = Status.BAD_FORMAT;
                return false;
            }

            try {
                if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
                    throw new HypothecInputException(HypothecInputException.BAD_MONTH);
                }
            } catch (HypothecInputException e) {
                status = e.getMessage();
                return false;
            }

            try {
                GregorianCalendar date = new GregorianCalendar(year, month, 1);
                hypothecForParsing.startDate(date);
            } catch (HypothecInputException e) {
                status = e.getMessage();
                return false;
            }
        }
        return true;
    }

    private Hypothec createHypothec() {
        Hypothec hypothec;
        try {
            hypothec = new Hypothec
                    .Builder(Double.parseDouble(houseCost), Integer.parseInt(countOfPeriods))
                    .currency(currencyType)
                    .periodType(periodType)
                    .downPayment(Double.parseDouble(downPayment))
                    .interestRate(Double.parseDouble(interestRate))
                    .interestRateType(interestRateType)
                    .flatFee(Double.parseDouble(flatFee))
                    .flatFeeType(flatFeeType)
                    .monthlyFee(Double.parseDouble(monthlyFee))
                    .monthlyFeeType(monthlyFeeType)
                    .creditType(creditType)
                    .startDate(
                            new GregorianCalendar(Integer.parseInt(startYear),
                                    Integer.parseInt(startMonth) - 1, 1))
                    .build();
        } catch (HypothecInputException e) {
            hypothec = new Hypothec.Builder().build();
        }
        return hypothec;
    }

    private void computeMonthlyPayment() {
        double highestMonthlyPayment =
                creditCalculator.computeHighestMonthlyPayment();
        double lowestMonthlyPayment =
                creditCalculator.computeLowestMonthlyPayment();

        if (highestMonthlyPayment - lowestMonthlyPayment < DOUBLE_DELTA) {
            monthlyPayment = Double.toString(highestMonthlyPayment);
        } else {
            monthlyPayment = Double.toString(highestMonthlyPayment)
                    + " ... "
                    + Double.toString(lowestMonthlyPayment);
        }
    }

    private void computeOverpayment() {
        overpayment = Double.toString(creditCalculator.computeOverpayment());
    }

    private void computeOverpaymentWithFees() {
        overpaymentWithFees = Double.toString(creditCalculator.computeOverpaymentWithFees());
    }

    private void cleanOutput() {
        monthlyPayment = "...";
        overpaymentWithFees = "...";
        overpayment = "...";

        graphicOfPayments = new DefaultTableModel();
    }

    private void createTable() {
        graphicOfPayments = graphicOfPaymentsMaker.getTableModel();
    }

    private void logParameterChange(final String parameter) {
        logger.addMessage(LogMessage.PARAMETER_WAS_CHANGED + parameter);
    }

    private void logComputationResult() {
        String message = LogMessage.COMPUTE
                + "Стоимость недвижимости: " + houseCost
                + " " + currencyType + "\n"
                + "Первоначальный взнос: " + downPayment
                + " " + currencyType + "\n"
                + "Срок ипотеки: " + countOfPeriods
                + " " + periodType + "\n"
                + "Процентная ставка: " + interestRate
                + " " + interestRateType + "\n"
                + "Единовременные комиссии: " + flatFee
                + " " + flatFeeType + "\n"
                + "Ежемесячные комиссии: " + monthlyFee
                + " " + monthlyFeeType + "\n"
                + "Начало выплат: " + startMonth
                + "." + startYear + "\n"
                + "Тип кредита: " + creditType;
        logger.addMessage(message);
    }

    public String getStatus() {
        return status;
    }

    public boolean isButtonEnabled() {
        return isButtonEnabled;
    }

    public String getHouseCost() {
        return houseCost;
    }

    public void setHouseCost(final String houseCost) {
        if (!houseCost.equals(this.houseCost)) {
            logParameterChange("\"Стоимость недвижимости\": " + houseCost);
        }
        this.houseCost = houseCost;
    }

    public String getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(final String downPayment) {
        if (!downPayment.equals(this.downPayment)) {
            logParameterChange("\"Первоначальный взнос\": " + downPayment);
        }
        this.downPayment = downPayment;
    }

    public String getCountOfPeriods() {
        return countOfPeriods;
    }

    public void setCountOfPeriods(final String countOfPeriods) {
        if (!countOfPeriods.equals(this.countOfPeriods)) {
            logParameterChange("\"Срок ипотеки\": " + countOfPeriods);
        }
        this.countOfPeriods = countOfPeriods;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(final String interestRate) {
        if (!interestRate.equals(this.interestRate)) {
            logParameterChange("\"Процентная ставка\": " + interestRate);
        }
        this.interestRate = interestRate;
    }

    public String getFlatFee() {
        return flatFee;
    }

    public void setFlatFee(final String flatFee) {
        if (!flatFee.equals(this.flatFee)) {
            logParameterChange("\"Единовременные комиссии\": " + flatFee);
        }
        this.flatFee = flatFee;
    }

    public String getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(final String monthlyFee) {
        if (!monthlyFee.equals(this.monthlyFee)) {
            logParameterChange("\"Ежемесячные комиссии\": " + monthlyFee);
        }
        this.monthlyFee = monthlyFee;
    }

    public Hypothec.CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(final Hypothec.CurrencyType currencyType) {
        if (currencyType != this.currencyType) {
            logParameterChange("\"Тип валюты\": " + currencyType);
        }
        this.currencyType = currencyType;
    }

    public Hypothec.PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(final Hypothec.PeriodType periodType) {
        if (periodType != this.periodType) {
            logParameterChange("\"Тип периода времени\": " + periodType);
        }
        this.periodType = periodType;
    }

    public Hypothec.InterestRateType getInterestRateType() {
        return interestRateType;
    }

    public void setInterestRateType(final Hypothec.InterestRateType interestRateType) {
        if (interestRateType != this.interestRateType) {
            logParameterChange("\"Тип процентной ставки\": " + interestRateType);
        }
        this.interestRateType = interestRateType;
    }

    public Hypothec.FlatFeeType getFlatFeeType() {
        return flatFeeType;
    }

    public void setFlatFeeType(final Hypothec.FlatFeeType flatFeeType) {
        if (flatFeeType != this.flatFeeType) {
            logParameterChange("\"Тип единовременной комиссии\": "
                    + flatFeeType);
        }
        this.flatFeeType = flatFeeType;
    }

    public Hypothec.MonthlyFeeType getMonthlyFeeType() {
        return monthlyFeeType;
    }

    public void setMonthlyFeeType(final Hypothec.MonthlyFeeType monthlyFeeType) {
        if (monthlyFeeType != this.monthlyFeeType) {
            logParameterChange("\"Тип ежемесячной комиссии\": "
                    + monthlyFeeType);
        }
        this.monthlyFeeType = monthlyFeeType;
    }

    public Hypothec.CreditType getCreditType() {
        return creditType;
    }

    public void setCreditType(final Hypothec.CreditType creditType) {
        if (creditType != this.creditType) {
            logParameterChange("\"Тип кредита\": " + creditType);
        }
        this.creditType = creditType;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(final String startMonth) {
        if (!startMonth.equals(this.startMonth)) {
            logParameterChange("\"Месяц начала выплат\": " + startMonth);
        }
        this.startMonth = startMonth;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(final String startYear) {
        if (!startYear.equals(this.startYear)) {
            logParameterChange("\"Год начала выплат\": " + startYear);
        }
        this.startYear = startYear;
    }

    public String getMonthlyPayment() {
        return monthlyPayment;
    }

    public String getOverpayment() {
        return overpayment;
    }

    public String getOverpaymentWithFees() {
        return overpaymentWithFees;
    }

    public DefaultTableModel getGraphicOfPayments() {
        return graphicOfPayments;
    }

    public final class Status {
        public static final String WAITING
                = "Введите параметры кредита";
        public static final String READY
                = "Нажмите кнопку \"Рассчитать\"";
        public static final String BAD_FORMAT
                = "Введены даннные неверного формата ";
        public static final String SUCCESS
                = "Успех полностью достигнут";

        private Status() { }
    }

    public final class LogMessage {
        public static final String PARAMETER_WAS_CHANGED
                = "Установлено новое значение параметра ";
        public static final String COMPUTE
                = "Произведены расчеты для кредита со "
                + "следующими параметрами: \n";

        private LogMessage() { }
    }
}
