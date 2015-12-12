package ru.unn.NewtonMethod.viewModel;

import ru.unn.agile.NewtonMethod.ConverterToPolishNotation;
import ru.unn.agile.NewtonMethod.NewtonMethod;

import java.util.List;

public class NewtonMethodViewModel {
    private boolean isCalculateButtonEnabled;
    private boolean isInputRangeChanged;
    private boolean isInputFunctionsChanged;
    private String function;
    private String derivative;
    private String leftPointOfRange;
    private String rightPointOfRange;
    private Status status;
    private double root;
    private ConverterToPolishNotation converter;
    private static final int NUMBER_FIELDS = 4;
    private static final int NUMBER_DECIMAL_PLACES = 1000;
    private int numberFillFields = 0;
    private INewtonMethodLogger logger;

    public NewtonMethodViewModel(final INewtonMethodLogger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("Logger parameter can't be null");
        }

        this.logger = logger;
        isCalculateButtonEnabled = false;
        isInputRangeChanged = false;
        isInputFunctionsChanged = false;
        function = "";
        derivative = "";
        leftPointOfRange = "";
        rightPointOfRange = "";
        root = 0;
        status = Status.WAITING;
        converter = new ConverterToPolishNotation();
    }

    public enum KeyboardKeys {
        ENTER(10),
        ANY(777);

        KeyboardKeys(final int key) {
            this.key = key;
        }

        public int getKey() {
            return key;
        }

        private int key;
    }

    public enum Status {
        WAITING("Please provide input data"),
        READY("Press 'Calculate' or Enter"),
        BAD_FORMAT_RANGE("Bad format of range"),
        BAD_FORMAT_FUNCTION("Incorrect function"),
        SUCCESS("Success"),
        NO_ROOT("Root is not in range");

        Status(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        private String message;
    }

    public enum LogMessages {
        RANGE_FIELD_CHANGED("Update borders of range: "),
        FUNCTION_CHANGED("Update functions: "),
        CALCULATE_BUTTON_PRESSED("Button calculate was pressed. ");

        private String logMessage;

        LogMessages(final String logMessage) {
            this.logMessage = logMessage;
        }

        @Override
        public String toString() {
            return logMessage;
        }
    }

    public boolean isCalculateButtonEnabled() {
        return isCalculateButtonEnabled;
    }

    public void processKeyInTextField(final int keyCode) {
        parseInput();

        if (keyCode == KeyboardKeys.ENTER.getKey()) {
            enterPressed();
        }
    }

    public List<String> getLog() {
        return logger.getLog();
    }

    public void valueFieldFocusLost() {
        logInputRangeParams();
        logInputFunctions();
    }

    public void setFunction(final String function) {
        if (function.equals(this.function)) {
            return;
        }
        this.function = function;
        isInputFunctionsChanged = true;
    }

    public void setDerivative(final String derivative) {
        if (derivative.equals(this.derivative)) {
            return;
        }
        this.derivative = derivative;
        isInputFunctionsChanged = true;
    }

    public void setLeftPointOfRange(final String leftPointOfRange) {
        if (leftPointOfRange.equals(this.leftPointOfRange)) {
            return;
        }
        this.leftPointOfRange = leftPointOfRange;
        isInputRangeChanged = true;
    }

    public void setRightPointOfRange(final String rightPointOfRange) {
        if (rightPointOfRange.equals(this.rightPointOfRange)) {
            return;
        }
        this.rightPointOfRange = rightPointOfRange;
        isInputRangeChanged = true;
    }

    public String  getLeftPoint() {
        return leftPointOfRange;
    }

    public String getRightPoint() {
        return rightPointOfRange;
    }

    public String getFunction() {
        return function;
    }

    public String getDerivative() {
        return derivative;
    }

    public String getRoot() {
        return String.valueOf(root);
    }

    public String getStatus() {
        return status.getMessage();
    }

    private void enterPressed() {
        logInputRangeParams();
        logInputFunctions();
        if (isCalculateButtonEnabled()) {
            calculateRoot();
        }
    }

    private void parseInput() {
        try {
            checkInputValues();
        } catch (ArithmeticException e) {
            status = Status.BAD_FORMAT_FUNCTION;
            isCalculateButtonEnabled = false;
            return;
        } catch (Exception e) {
            status = Status.BAD_FORMAT_RANGE;
            isCalculateButtonEnabled = false;
            return;
        }

        isCalculateButtonEnabled = isInputAvailable();
        if (isCalculateButtonEnabled) {
            checkRangeCorrect();
            status = Status.READY;
        } else {
            status = Status.WAITING;
        }
    }

    private void checkInputValues() {
        numberFillFields = 0;
        if (!leftPointOfRange.isEmpty()) {
            Double.parseDouble(leftPointOfRange);
            numberFillFields++;
        }
        if (!rightPointOfRange.isEmpty()) {
            Double.parseDouble(rightPointOfRange);
            numberFillFields++;
        }
        if (!function.isEmpty()) {
            function += "=";
            converter.convert(function);
            numberFillFields++;
        }
        if (!derivative.isEmpty()) {
            derivative += "=";
            converter.convert(derivative);
            numberFillFields++;
        }
    }

    private void checkRangeCorrect() {
        if (Double.parseDouble(leftPointOfRange) >= Double.parseDouble(rightPointOfRange)) {
            String temp = leftPointOfRange;
            leftPointOfRange = rightPointOfRange;
            rightPointOfRange = temp;
        }
    }

    private boolean isInputAvailable() {
        return numberFillFields == NUMBER_FIELDS;
    }

    private void calculateRoot() {
        logger.log(calculateLogMessage());
        NewtonMethod newtonMethod = new NewtonMethod(function, derivative);
        try {
            root = newtonMethod.searchRoot(Double.parseDouble(leftPointOfRange),
                                           Double.parseDouble(rightPointOfRange));
        } catch (IllegalArgumentException e) {
            Status.NO_ROOT.setMessage(e.getMessage());
            status = Status.NO_ROOT;
            return;
        }
        roundResult(NUMBER_DECIMAL_PLACES);
        status = Status.SUCCESS;
    }

    private String calculateLogMessage() {
        String message = LogMessages.CALCULATE_BUTTON_PRESSED + "Function: [" + function + "];"
                + "Range: [" + leftPointOfRange + ";" + rightPointOfRange + "].";
        return message;
    }

    private void roundResult(final int numberDecimalPlaces) {
        root *= numberDecimalPlaces;
        root = Math.round(root);
        root /= numberDecimalPlaces;
    }

    private void logInputFunctions() {
        if (!isInputFunctionsChanged) {
            return;
        }
        logger.log(editingLogMessageForFunction());
        isInputFunctionsChanged = false;
    }

    private String editingLogMessageForFunction() {
        String message = LogMessages.FUNCTION_CHANGED +
                "function [" + function + "]; derivative [" + derivative + "]";
        return message;
    }

    private void logInputRangeParams() {
        if (!isInputRangeChanged) {
            return;
        }
        logger.log(editingLogMessageForRange());
        isInputRangeChanged = false;
    }

    private String editingLogMessageForRange() {
        String message = LogMessages.RANGE_FIELD_CHANGED +
                "[" + leftPointOfRange + ";" + rightPointOfRange + "]";
        return message;
    }
}
