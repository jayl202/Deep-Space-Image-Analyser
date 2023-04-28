package com.example.deepspaceimageeditor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StarTest {

    @Test
    public void testStarConstructorAndGetters() {
        Star star = new Star(1, 100, 0.5, 0.3, 0.2);

        assertEquals(1, star.getId());
        assertEquals(100, star.getSize());
        assertEquals(0.5, star.getSulphur());
        assertEquals(0.3, star.getHydrogen());
        assertEquals(0.2, star.getOxygen());

        assertTrue(star.getSulphur() >= 0 && star.getSulphur() <= 1);
        assertTrue(star.getHydrogen() >= 0 && star.getHydrogen() <= 1);
        assertTrue(star.getOxygen() >= 0 && star.getOxygen() <= 1);
    }

    @Test
    public void testStarSetters() {
        Star star = new Star(1, 100, 0.5, 0.3, 0.2);

        star.setId(2);
        star.setSize(200);
        star.setSulphur(0.6);
        star.setHydrogen(0.4);
        star.setOxygen(0.3);

        assertEquals(2, star.getId());
        assertEquals(200, star.getSize());
        assertEquals(0.6, star.getSulphur());
        assertEquals(0.4, star.getHydrogen());
        assertEquals(0.3, star.getOxygen());

        assertTrue(star.getSulphur() >= 0 && star.getSulphur() <= 1);
        assertTrue(star.getHydrogen() >= 0 && star.getHydrogen() <= 1);
        assertTrue(star.getOxygen() >= 0 && star.getOxygen() <= 1);
    }
}