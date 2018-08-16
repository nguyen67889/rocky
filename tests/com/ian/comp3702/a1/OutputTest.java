package com.ian.comp3702.a1;

import org.junit.Test;

import static org.junit.Assert.*;

public class OutputTest {
    @Test
    public void testExample() {
        String[] args = new String[]{"iotests/input0.txt", "iotests/output0.tmp"};
        Main.main(args);

        assertTrue(true);
    }
}
