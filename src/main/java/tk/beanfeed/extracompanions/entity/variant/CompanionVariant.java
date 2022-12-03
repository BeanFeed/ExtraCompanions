package tk.beanfeed.extracompanions.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum CompanionVariant {
    MALE0(0),
    MALE1(1),
    MALE2(2),
    MALE3(3),
    MALE4(4),
    FEMALE0(5),
    FEMALE1(6),
    FEMALE2(7),
    FEMALE3(8),
    FEMALE4(9);

    private static final CompanionVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(CompanionVariant::getId)).toArray(CompanionVariant[]::new);
    private final int id;
    CompanionVariant(int id) {
        this.id = id;
    }

    public int getId() { return this.id; }

    public static CompanionVariant byId(int id) { return BY_ID[id % BY_ID.length]; }
}

