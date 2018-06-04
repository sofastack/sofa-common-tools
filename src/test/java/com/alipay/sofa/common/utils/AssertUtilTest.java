package com.alipay.sofa.common.utils;

import org.junit.Test;

/**
 * AssertUtil Tester.
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since 18/06/04
 */
public class AssertUtilTest {


    /**
     * Method: isTrue(boolean expression, String message)
     */
    @Test
    public void testIsTrueForExpressionMessage() throws Exception {
        boolean isSuccess = false;
        AssertUtil.isTrue(!isSuccess, "isTrue");
        boolean isException = false;
        try {
            AssertUtil.isTrue(isSuccess, "isTrue");
        } catch (Exception ex) {
            isException = true;
        }
        AssertUtil.isTrue(isException);
    }

    /**
     * Method: isNull(Object object, String message)
     */
    @Test
    public void testIsNullForObjectMessage() throws Exception {
        Object object = null;
        AssertUtil.isNull(object, "null");
        AssertUtil.isNull(object);
    }
} 
