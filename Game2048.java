package com.javarush.task.jdk13.task53.task5301;
import com.javarush.engine.cell.*;

import java.util.Arrays;

public class Game2048 extends Game {
    private static final int SIDE = 4;
    private final int[][] gameField = new int[SIDE][SIDE];
    private boolean isGameStopped = false;
    private int score = 0;




    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        //gameField = new int [][] {{2, 4, 8, 16}, {2, 64, 128, 256}, {512, 2, 4, 8}, {16, 32, 64, 128} };
        drawScene();

        //System.out.println(canUserMove());
    }

    private void drawScene() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
        }
    }

    private int getMaxTileValue() {
        int temp = 0;
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                if (gameField[i][j] > temp) {
                    temp = gameField[i][j];
                }
            }
        }
        return temp;
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.PURPLE, "You just won!!! Congrats", Color.BLACK, 60);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.ALICEBLUE, "Game is over. Start anew?", Color.BLACK, 60);

    }

    @Override
    public void setScore(int score) {
        super.setScore(score);
    }

    @Override
    public void onKeyPress(Key key) {
        if (!canUserMove()) {
            gameOver();
            return;    //чтобы не использовать loop - из onKeyPress - он всегда слушает
        }
        if (isGameStopped) {
            if (key == Key.SPACE) {
                isGameStopped = false;
                score = 0;
                setScore(0);
                createGame();
                drawScene();
                onKeyPress(key);
            } else {
                return;
            }
        }

        if (key == Key.LEFT) {
            moveLeft();
        } else if (key == Key.RIGHT) {
            moveRight();
        } else if (key == Key.UP) {
            moveUp();
        } else if (key == Key.DOWN) {
            moveDown();
        } else {
            return;    //чтобы не вызывалось при нажатии других клавиш
        }
        drawScene();
    }

    private void moveLeft() {
        boolean wasOnce = false;
        for (int i = 0; i < gameField.length; i++) {
            boolean compressedOnce = compressRow(gameField[i]);
            boolean mergedOnce = mergeRow(gameField[i]);
            if (mergedOnce) {
                compressRow(gameField[i]);
            }
            if (compressedOnce || mergedOnce) {
                wasOnce = true;
            }
        }
        if (wasOnce) {
            createNewNumber();
        }

    }
    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();

    }
    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();

    }
    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private boolean canUserMove() {
        int countZeroes = 0;
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                if (gameField[i][j] == 0) {
                    countZeroes++;
                }
            }
        }

        if (countZeroes > 0) {
            return true;
        } else {  // нет нулей
            int[][] array = Arrays.copyOf(gameField, gameField.length);
            for (int i = 0; i < array.length; i++) {
                for (int j = 0; j < array[i].length; j++) {
                    if ((j < array.length - 1 && array[j][i] == array[j + 1][i]) ||
                        (i < array.length - 1 && array[j][i] == array[j][i + 1])) {
                            return true;
                    }
                }
            }
        }
        return false;
    }

    private void createGame() {
        for (int i = 0; i < gameField.length; i++) {
            Arrays.fill(gameField[i], 0);
        }
        createNewNumber();
        createNewNumber();

    }

    private void rotateClockwise() {
        int n = gameField.length;

            // Step 1: Transpose the matrix
            for (int i = 0; i < n; i++) {
                for (int j = i; j < n; j++) {
                    // Swap matrix[i][j] with matrix[j][i]
                    int temp = gameField[i][j];
                    gameField[i][j] = gameField[j][i];
                    gameField[j][i] = temp;
                }
            }

            // Step 2: Reverse each row
            for (int i = 0; i < n; i++) {
                int left = 0;
                int right = n - 1;
                while (left < right) {
                    // Swap elements in the row
                    int temp = gameField[i][left];
                    gameField[i][left] = gameField[i][right];
                    gameField[i][right] = temp;

                    left++;
                    right--;
                }
            }

    }


     private boolean compressRow(int[] row) {

        int countnonZeroes = 0;
        for (int i = 0; i < row.length; i++) { //сколько ненулей в массиве
            if (row[i] != 0) {
                countnonZeroes++;
            }
        }

        boolean result = false;
        for (int i = row.length - 2; i >= 0; i--) {
                while (row[i] == 0 && row[i + 1] != 0 && countnonZeroes != 0) { //we found a Zero
                    int temp = row[i + 1];
                    row[i + 1] = 0;
                    row[i] = temp;
                           //  place that zero at the right beginning!
                    if (i != row.length - 1 && row[i] != 0 && i < row.length - 2) { // change the index to one right
                        i += 1;

                    } else if (row[i] == 0) {
                        countnonZeroes--;
                    }

                    result = true;
                }
        }
        return result;
    }

    private boolean mergeRow(int[] row) {
        int temp = 0;
        int tempI = 0;
        boolean result = false;
        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] != 0) {
                temp = row[i];
                tempI = i;
                //while (row[i + 1] == 0) {
                //    i = i + 1;        // теперь row[i+1] не равен нулю
                //}

                if (temp != row[i+1]) { // если числа не равны (4 и 2) то следующий виток
                    continue;
                }
                //row[i+1] равен не нулю

                row[i + 1] = 0;
                row[tempI] = temp * 2;
                score += temp * 2;
                setScore(score);
                result = true;
                i++;
            } else { // row[i] равен нулю - следующий элемент
            }

        }
        return result;
    }


    private void createNewNumber() {
        int maxTileValue = getMaxTileValue();
        if (maxTileValue == 2048) {
            win();
            return;
        }

        int x = getRandomNumber(SIDE);
        int y = getRandomNumber(SIDE);
        if (gameField[x][y] == 0) {  //если рандомная равна нулю то
            int temp = getRandomNumber(10);
            if (temp == 9) {
                gameField[x][y] = 4;

                //setCellValue(x, y, "4");
            } else {

                gameField[x][y] = 2;
                //setCellValue(x, y, "2");
            }
        } else if (gameField[x][y] != 0) {
            createNewNumber();
        }

    }

    private void setCellColoredNumber(int x, int y, int value) {
        setCellValueEx(x, y, getColorByValue(value), String.valueOf(value));

        if (value == 0) {
            setCellValueEx(x, y, getColorByValue(value), "");
        }
    }

    private Color getColorByValue(int value) {
        if (value == 0) {
            return Color.values()[Color.values().length - 1];
        }
        int indexForValue = (int) (Math.log(value) / Math.log(2));

        return Color.values()[indexForValue];
    }
}

