package com.ramon.ramonrm.util;

import java.util.Random;

public class RoomUtil {
    public static int createRoomId() {
        Random random = new Random(System.currentTimeMillis());
        return (int) (random.nextDouble() * 1000000) + 1000000;
    }
}
