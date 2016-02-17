package ru.unn.agile.LongArithmetic.viewmodel;

import ru.unn.agile.LongArithmetic.model.LongNumber;
import ru.unn.agile.LongArithmetic.model.Matrix;

public class ViewModel {

    private static final int INITIAL_SIZE_MATRIX = 1;

    private String heightFirstMatrix;
    private String widthFirstMatrix;
    private String heightSecondMatrix;
    private String widthSecondMatrix;
    private int height1;
    private int width1;
    private int height2;
    private int width2;

    private String [][] firstMatrixTable;
    private String [][] secondMatrixTable;
    private Matrix firstMultiplier;
    private Matrix secondMultiplier;
    private Matrix resultMatrix;

    private String status;
    private boolean isOkButtonEnabled;
    private boolean isMultiplyButtonEnabled;
    private boolean isInputMatricesAvailable;


    public ViewModel() {
        heightFirstMatrix = "";
        widthFirstMatrix = "";
        heightSecondMatrix = "";
        widthSecondMatrix = "";
        status = Status.WAITING_COLS_AND_ROWS;

        firstMatrixTable = new String[INITIAL_SIZE_MATRIX][INITIAL_SIZE_MATRIX];
        secondMatrixTable = new String[INITIAL_SIZE_MATRIX][INITIAL_SIZE_MATRIX];

        isOkButtonEnabled = false;
        isMultiplyButtonEnabled = false;
        isInputMatricesAvailable = false;
    }

    public void processingInputMatrixSizes() {
        parseInputMatrixSizes();
        if (isOkButtonEnabled()) {
            firstMatrixTable = new String[height1][width1];
            secondMatrixTable = new String[height2][width2];
            firstMultiplier = new Matrix(height1, width1);
            secondMultiplier = new Matrix(height2, width2);
            status = Status.WAITING;
            isMultiplyButtonEnabled = true;
        }
    }

    public void parseInputMatrixSizes() {
        isMultiplyButtonEnabled = false;
        isOkButtonEnabled = false;

        try {
            tryParseInputMatrixSizes();

            if (isInputMatrixSizesAvailable() && width1 == height2) {
                isOkButtonEnabled = true;
            }
            if (status != Status.WAITING) {
                if (isOkButtonEnabled) {
                    status = Status.READY_OK;
                } else {
                    status = Status.WAITING_COLS_AND_ROWS;
                }
            }
        } catch (Exception e) {
            status = Status.BAD_FORMAT;
        }
    }

    private void tryParseInputMatrixSizes() {
        if (!heightFirstMatrix.isEmpty()) {
            height1 = Integer.parseInt(heightFirstMatrix);
        }
        if (!widthFirstMatrix.isEmpty()) {
            width1 = Integer.parseInt(widthFirstMatrix);
        }
        if (!heightSecondMatrix.isEmpty()) {
            height2 = Integer.parseInt(heightSecondMatrix);
        }
        if (!widthSecondMatrix.isEmpty()) {
            width2 = Integer.parseInt(widthSecondMatrix);
        }
    }

    public boolean isOkButtonEnabled() { return isOkButtonEnabled; }

    private boolean isInputMatrixSizesAvailable() {
        return !(heightFirstMatrix.isEmpty() || widthFirstMatrix.isEmpty()
               || heightSecondMatrix.isEmpty() || widthSecondMatrix.isEmpty());
    }

    public void processingInputMatrices() {
        parseInputMatrices();
        if (isInputMatricesAvailable()) {
            multiplyMatrices();
            status = Status.SUCCESS;
        }
    }

    public  void parseInputMatrices() {
        if (status == Status.WAITING_COLS_AND_ROWS) {
            return;
        }

        try {
            tryParseMatrix(firstMatrixTable);
            tryParseMatrix(secondMatrixTable);
            isInputMatricesAvailable = true;
            status = Status.READY_MULTIPLY;
        } catch (Exception e) {
            isInputMatricesAvailable = false;
            status = Status.BAD_FORMAT;
        }
    }

    private boolean isInputMatricesAvailable() { return isInputMatricesAvailable; }

    private void tryParseMatrix(final String[][] matrix) throws Exception {
        LongNumber value;
        int rows = matrix.length;
        for (int i = 0; i < rows; i++) {
            int cols = matrix[i].length;
            for (int j = 0; j < cols; j++) {
                value = new LongNumber(matrix[i][j]);
                if (value.isUndefined()) {
                    throw new BadFormatToCellException("Can't parse input matrices, bad format");
                }

                if (matrix.equals(firstMatrixTable)) {
                    firstMultiplier.setElement(i, j, value);
                } else {
                    secondMultiplier.setElement(i, j, value);
                }
            }
        }
    }

    private void multiplyMatrices() {
        if (status == Status.READY_MULTIPLY) {
            resultMatrix = firstMultiplier.multiply(secondMultiplier);
        }
    }

    public String getHeightFirstMatrixStr() { return heightFirstMatrix; }

    public void setHeightFirstMatrixStr(final String heightFirstMatrix) {
        if (!heightFirstMatrix.equals(this.heightFirstMatrix)) {
            this.heightFirstMatrix = heightFirstMatrix;
        }
    }

    public String getWidthFirstMatrixStr() { return widthFirstMatrix; }

    public void setWidthFirstMatrixStr(final String widthFirstMatrix) {
        if (!widthFirstMatrix.equals(this.widthFirstMatrix)) {
            this.widthFirstMatrix = widthFirstMatrix;
        }
    }

    public String getHeightSecondMatrixStr() { return heightSecondMatrix; }

    public void setHeightSecondMatrixStr(final String heightSecondMatrix) {
        if (!heightSecondMatrix.equals(this.heightSecondMatrix)) {
            this.heightSecondMatrix = heightSecondMatrix;
        }
    }

    public String getWidthSecondMatrixStr() { return widthSecondMatrix; }

    public void setWidthSecondMatrixStr(final String widthSecondMatrix) {
        if (!widthSecondMatrix.equals(this.widthSecondMatrix)) {
            this.widthSecondMatrix = widthSecondMatrix;
        }
    }

    public boolean isMultiplyButtonEnabled() { return isMultiplyButtonEnabled; }

    public int getHeightFirstMatrix() { return height1; }

    public int getWidthFirstMatrix() { return width1; }

    public int getHeightSecondMatrix() { return height2; }

    public int getWidthSecondMatrix() { return width2; }

    public Matrix getResultMatrix() { return resultMatrix; }

    public String getStatus() { return status; }

    public void setValueToFirstMatrix(final int i, final int j, final String newValue) {
        if (i > -1 && i < height1 && i > -1 && j < width1) {
            firstMatrixTable[i][j] = newValue;
        }
    }

    public void setValueToSecondMatrix(final int i, final int j, final String newValue) {
        if (i > -1 && i < height2 && i > -1 && j < width2) {
            secondMatrixTable[i][j] = newValue;
        }
    }

    public final class Status {
        public static final String WAITING_COLS_AND_ROWS
                                        = "Please provide input data: M and N for matrices";
        public static final String WAITING = "Please provide input data: write in matrices";
        public static final String READY_OK = "Press 'Ok'";
        public static final String READY_MULTIPLY = "Press 'Multiply'";
        public static final String BAD_FORMAT = "Bad format";
        public static final String SUCCESS = "Success";

        private Status() { }
    }
}
