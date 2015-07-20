package org.luhn;

/**
 The MIT License (MIT)

 Copyright (c) 2014 Nishan Naseer

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

public class Luhn {

    /**
     * Validate a number string using Luhn algorithm
     *
     * @param numberString
     * @return
     */
    public static boolean validate(String numberString) {
        int checkDigit = Integer.parseInt(String.valueOf(numberString.charAt(numberString.length()-1)));
        return checkSum(numberString) == checkDigit;
    }

    /**
     * Generate check digit for a number string. Assumes check digit or a place
     * holder is already appended at end of the string.
     *
     * @param numberString
     * @return
     */
    public static int checkSum(String numberString) {
        return checkSum(numberString, false);
    }

    /**
     * Generate check digit for a number string.
     *
     * @param numberString
     * @param noCheckDigit
     *            Whether check digit is present or not. True if no check Digit
     *            is appended.
     * @return
     */
    public static int checkSum(String numberString, boolean noCheckDigit) {
        int sum = 0, checkDigit = 0;

        if(!noCheckDigit)
            // Drop the last digit from the number. The last digit is what we want to check against
            numberString = numberString.substring(0, numberString.length()-1);

        boolean isDouble = true;
        // Reverse the numbers
        for (int i = numberString.length() - 1; i >= 0; i--) {
            // Multiply the digits in odd (here even, as taking into account dropped number)
            // positions (1, 3, 5, etc.) by 2 and subtract 9 to all any result higher than 9
            int k = Integer.parseInt(String.valueOf(numberString.charAt(i)));
            sum += sumToSingleDigit((k * (isDouble ? 2 : 1)));
            // Add all the numbers together
            isDouble = !isDouble;
        }

        // The check digit (the last number of the card) is the amount that you would need to add to get a multiple of 10 (Modulo 10)
        if ((sum % 10) > 0)
            checkDigit = (10 - (sum % 10));

        return checkDigit;
    }

    private static int sumToSingleDigit(int k) {
        if (k < 10)
            return k;
        return sumToSingleDigit(k / 10) + (k % 10);
    }

}