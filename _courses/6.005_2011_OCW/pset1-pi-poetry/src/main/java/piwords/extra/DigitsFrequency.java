package piwords.extra;

import pigen.BBPHex;
import piwords.main.BaseTranslator;
import piwords.main.DigitsToStringConverter;
import piwords.utils.Utils;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static piwords.main.DigitsToStringConverter.convertDigitsToString;
import static piwords.main.DigitsToStringConverter.convertDigitsToStringWithFreq;

/**
 * Created by ilyarudyak on 11/21/16.
 */
public class DigitsFrequency {

    // https://en.wikipedia.org/wiki/Letter_frequency
    private static final String ORDERED_ALPHABET = "etaoinshrdlcumwfgypbvkjxqz";

    public static Map<Integer, Long> getFreqMap(List<Integer> pi) {

        return pi.stream()
                .collect(Collectors.groupingBy(
                       Function.identity(),
                       Collectors.counting()
                ));

    }


    /**
     * Read hex digits from file (not generate them) and convert them
     * into list of digits in base26.
     *
     * @param precision number of digits *after* dot
     * @return List of digits after dot in base26.
     */
    public static List<Integer> getPiBase26(int precision) {

        int baseA = 16;
        int baseB = 26;
        int precisionToUse = (int) (precision * 1.5);

        Stream<Integer> piStream = BBPHex.piInHexStream();

        int[] digitsInHex = Utils.toIntArray(Utils.readPiHex(precisionToUse));

        int[] convertedArray = BaseTranslator.convertBase(
                digitsInHex, baseA, baseB, precisionToUse);

        List<Integer> convertedList = IntStream.of(convertedArray)
                .mapToObj(Integer::valueOf)
                .limit(precision)
                .collect(Collectors.toList());

        return convertedList;
    }

    /**
     * We order digits based on their frequency in Pi.
     */
    public static List<Integer> getOrderedDigitsFromBase26(int precision) {

        Map<Integer, Long> freqMap = getFreqMap(getPiBase26(precision));
        List<Integer> orderedDigits = new ArrayList<>(freqMap.keySet());
        Comparator<Integer> byFrequency = (d1, d2) -> freqMap.get(d2).intValue() - freqMap.get(d1).intValue();
        orderedDigits.sort(byFrequency);
        return orderedDigits;
    }

    public static String getPiBase26WithFreq(int precision) {

        List<Integer> digits = getPiBase26(10000);
        List<Integer> digitsFreq = getOrderedDigitsFromBase26(precision);

        int base = 26;
        List<String> alphabet = Pattern.compile("").splitAsStream(ORDERED_ALPHABET)
                .collect(Collectors.toList());

        String convertedString = convertDigitsToStringWithFreq(digits, digitsFreq, base, alphabet);

        return convertedString;
    }
}




















