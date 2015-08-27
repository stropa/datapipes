package org.stropa.data.util;


import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple hash based validator for inout params and generator for output params
 */
public class HashGenerator {

    private static final Logger logger = LoggerFactory.getLogger(HashGenerator.class);


    /**
     * Don't forget that for correct calculation extremely important an order of parameters
     * <p>
     * For example, secret word - mainly it's using at the end of the list of parameters
     *
     * @param args - list of arrays
     * @return String - MD5 Hash
     */
    public static String getMd5Hash(int times, Object... args) {
        if (times < 0) times = 0;
        StringBuilder sb = new StringBuilder();
        String md5Hash;
        for (Object a : args) {
            if (a == null)
                continue;
            sb.append(a);
        }

        md5Hash = sb.toString();
        for (int c = 0; c < times; c++) {
            md5Hash = DigestUtils.md5Hex(md5Hash);
            logger.trace("Step " + (c + 1) + " hash: " + md5Hash);
        }
        return md5Hash;
    }
}

